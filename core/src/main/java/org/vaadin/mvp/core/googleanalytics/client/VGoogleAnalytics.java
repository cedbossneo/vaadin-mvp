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

package org.vaadin.mvp.core.googleanalytics.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import org.vaadin.mvp.core.googleanalytics.GoogleAnalyticsTracker;

@Connect(value = GoogleAnalyticsTracker.class)
public class VGoogleAnalytics extends AbstractComponentConnector {

    @Override
    public GoogleAnalyticsState getState() {
        return (GoogleAnalyticsState) super.getState();
    }

    @Override
    public SimplePanel getWidget() {
        return (SimplePanel) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();
        RootPanel.get().add(getWidget());
        if (BrowserInfo.get().isIE() && BrowserInfo.get().getIEVersion() == 6) {
            getWidget().getElement().getStyle().setProperty("overflow", "hidden");
            getWidget().getElement().getStyle().setProperty("height", "0");
            getWidget().getElement().getStyle().setProperty("width", "0");
        }
        registerRpc(GoogleAnalyticsRPC.class, new GoogleAnalyticsRPC() {
            @Override
            public void trackPageview(String pageId) {
                _trackPageview(getState().getTrackerId(), pageId, getState().getDomainName(), getState().isAllowAnchor());
            }
        });
    }

    /**
     * Native JS call to invoke _trackPageview from ga.js.
     *
     * @param trackerId
     * @param pageId
     * @param domainName
     * @param allowAnchor
     * @return
     */
    private native String _trackPageview(String trackerId, String pageId,
                                         String domainName, boolean allowAnchor)
    /*-{
        if (!$wnd._gat) {
            return "Tracker not found (running offline?)";
        }
        try {
            var pageTracker = $wnd._gat._getTracker(trackerId);
            if (!pageTracker) {
                return "Failed to get tracker for " + trackerId;
            }

            if (domainName) {
                pageTracker._setDomainName(domainName);
            }

            pageTracker._setAllowAnchor(allowAnchor);

            if (pageId) {
                pageTracker._trackPageview(pageId);
            } else {
                pageTracker._trackPageview();
            }
            return null;
        } catch (err) {
            return "" + err;
        }
    }-*/;
}
