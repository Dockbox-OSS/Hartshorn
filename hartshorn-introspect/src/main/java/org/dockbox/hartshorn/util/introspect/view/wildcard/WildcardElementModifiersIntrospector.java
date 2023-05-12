package org.dockbox.hartshorn.util.introspect.view.wildcard;

import org.dockbox.hartshorn.util.introspect.AccessModifier;
import org.dockbox.hartshorn.util.introspect.ElementModifiersIntrospector;

public class WildcardElementModifiersIntrospector implements ElementModifiersIntrospector {

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public boolean has(final AccessModifier modifier) {
        return false;
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean isVolatile() {
        return false;
    }

    @Override
    public boolean isSynchronized() {
        return false;
    }

    @Override
    public boolean isNative() {
        return false;
    }

    @Override
    public boolean isStrict() {
        return false;
    }

    @Override
    public boolean isMandated() {
        return false;
    }

    @Override
    public boolean isSynthetic() {
        return false;
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
