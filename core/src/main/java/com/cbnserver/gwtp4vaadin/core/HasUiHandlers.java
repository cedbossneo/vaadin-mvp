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

package com.cbnserver.gwtp4vaadin.core;

/**
 * Interface meant to be implemented by the {@link View} so that its controller can attach its
 * {@link UiHandlers}. <b>Important</b>, when using this interface, you should always call
 * {@link #setUiHandlers(UiHandlers)} from your presenter constructor.
 *
 * @param <C> Your {@link UiHandlers} interface type.
 * @author Christian Goudreau
 */
public interface HasUiHandlers<C extends UiHandlers> {

    /**
     * Sets the {@link UiHandlers} subclass associated with this object.
     *
     * @param uiHandlers The {@link UiHandlers} subclass (of type {@code C}) to associate with this
     *                   object.
     */
    void setUiHandlers(C uiHandlers);

}
