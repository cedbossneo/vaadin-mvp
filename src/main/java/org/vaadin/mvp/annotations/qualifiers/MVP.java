package org.vaadin.mvp.annotations.qualifiers;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Created with IntelliJ IDEA.
 * User: cedric
 * Date: 22/01/13
 * Time: 18:00
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER, CONSTRUCTOR})
public @interface MVP {
}
