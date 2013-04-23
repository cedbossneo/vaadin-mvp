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

import com.vaadin.ui.Component;

/**
 * A simple implementation of {@link View} that simply disregard every call to
 * {@link View#setInSlot(Object, PresenterWidget}, {@link View#addToSlot(Object, com.vaadin.ui.Component)}, and
 * {@link View#removeFromSlot(Object, PresenterWidget}.
 * <p/>
 * Feel free not to inherit from this if you need another base class (such as
 * {@link com.google.gwt.user.client.ui.Composite}), but you will have to define
 * the above methods.
 * <p/>
 * * <b>Important</b> call {@link #initWidget(com.vaadin.ui.Component)} in your {@link com.cbnserver.gwtp4vaadin.core.View}'s
 * constructor.
 */
public abstract class ViewImpl implements View {
    private Component component;

    @Override
    public void addToSlot(Object slot, Component content) {
    }

    @Override
    public void removeFromSlot(Object slot, Component content) {
    }

    @Override
    public void setInSlot(Object slot, Component content) {
    }

    @Override
    public Component asComponent() {
        return component;
    }

    protected void initWidget(Component component) {
        this.component = component;
    }
}
