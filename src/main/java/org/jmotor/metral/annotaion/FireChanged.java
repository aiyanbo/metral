package org.jmotor.metral.annotaion;

import org.jmotor.metral.dto.Operation;
import org.jmotor.metral.translator.FirstArgumentIdentity;
import org.jmotor.metral.translator.IdentityTranslator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Component:
 * Description:
 * Date: 2018/11/12
 *
 * @author AI
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FireChanged {

    String entity();

    int identityParameterIndex() default -1;

    Operation operation();

    String[] properties() default {};

    Class<? extends IdentityTranslator> returnTranslator() default FirstArgumentIdentity.class;

    Class<? extends IdentityTranslator> parameterTranslator() default FirstArgumentIdentity.class;

}

