/*
 * Copyright 2013 Cedric Hauber.
 *
 * Some methods, files, concepts came from ArcBees Inc.
 * http://code.google.com/p/gwt-platform/
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.mvp.core;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import org.vaadin.mvp.core.proxy.NavigationEvent;
import org.vaadin.mvp.core.proxy.NavigationHandler;

/**
 * A simple implementation of {@link PopupView} that can be used when the widget
 * returned by {@link ViewImpl#asComponent()} inherits from {@link PopupPanel}.
 * <p/>
 * Also, this implementation simply disregards every call to
 * {@link View#setInSlot(Object, PresenterWidget}, {@link View#addToSlot(Object, com.vaadin.ui.Component)}, and
 * {@link View#removeFromSlot(Object, PresenterWidget}.
 */
public abstract class PopupViewImpl extends ViewImpl implements PopupView {

    private HandlerRegistration autoHideHandler;

    private Window.CloseListener closeHandlerRegistration;
    private final MVPEventBus eventBus;

    /**
     * The {@link PopupViewImpl} class uses the {@link MVPEventBus} to listen to
     * {@link NavigationEvent} in order to automatically close when this event is
     * fired, if desired. See
     * {@link #setAutoHideOnNavigationEventEnabled(boolean)} for details.
     *
     * @param eventBus The {@link MVPEventBus}.
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
        asPopupPanel().close();
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
        if (closeHandlerRegistration != null) {
            asPopupPanel().removeCloseListener(closeHandlerRegistration);
        }
        if (popupViewCloseHandler == null) {
            closeHandlerRegistration = null;
        } else {
            closeHandlerRegistration = new Window.CloseListener() {
                @Override
                public void windowClose(Window.CloseEvent e) {
                    popupViewCloseHandler.onClose();
                }
            };
            asPopupPanel().addCloseListener(closeHandlerRegistration);
        }
    }

    @Override
    public void setPosition(int left, int top) {
        asPopupPanel().setPositionX(left);
        asPopupPanel().setPositionY(top);
    }

    @Override
    public void show() {
        UI.getCurrent().addWindow(asPopupPanel());
    }

    /**
     * Retrieves this view as a {@link PopupPanel}. See {@link ViewImpl#asComponent()}.
     *
     * @return This view as a {@link PopupPanel} object.
     */
    protected Window asPopupPanel() {
        return (Window) asComponent();
    }

    /**
     * This method centers the popup panel, temporarily making it visible if
     * needed.
     */
    private void doCenter() {
        // We can't use Element.center() method as it will show the popup
        // by default and not only centering it. This is resulting in onAttach()
        // being called twice when using setInSlot() or addToPopupSlot() in PresenterWidget

        // If left/top are set from a previous doCenter() call, and our content
        // has changed, we may get a bogus getOffsetWidth because our new content
        // is wrapping (giving a lower offset width) then it would without the
        // previous left. Clearing left/top to avoids this.
        Window popup = asPopupPanel();
        popup.center();
    }
}
