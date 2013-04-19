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
package com.cbnserver.vaadin.mvp.example;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.URLMapping;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import org.vaadin.mvp.core.MVP;

import javax.inject.Inject;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@CDIUI
@PreserveOnRefresh
@URLMapping
public class MyVaadinApplication extends UI {
    @Inject
    MVP mvp;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setLocale(vaadinRequest.getLocale());
        mvp.init();
    }
}

