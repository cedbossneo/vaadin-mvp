package org.jboss.errai.mvp.googleanalytics.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import org.jboss.errai.mvp.googleanalytics.GoogleAnalyticsTracker;

@Connect(value = GoogleAnalyticsTracker.class)
public class VGoogleAnalytics extends AbstractComponentConnector{

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

    /** Native JS call to invoke _trackPageview from ga.js.
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
                return "Failed to get tracker for "+trackerId;
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
        } catch(err) {
            return ""+err;
        }
    }-*/;
}
