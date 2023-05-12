package org.dockbox.hartshorn.util.introspect;

public interface ElementModifiersIntrospector {

    int asInt();

    boolean has(AccessModifier modifier);

    boolean isPublic();

    boolean isPrivate();

    boolean isProtected();

    boolean isStatic();

    boolean isFinal();

    boolean isAbstract();

    boolean isTransient();

    boolean isVolatile();

    boolean isSynchronized();

    boolean isNative();

    boolean isStrict();

    boolean isMandated();

    boolean isSynthetic();

    boolean isDefault();
}
