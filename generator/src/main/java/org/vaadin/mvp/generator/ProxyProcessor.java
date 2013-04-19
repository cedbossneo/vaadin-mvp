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

package org.vaadin.mvp.generator;

import com.sun.codemodel.*;
import com.vaadin.cdi.UIScoped;
import org.vaadin.mvp.core.MVPEventBus;
import org.vaadin.mvp.core.annotations.*;
import org.vaadin.mvp.core.proxy.*;

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
import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 09/04/13
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
@SupportedAnnotationTypes("org.vaadin.mvp.core.annotations.NameToken")
public class ProxyProcessor extends AbstractProcessor {

    private final Map<String, JDefinedClass> proxies = new HashMap<String, JDefinedClass>();
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
            File outDir = new File(processingEnv.getFiler().getResource(StandardLocation.SOURCE_OUTPUT, "", "mvp.xml").toUri().getPath());
            messager.printMessage(Diagnostic.Kind.NOTE, "ProxyProcessor - Output directory is : " + outDir.getParent().toString());
            messager.printMessage(Diagnostic.Kind.NOTE, "Creating PlaceTokenRegistry");
            createPlaceTokenRegistry();
            messager.printMessage(Diagnostic.Kind.NOTE, "Executing annotation processor for NameToken");
            Set<TypeElement> proxies = (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(NameToken.class);
            Set<TypeElement> gateKeepers = (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(DefaultGatekeeper.class);
            messager.printMessage(Diagnostic.Kind.NOTE, "Found " + proxies.size() + " proxies");
            if (gateKeepers.size() == 1) {
                defaultGateKeeper = gateKeepers.iterator().next();
                messager.printMessage(Diagnostic.Kind.NOTE, "Found default gatekeeper");
            }
            for (TypeElement proxyType : proxies) {
                TypeElement presenterType = getProxyPresenter(proxyType);
                if (presenterType == null)
                    continue;
                messager.printMessage(Diagnostic.Kind.NOTE, "Generating " + presenterType.getSimpleName().toString() + "ProxyImpl");
                JDefinedClass proxy = createPresenterProxy(presenterType, proxyType);
                createPresenterProxyPlace(presenterType, proxyType, proxy);
                messager.printMessage(Diagnostic.Kind.NOTE, "Generating " + presenterType.getSimpleName().toString() + "ProxyImpl done");
            }
            jCodeModel.build(outDir.getParentFile());
            messager.printMessage(Diagnostic.Kind.NOTE, "Annotation processor for PlaceToken done");
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
            if (!typeMirror.toString().startsWith(Proxy.class.getCanonicalName()))
                continue;
            String presenterType = typeMirror.toString().substring(typeMirror.toString().indexOf('<')).replaceAll("<|>", "");
            return processingEnv.getElementUtils().getTypeElement(presenterType);
        }
        return null;
    }

