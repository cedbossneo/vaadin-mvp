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

import com.cbnserver.gwtp4vaadin.core.Presenter;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * This event is fired by a {@link com.cbnserver.gwtp4vaadin.core.Presenter} that desires to reveal itself
 * within its parent. It is typically fired in the {@link com.cbnserver.gwtp4vaadin.core.Presenter#revealInParent()}
 * method. To reveal a presenter at the root of the application, fire either
 * {@link RevealRootContentEvent}, {@link RevealRootLayoutContentEvent} or
 * {@link RevealRootPopupContentEvent} instead.
 * <p/>
 * This event is handled by {@link Proxy} classes. Upon handling this
 * event, the proxy <b>first</b> sets the content appropriately in the
 * presenter, and then reveals the presenter.
 *
 * @author Philippe Beaudoin
 * @see RevealRootContentEvent
 * @see RevealRootLayoutContentEvent
 * @see RevealRootPopupContentEvent
 */
public final class RevealContentEvent extends GwtEvent<RevealContentHandler<?>> {

    /**
     * Fires a {@link RevealContentEvent} with a specific {@link com.google.gwt.event.shared.GwtEvent.Type}
     * into a source that has access to an {@link com.cbnserver.gwtp4vaadin.core.MVPEventBus}.
     *
     * @param source  The source that fires this event ({@link HasHandlers}).
     * @param type    The specific event {@link com.google.gwt.event.shared.GwtEvent.Type},
     *                usually defined in the parent presenter
     *                and annotated with {@link com.cbnserver.gwtp4vaadin.core.annotations.ContentSlot}.
     * @param content The {@link Presenter} that wants to set itself as content in his parent.
     */
    public static void fire(final HasHandlers source,
                            final Type<RevealContentHandler<?>> type, final Presenter<?, ?> content) {
        source.fireEvent(new RevealContentEvent(type, content));
    }

    private final Presenter<?, ?> content;

    private final Type<RevealContentHandler<?>> type;

    public RevealContentEvent(Type<RevealContentHandler<?>> type,
                              Presenter<?, ?> content) {
        this.type = type;
        this.content = content;
    }

    @Override
    public Type<RevealContentHandler<?>> getAssociatedType() {
        return type;
    }

    public Presenter<?, ?> getContent() {
        return content;
    }

    @Override
    protected void dispatch(RevealContentHandler<?> handler) {
        handler.onRevealContent(this);
    }

}
