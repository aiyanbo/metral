package org.jmotor.metral.annotaion;

import org.jmotor.metral.dto.Operation;
import org.jmotor.metral.event.EventSourceBuilder;

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

    Operation operation();

    Class<? extends EventSourceBuilder> builder();

}

