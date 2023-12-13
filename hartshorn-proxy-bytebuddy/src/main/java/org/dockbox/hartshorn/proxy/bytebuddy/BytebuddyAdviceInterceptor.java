package org.dockbox.hartshorn.proxy.bytebuddy;

import net.bytebuddy.asm.Advice.AllArguments;
import net.bytebuddy.asm.Advice.This;

public class BytebuddyAdviceInterceptor {

    public static Object interceptAlways(
            @This InterceptorAwareProxy<?> instance,
            @AllArguments Object[] args
    ) throws Throwable {
        return instance.$$__interceptor().intercept(instance, null, null, args);
    }
}
