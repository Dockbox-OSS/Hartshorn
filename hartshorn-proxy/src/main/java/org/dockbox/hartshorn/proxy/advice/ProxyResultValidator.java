package org.dockbox.hartshorn.proxy.advice;

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;

public interface ProxyResultValidator {

    Object validateResult(final MethodInvokable source, final Object result);
}
