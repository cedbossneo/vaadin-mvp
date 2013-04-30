package com.cbnserver.gwtp4vaadin.example;

import com.cbnserver.gwtp4vaadin.core.MVPEventBus;
import com.cbnserver.gwtp4vaadin.core.Presenter;
import com.cbnserver.gwtp4vaadin.core.proxy.RevealContentEvent;
import com.vaadin.cdi.UIScoped;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 30/04/13
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
@UIScoped
public class HelloPresenter extends Presenter<HelloView, HelloProxy> implements HelloUiHandlers {

    @Inject
    public HelloPresenter(MVPEventBus eventBus, HelloView view, HelloProxy proxy) {
        super(eventBus, view, proxy);
        getView().setUiHandlers(this);
    }


    @Override
    public void onGo() {
        getView().helloTo(getView().getName());
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPresenter.CONTENT, this);
    }
}
