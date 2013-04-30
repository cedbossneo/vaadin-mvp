package com.cbnserver.gwtp4vaadin.example;

import com.cbnserver.gwtp4vaadin.core.HasUiHandlers;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 30/04/13
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
public interface HelloView extends com.cbnserver.gwtp4vaadin.core.View, HasUiHandlers<HelloUiHandlers> {
    String getName();

    void helloTo(String name);
}