package org.dockbox.hartshorn.di.context.element;

public class WildcardTypeContext extends TypeContext<Void> {

    private WildcardTypeContext() {
        super(Void.class);
    }

    public static WildcardTypeContext create() {
        return new WildcardTypeContext();
    }

    @Override
    public boolean childOf(final Class<?> to) {
        return true;
    }

    @Override
    public String toString() {
        return "TypeContext{*}";
    }
}
