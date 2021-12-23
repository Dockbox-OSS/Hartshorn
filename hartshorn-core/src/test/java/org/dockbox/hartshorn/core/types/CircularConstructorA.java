package org.dockbox.hartshorn.core.types;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;

@Singleton
public class CircularConstructorA {

    @Getter
    private final CircularConstructorB b;

    @Inject
    public CircularConstructorA(final CircularConstructorB b) {
        this.b = b;
    }
}
