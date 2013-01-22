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

package org.vaadin.mvp.proxy;

import com.vaadin.navigator.Navigator;
import org.vaadin.mvp.places.Place;
import org.vaadin.mvp.presenters.Presenter;

/**
 * A useful mixing class to define a {@link Proxy} that is also a {@link org.vaadin.mvp.places.Place}.
 * See {@link ProxyPlaceAbstract} for more details.
 *
 * @param <P> Type of the associated {@link org.vaadin.mvp.presenters.Presenter}.
 *
 * @author David Peterson
 * @author Philippe Beaudoin
 */
public class ProxyPlaceImpl<P extends Presenter<?>> extends
    ProxyPlaceAbstract<P, Proxy<P>> {
    /**
     * Creates a {@link org.vaadin.mvp.proxy.ProxyPlaceAbstract}. That is, the {@link org.vaadin.mvp.proxy.Proxy} of a
     * {@link org.vaadin.mvp.presenters.Presenter} attached to a {@link org.vaadin.mvp.places.Place}. This presenter can be
     * invoked by setting a history token that matches its name token in the URL
     * bar.
     */
    public ProxyPlaceImpl(Proxy<P> proxy, Place place, Navigator navigator) {
        super(proxy, place, navigator);
    }
}
