package com.cbnserver.vaadin.mvp.example;

import com.google.web.bindery.event.shared.EventBus;
import org.vaadin.mvp.core.annotations.PlaceToken;
import org.vaadin.mvp.core.annotations.qualifiers.MVP;
import org.vaadin.mvp.core.events.RevealRootContentEvent;
import org.vaadin.mvp.core.presenters.Presenter;
import org.vaadin.mvp.core.views.HasUiHandlers;
import org.vaadin.mvp.core.views.View;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 15/04/13
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
@PlaceToken
public class MainPresenter extends Presenter<MainPresenter.MyView> implements MainUiHandlers {

    @Inject
    public MainPresenter(@MVP EventBus eventBus, MyView view) {
        super(eventBus, view);
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

    public interface MyView extends View, HasUiHandlers<MainUiHandlers>{
        String getName();

        void helloTo(String name);
    }
}
