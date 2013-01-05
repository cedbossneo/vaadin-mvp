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

package org.jboss.errai.mvp.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.vaadin.navigator.ViewChangeListener;

/**
 *
 * This event is fired by the {@link PlaceManager} whenever a new place is
 * requested, either by history navigation or directly.
 * <p />
 * <b>Important!</b> You should never fire that event directly. Instead, build a
 * {@link org.jboss.errai.mvp.places.PlaceRequest} and pass it to one of the following methods:
 * <ul>
 * <li>{@link PlaceManager#revealPlace(org.jboss.errai.mvp.places.PlaceRequest)}</li>
 * <li>{@link PlaceManager#revealRelativePlace(org.jboss.errai.mvp.places.PlaceRequest)}</li>
 * <li>{@link PlaceManager#revealRelativePlace(org.jboss.errai.mvp.places.PlaceRequest, int)}</li>
 * </ul>
 *
 * @author David Peterson
 * @author Philippe Beaudoin
 *
 */
public class PlaceRequestInternalEvent extends GwtEvent<PlaceRequestInternalHandler> {

  private static Type<PlaceRequestInternalHandler> TYPE;

  /**
   * Fires a {@link PlaceRequestInternalEvent}
   * into a source that has access to an {@com.google.web.bindery.event.shared.EventBus}.
   * <p />
   * <b>Important!</b> You should not fire that event directly, see
   * {@link PlaceRequestInternalEvent} for more details.
   *
   * @param source The source that fires this event ({@link com.google.gwt.event.shared.HasHandlers}).
   * @param request The request.
   * @param updateBrowserHistory {@code true} If the browser URL should be updated, {@code false}
   *          otherwise.
   */
  public static void fire(HasHandlers source, ViewChangeListener.ViewChangeEvent request) {
    source.fireEvent(new PlaceRequestInternalEvent(request));
  }

  public static Type<PlaceRequestInternalHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<PlaceRequestInternalHandler>();
    }
    return TYPE;
  }

  private boolean authorized = true;

  /**
   * The handled flag can let others know when the event has been handled.
   * Handlers should call {@link setHandled()} as soon as they figure they are
   * be responsible for this event. Handlers should not process this event if
   * {@link isHandled()} return {@code true}.
   */
  private boolean handled;

  private final ViewChangeListener.ViewChangeEvent request;

  public PlaceRequestInternalEvent(ViewChangeListener.ViewChangeEvent request) {
    this.request = request;
  }

  @Override
  public Type<PlaceRequestInternalHandler> getAssociatedType() {
    return getType();
  }

  public ViewChangeListener.ViewChangeEvent getRequest() {
    return request;
  }

  /**
   * Checks if the user was authorized to see the page.
   *
   * @return {@code true} if the user was authorized. {@code false} otherwise.
   */
  public boolean isAuthorized() {
    return authorized;
  }

  /**
   * Checks if the event was handled. If it was, then it should not be processed
   * further.
   *
   * @return {@code true} if the event was handled. {@code false} otherwise.
   */
  public boolean isHandled() {
    return handled;
  }

  /**
   * Indicates that the event was handled and that other contentHandlers should not
   * process it.
   */
  public void setHandled() {
    handled = true;
  }

  /**
   * Indicates that the event was handled but that the user was not authorized
   * to view the request page.
   */
  public void setUnauthorized() {
    authorized = false;
  }

  @Override
  protected void dispatch(PlaceRequestInternalHandler handler) {
    handler.onPlaceRequest(this);
  }
}