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

import com.vaadin.ui.*;
import org.vaadin.mvp.core.ViewWithUiHandlers;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 15/04/13
 * Time: 10:20
 * To change this template use File | Settings | File Templates.
 */
public class MainView extends ViewWithUiHandlers<MainUiHandlers> implements MainPresenter.MyView {
    private VerticalLayout panel;
    private TextField name;
    private Label label;

    @Override
    public Component asComponent() {
        if (panel == null) {
            panel = new VerticalLayout();
            label = new Label("Hello world");
            panel.addComponent(label);
            name = new TextField("Your name:");
            Button button = new Button("Go");
            panel.addComponent(name);
            button.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    getUiHandlers().onGo();
                }
            });
            panel.addComponent(button);
        }
        return panel;
    }

    @Override
    public String getName() {
        return name.getValue();
    }

    @Override
    public void helloTo(String name) {
        label.setValue(String.format("Hello world %s", name));
    }
}
