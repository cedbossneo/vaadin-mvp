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

import com.cbnserver.gwtp4vaadin.core.MVPEventBus;
import com.cbnserver.gwtp4vaadin.core.Presenter;

/**
 * The interface for light-weight singleton classes that listens for events
 * before the full {@link Presenter} is instantiated. This include, among
 * others, the presenter's specific {@link RevealContentEvent} that needs the
 * presenter to reveal itself.
 * <p/>
 * The relationship between a presenter and its proxy is two-way.
 * <p/>
 * {@link Presenter} subclasses will usually define their own interface called
 * MyProxy and be derived from this one.
 *
 * @param <P> The type of the {@link Presenter} associated with this proxy.
 * @author Philippe Beaudoin
 */
public interface Proxy<P extends Presenter<?, ?>> extends ProxyRaw {

    /**
     * Makes it possible to access the {@link com.cbnserver.gwtp4vaadin.core.MVPEventBus} object associated with
     * that proxy.
     *
     * @return The {@link com.cbnserver.gwtp4vaadin.core.MVPEventBus} associated with that proxy.
     */
    MVPEventBus getEventBus();

    /**
     * Get the associated {@link Presenter}. The presenter can only be obtained in
     * an asynchronous manner to support code splitting when needed. To access the
     * presenter, pass a callback.
     *
     * @param callback The callback in which the {@link Presenter} will be passed
     *                 as a parameter.
     */
    void getPresenter(NotifyingAsyncCallback<P> callback);
}
