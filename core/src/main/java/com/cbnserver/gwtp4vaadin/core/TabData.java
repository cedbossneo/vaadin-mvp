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

/**
 * Classes of this type contains all the data required to create
 * a new {@link Tab}. The default implementation is
 * {@link TabDataBasic} but you can create your own class, with
 * more information, if desired. See
 * {@link com.cbnserver.gwtp4vaadin.core.annotations.TabInfo}
 * for more details.
 *
 * @author Philippe Beaudoin
 */
public interface TabData {

    /**
     * A tab priority indicates where it should appear within the tab strip. In
     * typical implementations of {@link TabPanel}, a tab with low priority will
     * be placed more towards the left of the strip. Two tabs with the same
     * priority will be placed in an arbitrary order.
     *
     * @return The priority.
     */
    float getPriority();

    /**
     * Gets the label to display on the tab.
     *
     * @return The label.
     */
    String getLabel();
}
