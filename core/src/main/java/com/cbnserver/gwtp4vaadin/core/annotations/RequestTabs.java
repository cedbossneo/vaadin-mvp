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

package com.cbnserver.gwtp4vaadin.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use this annotation in classes implementing
 * {@link com.cbnserver.gwtp4vaadin.core.TabContainerPresenter}.
 * This annotates a static field containing the type of the event fired when the tab container wants
 * to discover its contained tabs. <b>Important!</b> The corresponding type must be passed to the
 * parent's constructor.
 *
 * @author Philippe Beaudoin
 */
@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface RequestTabs {
}
