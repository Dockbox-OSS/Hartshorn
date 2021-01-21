package org.dockbox.selene.core.server.bootstrap;

import org.dockbox.selene.core.annotations.proxy.Proxy;
import org.dockbox.selene.core.annotations.proxy.Instance;
import org.dockbox.selene.core.annotations.proxy.Proxy.Target;
import org.dockbox.selene.core.proxy.CancelProxyException;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.SeleneInformation;
import org.dockbox.selene.core.server.properties.ProxyProperty;
import org.dockbox.selene.core.util.Reflect;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class ProxyableBootstrap {

    protected void boostrapDelegates() {
        Reflect.getAnnotatedTypes(SeleneInformation.PACKAGE_PREFIX, Proxy.class).forEach(proxy -> {
            if (Modifier.isAbstract(proxy.getModifiers())) {
                Selene.log().warn("Proxy source cannot be abstract [" + proxy.getCanonicalName() + "]");
                return;
            }

            Proxy delegationInfo = proxy.getAnnotation(Proxy.class);
            if (delegationInfo.value().isAnnotationPresent(Proxy.class)) {
                Selene.log().warn("Proxy target cannot be another delegate [" + proxy.getCanonicalName() + "]");
                return;
            }

            this.delegateMethods(proxy);
        });
    }

    private void delegateMethods(Class<?> proxy) {
        @NotNull @Unmodifiable Collection<Method> targets = Reflect.getAnnotedMethods(
                proxy,
                Target.class,
                i -> true,
                false
        );

        targets.forEach(target -> this.delegateMethod(proxy, target));
    }

    private <T> void delegateMethod(Class<T> proxyClass, Method source) {
        if (Modifier.isAbstract(source.getModifiers())) {
            Selene.log().warn("Proxy method cannot be abstract [" + source.getName() + "]");
            return;
        }

        Proxy proxy = proxyClass.getAnnotation(Proxy.class);
        Class<?> proxyTargetClass = proxy.value();
        String methodName = source.getName();

        // Used only for method lookup, parameters annotated with @Instance are injected and thus not present on the
        // target method.
        Class<?>[] arguments = SeleneUtils.asList(source.getParameters()).stream()
                .filter(arg -> !arg.isAnnotationPresent(Instance.class))
                .map(Parameter::getType)
                .toArray(Class<?>[]::new);

        // By default the name of the proxy method is used, however it's possible two methods proxy separate stages of
        // the same method, so the method names are different. The @Target annotation allows setting the method name
        // manually.
        Target target = source.getAnnotation(Target.class);
        //noinspection CallToSuspiciousStringMethod
        if (!"".equals(target.method())) methodName = target.method();
        try {
            Method targetMethod = proxyTargetClass.getDeclaredMethod(methodName, arguments);

            // If the target method has a return type other than `void`, and the proxy wants to overwrite the return
            // value, we need to ensure the return value of the proxy method is assignable from the return type of the
            // target.
            if (!targetMethod.getReturnType().equals(Void.TYPE) && target.overwrite()) {
                if (!Reflect.isAssignableFrom(source.getReturnType(), targetMethod.getReturnType())) {
                    Selene.log().warn("Return type for '" + source.getName() + "' is not assignable from '" + targetMethod.getReturnType() + "' [" + proxyClass.getCanonicalName() + "]");
                    return;
                }
            }

            ProxyProperty<T, ?> property = ProxyProperty.of(proxyClass, targetMethod, (instance, args, holder) -> {
                Object[] invokingArgs = this.prepareArguments(source, args, instance);
                try {
                    return source.invoke(Selene.provide(proxyClass), invokingArgs);
                } catch (CancelProxyException e) {
                    holder.setCancelled(true);
                } catch (Throwable e) {
                    Selene.handle(e);
                }
                //noinspection ReturnOfNull
                return null;
            });
            property.setTarget(target.at());
            property.setPriority(target.priority());
            property.setOverwriteResult(target.overwrite());
            Selene.getServer().delegate(property);
        } catch (NoSuchMethodException e) {
            Selene.log().warn("Proxy target does not have declared method '" + methodName + "(" + Arrays.toString(arguments) + ") [" + proxyClass.getCanonicalName() + "]");
        }
    }

    private Object[] prepareArguments(Method method, Object[] args, Object instance) {
        List<Object> arguments = SeleneUtils.emptyList();
        if (method.getParameters()[0].isAnnotationPresent(Instance.class)) {
            arguments.add(instance);
        }
        arguments.addAll(Arrays.asList(args));
        return arguments.toArray();
    }

}