    public JDefinedClass createPresenterProxy(TypeElement presenter, TypeElement proxyInterface) {
        if (proxies.containsKey(presenter.getQualifiedName().toString())) //Check in map
            return proxies.get(presenter.getQualifiedName().toString());
        try {
            JDefinedClass proxyClass = jCodeModel._package(((PackageElement) presenter.getEnclosingElement()).getQualifiedName().toString())._class(presenter.getSimpleName() + "ProxyImpl");
            proxyClass.annotate(UIScoped.class);
            JClass presenterClass = jCodeModel.directClass(presenter.getQualifiedName().toString());
            proxyClass._extends(jCodeModel.ref(ProxyImpl.class).narrow(presenterClass));
            proxyClass._implements(jCodeModel.directClass(proxyInterface.getQualifiedName().toString()));
            proxyClass.method(JMod.PUBLIC, jCodeModel.ref(Class.class).narrow(presenterClass), "getPresenter").body()._return(JExpr.dotclass(presenterClass));
            JMethod constructor = proxyClass.constructor(JMod.PUBLIC);
            constructor.annotate(Inject.class);
            JVar eventBusParameter = constructor.param(MVPEventBus.class, "eventBus");
            JVar beanManagerParameter = constructor.param(BeanManager.class, "beanManager");
            constructor.body().invoke("super").arg(beanManagerParameter).arg(eventBusParameter);
            registerEventsHandler(constructor.body(), presenter, presenterClass, proxyClass);
            proxies.put(presenter.getQualifiedName().toString(), proxyClass);
            return proxyClass;
        } catch (JClassAlreadyExistsException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JDefinedClass createPresenterProxyPlace(TypeElement presenterType, TypeElement proxyType, JDefinedClass proxy) {
        try {
            String place = proxyType.getAnnotation(NameToken.class).value();
            placesMethod.body().add(placesVar.invoke("add").arg(place));
            JDefinedClass proxyPlaceClass = jCodeModel._package(((PackageElement) presenterType.getEnclosingElement()).getQualifiedName().toString())._class(presenterType.getSimpleName() + "ProxyPlaceImpl");
            proxyPlaceClass.annotate(UIScoped.class);
            JClass presenterJClass = jCodeModel.directClass(presenterType.getQualifiedName().toString());
            proxyPlaceClass._extends(jCodeModel.ref(ProxyPlaceImpl.class).narrow(presenterJClass));
            JMethod constructor = proxyPlaceClass.constructor(JMod.PUBLIC);
            constructor.annotate(Inject.class);
            JVar proxyParam = constructor.param(jCodeModel.ref(proxy.fullName()), "proxy");
            JVar placeManagerParam = constructor.param(PlaceManager.class, "placeManager");
            constructor.body().invoke("super").arg(proxyParam.invoke("getEventBus"));
            constructor.body().add(JExpr.invoke("setProxy").arg(proxyParam));
            constructor.body().add(JExpr.invoke("setPlaceManager").arg(placeManagerParam));
            if (presenterType.getAnnotation(NoGatekeeper.class) != null)
                constructor.body().add(JExpr.invoke("setPlace").arg(JExpr._new(jCodeModel.ref(PlaceImpl.class)).arg(place)));
            else {
                UseGatekeeper useGatekeeper = presenterType.getAnnotation(UseGatekeeper.class);
                if ((useGatekeeper != null) || defaultGateKeeper != null){
                    JInvocation placeWithGateKeeper;
                    JClass gateKeeperRef = null;
                    if (useGatekeeper != null){
                        try{
                            //BIG Hack to retrieve annotation class
                            useGatekeeper.value();
                        }catch (MirroredTypeException e){
                            gateKeeperRef = jCodeModel.ref(((TypeElement)processingEnv.getTypeUtils().asElement(e.getTypeMirror())).getQualifiedName().toString());
                        }
                    }else{
                        gateKeeperRef = jCodeModel.ref(defaultGateKeeper.getQualifiedName().toString());
                    }
                    if (presenterType.getAnnotation(GatekeeperParams.class) != null){
                        String[] value = presenterType.getAnnotation(GatekeeperParams.class).value();
                        JArray array = JExpr.newArray(jCodeModel._ref(String.class));
                        for (String s : value) {
                            array.add(JExpr.lit(s));
                        }
                        placeWithGateKeeper = JExpr._new(jCodeModel.ref(PlaceWithGatekeeperWithParams.class)).arg(place).arg(JExpr._new(gateKeeperRef)).arg(array);
                    }else
                        placeWithGateKeeper = JExpr._new(jCodeModel.ref(PlaceWithGatekeeper.class)).arg(place).arg(JExpr._new(gateKeeperRef));
                    constructor.body().add(JExpr.invoke("setPlace").arg(placeWithGateKeeper));
                }
                else
                    constructor.body().add(JExpr.invoke("setPlace").arg(JExpr._new(jCodeModel.ref(PlaceImpl.class)).arg(place)));
            }
            if (presenterType.getAnnotation(Title.class) != null) {
                JMethod placeTitle = proxyPlaceClass.method(JMod.PROTECTED, void.class, "getPlaceTitle");
                JVar eventParam = placeTitle.param(GetPlaceTitleEvent.class, "event");
                placeTitle.body().invoke(eventParam, "getHandler").invoke("onSetPlaceTitle").arg(presenterType.getAnnotation(Title.class).value());
            }
            List<? extends Element> fields = presenterType.getEnclosedElements();
            for (Element field : fields) {
                if (!(field instanceof VariableElement))
                    continue;
                if (field.getAnnotation(ContentSlot.class) == null)
                    continue;
                constructor.body().add(JExpr.invoke(proxyParam, "getEventBus").invoke("addHandler").arg(presenterJClass.staticRef(field.getSimpleName().toString())).arg(JExpr._new(jCodeModel.ref(RevealContentHandler.class).narrow(presenterJClass)).arg(proxyParam.invoke("getEventBus")).arg(proxyParam)));
            }
            return proxyPlaceClass;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void registerEventsHandler(JBlock constructoryBody, TypeElement presenter, JClass presenterClass, JDefinedClass proxyClass) {
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
