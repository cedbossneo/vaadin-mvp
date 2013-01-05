package org.jboss.errai.mvp.googleanalytics;

import com.vaadin.ui.AbstractComponent;
import org.jboss.errai.mvp.googleanalytics.client.GoogleAnalyticsRPC;
import org.jboss.errai.mvp.googleanalytics.client.GoogleAnalyticsState;

/**
 * Component for triggering Google Analytics page views.
 * Usage:
 * <pre>
 * GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker("UA-658457-8", "vaadin.com");
 * window.addComponent(tracker);
 *   ....
 * tracker.trackPageview("/samplecode/googleanalytics");
 * </pre>
 *
 * @author Sami Ekblad / Marc Englund / IT Mill
 *
 */
public class GoogleAnalyticsTracker extends AbstractComponent {

    private static final long serialVersionUID = 2973391903850855532L;

    /**
     * Instantiate new Google Analytics tracker by id.
     *
     * @param trackerId
     *            The tracking id from Google Analytics. Something like
     *            'UA-658457-8'.
     */
    public GoogleAnalyticsTracker(String trackerId) {
        getState().setTrackerId(trackerId);
    }

    @Override
    protected GoogleAnalyticsState getState() {
        return (GoogleAnalyticsState) super.getState();
    }

    /**
     * Instantiate new Google Analytics tracker by id and domain.
     *
     * @param trackerId
     *            The tracking id from Google Analytics. Something like
     *            'UA-658457-8'.
     * @param domainName
     *            The name of the domain to be tracked. Something like
     *            'vaadin.com'.
     */

    public GoogleAnalyticsTracker(String trackerId, String domainName) {
        this(trackerId);
        getState().setDomainName(domainName);
    }


    /**
     * Get the Google Analytics tracking id.
     *
     * @return Tracking id like 'UA-658457-8'.
     */
    public String getTrackerId() {
        return getState().getTrackerId();
    }

    /**
     * Get the domain name associated with this tracker.
     *
     * @return
     */
    public String getDomainName() {
        return getState().getDomainName();
    }

    /**
     * Track a single page view. This effectively invokes the 'trackPageview' in
     * ga.js file.
     *
     * Note that when ever the component is repainted (for example during
     * explicit page reload), a new track event is generated.
     *
     * @param pageId
     *            The page id. Use a scheme like '/topic/page' or
     *            '/view/action'.
     */
    public void trackPageview(String pageId) {
        getRpcProxy(GoogleAnalyticsRPC.class).trackPageview(pageId);
    }

    /** Allow anchors in tracked URLs.
     *  As specified in http://code.google.com/apis/analytics/docs/gaJS/gaJSApiCampaignTracking.html#_gat.GA_Tracker_._setAllowAnchor
     *
     * @param allowAnchor
     */
    public void setAllowAnchor(boolean allowAnchor) {
        getState().setAllowAnchor(allowAnchor);
    }

    /** Allow anchors in tracked URLs.
     *  As specified in http://code.google.com/apis/analytics/docs/gaJS/gaJSApiCampaignTracking.html#_gat.GA_Tracker_._setAllowAnchor
     *
     */
    public boolean isAllowAnchor() {
        return getState().isAllowAnchor();
    }

}
