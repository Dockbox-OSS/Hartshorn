package test.org.dockbox.hartshorn.components;

import jakarta.inject.Inject;

public class BoundCircularDependencyB implements InterfaceCircularDependencyB {

    private final BoundCircularDependencyA dependencyA;

    @Inject
    public BoundCircularDependencyB(BoundCircularDependencyA dependencyA) {
        this.dependencyA = dependencyA;
    }

    public BoundCircularDependencyA dependencyA() {
        return dependencyA;
    }
}
