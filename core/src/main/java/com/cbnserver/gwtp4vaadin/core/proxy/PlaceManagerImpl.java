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
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Command;
import com.vaadin.server.Page;
import com.vaadin.ui.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation to be extended of the {@link PlaceManager}.
 *
 * @see DefaultPlaceManager
 */
public abstract class PlaceManagerImpl implements PlaceManager, Page.UriFragmentChangedListener {

    private final Page page;
    private MVPEventBus eventBus;
    private String currentHistoryToken = "";

    private boolean internalError;
    private List<PlaceRequest> placeHierarchy = new ArrayList<PlaceRequest>();

    private TokenFormatter tokenFormatter;

    private boolean locked;
    private Command defferedNavigation;

    public PlaceManagerImpl(MVPEventBus eventBus, TokenFormatter tokenFormatter) {
        this.eventBus = eventBus;
        this.tokenFormatter = tokenFormatter;
        page = UI.getCurrent().getPage();
        page.addUriFragmentChangedListener(this);
    }

    public Page getPage() {
        return page;
    }

    @Override
    public void uriFragmentChanged(final Page.UriFragmentChangedEvent event) {
        String uriFragment = event.getUriFragment();
        if (uriFragment == null)
            uriFragment = "";
        if (locked) {
            defferedNavigation = new Command() {
                @Override
                public void execute() {
                    uriFragmentChanged(event);
                }
            };
            return;
        }
        if (!getLock()) {
            return;
        }
        try {
            if (uriFragment.trim().equals("")) {
                unlock();
                revealDefaultPlace();
            } else {
                placeHierarchy = tokenFormatter.toPlaceRequestHierarchy(uriFragment);
                doRevealPlace(getCurrentPlaceRequest(), true);
            }
        } catch (TokenFormatException e) {
            unlock();
            error(uriFragment);
            NavigationEvent.fire(this, null);
        }
    }

    @Override
    public String buildHistoryToken(PlaceRequest request) {
        return tokenFormatter.toPlaceToken(request);
    }

    @Override
    public String buildRelativeHistoryToken(int level) {
        List<PlaceRequest> placeHierarchyCopy = truncatePlaceHierarchy(level);
        if (placeHierarchyCopy.size() == 0) {
            return "";
        }
        return tokenFormatter.toHistoryToken(placeHierarchyCopy);
    }

    @Override
    public String buildRelativeHistoryToken(PlaceRequest request) {
        return buildRelativeHistoryToken(request, 0);
    }

    @Override
    public String buildRelativeHistoryToken(PlaceRequest request, int level) {
        List<PlaceRequest> placeHierarchyCopy = truncatePlaceHierarchy(level);
        placeHierarchyCopy.add(request);
        return tokenFormatter.toHistoryToken(placeHierarchyCopy);
    }

    @Override
    public void navigateBack() {
        page.setUriFragment(currentHistoryToken);
        }

    /**
     * Fires the {@link PlaceRequestInternalEvent} for the given
     * {@link PlaceRequest}. Do not call this method directly,
     * instead call {@link #revealPlace(PlaceRequest)} or a related method.
     *
     * @param request          The {@link PlaceRequest} to fire.
     * @param updateBrowserUrl {@code true} If the browser URL should be updated, {@code false}
     *                         otherwise.
     */
    protected void doRevealPlace(PlaceRequest request, boolean updateBrowserUrl) {
        PlaceRequestInternalEvent requestEvent = new PlaceRequestInternalEvent(request,
                updateBrowserUrl);
        fireEvent(requestEvent);
        if (!requestEvent.isHandled()) {
            unlock();
            error(tokenFormatter.toHistoryToken(placeHierarchy));
        } else if (!requestEvent.isAuthorized()) {
            unlock();
            illegalAccess(tokenFormatter.toHistoryToken(placeHierarchy));
        }
    }

    /**
     * Called whenever an error occurred that requires the error page to be shown
     * to the user. This method will detect infinite reveal loops and throw an
     * {@link RuntimeException} in that case.
     *
     * @param invalidHistoryToken The history token that was not recognised.
     */
    private void error(String invalidHistoryToken) {
        startError();
        revealErrorPlace(invalidHistoryToken);
        stopError();
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        getEventBus().fireEventFromSource(event, this);
    }

