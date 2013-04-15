package org.vaadin.mvp.core;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.UI;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 15/04/13
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
public class MVPNavigator extends Navigator {
    public MVPNavigator(UI currentUI) {
        super(currentUI, new ViewDisplay() {
            @Override
            public void showView(View view) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }
}
