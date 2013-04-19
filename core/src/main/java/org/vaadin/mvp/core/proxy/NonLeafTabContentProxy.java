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
import org.vaadin.mvp.core.TabData;

/**
 * The interface for the {@link Proxy} of a {@link Presenter} that does not have a name token and
 * that can be displayed within a
 * {@link org.vaadin.mvp.core.TabContainerPresenter TabContainerPresenter}'s main area.
 * Useful, among other things, for tabs within tabs.
 * If the presenter is associated to a name token use {@link TabContentProxyPlace} instead.
 * Example of use:
 * <pre>
 * {@literal @}ProxyCodeSplit
 * {@literal @}TabInfo(container = MainPagePresenter.class, priority = 0,
 *          label = "Home", nameToken = "homepage")
 * public interface MyProxy extends NonLeafTabContentProxy&lt;ThisPresenter&gt; { }
 * </pre>
 * In this case, the {@code nameToken} parameter indicates the presenter to reveal
 * when this tab is selected.
 *
 * @param <P> The type of the {@link Presenter} associated with this proxy.
 * @author Philippe Beaudoin
 * @see org.vaadin.mvp.core.annotations.TabInfo TabInfo
 */
public interface NonLeafTabContentProxy<P extends Presenter<?, ?>> extends TabContentProxy<P> {

    /**
     * Changes the data and target history token associated with this tab. This will automatically
     * cause the displayed tab to change, provided the
     * {@link org.vaadin.mvp.core.TabContainerPresenter TabContainerPresenter} containing this
     * tab defines a {@link org.vaadin.mvp.core.annotations.ChangeTab ChangeTab} field and
     * passes it to the parent constructor.
     */
    void changeTab(TabData tabData, String targetHistoryToken);
}
