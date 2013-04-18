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

import com.google.gwt.event.shared.GwtEvent;
import org.vaadin.mvp.core.MVPEventBus;
import org.vaadin.mvp.core.events.NotifyingAsyncCallback;
import org.vaadin.mvp.core.presenters.Presenter;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Philippe Beaudoin
 *
 * @param <P> The presenter's type.
 */
public class ProxyImpl<P extends Presenter<?>> implements Proxy<P> {

    @Inject
    BeanManager beanManager;

  protected MVPEventBus eventBus;
    private CreationalContext<?> creationalContext;

    /**
   * Creates a Proxy class for a specific presenter.
   */
  public ProxyImpl(MVPEventBus eventBus) {
      this.eventBus = eventBus;
  }

  @Override
  public void getPresenter(NotifyingAsyncCallback<P> callback) {
    callback.prepare();
    callback.checkLoading();
      Set<Bean<?>> presenters = beanManager.getBeans(getPresenter());
      Bean<?> presenterBean = null;
      if (presenters.size() != 1)
          callback.onFailure(new Throwable("Found none or more than one presenter for " + getPresenter().toString()));
      else
        presenterBean = presenters.iterator().next();
      if (creationalContext == null) {
          creationalContext = beanManager.createCreationalContext(presenterBean);
      }
      P presenterInstance = (P) beanManager.getReference(presenterBean, getPresenter(), creationalContext);
      if (presenterInstance != null){
          Logger.getLogger(getClass().getName()).fine("New presenter created: " + presenterInstance.getClass().getName());
          callback.onSuccess(presenterInstance);
      }else
          callback.onFailure(new Throwable("Error while getting bean"));
    callback.checkLoading();
  }

    public Class<P> getPresenter() {
        return null;
    }

    @Override
  public void fireEvent(GwtEvent<?> event) {
        eventBus.fireEventFromSource(event, this);
  }

  @Override
  public final MVPEventBus getEventBus() {
    return eventBus;
  }
}
