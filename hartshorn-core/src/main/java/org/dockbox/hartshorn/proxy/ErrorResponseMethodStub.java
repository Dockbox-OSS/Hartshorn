package org.dockbox.hartshorn.proxy;

public class ErrorResponseMethodStub<T> implements MethodStub<T> {

    @Override
    public Object invoke(final MethodStubContext<T> stubContext) {
        final Class<T> targetClass = stubContext.manager().targetClass();
        final String className = targetClass == null ? "" : targetClass.getSimpleName() + ".";
        final String name = stubContext.target().name();
        throw new AbstractMethodError("Cannot invoke method '" + className + name + "' because it is abstract. This type is proxied, but no proxy property was found for the method.");
    }
}
