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

import com.google.web.bindery.event.shared.EventBus;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import org.vaadin.mvp.core.events.NotifyingAsyncCallback;
import org.vaadin.mvp.core.presenters.Presenter;
import org.vaadin.mvp.core.presenters.RootPresenter;
import org.vaadin.mvp.core.proxy.ProxyPlace;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;

public class MVP implements Serializable{
    @Inject
    BeanManager beanManager;

    @Inject
    CDIViewProvider cdiViewProvider;

    @Inject
    Provider<RootPresenter> rootPresenterProvider;
    RootPresenter rootPresenter;

   public void init(){
       VaadinSession.getCurrent().setAttribute("mvp", this);
       rootPresenter = rootPresenterProvider.get();
       Set<Bean<?>> proxies = beanManager.getBeans(ProxyPlace.class);
       for (Bean<?> proxy : proxies) {
           beanManager.getReference(proxy, proxy.getBeanClass(), beanManager.createCreationalContext(proxy));
       }
    }

    public void initGoogleAnalytics(String gaAccount){
        getNavigator().addViewChangeListener(new MVPViewChangeListener(gaAccount));
   }

    @org.vaadin.mvp.core.annotations.qualifiers.MVP
    @Produces
    public EventBus getEventBus(){
        UI currentUI = UI.getCurrent();
        if (currentUI == null)
            return null;
        EventBus eventBus = (EventBus) currentUI.getSession().getAttribute("eventBus");
        if (eventBus == null){
            eventBus = new MVPEventBus();
            currentUI.getSession().setAttribute("eventBus", eventBus);
        }
        return eventBus;
    }

    @org.vaadin.mvp.core.annotations.qualifiers.MVP
    @Produces
    public MVP getMVP(){
        UI currentUI = UI.getCurrent();
        if (currentUI == null)
            return null;
        return  (MVP) currentUI.getSession().getAttribute("mvp");
    }

    @org.vaadin.mvp.core.annotations.qualifiers.MVP
    @Produces
    public Navigator getNavigator(){
        UI currentUI = UI.getCurrent();
        if (currentUI == null)
            return null;
        Navigator navigator = currentUI.getNavigator();
        if (navigator == null){
            navigator = new Navigator(currentUI, new ViewDisplay() {
                @Override
                public void showView(com.vaadin.navigator.View view) {

                }
            });
            currentUI.setNavigator(navigator);
//            navigator.addProvider(cdiViewProvider);
        }
        return navigator;
    }

    public <P extends Presenter<?>> void getPresenter(Class<P> presenterClass, NotifyingAsyncCallback<P> callback) {
        callback.prepare();
        callback.checkLoading();
        Bean<P> bean = (Bean<P>) beanManager.getBeans(presenterClass).iterator().next();
        P obj = (P) beanManager.getReference(bean, presenterClass,
                beanManager.createCreationalContext(bean));
        if (bean == null)
            callback.onFailure(new Throwable("Error while getting bean"));
        else{
            Logger.getLogger(getClass().getName()).fine("New presenter created: " + presenterClass.getName());
            callback.onSuccess(obj);
        }
        callback.checkLoading();
    }

}
