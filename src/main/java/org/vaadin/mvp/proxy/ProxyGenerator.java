package org.vaadin.mvp.proxy;

import javassist.*;
import org.vaadin.mvp.annotations.ProxyEvent;
import org.vaadin.mvp.presenters.Presenter;
import org.vaadin.mvp.views.View;

import javax.inject.Singleton;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 06/01/13
 * Time: 17:05
 */
@Singleton
public class ProxyGenerator implements Serializable{
    private final Map<Class<? extends Presenter<? extends View>>, Class> proxies = new HashMap<Class<? extends Presenter<? extends View>>, Class>();
    private final ClassPool poll;

    public ProxyGenerator() {
        poll = ClassPool.getDefault();
        poll.appendClassPath(new LoaderClassPath(getClass().getClassLoader()));
    }

    public  <P extends Presenter<? extends View>> Class createPresenterProxy(Class<P> presenter) {
        if (proxies.containsKey(presenter)) //Check in map
            return proxies.get(presenter);
        try { //Check in pool
            Class<?> loadedClass = poll.get(presenter.getSimpleName() + "Proxy").toClass();
            proxies.put(presenter, loadedClass);
            return loadedClass;
        } catch (CannotCompileException e) {
        } catch (NotFoundException e) {
        }
        try {
            CtClass proxyImplClass = poll.getCtClass(ProxyImpl.class.getName());
            CtClass proxyClass = poll.makeClass(presenter.getSimpleName() + "Proxy", proxyImplClass);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("public " + presenter.getSimpleName() + "Proxy(com.google.web.bindery.event.shared.EventBus eventBus) {");
            stringBuffer.append("   super(" + presenter.getName() + ".class, eventBus);");
            stringBuffer.append(registerEventsHandler(poll, presenter, proxyClass));
            stringBuffer.append("}");
            proxyClass.addConstructor(CtNewConstructor.make(stringBuffer.toString(), proxyClass));
            Class finalProxyClass = proxyClass.toClass();
            proxies.put(presenter, finalProxyClass);
            return finalProxyClass;
        } catch (NotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CannotCompileException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    private <P extends Presenter<? extends View>> StringBuffer registerEventsHandler(ClassPool pool, Class<P> presenter, CtClass proxyClass) {
        StringBuffer stringBuffer = new StringBuffer();
        Method[] methods = presenter.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(ProxyEvent.class))
                try {
                    int modifiers = method.getParameterTypes()[0].getMethod("getType").getModifiers();
                    if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
                        try {
                            proxyClass.addInterface(pool.getCtClass(getHandler(presenter, method).getName()));
                            String event = method.getParameterTypes()[0].getName();
                            proxyClass.addMethod(CtNewMethod.make(
                                    "public void "+method.getName()+"("+ event +" event){"+
                                            "       invokeMethod(\""+method.getName()+"\", event, "+event+".class);" +
                                            "}", proxyClass));
                            stringBuffer.append("getEventBus().addHandler(" + event + ".getType(), this);");
                        } catch (NotFoundException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (CannotCompileException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                } catch (NoSuchMethodException e) {
                    System.out.println("Not such static method getType for Event " + method.getName());
                }
        }
        return stringBuffer;
    }

    private <P extends Presenter<? extends View>> Class<?> getHandler(Class<P> presenter, Method method) {
        Class<?>[] interfaces = presenter.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            try {
                if (anInterface.getMethod(method.getName(), method.getParameterTypes()) != null)
                    return anInterface;
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }




}