    @Override
    public List<PlaceRequest> getCurrentPlaceHierarchy() {
        return placeHierarchy;
    }

    @Override
    public PlaceRequest getCurrentPlaceRequest() {
        if (placeHierarchy.size() > 0) {
            return placeHierarchy.get(placeHierarchy.size() - 1);
        } else {
            return new PlaceRequest.Builder().build();
        }
    }

    @Override
    public void getCurrentTitle(SetPlaceTitleHandler handler) {
        getTitle(placeHierarchy.size() - 1, handler);
    }

    @Override
    public MVPEventBus getEventBus() {
        return eventBus;
    }

    @Override
    public int getHierarchyDepth() {
        return placeHierarchy.size();
    }

    /**
     * Checks that the place manager is not locked. If the
     * application is allowed to navigate, this method locks navigation.
     *
     * @return true if the place manager can get the lock false otherwise.
     */
    private boolean getLock() {
        if (locked) {
            return false;
        }
        lock();
        return true;
    }

    @Override
    public void getTitle(int index, SetPlaceTitleHandler handler)
            throws IndexOutOfBoundsException {
        GetPlaceTitleEvent event = new GetPlaceTitleEvent(
                placeHierarchy.get(index), handler);
        fireEvent(event);
        // If nobody took care of the title, indicate it's null
        if (!event.isHandled()) {
            handler.onSetPlaceTitle(null);
        }
    }

    @Override
    public boolean hasPendingNavigation() {
        return defferedNavigation != null;
    }

    /**
     * Called whenever the user tries to access an page to which he doesn't have
     * access, and we need to reveal the user-defined unauthorized place. This
     * method will detect infinite reveal loops and throw an
     * {@link RuntimeException} in that case.
     *
     * @param historyToken The history token that was not recognised.
     */
    private void illegalAccess(String historyToken) {
        startError();
        revealUnauthorizedPlace(historyToken);
        stopError();
    }

    private void lock() {
        if (!locked) {
            locked = true;
            LockInteractionEvent.fire(this, true);
        }
    }

    @Override
    public void revealCurrentPlace() {
        uriFragmentChanged(new Page.UriFragmentChangedEvent(page, page.getUriFragment()));
    }

    @Override
    public void revealErrorPlace(String invalidHistoryToken) {
        revealDefaultPlace();
    }

    @Override
    public void revealPlace(final PlaceRequest request) {
        revealPlace(request, true);
    }

    @Override
    public void revealPlace(final PlaceRequest request, final boolean updateBrowserUrl) {
        if (locked) {
            defferedNavigation = new Command() {
                @Override
                public void execute() {
                    revealPlace(request, updateBrowserUrl);
                }
            };
            return;
        }
        if (!getLock()) {
            return;
        }
        placeHierarchy.clear();
        placeHierarchy.add(request);
        doRevealPlace(request, updateBrowserUrl);
    }

    @Override
    public void revealPlaceHierarchy(
            final List<PlaceRequest> placeRequestHierarchy) {
        if (locked) {
            defferedNavigation = new Command() {
                @Override
                public void execute() {
                    revealPlaceHierarchy(placeRequestHierarchy);
                }
            };
            return;
        }
        if (!getLock()) {
            return;
        }
        if (placeRequestHierarchy.size() == 0) {
            unlock();
            revealDefaultPlace();
        } else {
            placeHierarchy = placeRequestHierarchy;
            doRevealPlace(getCurrentPlaceRequest(), true);
        }
    }

    @Override
    public void revealRelativePlace(final int level) {
        if (locked) {
            defferedNavigation = new Command() {
                @Override
                public void execute() {
                    revealRelativePlace(level);
                }
            };
            return;
        }
        if (!getLock()) {
            return;
        }
        placeHierarchy = truncatePlaceHierarchy(level);
        int hierarchySize = placeHierarchy.size();
        if (hierarchySize == 0) {
            unlock();
            revealDefaultPlace();
        } else {
            PlaceRequest request = placeHierarchy.get(hierarchySize - 1);
            doRevealPlace(request, true);
        }
    }

