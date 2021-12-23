package org.dockbox.hartshorn.core.types;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;

@Singleton
public class CircularDependencyB {
    @Inject
    @Getter
    private CircularDependencyA a;
}
