package org.vaadin.mvp.generator;

import com.sun.codemodel.*;
import com.vaadin.cdi.UIScoped;
import org.vaadin.mvp.core.MVPEventBus;
import org.vaadin.mvp.core.MVPNavigator;
import org.vaadin.mvp.core.annotations.*;
import org.vaadin.mvp.core.events.GetPlaceTitleEvent;
import org.vaadin.mvp.core.events.NotifyingAsyncCallback;
import org.vaadin.mvp.core.events.RevealContentHandler;
import org.vaadin.mvp.core.places.PlaceImpl;
import org.vaadin.mvp.core.places.PlaceWithGatekeeper;
import org.vaadin.mvp.core.proxy.ProxyImpl;
import org.vaadin.mvp.core.proxy.ProxyPlaceImpl;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.inject.Inject;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 09/04/13
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
@SupportedAnnotationTypes("org.vaadin.mvp.core.annotations.PlaceToken")
public class ProxyProcessor extends AbstractProcessor {

    private final Map<String, JDefinedClass> proxies = new HashMap<String, JDefinedClass>();
    private final JCodeModel jCodeModel;
    private TypeElement defaultGateKeeper;
    private Messager messager;

    public ProxyProcessor() {
        jCodeModel = new JCodeModel();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            messager = processingEnv.getMessager();
            File outDir = new File(processingEnv.getFiler().getResource(StandardLocation.SOURCE_OUTPUT, "", "mvp.xml").toUri().getPath());
            messager.printMessage(Diagnostic.Kind.NOTE, "ProxyProcessor - Output directory is : " + outDir);
            messager.printMessage(Diagnostic.Kind.NOTE, "Executing annotation processor for PlaceToken");
            Set<TypeElement> presenters = (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(PlaceToken.class);
            Set<TypeElement> gateKeepers = (Set<TypeElement>) roundEnv.getElementsAnnotatedWith(DefaultGatekeeper.class);
            messager.printMessage(Diagnostic.Kind.NOTE, "Found " + presenters.size() + " presenters");
            if (gateKeepers.size() == 1){
                defaultGateKeeper = gateKeepers.iterator().next();
                messager.printMessage(Diagnostic.Kind.NOTE, "Found default gatekeeper");
            }
            for (TypeElement presenterType : presenters) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Generating " + presenterType.getSimpleName().toString() + "Proxy");
                JDefinedClass proxy = createPresenterProxy(presenterType);
                createPresenterProxyPlace(presenterType, proxy);
                messager.printMessage(Diagnostic.Kind.NOTE, "Generating " + presenterType.getSimpleName().toString() + "Proxy done");
            }
            jCodeModel.build(outDir.getParentFile());
            messager.printMessage(Diagnostic.Kind.NOTE, "Annotation processor for PlaceToken done");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public JDefinedClass createPresenterProxy(TypeElement presenter) {
        if (proxies.containsKey(presenter)) //Check in map
            return proxies.get(presenter);
        try {
            JDefinedClass proxyClass = jCodeModel._package(((PackageElement)presenter.getEnclosingElement()).getQualifiedName().toString())._class(presenter.getSimpleName() + "Proxy");
            proxyClass.annotate(UIScoped.class);
            JClass presenterClass = jCodeModel.directClass(presenter.getQualifiedName().toString());
            proxyClass = proxyClass._extends(jCodeModel.ref(ProxyImpl.class).narrow(presenterClass));
            proxyClass.method(JMod.PUBLIC, jCodeModel.ref(Class.class).narrow(presenterClass), "getPresenter").body()._return(JExpr.dotclass(presenterClass));
            JMethod constructor = proxyClass.constructor(JMod.PUBLIC);
            constructor.annotate(Inject.class);
            JVar eventBusParameter = constructor.param(MVPEventBus.class, "eventBus");
            constructor.body().invoke("super").arg(eventBusParameter);
            registerEventsHandler(constructor.body(), presenter, presenterClass, proxyClass);
            proxies.put(presenter.getQualifiedName().toString(), proxyClass);
            return proxyClass;
        } catch (JClassAlreadyExistsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public JDefinedClass createPresenterProxyPlace(TypeElement presenterType, JDefinedClass proxy) {
        try{
            String place = presenterType.getAnnotation(PlaceToken.class).value();
            JDefinedClass proxyPlaceClass = jCodeModel._package(((PackageElement) presenterType.getEnclosingElement()).getQualifiedName().toString())._class(presenterType.getSimpleName() + "ProxyPlace");
            proxyPlaceClass.annotate(UIScoped.class);
            JClass presenterJClass = jCodeModel.directClass(presenterType.getQualifiedName().toString());
            proxyPlaceClass = proxyPlaceClass._extends(jCodeModel.ref(ProxyPlaceImpl.class).narrow(presenterJClass));
            JMethod constructor = proxyPlaceClass.constructor(JMod.PUBLIC);
            constructor.annotate(Inject.class);
            JVar proxyParam = constructor.param(jCodeModel.ref(proxy.fullName()), "proxy");
            JVar navigatorParam = constructor.param(MVPNavigator.class, "navigator");
            if (presenterType.getAnnotation(NoGatekeeper.class) != null){
                constructor.body().invoke("super").arg(proxyParam).arg(JExpr._new(jCodeModel.ref(PlaceImpl.class)).arg(place)).arg(navigatorParam);
            }else if (presenterType.getAnnotation(UseGatekeeper.class) != null)
                constructor.body().invoke("super").arg(proxyParam).arg(JExpr._new(jCodeModel.ref(PlaceWithGatekeeper.class)).arg(place).arg(JExpr._new(jCodeModel.ref(presenterType.getAnnotation(UseGatekeeper.class).value())))).arg(navigatorParam);
            else if (defaultGateKeeper != null)
                constructor.body().invoke("super").arg(proxyParam).arg(JExpr._new(jCodeModel.ref(PlaceWithGatekeeper.class)).arg(place).arg(JExpr._new(jCodeModel.ref(defaultGateKeeper.getQualifiedName().toString())))).arg(navigatorParam);
            else
                constructor.body().invoke("super").arg(proxyParam).arg(JExpr._new(jCodeModel.ref(PlaceImpl.class)).arg(place)).arg(navigatorParam);
            if (presenterType.getAnnotation(Title.class) != null){
                JMethod placeTitle = proxyPlaceClass.method(JMod.PROTECTED, void.class, "getPlaceTitle");
                JVar eventParam = placeTitle.param(GetPlaceTitleEvent.class, "event");
                placeTitle.body().invoke(eventParam, "getHandler").invoke("onSetPlaceTitle").arg(presenterType.getAnnotation(Title.class).value());
            }
            List<? extends Element> methods = presenterType.getEnclosedElements();
            for (Element method : methods) {
                if (!(method instanceof ExecutableElement))
                    continue;
                if (method.getAnnotation(ContentSlot.class) == null)
                    continue;
                constructor.body().add(JExpr.invoke(proxyParam, "getEventBus").invoke("addHandler").arg(presenterJClass.staticInvoke(method.getSimpleName().toString())).arg(JExpr._new(jCodeModel.ref(RevealContentHandler.class).narrow(presenterJClass)).arg(proxyParam.invoke("getEventBus")).arg(proxyParam)));
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
            if (method.getAnnotation(ProxyEvent.class) != null){
                    messager.printMessage(Diagnostic.Kind.NOTE, "Found ProxyEvent on method " + method.getSimpleName().toString());
                    VariableElement firstParameter = method.getParameters().iterator().next();
                    TypeElement handler = getHandler(presenter, method);
                    proxyClass._implements(jCodeModel.directClass(handler.getQualifiedName().toString()));
                    String event = ((TypeElement)processingEnv.getTypeUtils().asElement(firstParameter.asType())).getQualifiedName().toString();
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
