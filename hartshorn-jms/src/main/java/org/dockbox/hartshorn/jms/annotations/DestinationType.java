package org.dockbox.hartshorn.jms.annotations;

import org.dockbox.hartshorn.inject.Key;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;

public enum DestinationType {
    QUEUE(Key.of(Queue.class)),
    TOPIC(Key.of(Topic.class)),
    ;

    private final Key<? extends Destination> key;

    DestinationType(final Key<? extends Destination> key) {
        this.key = key;
    }

    public Key<? extends Destination> key() {
        return this.key;
    }
}
