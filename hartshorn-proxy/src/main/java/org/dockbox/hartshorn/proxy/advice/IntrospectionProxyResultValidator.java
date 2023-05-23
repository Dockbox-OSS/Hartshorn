package org.dockbox.hartshorn.proxy.advice;

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class IntrospectionProxyResultValidator implements ProxyResultValidator {

    private final Introspector introspector;

    public IntrospectionProxyResultValidator(final Introspector introspector) {
        this.introspector = introspector;
    }

    @Override
    public Object validateResult(final MethodInvokable source, final Object result) {
        final TypeView<?> returnType = source.toIntrospector().returnType();
        if (returnType.isVoid()) {
            return null;
        }
        else if (returnType.isPrimitive()) {
            if (result == null) {
                return returnType.defaultOrNull();
            }
            else {
                final TypeView<Object> resultView = this.introspector.introspect(result);
                if (resultView.isPrimitive() || resultView.isChildOf(returnType.type())) {
                    return result;
                }
                else throw new IllegalArgumentException("Invalid return type: " + resultView.name() + " for " + source.qualifiedName());
            }
        }
        else if (result == null) {
            return null;
        }
        else {
            final TypeView<Object> resultView = this.introspector.introspect(result);
            if (resultView.isChildOf(returnType.type())) {
                return result;
            }
            else throw new IllegalArgumentException("Invalid return type: " + resultView.name() + " for " + source.qualifiedName());
        }
    }
}
