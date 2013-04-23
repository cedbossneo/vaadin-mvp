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

package com.cbnserver.gwtp4vaadin.core;

import com.cbnserver.gwtp4vaadin.core.googleanalytics.GoogleAnalyticsTracker;
import com.vaadin.navigator.ViewChangeListener;

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
