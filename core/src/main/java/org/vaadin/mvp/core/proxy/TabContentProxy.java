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

package org.vaadin.mvp.core.proxy;

import org.vaadin.mvp.core.Presenter;
import org.vaadin.mvp.core.Tab;
import org.vaadin.mvp.core.TabData;

/**
 * The interface for the {@link Proxy} of a {@link Presenter} that can
 * be displayed within a
 * {@link org.vaadin.mvp.core.TabContainerPresenter TabContainerPresenter}'s main area.
 * You should not use this proxy directly. If the presenter is associated to a name token use
 * {@link TabContentProxyPlace} instead. If the presenter is not a leaf, so it is not associated
 * with a name token, use {@link NonLeafTabContentProxy} instead.
 *
 * @param <P> The type of the {@link Presenter} associated with this proxy.
 * @author Philippe Beaudoin
 * @see org.vaadin.mvp.core.annotations.TabInfo TabInfo
 */
public interface TabContentProxy<P extends Presenter<?, ?>> extends Proxy<P> {
    /**
     * Retrieves the {@link TabData} that should be used to create this tab.
     *
     * @return The tab data.
     */
    TabData getTabData();

    /**
     * Gets the history token that should be accessed when the tab is clicked.
     * In the fairly typical scenario where a tab directly contains a place,
     * this should return the name token of that place. In the case of tabs
     * that contain non-leaf presenters (for example, other tabs), this should
     * return the name token of a leaf-level presenter. See {@link NonLeafTabContentProxy}.
     *
     * @return The history token.
     */
    String getTargetHistoryToken();

    /**
     * Retrieves the {@link Tab} object that was created from the
     * {@link TabData} returned by {@link #getTabData()}.
     *
     * @return The tab.
     */
    Tab getTab();

    /**
     * Changes the data associated with this tab. This will automatically cause the displayed tab to
     * change, provided the
     * {@link org.vaadin.mvp.core.TabContainerPresenter TabContainerPresenter} containing this
     * tab defines a {@link org.vaadin.mvp.core.annotations.ChangeTab ChangeTab} field and
     * passes it to the parent constructor.
     */
    void changeTab(TabData tabData);
}
