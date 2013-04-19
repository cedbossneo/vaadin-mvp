package com.cbnserver.vaadin.mvp.example;

import com.vaadin.cdi.UIScoped;
import org.vaadin.mvp.core.HasUiHandlers;
import org.vaadin.mvp.core.MVPEventBus;
import org.vaadin.mvp.core.Presenter;
import org.vaadin.mvp.core.View;
import org.vaadin.mvp.core.annotations.NameToken;
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
