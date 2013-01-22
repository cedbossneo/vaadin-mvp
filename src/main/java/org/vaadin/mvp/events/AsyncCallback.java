package org.vaadin.mvp.events;

/**
 * Created with IntelliJ IDEA.
 * User: cedric
 * Date: 03/01/13
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
public interface AsyncCallback <T>  {
    void onFailure(java.lang.Throwable throwable);

    void onSuccess(T t);
}
