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

import com.cbnserver.gwtp4vaadin.core.proxy.*;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

import javax.inject.Inject;

/**
 * This is the presenter for the top-level of the application. It is derived
 * from presenter widget, but it's just because it doesn't need a proxy has it
 * will be bound as an eager singleton.
 * <p/>
 * Fire a {@link RevealRootContentEvent} or {@link RevealRootLayoutContentEvent}
 * to set your presenter at the top level. The choice depends on whether your
 * presenter works as a {@link com.google.gwt.user.client.ui.Panel} or as a
 * {@link com.google.gwt.user.client.ui.LayoutPanel}.
 *
 * @author Philippe Beaudoin
 */
public class RootPresenter extends PresenterWidget<RootPresenter.RootView>
        implements ResetPresentersHandler, RevealRootContentHandler,
        RevealRootLayoutContentHandler, RevealRootPopupContentHandler,
        LockInteractionHandler {

    /**
     * {@link RootPresenter}'s view.
     */
    public static class RootView extends ViewImpl {

        @Override
        public Component asComponent() {
            assert false : "Root view has no widget, you should never call asComponent()";
            return null;
        }

        @Override
        public void setInSlot(Object slot, Component content) {
            assert slot == rootSlot : "Unknown slot used in the root proxy.";
            if (content != null) {
                UI.getCurrent().setContent(content);
            }
        }

        public void lockScreen() {
        }

        public void unlockScreen() {
        }

        public void ensureGlass() {
        }
    }

    private static final Object rootSlot = new Object();

    private boolean isResetting;

    /**
     * Creates a proxy class for a presenter that can contain tabs.
     *
     * @param eventBus The event bus.
     */
    @Inject
    public RootPresenter(final MVPEventBus eventBus, final RootView view) {
        super(eventBus, view);
        visible = true;
    }

    @Override
    protected void onBind() {
        super.onBind();

        addRegisteredHandler(ResetPresentersEvent.getType(), this);

        addRegisteredHandler(RevealRootContentEvent.getType(), this);

        addRegisteredHandler(RevealRootLayoutContentEvent.getType(), this);

        addRegisteredHandler(RevealRootPopupContentEvent.getType(), this);

        addRegisteredHandler(LockInteractionEvent.getType(), this);
    }

    @Override
    public void onResetPresenters(ResetPresentersEvent resetPresentersEvent) {
        if (!isResetting) {
            isResetting = true;
            internalReset();
            isResetting = false;
        }
    }

    @Override
    public void onRevealRootContent(
            final RevealRootContentEvent revealContentEvent) {
        setInSlot(rootSlot, revealContentEvent.getContent());
    }

    public void onRevealRootLayoutContent(
            final RevealRootLayoutContentEvent revealContentEvent) {
        setInSlot(rootSlot, revealContentEvent.getContent());
    }

    @Override
    public void onRevealRootPopupContent(
            final RevealRootPopupContentEvent revealContentEvent) {
        if (revealContentEvent.isCentered()) {
            addToPopupSlot(revealContentEvent.getContent());
        } else {
            addToPopupSlot(revealContentEvent.getContent(), false);
        }
    }

    @Override
    public void onLockInteraction(LockInteractionEvent lockInteractionEvent) {
        if (lockInteractionEvent.shouldLock()) {
            getView().lockScreen();
        } else {
            getView().unlockScreen();
        }
    }

}
