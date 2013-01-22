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

package org.vaadin.mvp.proxy;

import org.vaadin.mvp.places.Place;
import org.vaadin.mvp.presenters.Presenter;

/**
 * The interface of a {@link Proxy} that is also a {@link org.vaadin.mvp.places.Place}.
 *
 * @param <P> The type of the {@link org.vaadin.mvp.presenters.Presenter} associated with this proxy.
 *
 * @author Philippe Beaudoin
 */
public interface ProxyPlace<P extends Presenter<?>> extends Proxy<P>, Place {

  /**
   * Manually reveals a presenter. Only use this method if your presenter is configured
   * to use manual reveal via {@link org.vaadin.mvp.presenters.Presenter#useManualReveal()}. This method should be
   * called following one or more asynchronous server calls in
   * {@link org.vaadin.mvp.presenters.Presenter#prepareFromRequest(PlaceRequest)}.
   * You should manually reveal your presenter exactly once, when all the data needed to use it is available.
   * <p />
   * If you failed to fetch the data or cannot reveal the presenter you must call
   * {@link #manualRevealFailed()} otherwise navigation will be blocked and your application
   * will appear to be frozen.
   * <p />
   * Also consider using {@link ManualRevealCallback}, which will automatically call
   * {@link #manualReveal(org.vaadin.mvp.presenters.Presenter)} upon success and {@link #manualRevealFailed()} upon
   * failure.
   *
   * @see org.vaadin.mvp.presenters.Presenter#useManualReveal()
   * @see #manualRevealFailed()
   *
   * @param presenter The presenter that will be delayed revealed.
   */
  void manualReveal(Presenter<?> presenter);

  /**
   * Cancels manually revealing a presenter. Only use this method if your presenter is configured
   * to use manual reveal via {@link org.vaadin.mvp.presenters.Presenter#useManualReveal()}. For more details see
   * {@link #manualReveal(org.vaadin.mvp.presenters.Presenter)}.
   *
   * @see #manualReveal(org.vaadin.mvp.presenters.Presenter)
   */
  void manualRevealFailed();

}
