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

package com.cbnserver.gwtp4vaadin.core.proxy;

import com.cbnserver.gwtp4vaadin.core.MVPEventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Event fired after any asynchronous call to the server performed by GWTP MVP returns with
 * Such asynchronous calls only occur when using code splitting.
 *
 * @author Philippe Beaudoin
 * @see AsyncCallFailHandler
 * @see AsyncCallStartEvent
 * @see AsyncCallSucceedEvent
 */
public class AsyncCallFailEvent extends GwtEvent<AsyncCallFailHandler> {
    private static final Type<AsyncCallFailHandler> TYPE = new Type<AsyncCallFailHandler>();

    /**
     * Fires a {@link AsyncCallFailEvent}
     * into a source that has access to an {@link com.cbnserver.gwtp4vaadin.core.MVPEventBus}.
     *
     * @param source The source that fires this event ({@link com.cbnserver.gwtp4vaadin.core.MVPEventBus}).
     * @param caught failure encountered while executing a remote procedure call.
     */
    public static void fire(MVPEventBus source, Throwable caught) {
        source.fireEvent(new AsyncCallFailEvent(caught));
    }

    /**
     * Fires a {@link AsyncCallFailEvent}
     * into a source that has access to an {@link com.cbnserver.gwtp4vaadin.core.MVPEventBus}.
     *
     * @param source The source that fires this event ({@link HasHandlers}).
     * @param caught failure encountered while executing a remote procedure call.
     * @deprecated Use {@link #fire(com.cbnserver.gwtp4vaadin.core.MVPEventBus, Throwable)} instead.
     */
    @Deprecated
    public static void fire(final HasHandlers source, Throwable caught) {
        source.fireEvent(new AsyncCallFailEvent(caught));
    }

    private final Throwable caught;

    /**
     * Creates an event indicating that an asynchronous call has failed, and attach a {@link Throwable}
     * to it.
     *
     * @param caught failure encountered while executing a remote procedure call.
     */
    AsyncCallFailEvent(Throwable caught) {
        this.caught = caught;
    }

    public static Type<AsyncCallFailHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<AsyncCallFailHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(AsyncCallFailHandler handler) {
        handler.onAsyncCallFail(this);
    }

    /**
     * Access the {@link Throwable} that was obtained when this asynchronous call failed.
     *
     * @return The {@link PlaceRequest} or {@code null} if no place request is
     *         known.
     */
    public Throwable getCaught() {
        return caught;
    }
}
