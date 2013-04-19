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
