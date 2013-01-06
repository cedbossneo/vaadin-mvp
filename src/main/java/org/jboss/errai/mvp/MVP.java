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

package org.jboss.errai.mvp;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.VaadinView;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import javassist.*;
import org.jboss.errai.mvp.annotations.*;
import org.jboss.errai.mvp.events.GetPlaceTitleEvent;
import org.jboss.errai.mvp.events.NotifyingAsyncCallback;
import org.jboss.errai.mvp.events.PlaceRequestInternalEvent;
import org.jboss.errai.mvp.events.RevealContentHandler;
import org.jboss.errai.mvp.googleanalytics.GoogleAnalyticsTracker;
import org.jboss.errai.mvp.places.Gatekeeper;
import org.jboss.errai.mvp.places.Place;
import org.jboss.errai.mvp.places.PlaceImpl;
import org.jboss.errai.mvp.places.PlaceWithGatekeeper;
import org.jboss.errai.mvp.presenters.Presenter;
import org.jboss.errai.mvp.presenters.RootPresenter;
import org.jboss.errai.mvp.proxy.ProxyGenerator;
import org.jboss.errai.mvp.proxy.ProxyImpl;
import org.jboss.errai.mvp.proxy.ProxyPlace;
import org.jboss.errai.mvp.proxy.ProxyPlaceImpl;
import org.jboss.errai.mvp.views.View;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SessionScoped
public class MVP implements ViewChangeListener {
    @Inject
    ProxyGenerator proxyGenerator;

    @Inject
    BeanManager beanManager;

    @Inject
    CDIViewProvider cdiViewProvider;

    @Inject
    RootPresenter rootPresenter;

    private GoogleAnalyticsTracker analytics;
    private Gatekeeper defaultGateKeeper;
    private Map<Class<? extends Presenter<? extends View>>, ProxyImpl<? extends Presenter<? extends View>>> presentersProxy;
    private Map<Class<? extends Presenter<? extends View>>, ProxyPlace<? extends Presenter<? extends View>>> presentersPlaces;
    private Map<Class<? extends Gatekeeper>, Gatekeeper> gateKeepers;

    @PostConstruct
    private void init(){
        VaadinSession.getCurrent().setAttribute("mvp", this);
        presentersProxy = new HashMap<Class<? extends Presenter<? extends View>>, ProxyImpl<? extends Presenter<? extends View>>>();
        presentersPlaces = new HashMap<Class<? extends Presenter<? extends View>>, ProxyPlace<? extends Presenter<? extends View>>>();
        gateKeepers = new HashMap<Class<? extends Gatekeeper>, Gatekeeper>();
    }

    private void initPresenters() {
        Set<Bean<?>> presenters = beanManager.getBeans(Presenter.class,
                new AnnotationLiteral<Any>() {
                });
        for (Bean<?> bean : presenters){
            Class<? extends Presenter<? extends View>> presenter = (Class<? extends Presenter<? extends View>>) bean.getBeanClass();
            registerPresenter(presenter);
        }

        Set<Bean<?>> gatekeepers = beanManager.getBeans(Gatekeeper.class,
                new AnnotationLiteral<Any>() {
                });
        for (Bean<?> bean : gatekeepers){
            Class<? extends Gatekeeper> gatekeeper = (Class<? extends Gatekeeper>) bean.getBeanClass();
            registerGateKeeper(gatekeeper);
        }
    }

    public void initGoogleAnalytics(String gaAccount){
        analytics = new GoogleAnalyticsTracker(gaAccount);
    }

