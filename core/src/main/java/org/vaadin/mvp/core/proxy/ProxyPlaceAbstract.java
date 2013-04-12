/*
 * Copyright 2012 Cedric Hauber
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.vaadin.mvp.core.proxy;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.EventBus;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import org.vaadin.mvp.core.events.*;
import org.vaadin.mvp.core.places.Place;
import org.vaadin.mvp.core.presenters.Presenter;

/**
 * A useful mixing class to define a {@link Proxy} that is also a {@link org.vaadin.mvp.core.places.Place}.
 * You can usually inherit from the simpler form {@link ProxyPlace}.
 * <p />
 *
 * @param <P> The Presenter's type.
 * @param <Proxy_> Type of the associated {@link Proxy}.
 *
 * @author David Peterson
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
public class ProxyPlaceAbstract<P extends Presenter<?>, Proxy_ extends Proxy<P>>
    implements ProxyPlace<P> {

  protected Place place;
  protected Navigator navigator;
  protected Proxy_ proxy;

  private EventBus eventBus;

  /**
   * Creates a {@link ProxyPlaceAbstract}. That is, the {@link Proxy} of a
   * {@link org.vaadin.mvp.core.presenters.Presenter} attached to a {@link org.vaadin.mvp.core.places.Place}. This presenter can be
   * invoked by setting a history token that matches its name token in the URL
   * bar.
   */
  public ProxyPlaceAbstract(Proxy_ proxy, Place place, final Navigator navigator) {
      this.proxy = proxy;
      this.place = place;
      this.navigator = navigator;
      this.eventBus = proxy.getEventBus();
      eventBus.addHandler(GetPlaceTitleEvent.getType(),
              new GetPlaceTitleHandler() {
                  @Override
                  public void onGetPlaceTitle(GetPlaceTitleEvent event) {
                      if (event.isHandled()) {
                          return;
                      }
                      ViewChangeListener.ViewChangeEvent request = event.getRequest();
                      if (matchesRequest(request)) {
                          if (canReveal()) {
                              event.setHandled();
                              getPlaceTitle(event);
                          }
                      }
                  }
              });
      navigator.addView(place.getNameToken(), this);
  }

  @Override
  public boolean canReveal() {
    return place.canReveal();
  }

  // /////////////////////
  // Inherited from Proxy

  @Override
  public final boolean equals(Object o) {
    return place.equals(o);
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    getEventBus().fireEventFromSource(event, this);
  }

  @Override
  public final EventBus getEventBus() {
    return eventBus;
  }

  @Override
  public String getNameToken() {
    return place.getNameToken();
  }

  // /////////////////////
  // Inherited from Place

  @Override
  public void getPresenter(NotifyingAsyncCallback<P> callback) {
    proxy.getPresenter(callback);
  }

    @Override
    public Class<P> getPresenterClass() {
        return proxy.getPresenterClass();
    }

    @Override
  public final int hashCode() {
    return place.hashCode();
  }

  @Override
  public boolean matchesRequest(ViewChangeListener.ViewChangeEvent request) {
    return place.matchesRequest(request);
  }

  // /////////////////////
  // Protected methods that can be overridden

  @Override
  public final String toString() {
    return place.toString();
  }

  // /////////////////////
  // Private methods

  /**
   * Obtains the title for this place and invoke the passed handler when the
   * title is available. By default, places don't have a title and will invoke
   * the handler with {@code null}, but override this method to provide your own
   * title.
   *
   * @param event The {@link org.vaadin.mvp.core.events.GetPlaceTitleEvent} to invoke once the title is
   *          available.
   */
  protected void getPlaceTitle(GetPlaceTitleEvent event) {
    event.getHandler().onSetPlaceTitle(null);
  }

  /**
   * Prepares the presenter with the information contained in the current
   * request, then reveals it. Will refuse to reveal the display and do nothing
   * if {@link #canReveal()} returns <code>false</code>.
   *
   * @param request The request to handle. Can pass <code>null</code> if no
   *          request is used, in which case the presenter will be directly
   *          revealed.
   */
  private void handleRequest(final ViewChangeListener.ViewChangeEvent request) {
    proxy.getPresenter(new NotifyingAsyncCallback<P>(eventBus) {

        @Override
        public void success(final P presenter) {
            presenter.onPlaceRequest(request);
            NavigationEvent.fire(proxy, request);
            if (!presenter.useManualReveal()) {
                // Automatic reveal
                manualReveal(presenter);
            }
        }
    });
  }

  @Override
  public void manualReveal(Presenter<?> presenter) {
    // Reveal only if there are no pending navigation requests
      if (!presenter.isVisible()) {
        // This will trigger a reset in due time
        presenter.forceReveal();
      } else {
        // We have to do the reset ourselves
        ResetPresentersEvent.fire(this);
      }
  }

  @Override
  public void manualRevealFailed() {
  }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        if (canReveal()) {
            handleRequest(viewChangeEvent);
        }
    }
}
