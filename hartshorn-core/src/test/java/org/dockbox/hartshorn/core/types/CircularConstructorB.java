package org.dockbox.hartshorn.core.types;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;

@Singleton
public class CircularConstructorB {

    @Getter
    private final CircularConstructorA a;

    @Inject
    public CircularConstructorB(final CircularConstructorA a) {
        this.a = a;
    }
}
