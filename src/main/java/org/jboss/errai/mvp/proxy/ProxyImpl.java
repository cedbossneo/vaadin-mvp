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

package org.jboss.errai.mvp.proxy;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.EventBus;
import com.vaadin.server.VaadinSession;
import org.jboss.errai.mvp.MVP;
import org.jboss.errai.mvp.events.NotifyingAsyncCallback;
import org.jboss.errai.mvp.presenters.Presenter;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Philippe Beaudoin
 *
 * @param <P> The presenter's type.
 */
public class ProxyImpl<P extends Presenter<?>> implements Proxy<P> {

  protected EventBus eventBus;
  private Class<P> presenterClass;

    /**
   * Creates a Proxy class for a specific presenter.
   */
  public ProxyImpl(Class<P> presenterClass, EventBus eventBus) {
      this.presenterClass = presenterClass;
      this.eventBus = eventBus;
  }

  @Override
  public void getPresenter(NotifyingAsyncCallback<P> callback) {
    callback.prepare();
    MVP mvp = (MVP) VaadinSession.getCurrent().getAttribute("mvp");
    mvp.getPresenter(presenterClass, callback);
    callback.checkLoading();
  }

  public void invokeMethod(final String methodName, final Object event, final Class<?> eventClass){
      getPresenter(new NotifyingAsyncCallback<P>(getEventBus()) {
          @Override
          protected void success(P result) {
              try {
                  presenterClass.getMethod(methodName, eventClass).invoke(result, event);
              } catch (IllegalAccessException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
              } catch (InvocationTargetException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
              } catch (NoSuchMethodException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
              }
          }
      });
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    eventBus.fireEventFromSource(event, this);
  }

  @Override
  public final EventBus getEventBus() {
    return eventBus;
  }

    public Class<P> getPresenterClass() {
        return presenterClass;
    }
}
