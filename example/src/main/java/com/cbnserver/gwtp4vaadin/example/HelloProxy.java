package com.cbnserver.gwtp4vaadin.example;

import com.cbnserver.gwtp4vaadin.core.annotations.NameToken;
import com.cbnserver.gwtp4vaadin.core.annotations.ProxyStandard;
import com.cbnserver.gwtp4vaadin.core.proxy.ProxyPlace;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 30/04/13
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
@ProxyStandard
@NameToken("")
public interface HelloProxy extends ProxyPlace<HelloPresenter>{
}
