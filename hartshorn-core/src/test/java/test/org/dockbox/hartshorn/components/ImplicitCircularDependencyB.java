package test.org.dockbox.hartshorn.components;

public class ImplicitCircularDependencyB implements InterfaceCircularDependencyB {

    private final InterfaceCircularDependencyA dependencyA;

    public ImplicitCircularDependencyB(InterfaceCircularDependencyA dependencyA) {
        this.dependencyA = dependencyA;
    }

    public InterfaceCircularDependencyA dependencyA() {
        return dependencyA;
    }
}
