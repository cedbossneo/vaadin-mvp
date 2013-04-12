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

package org.vaadin.mvp.core.proxy;

import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.EventBus;
import org.vaadin.mvp.core.presenters.Presenter;
import org.vaadin.mvp.core.events.NotifyingAsyncCallback;
import org.vaadin.mvp.core.presenters.Presenter;

import java.io.Serializable;

import java.io.Serializable;

/**
 * The interface for light-weight singleton classes that listens for events
 * before the full {@link org.vaadin.mvp.core.presenters.Presenter} is instantiated. This include, among
 * others, the presenter's specific {@link org.vaadin.mvp.core.events.RevealContentEvent} that needs the
 * presenter to reveal itself.
 * <p />
 * The relationship between a presenter and its proxy is two-way.
 * <p />
 * {@link org.vaadin.mvp.core.presenters.Presenter} subclasses will usually define their own interface called
 * MyProxy and be derived from this one.
 *
 * @param <P> The type of the {@link org.vaadin.mvp.core.presenters.Presenter} associated with this proxy.
 *
 * @author Philippe Beaudoin
 */
public interface Proxy<P extends Presenter<?>> extends HasHandlers, Serializable {

  /**
   * Makes it possible to access the {@link com.google.web.bindery.event.shared.EventBus} object associated with
   * that proxy.
   *
   * @return The {@link com.google.web.bindery.event.shared.EventBus} associated with that proxy.
   */
  EventBus getEventBus();

  /**
   * Get the associated {@link org.vaadin.mvp.core.presenters.Presenter}. The presenter can only be obtained in
   * an asynchronous manner to support code splitting when needed. To access the
   * presenter, pass a callback.
   *
   * @param callback The callback in which the {@link org.vaadin.mvp.core.presenters.Presenter} will be passed
   *          as a parameter.
   */
  void getPresenter(NotifyingAsyncCallback<P> callback);

  public Class<P> getPresenterClass();
}