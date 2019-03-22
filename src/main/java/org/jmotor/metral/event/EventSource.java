package org.jmotor.metral.event;

import java.util.Map;

/**
 * @author AI
 * 2019-03-22
 */
public class EventSource {

    private byte[] identity;
    private Map<String, byte[]> attributes;

    public byte[] getIdentity() {
        return identity;
    }

    public EventSource setIdentity(byte[] identity) {
        this.identity = identity;
        return this;
    }

    public Map<String, byte[]> getAttributes() {
        return attributes;
    }

    public EventSource setAttributes(Map<String, byte[]> attributes) {
        this.attributes = attributes;
        return this;
    }

    public static EventSource build(byte[] identity) {
        return new EventSource().setIdentity(identity);
    }

}
