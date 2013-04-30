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

import com.cbnserver.gwtp4vaadin.core.annotations.DefaultPlace;
import com.cbnserver.gwtp4vaadin.core.annotations.ErrorPlace;
import com.cbnserver.gwtp4vaadin.core.annotations.UnauthorizedPlace;
import com.cbnserver.gwtp4vaadin.core.proxy.PlaceManager;
import com.cbnserver.gwtp4vaadin.core.proxy.Proxy;
import com.cbnserver.gwtp4vaadin.core.proxy.ProxyPlace;
import com.vaadin.cdi.UIScoped;
import com.vaadin.server.VaadinSession;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.Serializable;
import java.util.Set;

@UIScoped
public class MVP implements Serializable {
    @Inject
    BeanManager beanManager;

    @Inject
    Provider<RootPresenter> rootPresenterProvider;
    @Inject
    Provider<PlaceManager> placeManagerProvider;
    RootPresenter rootPresenter;

    public void init() {
        VaadinSession.getCurrent().setAttribute("mvp", this);
        rootPresenter = rootPresenterProvider.get();
        Set<Bean<?>> proxies = beanManager.getBeans(Proxy.class);
        for (Bean<?> proxy : proxies) {
            beanManager.getReference(proxy, proxy.getBeanClass(), beanManager.createCreationalContext(proxy));
        }
        placeManagerProvider.get().revealCurrentPlace();
    }

    @Produces
    @DefaultPlace
    String getDefaultPlaceNameToken() {
        return "";
    }

    @Produces
    @ErrorPlace
    String getErrorPlaceNameToken() {
        return "error";
    }

    @Produces
    @UnauthorizedPlace
    String getUnauthorizedPlaceNameToken() {
        return "unauthorized";
    }
}
