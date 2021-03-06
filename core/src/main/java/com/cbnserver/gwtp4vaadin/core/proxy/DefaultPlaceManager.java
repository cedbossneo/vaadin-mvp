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

package com.cbnserver.gwtp4vaadin.core.proxy;

import com.cbnserver.gwtp4vaadin.core.MVPEventBus;
import com.cbnserver.gwtp4vaadin.core.annotations.DefaultPlace;
import com.cbnserver.gwtp4vaadin.core.annotations.ErrorPlace;
import com.cbnserver.gwtp4vaadin.core.annotations.UnauthorizedPlace;
import com.vaadin.cdi.UIScoped;

import javax.inject.Inject;

/**
 * This is a subtype of {@link com.cbnserver.gwtp4vaadin.core.proxy.PlaceManagerImpl PlaceManagerImpl} that uses
 * custom name tokens to reveal default, error and unauthorized places.
 * <p/>
 * <b>Important! </b>If you use this class, don't forget to bind
 * {@link com.cbnserver.gwtp4vaadin.core.annotations.DefaultPlace DefaultPlace},
 * {@link com.cbnserver.gwtp4vaadin.core.annotations.ErrorPlace ErrorPlace} and
 * {@link com.cbnserver.gwtp4vaadin.core.annotations.UnauthorizedPlace UnauthorizedPlace} to Presenter name tokens in
 * your Gin module.
 * <p/>
 * <i>Note: </i>The default, error and unauthorized places are revealed without updating the browser's URL (hence
 * the false value passed in {@link #revealPlace(PlaceRequest, boolean) revealPlace}). This will avoid stepping into
 * an infinite navigation loop if the user navigates back (using the browser's back button).
 * <p/>
 * Here's an example of infinite navigation loop that we want to avoid:
 * <ol>
 * <li>An unauthenticated hits #admin (a place reserved to authenticated admins)</li>
 * <li>The #unauthorized place is revealed, and the browser's URL is updated to #unauthorized</li>
 * <li>The user clicks the back button in his browser, lands in #admin, then #unauthorized, then #admin, and so on.</li>
 * </ol>
 */
@UIScoped
public class DefaultPlaceManager extends PlaceManagerImpl {
    private final PlaceRequest defaultPlaceRequest;
    private final PlaceRequest errorPlaceRequest;
    private final PlaceRequest unauthorizedPlaceRequest;

    @Inject
    public DefaultPlaceManager(MVPEventBus eventBus,
                               TokenFormatter tokenFormatter,
                               @DefaultPlace String defaultPlaceNameToken,
                               @ErrorPlace String errorPlaceNameToken,
                               @UnauthorizedPlace String unauthorizedPlaceNameToken) {
        super(eventBus, tokenFormatter);

        defaultPlaceRequest = new PlaceRequest.Builder().nameToken(defaultPlaceNameToken).build();
        errorPlaceRequest = new PlaceRequest.Builder().nameToken(errorPlaceNameToken).build();
        unauthorizedPlaceRequest = new PlaceRequest.Builder().nameToken(unauthorizedPlaceNameToken).build();
    }

    @Override
    public void revealDefaultPlace() {
        revealPlace(defaultPlaceRequest, false);
    }

    @Override
    public void revealErrorPlace(String invalidHistoryToken) {
        revealPlace(errorPlaceRequest, false);
    }

    @Override
    public void revealUnauthorizedPlace(String unauthorizedHistoryToken) {
        revealPlace(unauthorizedPlaceRequest, false);
    }
}
