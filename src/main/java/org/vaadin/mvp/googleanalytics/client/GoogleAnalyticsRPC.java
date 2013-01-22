package org.vaadin.mvp.googleanalytics.client;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 05/01/13
 * Time: 02:15
 */
public interface GoogleAnalyticsRPC extends ClientRpc{
    public void trackPageview(String pageId);
    }
