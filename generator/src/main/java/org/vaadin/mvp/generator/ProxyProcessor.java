package org.vaadin.mvp.generator;

import com.google.web.bindery.event.shared.EventBus;
import com.sun.codemodel.*;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.Navigator;
import org.vaadin.mvp.core.annotations.*;
import org.vaadin.mvp.core.annotations.qualifiers.MVP;
import org.vaadin.mvp.core.events.GetPlaceTitleEvent;
import org.vaadin.mvp.core.proxy.ProxyImpl;
import org.vaadin.mvp.core.proxy.ProxyPlaceImpl;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.enterprise.context.Dependent;
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
            proxyClass.annotate(Dependent.class);
            proxyClass = proxyClass._extends(jCodeModel.ref(ProxyImpl.class).narrow(jCodeModel.directClass(presenter.getQualifiedName().toString())));
            JMethod constructor = proxyClass.constructor(JMod.PUBLIC);
            constructor.annotate(Inject.class);
            constructor.param(EventBus.class, "eventBus").annotate(MVP.class);
            constructor.body().directStatement("super(" + presenter.getQualifiedName().toString() + ".class, eventBus);");
            String source = registerEventsHandler(presenter, proxyClass);
            if (source.length() > 0)
                constructor.body().directStatement(source);
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
            proxyPlaceClass.annotate(CDIView.class).param("value", place);
            proxyPlaceClass = proxyPlaceClass._extends(jCodeModel.ref(ProxyPlaceImpl.class).narrow(jCodeModel.directClass(presenterType.getQualifiedName().toString())));
            JMethod constructor = proxyPlaceClass.constructor(JMod.PUBLIC);
            constructor.annotate(Inject.class);
            constructor.param(jCodeModel.ref(proxy.fullName()), "proxy");
            constructor.param(Navigator.class, "navigator").annotate(MVP.class);
            if (presenterType.getAnnotation(NoGatekeeper.class) != null){
                constructor.body().directStatement("super(proxy, new org.vaadin.mvp.core.places.PlaceImpl(\""+place+"\"), navigator);");
            }else if (presenterType.getAnnotation(UseGatekeeper.class) != null)
                constructor.body().directStatement("super(proxy, new org.vaadin.mvp.core.places.PlaceWithGatekeeper(\""+place+"\", new "+presenterType.getAnnotation(UseGatekeeper.class).value().getCanonicalName()+"()), navigator);");
            else if (defaultGateKeeper != null)
                constructor.body().directStatement("super(proxy, new org.vaadin.mvp.core.places.PlaceWithGatekeeper(\""+place+"\", new "+defaultGateKeeper.getQualifiedName().toString()+"()), navigator);");
            else
                constructor.body().directStatement("super(proxy, new org.vaadin.mvp.core.places.PlaceImpl(\"" + place + "\"), navigator);");
            if (presenterType.getAnnotation(Title.class) != null){
                JMethod placeTitle = proxyPlaceClass.method(JMod.PROTECTED, void.class, "getPlaceTitle");
                placeTitle.param(GetPlaceTitleEvent.class, "event");
                placeTitle.body().directStatement("event.getHandler().onSetPlaceTitle(\"" + presenterType.getAnnotation(Title.class).value() + "\");");
            }
            List<? extends Element> methods = presenterType.getEnclosedElements();
            for (Element method : methods) {
                if (!(method instanceof ExecutableElement))
                    continue;
                if (method.getAnnotation(ContentSlot.class) == null)
                    continue;
                constructor.body().directStatement("proxy.getEventBus().addHandler("+presenterType.getQualifiedName().toString()+"."+method.getSimpleName()+"(), new org.vaadin.mvp.core.events.RevealContentHandler<"+presenterType.getQualifiedName().toString()+">(proxy.getEventBus(), proxy));");
            }
        return proxyPlaceClass;
    } catch (Exception e) {
        e.printStackTrace();
    }
        return null;
    }

    private String registerEventsHandler(TypeElement presenter, JDefinedClass proxyClass) {
        StringBuffer stringBuffer = new StringBuffer();
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
                    eventMethod.param(JMod.FINAL, jCodeModel.directClass(event), "event");
                    eventMethod.body().directStatement("getPresenter(new org.vaadin.mvp.core.events.NotifyingAsyncCallback<"+presenter.getQualifiedName().toString()+">(getEventBus()) {\n" +
                            "          @Override\n" +
                            "          protected void success("+presenter.getQualifiedName().toString()+" result) {\n" +
                            "              result."+method.getSimpleName().toString()+"(event);"+
                            "          }\n" +
                            "      });");
                    stringBuffer.append("getEventBus().addHandler(" + event + ".getType(), this);");
            }
        }
        return stringBuffer.toString();
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
