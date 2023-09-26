package test.org.dockbox.hartshorn.components;

public class ImplicitCircularDependencyA implements InterfaceCircularDependencyA {

    private final InterfaceCircularDependencyB dependencyB;

    public ImplicitCircularDependencyA(InterfaceCircularDependencyB dependencyB) {
        this.dependencyB = dependencyB;
    }

    public InterfaceCircularDependencyB dependencyB() {
        return dependencyB;
    }
}
