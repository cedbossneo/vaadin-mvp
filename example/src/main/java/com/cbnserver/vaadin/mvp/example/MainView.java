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
