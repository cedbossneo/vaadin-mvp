/**
 * Copyright 2011 ArcBees Inc.
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

package org.vaadin.mvp.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation lets you define the parameters for a
 * {@link GatekeeperWithParams} to use for the
 * {@link org.vaadin.mvp.core.proxy.Place} associated
 * with your proxy. Your custom {@code Ginjector} must have a
 * method returning the {@link GatekeeperWithParams} specified
 * in this annotation.
 *
 * @author Juan Carlos González
 */
@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface GatekeeperParams {
    String[] value();
}
