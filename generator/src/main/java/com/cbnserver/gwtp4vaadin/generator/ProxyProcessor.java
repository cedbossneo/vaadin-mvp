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

package com.cbnserver.gwtp4vaadin.generator;

import com.cbnserver.gwtp4vaadin.core.MVPEventBus;
import com.cbnserver.gwtp4vaadin.core.TabData;
import com.cbnserver.gwtp4vaadin.core.TabDataBasic;
import com.cbnserver.gwtp4vaadin.core.annotations.*;
import com.cbnserver.gwtp4vaadin.core.proxy.*;
import com.sun.codemodel.*;
import com.vaadin.cdi.UIScoped;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 09/04/13
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
@SupportedAnnotationTypes({"com.cbnserver.gwtp4vaadin.core.annotations.ProxyStandard", "com.cbnserver.gwtp4vaadin.core.annotations.DefaultGatekeeper"})
public class ProxyProcessor extends AbstractProcessor {

    private final JCodeModel jCodeModel;
    private TypeElement defaultGateKeeper;
    private Messager messager;
    private JVar placesVar;
    private JMethod placesMethod;

    public ProxyProcessor() {
        jCodeModel = new JCodeModel();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            messager = processingEnv.getMessager();
            messager.printMessage(Diagnostic.Kind.NOTE, "ProxyProcessor");
            messager.printMessage(Diagnostic.Kind.NOTE, "Executing annotation processor for NameToken");
            Set<TypeElement> proxies = (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(ProxyStandard.class);
            Set<TypeElement> gateKeepers = (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(DefaultGatekeeper.class);
            messager.printMessage(Diagnostic.Kind.NOTE, "Found " + proxies.size() + " proxies");
            if (gateKeepers.size() == 1) {
                defaultGateKeeper = gateKeepers.iterator().next();
                messager.printMessage(Diagnostic.Kind.NOTE, "Found default gatekeeper");
            }
            if (proxies.size() > 0)
                createPlaceTokenRegistry();
            for (TypeElement proxy : proxies) {
                TypeElement presenterType = getProxyPresenter(proxy);
                if (presenterType == null)
                    continue;
                if (proxy.getAnnotation(NameToken.class) != null)
                    createPresenterProxyPlace(presenterType, proxy, createPresenterProxy(presenterType, proxy, false));
                else
                    createPresenterProxy(presenterType, proxy, true);
            }
            jCodeModel.build(new CodeWriter() {
                Stack<OutputStream> streams = new Stack<OutputStream>();

                @Override
                public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
                    return streams.push(processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, pkg.name(), fileName).openOutputStream());
                }

                @Override
                public void close() throws IOException {
                    while (!streams.empty())
                        streams.pop().close();
                }
            });
            messager.printMessage(Diagnostic.Kind.NOTE, "Annotation processor for PlaceToken done");
            ((Closeable)processingEnv.getFiler()).close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void createPlaceTokenRegistry() {
        if (jCodeModel._getClass("PlaceTokenRegistryImpl") != null)
            return;
        try {
            messager.printMessage(Diagnostic.Kind.NOTE, "Creating PlaceTokenRegistry");
            JDefinedClass placeTokenRegistry = jCodeModel._class("PlaceTokenRegistryImpl");
            placeTokenRegistry._implements(PlaceTokenRegistry.class);
            placeTokenRegistry.annotate(Singleton.class);
            JMethod placesTokensMethod = placeTokenRegistry.method(JMod.PUBLIC, jCodeModel.ref(Set.class).narrow(String.class), "getAllPlaceTokens");
            JClass hashSet = jCodeModel.ref(HashSet.class).narrow(String.class);
            JVar placesTokensVar = placesTokensMethod.body().decl(hashSet, "tokens").init(JExpr._new(hashSet));
            placesMethod = placeTokenRegistry.method(JMod.PRIVATE, void.class, "init");
            placesVar = placesMethod.param(hashSet, "tokens");
            placesTokensMethod.body().add(JExpr.invoke("init").arg(placesTokensVar));
            placesTokensMethod.body()._return(placesVar);
        } catch (JClassAlreadyExistsException e) {
            e.printStackTrace();
        }

    }

    private TypeElement getProxyPresenter(TypeElement proxyType) {
        for (TypeMirror typeMirror : proxyType.getInterfaces()) {
            if (!typeMirror.toString().startsWith(ProxyPlace.class.getCanonicalName())
                    && !typeMirror.toString().startsWith(TabContentProxyPlace.class.getCanonicalName())
                    && !typeMirror.toString().startsWith(NonLeafTabContentProxy.class.getCanonicalName())
                    && !typeMirror.toString().startsWith(Proxy.class.getCanonicalName()))
                continue;
            String presenterType = typeMirror.toString().substring(typeMirror.toString().indexOf('<')).replaceAll("<|>", "");
            return processingEnv.getElementUtils().getTypeElement(presenterType);
        }
        return null;
    }

    public JDefinedClass createPresenterProxy(TypeElement presenter, TypeElement proxyInterface, boolean implement) {
        try {
            messager.printMessage(Diagnostic.Kind.NOTE, "Generating " + presenter.getSimpleName().toString() + "ProxyImpl");
            JDefinedClass proxyClass = jCodeModel._package(((PackageElement) presenter.getEnclosingElement()).getQualifiedName().toString())._class(presenter.getSimpleName() + "ProxyImpl");
            proxyClass.annotate(UIScoped.class);
            JClass presenterClass = jCodeModel.directClass(presenter.getQualifiedName().toString());
            TabInfo tabInfo = proxyInterface.getAnnotation(TabInfo.class);
            ExecutableElement presenterTabInfo = findTabInfo(presenter);
            if (presenterTabInfo != null)
                tabInfo = presenterTabInfo.getAnnotation(TabInfo.class);
            JClass iface = jCodeModel.directClass(proxyInterface.getQualifiedName().toString());
            if (tabInfo != null) {
                proxyClass._extends(jCodeModel.ref(NonLeafTabContentProxyImpl.class).narrow(presenterClass));
            }else
                proxyClass._extends(jCodeModel.ref(ProxyImpl.class).narrow(presenterClass));
            if (implement)
                proxyClass._implements(iface);
            proxyClass.method(JMod.PUBLIC, jCodeModel.ref(Class.class).narrow(presenterClass), "getPresenter").body()._return(JExpr.dotclass(presenterClass));
            JMethod constructor = proxyClass.constructor(JMod.PUBLIC);
            constructor.annotate(Inject.class);
            JVar eventBusParameter = constructor.param(MVPEventBus.class, "eventBus");
            JVar beanManagerParameter = constructor.param(BeanManager.class, "beanManager");
            constructor.body().invoke("super").arg(beanManagerParameter).arg(eventBusParameter);
            if (tabInfo != null){
                if (presenterTabInfo != null && presenterTabInfo.getReturnType().toString().equals(String.class.getCanonicalName()))
                    constructor.body().assign(JExpr.refthis("tabData"), JExpr._new(jCodeModel.ref(TabDataBasic.class)).arg(jCodeModel.ref(presenter.getQualifiedName().toString()).staticInvoke(presenterTabInfo.getSimpleName().toString())).arg(JExpr.lit(tabInfo.priority())));
                else if (presenterTabInfo != null && presenterTabInfo.getReturnType().toString().equals(TabData.class.getCanonicalName()))
                    constructor.body().assign(JExpr.refthis("tabData"), jCodeModel.ref(presenter.getQualifiedName().toString()).staticInvoke(presenterTabInfo.getSimpleName().toString()));
                else
                    constructor.body().assign(JExpr.refthis("tabData"), JExpr._new(jCodeModel.ref(TabDataBasic.class)).arg(tabInfo.label()).arg(JExpr.lit(tabInfo.priority())));
                constructor.body().assign(JExpr.refthis("targetHistoryToken"), JExpr.lit(tabInfo.nameToken()));
                try {
                    //BIG Hack to retrieve annotation class
                    tabInfo.container();
                } catch (MirroredTypeException e) {
                    TypeElement ref = processingEnv.getElementUtils().getTypeElement(((TypeElement) processingEnv.getTypeUtils().asElement(e.getTypeMirror())).getQualifiedName().toString());
                    constructor.body().assign(JExpr.refthis("requestTabsEventType"), findRequestTab(ref));
                    constructor.body().assign(JExpr.refthis("changeTabEventType"), findChangeTab(ref));
                }
                constructor.body().add(JExpr.invoke("addRequestTabsHandler"));
            }
            processEventsHandler(constructor.body(), presenter, presenterClass, proxyClass);
            processContentSlot(presenter, presenterClass, constructor);
            messager.printMessage(Diagnostic.Kind.NOTE, "Generated " + presenter.getSimpleName().toString() + "ProxyImpl");
            return proxyClass;
        } catch (JClassAlreadyExistsException e) {
            e.printStackTrace();
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "Error while generating " + presenter.getSimpleName().toString() + "ProxyImpl");
        return null;
    }

    private JExpression findChangeTab(TypeElement container) {
        List<? extends Element> fields = container.getEnclosedElements();
        for (Element field : fields) {
            if (!(field instanceof VariableElement))
                continue;
            if (field.getAnnotation(ChangeTab.class) != null)
                return jCodeModel.ref(container.getQualifiedName().toString()).staticRef(field.getSimpleName().toString());
        }
        return null;
    }

    private JExpression findRequestTab(TypeElement container) {
        List<? extends Element> fields = container.getEnclosedElements();
        for (Element field : fields) {
            if (!(field instanceof VariableElement))
                continue;
            if (field.getAnnotation(RequestTabs.class) != null)
                return jCodeModel.ref(container.getQualifiedName().toString()).staticRef(field.getSimpleName().toString());
        }
        return null;
    }

    private ExecutableElement findTabInfo(TypeElement container) {
        List<? extends Element> fields = container.getEnclosedElements();
        for (Element method : fields) {
            if (!(method instanceof ExecutableElement))
                continue;
            if (method.getAnnotation(TabInfo.class) != null)
                return (ExecutableElement)method;
        }
        return null;
    }
    public JDefinedClass createPresenterProxyPlace(TypeElement presenter, TypeElement proxyPlaceType, JDefinedClass presenterProxy) {
        try {
            messager.printMessage(Diagnostic.Kind.NOTE, "Generating " + presenter.getSimpleName().toString() + "ProxyPlaceImpl");
            String place = proxyPlaceType.getAnnotation(NameToken.class).value();
            placesMethod.body().add(placesVar.invoke("add").arg(place));
            JDefinedClass proxyPlaceClass = jCodeModel._package(((PackageElement) presenter.getEnclosingElement()).getQualifiedName().toString())._class(presenter.getSimpleName() + "ProxyPlaceImpl");
            proxyPlaceClass.annotate(UIScoped.class);
            JClass presenterClass = jCodeModel.directClass(presenter.getQualifiedName().toString());
            if (proxyPlaceType.getAnnotation(TabInfo.class) != null || findTabInfo(presenter) != null)
                proxyPlaceClass._extends(jCodeModel.ref(TabContentProxyPlaceImpl.class).narrow(presenterClass));
            else
                proxyPlaceClass._extends(jCodeModel.ref(ProxyPlaceImpl.class).narrow(presenterClass));
            proxyPlaceClass._implements(jCodeModel.directClass(proxyPlaceType.getQualifiedName().toString()));
            JMethod constructor = proxyPlaceClass.constructor(JMod.PUBLIC);
            constructor.annotate(Inject.class);
            JVar proxyParam = constructor.param(presenterProxy, "proxy");
            JVar eventBusParam = constructor.param(MVPEventBus.class, "eventBus");
            JVar placeManagerParam = constructor.param(PlaceManager.class, "placeManager");
            constructor.body().invoke("super").arg(eventBusParam);
            constructor.body().add(JExpr.invoke("setProxy").arg(proxyParam));
            constructor.body().add(JExpr.invoke("setPlaceManager").arg(placeManagerParam));
            processGateKeeper(proxyPlaceType, place, constructor);
            processTitle(presenter, proxyPlaceClass);
            messager.printMessage(Diagnostic.Kind.NOTE, "Generated " + presenter.getSimpleName().toString() + "ProxyPlaceImpl");
            return proxyPlaceClass;
        } catch (Exception e) {
            e.printStackTrace();
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "Error while generating " + presenter.getSimpleName().toString() + "ProxyPlaceImpl");
        return null;
    }

    private void processContentSlot(TypeElement presenterType, JClass presenterJClass, JMethod constructor) {
        List<? extends Element> fields = presenterType.getEnclosedElements();
        for (Element field : fields) {
            if (!(field instanceof VariableElement))
                continue;
            if (field.getAnnotation(ContentSlot.class) == null)
                continue;
            constructor.body().add(JExpr.invoke("getEventBus").invoke("addHandler").arg(presenterJClass.staticRef(field.getSimpleName().toString())).arg(JExpr._new(jCodeModel.ref(RevealContentHandler.class).narrow(presenterJClass)).arg(JExpr.invoke("getEventBus")).arg(JExpr._this())));
        }
    }

    private void processTitle(TypeElement presenterType, JDefinedClass proxyPlaceClass) {
        if (presenterType.getAnnotation(Title.class) != null) {
            JMethod placeTitle = proxyPlaceClass.method(JMod.PROTECTED, void.class, "getPlaceTitle");
            JVar eventParam = placeTitle.param(GetPlaceTitleEvent.class, "event");
            placeTitle.body().invoke(eventParam, "getHandler").invoke("onSetPlaceTitle").arg(presenterType.getAnnotation(Title.class).value());
        }
    }

    private void processGateKeeper(TypeElement proxyPlaceType, String place, JMethod constructor) {
        if (proxyPlaceType.getAnnotation(NoGatekeeper.class) != null)
            constructor.body().add(JExpr.invoke("setPlace").arg(JExpr._new(jCodeModel.ref(PlaceImpl.class)).arg(place)));
        else {
            UseGatekeeper useGatekeeper = proxyPlaceType.getAnnotation(UseGatekeeper.class);
            if ((useGatekeeper != null) || defaultGateKeeper != null) {
                JInvocation placeWithGateKeeper;
                JClass gateKeeperRef = null;
                if (useGatekeeper != null) {
                    try {
                        //BIG Hack to retrieve annotation class
                        useGatekeeper.value();
                    } catch (MirroredTypeException e) {
                        gateKeeperRef = jCodeModel.ref(((TypeElement) processingEnv.getTypeUtils().asElement(e.getTypeMirror())).getQualifiedName().toString());
                    }
                } else {
                    gateKeeperRef = jCodeModel.ref(defaultGateKeeper.getQualifiedName().toString());
                }
                if (proxyPlaceType.getAnnotation(GatekeeperParams.class) != null) {
                    String[] value = proxyPlaceType.getAnnotation(GatekeeperParams.class).value();
                    JArray array = JExpr.newArray(jCodeModel._ref(String.class));
                    for (String s : value) {
                        array.add(JExpr.lit(s));
                    }
                    placeWithGateKeeper = JExpr._new(jCodeModel.ref(PlaceWithGatekeeperWithParams.class)).arg(place).arg(JExpr._new(gateKeeperRef)).arg(array);
                } else
                    placeWithGateKeeper = JExpr._new(jCodeModel.ref(PlaceWithGatekeeper.class)).arg(place).arg(JExpr._new(gateKeeperRef));
                constructor.body().add(JExpr.invoke("setPlace").arg(placeWithGateKeeper));
            } else
                constructor.body().add(JExpr.invoke("setPlace").arg(JExpr._new(jCodeModel.ref(PlaceImpl.class)).arg(place)));
        }
    }

    private void processEventsHandler(JBlock constructoryBody, TypeElement presenter, JClass presenterClass, JDefinedClass proxyClass) {
        List<? extends Element> methods = presenter.getEnclosedElements();
        for (Element methodElement : methods) {
            if (!(methodElement instanceof ExecutableElement))
                continue;
            ExecutableElement method = (ExecutableElement) methodElement;
            if (method.getAnnotation(ProxyEvent.class) != null) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Found ProxyEvent on method " + method.getSimpleName().toString());
                VariableElement firstParameter = method.getParameters().iterator().next();
                TypeElement handler = getHandler(presenter, method);
                proxyClass._implements(jCodeModel.directClass(handler.getQualifiedName().toString()));
                String event = ((TypeElement) processingEnv.getTypeUtils().asElement(firstParameter.asType())).getQualifiedName().toString();
                JMethod eventMethod = proxyClass.method(JMod.PUBLIC, void.class, method.getSimpleName().toString());
                JVar eventParam = eventMethod.param(JMod.FINAL, jCodeModel.directClass(event), "event");
                JDefinedClass innerClass = jCodeModel.anonymousClass(jCodeModel.ref(NotifyingAsyncCallback.class).narrow(presenterClass));
                JMethod successMethod = innerClass.method(JMod.PROTECTED, void.class, "success");
                JVar resultParam = successMethod.param(presenterClass, "result");
                successMethod.body().invoke(resultParam, method.getSimpleName().toString()).arg(eventParam);
                eventMethod.body().invoke("getPresenter").arg(JExpr._new(innerClass).arg(JExpr.invoke("getEventBus")));
                constructoryBody.add(JExpr.invoke("getEventBus").invoke("addHandler").arg(jCodeModel.directClass(event).staticInvoke("getType")).arg(JExpr._this()));
            }
        }
    }

    private TypeElement getHandler(TypeElement presenter, ExecutableElement method) {
        List<? extends TypeMirror> interfaces = presenter.getInterfaces();

        messager.printMessage(Diagnostic.Kind.NOTE, "Search for method " + method.getSimpleName().toString() + " in interfaces");
        for (TypeMirror anInterface : interfaces) {
            TypeElement interfaceElement = (TypeElement) processingEnv.getTypeUtils().asElement(anInterface);
            messager.printMessage(Diagnostic.Kind.NOTE, "Found interface " + interfaceElement.getQualifiedName().toString());
            for (Element element : interfaceElement.getEnclosedElements()) {
                if (!(element instanceof ExecutableElement))
                    continue;
                messager.printMessage(Diagnostic.Kind.NOTE, "Found method " + element.getSimpleName().toString());
                if (!element.getSimpleName().toString().equals(method.getSimpleName().toString()))
                    continue;
                return interfaceElement;
            }
        }
        return null;
    }

}