    @Override
    public void revealRelativePlace(PlaceRequest request) {
        revealRelativePlace(request, 0);
    }

    @Override
    public void revealRelativePlace(final PlaceRequest request, final int level) {
        if (locked) {
            defferedNavigation = new Command() {
                @Override
                public void execute() {
                    revealRelativePlace(request, level);
                }
            };
            return;
        }
        if (!getLock()) {
            return;
        }
        placeHierarchy = truncatePlaceHierarchy(level);
        placeHierarchy.add(request);
        doRevealPlace(request, true);
    }

    @Override
    public void revealUnauthorizedPlace(String unauthorizedHistoryToken) {
        revealErrorPlace(unauthorizedHistoryToken);
    }

    /**
     * This method saves the history token, making it possible to correctly restore the browser's
     * URL if the user refuses to navigate.
     *
     * @param historyToken The current history token, a string.
     */
    private void saveHistoryToken(String historyToken) {
        currentHistoryToken = historyToken;
    }

    void setBrowserHistoryToken(String historyToken) {
        page.setUriFragment(historyToken, false);
    }

    /**
     * Start revealing an error or unauthorized page. This method will throw an
     * exception if an infinite loop is detected.
     *
     * @see #stopError()
     */
    private void startError() {
        if (this.internalError) {
            throw new RuntimeException(
                    "Encountered repeated errors resulting in an infinite loop. Make sure all users have access "
                            + "to the pages revealed by revealErrorPlace and revealUnauthorizedPlace. (Note that the " +
                            "default "
                            + "implementations call revealDefaultPlace)");
        }
        internalError = true;
    }

    /**
     * Indicates that an error page has successfully been revealed. Makes it
     * possible to detect infinite loops.
     *
     * @see #startError()
     */
    private void stopError() {
        internalError = false;
    }

    @Override
    public void unlock() {
        if (locked) {
            locked = false;
            LockInteractionEvent.fire(this, false);
            if (hasPendingNavigation()) {
                Command navigation = defferedNavigation;
                defferedNavigation = null;
                navigation.execute();
            }
        }
    }

    @Override
    public void updateHistory(PlaceRequest request, boolean updateBrowserUrl) {
        try {
            // Make sure the request match
            assert request.hasSameNameToken(getCurrentPlaceRequest()) : "Internal error, PlaceRequest passed to" +
                    "updateHistory doesn't match the tail of the place hierarchy.";
            placeHierarchy.set(placeHierarchy.size() - 1, request);
            if (updateBrowserUrl) {
                String historyToken = tokenFormatter.toHistoryToken(placeHierarchy);
                String browserHistoryToken = page.getUriFragment();
                if (browserHistoryToken == null
                        || !browserHistoryToken.equals(historyToken)) {
                    setBrowserHistoryToken(historyToken);
                }
                saveHistoryToken(historyToken);
            }
        } catch (TokenFormatException e) {
            // Do nothing.
        }
    }

    /**
     * Returns a modified copy of the place hierarchy based on the specified
     * {@code level}.
     *
     * @param level If negative, take back that many elements from the tail of the
     *              hierarchy. If positive, keep only that many elements from the head
     *              of the hierarchy. Passing {@code 0} leaves the hierarchy
     *              untouched.
     */
    private List<PlaceRequest> truncatePlaceHierarchy(int level) {
        int size = placeHierarchy.size();
        if (level < 0) {
            if (-level >= size) {
                return new ArrayList<PlaceRequest>();
            } else {
                return new ArrayList<PlaceRequest>(placeHierarchy.subList(0, size
                        + level));
            }
        } else if (level > 0) {
            if (level >= size) {
                return new ArrayList<PlaceRequest>(placeHierarchy);
            } else {
                return new ArrayList<PlaceRequest>(placeHierarchy.subList(0, level));
            }
        }
        return new ArrayList<PlaceRequest>(placeHierarchy);
    }
}
