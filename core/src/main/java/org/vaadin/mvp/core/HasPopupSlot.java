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

/**
 * @author Christian Goudreau
 */
public interface HasPopupSlot {

    /**
     * This method sets some popup content within the {@link Presenter} and
     * centers it. The view associated with the {@code content}'s presenter must
     * inherit from {@link PopupView}. The popup will be visible and the
     * corresponding presenter will receive the lifecycle events as needed.
     * <p/>
     * Contrary to the {@link View#setInSlot(Object, com.vaadin.ui.Component)}
     * method, no {@link org.vaadin.mvp.core.proxy.ResetPresentersEvent}
     * is fired, so {@link PresenterWidget#onReset()} is not invoked.
     *
     * @param child The popup child, a {@link PresenterWidget}.
     * @see #addToPopupSlot(PresenterWidget)
     */
    void addToPopupSlot(final PresenterWidget<? extends PopupView> child);

    /**
     * This method sets some popup content within the {@link Presenter}. The view
     * associated with the {@code content}'s presenter must inherit from
     * {@link PopupView}. The popup will be visible and the corresponding
     * presenter will receive the lifecycle events as needed.
     * <p/>
     * Contrary to the {@link View#setInSlot(Object, com.vaadin.ui.Component)}
     * method, no {@link org.vaadin.mvp.core.proxy.ResetPresentersEvent}
     * is fired, so {@link PresenterWidget#onReset()} is not invoked.
     *
     * @param child  The popup child, a {@link PresenterWidget}.
     * @param center Pass {@code true} to center the popup, otherwise its position
     *               will not be adjusted.
     * @see #addToPopupSlot(PresenterWidget)
     */
    void addToPopupSlot(final PresenterWidget<? extends PopupView> child, boolean center);

    /**
     * This method removes popup content within the {@link Presenter}. The view
     * associated with the {@code content}'s presenter must inherit from {@link PopupView}.
     *
     * @param child The popup child, a {@link PresenterWidget}, which has
     *              previously been added using {@link #addToPopupSlot(PresenterWidget)}
     *              or {@link #addToPopupSlot(PresenterWidget, boolean)}
     */
    void removeFromPopupSlot(PresenterWidget<? extends PopupView> child);
}
