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

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.vaadin.mvp.core.MVPEventBus;

/**
 * Event fired after any asynchronous call to the server performed by GWTP MVP has succeeded.
 * Such asynchronous calls only occur when using code splitting.
 *
 * @author Philippe Beaudoin
 * @see AsyncCallSucceedHandler
 * @see AsyncCallStartEvent
 * @see AsyncCallFailEvent
 */
public class AsyncCallSucceedEvent extends GwtEvent<AsyncCallSucceedHandler> {
    private static final Type<AsyncCallSucceedHandler> TYPE = new Type<AsyncCallSucceedHandler>();

    /**
     * Fires a {@link AsyncCallSucceedEvent}
     * into a source that has access to an {@link org.vaadin.mvp.core.MVPEventBus}.
     *
     * @param source The source that fires this event ({@link org.vaadin.mvp.core.MVPEventBus}).
     */
    public static void fire(MVPEventBus source) {
        source.fireEvent(new AsyncCallSucceedEvent());
    }

    /**
     * Fires a {@link AsyncCallSucceedEvent}
     * into a source that has access to an {@link org.vaadin.mvp.core.MVPEventBus}.
     *
     * @param source The source that fires this event ({@link HasHandlers}).
     * @deprecated Use {@link #fire(org.vaadin.mvp.core.MVPEventBus)} instead.
     */
    @Deprecated
    public static void fire(final HasHandlers source) {
        source.fireEvent(new AsyncCallSucceedEvent());
    }

    AsyncCallSucceedEvent() {
    }

    public static Type<AsyncCallSucceedHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<AsyncCallSucceedHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(AsyncCallSucceedHandler handler) {
        handler.onAsyncCallSucceed(this);
    }
}
