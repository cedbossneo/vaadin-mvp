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

package org.vaadin.mvp.core.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.vaadin.mvp.core.MVPEventBus;

/**
 * Event fired right before any asynchronous call to the server is performed by GWTP MVP.
 * Such asynchronous calls only occur when using code splitting. You can hook on this event
 * to display a "Loading..." message if desired.
 *
 * @see AsyncCallStartHandler
 * @see AsyncCallSucceedEvent
 * @see AsyncCallFailEvent
 *
 * @author Philippe Beaudoin
 */
public class AsyncCallStartEvent extends GwtEvent<AsyncCallStartHandler> {
  private static final Type<AsyncCallStartHandler> TYPE = new Type<AsyncCallStartHandler>();

  /**
   * Fires a {@link AsyncCallStartEvent}
   * into a source that has access to an {@link org.vaadin.mvp.core.MVPEventBus}.
   *
   * @param source The source that fires this event ({@link org.vaadin.mvp.core.MVPEventBus}).
   */
  public static void fire(MVPEventBus source) {
    source.fireEvent(new AsyncCallStartEvent());
  }

  /**
   * Fires a {@link AsyncCallStartEvent}
   * into a source that has access to an {@link org.vaadin.mvp.core.MVPEventBus}.
   * @deprecated Use {@link #fire(org.vaadin.mvp.core.MVPEventBus)} instead.
   *
   * @param source The source that fires this event ({@link com.google.gwt.event.shared.HasHandlers}).
   */
  @Deprecated
  public static void fire(final HasHandlers source) {
    source.fireEvent(new AsyncCallStartEvent());
  }

  public static Type<AsyncCallStartHandler> getType() {
    return TYPE;
  }

  AsyncCallStartEvent() {
  }

  @Override
  public Type<AsyncCallStartHandler> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(AsyncCallStartHandler handler) {
    handler.onAsyncCallStart(this);
  }
}
