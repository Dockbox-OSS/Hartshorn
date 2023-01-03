package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class DefaultValueResponseMethodStub<T> implements MethodStub<T> {

    @Override
    public Object invoke(final MethodStubContext<T> stubContext) {
        final Class<?> returnType = stubContext.target().returnType();
        final TypeView<?> introspectedType = stubContext.applicationContext()
                .environment()
                .introspect(returnType);
        return introspectedType.defaultOrNull();
    }
}
