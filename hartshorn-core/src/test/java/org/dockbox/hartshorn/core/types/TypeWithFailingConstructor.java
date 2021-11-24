package org.dockbox.hartshorn.core.types;

public class TypeWithFailingConstructor {

    public TypeWithFailingConstructor() {
        throw new IllegalStateException("This type cannot be instantiated");
    }
}
