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

import org.vaadin.mvp.core.MVPNavigator;
import org.vaadin.mvp.core.places.Place;
import org.vaadin.mvp.core.presenters.Presenter;

/**
 * A useful mixing class to define a {@link Proxy} that is also a {@link org.vaadin.mvp.core.places.Place}.
 * See {@link ProxyPlaceAbstract} for more details.
 *
 * @param <P> Type of the associated {@link org.vaadin.mvp.core.presenters.Presenter}.
 *
 * @author David Peterson
 * @author Philippe Beaudoin
 */
public class ProxyPlaceImpl<P extends Presenter<?>> extends
    ProxyPlaceAbstract<P, Proxy<P>> {
    /**
     * Creates a {@link ProxyPlaceAbstract}. That is, the {@link Proxy} of a
     * {@link org.vaadin.mvp.core.presenters.Presenter} attached to a {@link org.vaadin.mvp.core.places.Place}. This presenter can be
     * invoked by setting a history token that matches its name token in the URL
     * bar.
     */
    public ProxyPlaceImpl(Proxy<P> proxy, Place place, MVPNavigator navigator) {
        super(proxy, place, navigator);
    }
}
