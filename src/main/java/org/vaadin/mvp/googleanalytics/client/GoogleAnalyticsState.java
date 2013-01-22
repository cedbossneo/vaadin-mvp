package org.vaadin.mvp.googleanalytics.client;

import com.vaadin.shared.AbstractComponentState;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 05/01/13
 * Time: 01:48
 */
public class GoogleAnalyticsState extends AbstractComponentState {
    private String trackerId;
    private String domainName;
    private boolean allowAnchor;

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public boolean isAllowAnchor() {
        return allowAnchor;
    }

    public void setAllowAnchor(boolean allowAnchor) {
        this.allowAnchor = allowAnchor;
    }
}