    public <P extends Presenter<? extends View>> void registerPresenter(Class<P> presenter) {
        VaadinView place = presenter.getAnnotation(VaadinView.class);
        if (place == null)
            return;
        Class proxy = proxyGenerator.createPresenterProxy(presenter);
        ProxyImpl<P> presenterProxy = null;
        try {
            presenterProxy = (ProxyImpl<P>) proxy.getConstructor(EventBus.class).newInstance(getEventBus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        presentersProxy.put(presenter, presenterProxy);
        if (presenter.isAnnotationPresent(NoGatekeeper.class)){
            produceProxyPlace(presenter, new PlaceImpl(place.value()), presenterProxy);
        }else if (presenter.isAnnotationPresent(UseGatekeeper.class))
            produceProxyPlace(presenter, new PlaceWithGatekeeper(place.value(), gateKeepers.get(presenter.getAnnotation(UseGatekeeper.class).value())), presenterProxy);
        else if (defaultGateKeeper != null)
            produceProxyPlace(presenter, new PlaceWithGatekeeper(place.value(), defaultGateKeeper), presenterProxy);
        else
            produceProxyPlace(presenter, new PlaceImpl(place.value()), presenterProxy);
    }

    private <P extends Presenter<? extends View>> void produceProxyPlace(final Class<P> presenter, Place place, ProxyImpl<P> proxy) {
        Navigator navigator = getNavigator();
        ProxyPlaceImpl<P> proxyPlace;
        if (presenter.isAnnotationPresent(Title.class)){
            proxyPlace = new ProxyPlaceImpl<P>(proxy, place, navigator) {
                @Override
                protected void getPlaceTitle(GetPlaceTitleEvent event) {
                    event.getHandler().onSetPlaceTitle(presenter.getAnnotation(Title.class).value());
                }
            };
            presentersPlaces.put(presenter, proxyPlace);
        }else {
            proxyPlace = new ProxyPlaceImpl<P>(proxy, place, navigator);
            presentersPlaces.put(presenter, proxyPlace);
        }
        registerContentSlots(presenter, proxy);
    }

    private <P extends Presenter<? extends View>> void registerContentSlots(Class<P> presenter, ProxyImpl<P> proxy) {
        Method[] methods = presenter.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(ContentSlot.class))
                try {
                    getEventBus().addHandler((GwtEvent.Type<RevealContentHandler<P>>) method.invoke(null), new RevealContentHandler<P>(getEventBus(), proxy));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
        }
    }

    public void registerGateKeeper(Class<? extends Gatekeeper> gateKeeper){
        Gatekeeper gk = null;
        try {
           gk= gateKeeper.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (gk == null)
            return;
        if (gateKeeper.isAnnotationPresent(DefaultGatekeeper.class))
            defaultGateKeeper = gk;
        gateKeepers.put(gateKeeper, gk);
    }

    @Produces
    public EventBus getEventBus(){
        EventBus eventBus = (EventBus) UI.getCurrent().getSession().getAttribute("eventBus");
        if (eventBus == null){
            eventBus = new SimpleEventBus();
            UI.getCurrent().getSession().setAttribute("eventBus", eventBus);
        }
        return eventBus;
    }

    @Produces
    public Navigator getNavigator(){
        Navigator navigator = UI.getCurrent().getNavigator();
        if (navigator == null){
            navigator = new Navigator(UI.getCurrent(), new ViewDisplay() {
                @Override
                public void showView(com.vaadin.navigator.View view) {

                }
            });
            UI.getCurrent().setNavigator(navigator);
            navigator.addProvider(cdiViewProvider);
            navigator.addViewChangeListener(this);
        }
        return navigator;
    }

    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {
        return true;
    }

    @Override
    public void afterViewChange(ViewChangeEvent event) {
        if (analytics != null)
            analytics.trackPageview(event.getViewName());
        getEventBus().fireEvent(new PlaceRequestInternalEvent(event));
    }

    public <P extends Presenter<?>> void getPresenter(Class<P> presenterClass, NotifyingAsyncCallback<P> callback) {
        callback.prepare();
        callback.checkLoading();
        Bean<P> bean = (Bean<P>) beanManager.getBeans(presenterClass).iterator().next();
        P obj = (P) beanManager.getReference(bean, presenterClass,
                beanManager.createCreationalContext(bean));
        if (bean == null)
            callback.onFailure(new Throwable("Error while getting bean"));
        else
            callback.onSuccess(obj);
        callback.checkLoading();
    }

    public <P extends Presenter<? extends View>>void initNavigator(Class<P> defaultPresenter) {
        initPresenters();
        getPresenter(defaultPresenter, new NotifyingAsyncCallback<P>(getEventBus()) {
            @Override
            protected void success(P result) {
                UI.getCurrent().getNavigator().addView("", result);
            }
        });
    }
}
