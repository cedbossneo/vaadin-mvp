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

package org.vaadin.mvp.core.proxy;

/**
 * The interface for a handler that will be invoked when a place title is
 * available. For more information see
 * {@link PlaceManager#getTitle(int, SetPlaceTitleHandler)}.
 *
 * @author Philippe Beaudoin
 */
public interface SetPlaceTitleHandler {
    /**
     * Invoked when the title of the place is available.
     *
     * @param title The place title.
     */
    void onSetPlaceTitle(String title);
}
