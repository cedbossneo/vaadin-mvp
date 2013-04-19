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

import com.vaadin.cdi.UIScoped;
import org.vaadin.mvp.core.HasUiHandlers;
import org.vaadin.mvp.core.MVPEventBus;
import org.vaadin.mvp.core.Presenter;
import org.vaadin.mvp.core.View;
import org.vaadin.mvp.core.annotations.GatekeeperParams;
import org.vaadin.mvp.core.annotations.NameToken;
import org.vaadin.mvp.core.annotations.UseGatekeeper;
import org.vaadin.mvp.core.proxy.Proxy;
import org.vaadin.mvp.core.proxy.RevealRootContentEvent;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 15/04/13
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
@UIScoped
@UseGatekeeper(GateKeeperWithParamsTest.class)
@GatekeeperParams(value = {"efe"})
public class MainPresenter extends Presenter<MainPresenter.MyView, MainPresenter.MyProxy> implements MainUiHandlers {

    @Inject
    public MainPresenter(MyProxy proxy, MVPEventBus eventBus, MyView view) {
        super(eventBus, view, proxy);
        getView().setUiHandlers(this);
    }

    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);
    }

    @Override
    public void onGo() {
        getView().helloTo(getView().getName());
    }

    public interface MyView extends View, HasUiHandlers<MainUiHandlers> {
        String getName();

        void helloTo(String name);
    }

    @NameToken("")
    public interface MyProxy extends Proxy<MainPresenter> {
    }
}
