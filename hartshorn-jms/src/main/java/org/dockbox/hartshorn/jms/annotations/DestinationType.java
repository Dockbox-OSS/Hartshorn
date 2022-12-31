package org.dockbox.hartshorn.jms.annotations;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;

public enum DestinationType {
    QUEUE(Queue.class),
    TOPIC(Topic.class),
    ;

    private final Class<? extends Destination> destinationClass;

    DestinationType(final Class<? extends Destination> destinationClass) {
        this.destinationClass = destinationClass;
    }

    public Class<? extends Destination> destinationClass() {
        return this.destinationClass;
    }
}
