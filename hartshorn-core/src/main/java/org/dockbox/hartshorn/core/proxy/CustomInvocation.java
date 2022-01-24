package org.dockbox.hartshorn.core.proxy;

@FunctionalInterface
public interface CustomInvocation {
    Object call(Object... args);
}
