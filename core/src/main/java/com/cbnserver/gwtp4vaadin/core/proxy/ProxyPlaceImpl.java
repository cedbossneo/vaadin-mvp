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

package com.cbnserver.gwtp4vaadin.core.proxy;

import com.cbnserver.gwtp4vaadin.core.Presenter;

/**
 * A useful mixing class to define a {@link Proxy} that is also a {@link Place}.
 * See {@link ProxyPlaceAbstract} for more details.
 *
 * @param <P> Type of the associated {@link Presenter}.
 * @author David Peterson
 * @author Philippe Beaudoin
 */
public class ProxyPlaceImpl<P extends Presenter<?, ?>> extends
        ProxyPlaceAbstract<P, Proxy<P>> {


    /**
     * Creates a {@link com.cbnserver.gwtp4vaadin.core.proxy.ProxyPlaceAbstract}. That is, the {@link com.cbnserver.gwtp4vaadin.core.proxy.Proxy} of a
     * {@link com.cbnserver.gwtp4vaadin.core.Presenter} attached to a {@link com.cbnserver.gwtp4vaadin.core.proxy.Place}. This presenter can be
     * invoked by setting a history token that matches its name token in the URL
     * bar.
     */
    public ProxyPlaceImpl() {
    }
}
