package org.jmotor.metral.translator;

import java.util.Objects;

/**
 * Component:
 * Description:
 * Date: 2018/11/13
 *
 * @author AI
 */
public class FirstArgumentIdentity implements IdentityTranslator {

    @Override
    public String translate(Object[] args) {
        Object arg = args[0];
        if (arg instanceof String) {
            return (String) arg;
        }
        return Objects.toString(arg);
    }
}
