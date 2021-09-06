package test.types.dual;

import org.dockbox.hartshorn.di.annotations.inject.Bound;

import test.types.SampleInterface;

public class DualConstructableType implements SampleInterface {

    private final String name;

    @Bound
    public DualConstructableType(final String name) {
        this.name = name;
    }

    public DualConstructableType() {
        this.name = "DefaultConstructor";
    }

    @Override
    public String name() {
        return this.name;
    }
}
