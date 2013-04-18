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

package org.vaadin.mvp.core.views;

import org.vaadin.mvp.core.MVPEventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import org.vaadin.mvp.core.events.NavigationEvent;
import org.vaadin.mvp.core.events.NavigationHandler;

/**
 * A simple implementation of {@link PopupView} that can be used when the widget
 * returned by {@link #asComponent()} ()} inherits from {@link com.google.gwt.user.client.ui.PopupPanel}.
 *
 * Also, this implementation simply disregards every call to
 * {@link #setInSlot(Object, com.google.gwt.user.client.ui.Widget)}, {@link #addToSlot(Object, com.google.gwt.user.client.ui.Widget)}, and
 * {@link #removeFromSlot(Object, com.google.gwt.user.client.ui.Widget)}.
 *
 * @author Philippe Beaudoin
 */
public abstract class PopupViewImpl extends ViewImpl implements PopupView {

  private HandlerRegistration autoHideHandler;

  private Window.CloseListener closeListener;
  private final MVPEventBus eventBus;

  /**
   * The {@link PopupViewImpl} class uses the {@link org.vaadin.mvp.core.MVPEventBus} to listen to
   * {@link NavigationEvent} in order to automatically close when this event is
   * fired, if desired. See
   * {@link #setAutoHideOnNavigationEventEnabled(boolean)} for details.
   *
   * @param eventBus The {@link org.vaadin.mvp.core.MVPEventBus}.
   */
  protected PopupViewImpl(MVPEventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void center() {
    doCenter();
  }

  @Override
  public void hide() {
    asWindow().close();
  }

  @Override
  public void setAutoHideOnNavigationEventEnabled(boolean autoHide) {
    if (autoHide) {
      if (autoHideHandler != null) {
        return;
      }
      autoHideHandler = eventBus.addHandler(NavigationEvent.getType(),
          new NavigationHandler() {
            @Override
            public void onNavigation(NavigationEvent navigationEvent) {
              hide();
            }
          });
    } else {
      if (autoHideHandler != null) {
        autoHideHandler.removeHandler();
      }
    }
  }

  @Override
  public void setCloseHandler(final PopupViewCloseHandler popupViewCloseHandler) {
    if (closeListener != null) {
      asWindow().removeCloseListener(closeListener);
    }
    if (popupViewCloseHandler == null) {
      closeListener = null;
    } else {
        closeListener = new Window.CloseListener() {
            @Override
            public void windowClose(Window.CloseEvent e) {
                popupViewCloseHandler.onClose();
            }
        };
        asWindow().addCloseListener(closeListener);
    }
  }

  @Override
  public void setPosition(int left, int top) {
    asWindow().setPositionX(left);
    asWindow().setPositionY(top);
  }

  @Override
  public void show() {
    UI.getCurrent().addWindow(asWindow());
  }

  /**
   * Retrieves this view as a {@link com.google.gwt.user.client.ui.PopupPanel}. See {@link #asWidget()}.
   *
   * @return This view as a {@link com.google.gwt.user.client.ui.PopupPanel} object.
   */
  protected Window asWindow() {
    return (Window) asComponent();
  }

  /**
   * This method centers the popup panel, temporarily making it visible if
   * needed.
   */
  private void doCenter() {
    boolean wasVisible = asWindow().isVisible();
    asWindow().center();
    if (!wasVisible) {
      asWindow().close();
    }
  }
}
