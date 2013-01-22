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

package org.vaadin.mvp.presenters;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.vaadin.mvp.views.ViewImpl;

/**
 * {@link org.vaadin.mvp.presenters.RootPresenter}'s view.
 */
public class RootView extends ViewImpl implements RootPresenter.MyView {

    @Override
  public Component asComponent() {
    assert false : "Root view has no component, you should never call asComponent()";
    return null;
  }

  @Override
  public void setInSlot(Object slot, Component content) {
    assert slot == RootPresenter.rootSlot : "Unknown slot used in the root proxy.";

    UI.getCurrent().setContent(content);
  }

    public void lockScreen() {

  }

  public void unlockScreen() {

  }

}
