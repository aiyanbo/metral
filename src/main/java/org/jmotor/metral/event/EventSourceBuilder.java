package org.jmotor.metral.event;

/**
 * @author AI
 * 2019-03-22
 */
public interface EventSourceBuilder {

    public EventSource build(Object[] arguments, Object returnObject);

}
