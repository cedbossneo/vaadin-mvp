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

package org.vaadin.mvp.core;

import com.vaadin.cdi.UIScoped;
import com.vaadin.server.VaadinSession;
import org.vaadin.mvp.core.annotations.DefaultPlace;
import org.vaadin.mvp.core.annotations.ErrorPlace;
import org.vaadin.mvp.core.annotations.UnauthorizedPlace;
import org.vaadin.mvp.core.proxy.ProxyPlace;

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
    RootPresenter rootPresenter;

    public void init() {
        VaadinSession.getCurrent().setAttribute("mvp", this);
        rootPresenter = rootPresenterProvider.get();
        Set<Bean<?>> proxies = beanManager.getBeans(ProxyPlace.class);
        for (Bean<?> proxy : proxies) {
            beanManager.getReference(proxy, proxy.getBeanClass(), beanManager.createCreationalContext(proxy));
        }
    }

    @Produces
    @DefaultPlace
    String defaultPlaceNameToken = "";

    @Produces
    @ErrorPlace
    String errorPlaceNameToken = "error";

    @Produces
    @UnauthorizedPlace
    String unauthorizedPlaceNameToken = "unauthorized";
}
