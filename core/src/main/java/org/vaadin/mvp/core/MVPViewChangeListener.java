package org.vaadin.mvp.core;

import com.vaadin.navigator.ViewChangeListener;
import org.vaadin.mvp.core.googleanalytics.GoogleAnalyticsTracker;

/**
* Created with IntelliJ IDEA.
* User: hauber_c
* Date: 12/04/13
* Time: 18:24
* To change this template use File | Settings | File Templates.
*/
class MVPViewChangeListener implements ViewChangeListener {
    private GoogleAnalyticsTracker analytics;

    public MVPViewChangeListener(String gaAccount) {
        this.analytics = new GoogleAnalyticsTracker(gaAccount);
    }

    @Override
    public boolean beforeViewChange(ViewChangeEvent viewChangeEvent) {
        return true;
    }

    @Override
    public void afterViewChange(ViewChangeEvent viewChangeEvent) {
        if (analytics != null)
            analytics.trackPageview(viewChangeEvent.getViewName());
    }
}
