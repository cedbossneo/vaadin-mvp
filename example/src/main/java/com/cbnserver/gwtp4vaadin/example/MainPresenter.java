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

package com.cbnserver.gwtp4vaadin.example;

import com.cbnserver.gwtp4vaadin.core.MVPEventBus;
import com.cbnserver.gwtp4vaadin.core.Presenter;
import com.cbnserver.gwtp4vaadin.core.View;
import com.cbnserver.gwtp4vaadin.core.annotations.ContentSlot;
import com.cbnserver.gwtp4vaadin.core.annotations.ProxyStandard;
import com.cbnserver.gwtp4vaadin.core.proxy.Proxy;
import com.cbnserver.gwtp4vaadin.core.proxy.RevealContentHandler;
import com.cbnserver.gwtp4vaadin.core.proxy.RevealRootContentEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.cdi.UIScoped;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 15/04/13
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
@UIScoped
public class MainPresenter extends Presenter<MainPresenter.MyView, MainPresenter.MyProxy> {

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> CONTENT = new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public MainPresenter(MyProxy proxy, MVPEventBus eventBus, MyView view) {
        super(eventBus, view, proxy);
    }

    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);
    }

    public interface MyView extends View{

    }

    @ProxyStandard
    public interface MyProxy extends Proxy<MainPresenter> {
    }
}
