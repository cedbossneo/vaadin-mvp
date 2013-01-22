/*
 * Copyright 2012 Cedric Hauber
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.vaadin.mvp.views;

import com.vaadin.ui.Component;

/**
 * A simple implementation of {@link org.vaadin.mvp.views.View} that simply disregards every call to
 * {@link #setInSlot(Object, com.google.gwt.user.client.ui.Widget)}, {@link #addToSlot(Object, com.google.gwt.user.client.ui.Widget)}, and
 * {@link #removeFromSlot(Object, com.google.gwt.user.client.ui.Widget)}.
 * <p />
 * Feel free not to inherit from this if you need another base class (such as
 * {@link com.google.gwt.user.client.ui.Composite}), but you will have to define
 * the above methods.
 *
 * @author Philippe Beaudoin
 * @author Christian Goudreau
 */
public abstract class ViewImpl implements View {

  @Override
  public void addToSlot(Object slot, Component content) {
  }

  @Override
  public void removeFromSlot(Object slot, Component content) {
  }

  @Override
  public void setInSlot(Object slot, Component content) {
  }
}