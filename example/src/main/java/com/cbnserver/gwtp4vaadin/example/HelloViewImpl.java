package com.cbnserver.gwtp4vaadin.example;

import com.cbnserver.gwtp4vaadin.core.ViewWithUiHandlers;
import com.vaadin.ui.*;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 30/04/13
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class HelloViewImpl extends ViewWithUiHandlers<HelloUiHandlers> implements HelloView {
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
