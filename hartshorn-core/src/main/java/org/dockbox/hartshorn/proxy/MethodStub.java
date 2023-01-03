package org.dockbox.hartshorn.proxy;

@FunctionalInterface
public interface MethodStub<T> {

    Object invoke(MethodStubContext<T> stubContext) throws Throwable;

}
