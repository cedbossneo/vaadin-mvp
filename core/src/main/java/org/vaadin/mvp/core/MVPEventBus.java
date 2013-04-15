package org.vaadin.mvp.core;

import com.google.web.bindery.event.shared.SimpleEventBus;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 11/04/13
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
public class MVPEventBus extends SimpleEventBus implements Serializable {
    public MVPEventBus() {
        Logger.getLogger("MVPEventBus").info("New MVPEventBus");
    }
}
