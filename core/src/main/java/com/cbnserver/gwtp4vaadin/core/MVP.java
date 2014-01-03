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
import com.cbnserver.gwtp4vaadin.core.proxy.*;
import com.vaadin.cdi.UIScoped;
import com.vaadin.server.VaadinSession;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

@UIScoped
public class MVP implements Serializable {
    private static Reflections REFLECTIONS;

    @Inject
    BeanManager beanManager;

    @Inject
    Provider<RootPresenter> rootPresenterProvider;
    @Inject
    Provider<PlaceManager> placeManagerProvider;
    RootPresenter rootPresenter;
    private String defaultPlace;
    private String errorPlace;
    private String unauthorizedPlace;

    public MVP() {
        defaultPlace = "";
        errorPlace = "error";
        unauthorizedPlace = "unauthorized";
    }

    public void init() {
        VaadinSession.getCurrent().setAttribute("mvp", this);
        rootPresenter = rootPresenterProvider.get();
        Set<Class<? extends Proxy>> proxies = getSubTypesOf(Proxy.class);
        proxies.addAll(getSubTypesOf(ProxyImpl.class));
        proxies.addAll(getSubTypesOf(ProxyPlaceImpl.class));
        proxies.addAll(getSubTypesOf(TabContentProxyPlaceImpl.class));
        for (Class<? extends Proxy> proxy : proxies) {
            Set<Bean<?>> beans = beanManager.getBeans(proxy);
            if (beans.size() == 1) {
                Bean<?> bean = beans.iterator().next();
                beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
            }
        }
        placeManagerProvider.get().revealCurrentPlace();
    }

    @Produces
    @DefaultPlace
    String getDefaultPlaceNameToken() {
        return defaultPlace;
    }

    @Produces
    @ErrorPlace
    String getErrorPlaceNameToken() {
       return errorPlace;
    }

    @Produces
    @UnauthorizedPlace
    String getUnauthorizedPlaceNameToken() {
        return unauthorizedPlace;
    }

    public static <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type){
        if (REFLECTIONS == null)
            REFLECTIONS = new Reflections("", new SubTypesScanner());
        return REFLECTIONS.getSubTypesOf(type);
    }

    public void setDefaultPlace(String defaultPlace) {
        this.defaultPlace = defaultPlace;
    }

    public void setErrorPlace(String errorPlace) {
        this.errorPlace = errorPlace;
    }

    public void setUnauthorizedPlace(String unauthorizedPlace) {
        this.unauthorizedPlace = unauthorizedPlace;
    }
}
