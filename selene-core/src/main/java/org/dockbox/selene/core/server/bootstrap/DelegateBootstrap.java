package org.dockbox.selene.core.server.bootstrap;

import org.dockbox.selene.core.annotations.delegate.Proxy;
import org.dockbox.selene.core.annotations.delegate.Instance;
import org.dockbox.selene.core.annotations.delegate.Proxy.Target;
import org.dockbox.selene.core.delegate.CancelDelegateException;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.SeleneInformation;
import org.dockbox.selene.core.server.properties.DelegateProperty;
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
public abstract class DelegateBootstrap {

    protected void boostrapDelegates() {
        Reflect.getAnnotatedTypes(SeleneInformation.PACKAGE_PREFIX, Proxy.class).forEach(proxy -> {
            //
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
        Class<?> delegateTarget = proxy.value();
        String methodName = source.getName();
        Class<?>[] arguments = SeleneUtils.asList(source.getParameters()).stream()
                .filter(arg -> !arg.isAnnotationPresent(Instance.class))
                .map(Parameter::getType)
                .toArray(Class<?>[]::new);
        try {
            Target target = source.getAnnotation(Target.class);
            //noinspection CallToSuspiciousStringMethod
            if (!"".equals(target.method())) methodName = target.method();
            Method targetMethod = delegateTarget.getDeclaredMethod(methodName, arguments);

            if (!targetMethod.getReturnType().equals(Void.TYPE) && target.overwrite()) {
                if (!Reflect.isAssignableFrom(source.getReturnType(), targetMethod.getReturnType())) {
                    Selene.log().warn("Return type for '" + source.getName() + "' is not assignable from '" + targetMethod.getReturnType() + "' [" + proxyClass.getCanonicalName() + "]");
                }
            }
            DelegateProperty<T, ?> property = DelegateProperty.of(proxyClass, targetMethod, (instance, args, holder) -> {
                Object[] invokingArgs = this.prepareArguments(source, args, instance);
                try {
                    return source.invoke(Selene.provide(proxyClass), invokingArgs);
                } catch (CancelDelegateException e) {
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
