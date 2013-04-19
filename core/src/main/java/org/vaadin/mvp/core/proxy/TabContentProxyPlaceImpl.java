/**
 * Copyright 2011 ArcBees Inc.
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

import org.vaadin.mvp.core.MVPEventBus;
import org.vaadin.mvp.core.Presenter;
import org.vaadin.mvp.core.Tab;
import org.vaadin.mvp.core.TabData;

/**
 * A useful mixing class to define a {@link TabContentProxy} that is also a
 * {@link Place}.
 *
 * @param <T> The Presenter's type.
 * @author Philippe Beaudoin
 */
public class TabContentProxyPlaceImpl<T extends Presenter<?, ?>> extends
        ProxyPlaceAbstract<T, TabContentProxy<T>> implements TabContentProxyPlace<T> {


    /**
     * Creates a {@link org.vaadin.mvp.core.proxy.ProxyPlaceAbstract}. That is, the {@link org.vaadin.mvp.core.proxy.Proxy} of a
     * {@link org.vaadin.mvp.core.Presenter} attached to a {@link org.vaadin.mvp.core.proxy.Place}. This presenter can be
     * invoked by setting a history token that matches its name token in the URL
     * bar.
     */
    public TabContentProxyPlaceImpl(MVPEventBus eventBus) {
        super(eventBus);
    }

    @Override
    public String getTargetHistoryToken() {
        return getNameToken();
    }

    @Override
    public TabData getTabData() {
        return getProxy().getTabData();
    }

    @Override
    public Tab getTab() {
        return getProxy().getTab();
    }

    @Override
    public void changeTab(TabData tabData) {
        super.getProxy().changeTab(tabData);
    }
}
