package test.org.dockbox.hartshorn.components;

import jakarta.inject.Inject;

public class BoundCircularDependencyA implements InterfaceCircularDependencyA {

    private final BoundCircularDependencyB dependencyB;

    @Inject
    public BoundCircularDependencyA(BoundCircularDependencyB dependencyB) {
        this.dependencyB = dependencyB;
    }

    public BoundCircularDependencyB dependencyB() {
        return dependencyB;
    }
}
