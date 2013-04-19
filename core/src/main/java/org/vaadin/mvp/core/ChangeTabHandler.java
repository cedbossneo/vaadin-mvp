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

package org.vaadin.mvp.core;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Philippe Beaudoin
 */
public interface ChangeTabHandler extends EventHandler {

    /**
     * Called whenever a tab contained in a {@link TabContainerPresenter} wants to change its
     * information.
     *
     * @param event The event.
     */
    void onChangeTab(ChangeTabEvent event);

}
