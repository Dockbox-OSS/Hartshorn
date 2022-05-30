package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.proxy.loaders.UnproxyingParameterLoader;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.MethodInvoker;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import javassist.util.proxy.ProxyFactory;

public class StandardMethodInvocationHandler<T> {

    private static final Map<Invokable, MethodHandle> METHOD_HANDLE_CACHE = new ConcurrentHashMap<>();

    private final ProxyManager<T> manager;
    private final ApplicationContext applicationContext;
    private final ParameterLoader<ParameterLoaderContext> parameterLoader = new UnproxyingParameterLoader();

    public StandardMethodInvocationHandler(final ProxyManager<T> manager, final ApplicationContext applicationContext) {
        this.manager = manager;
        this.applicationContext = applicationContext;
    }

    public ProxyManager<T> manager() {
        return this.manager;
    }

    public Object invoke(final Object self, final MethodInvokable thisMethod, final Invokable proceed, final Object[] args) throws Throwable {
        final T callbackTarget = this.manager.delegate().or((T) self);
        final MethodContext<?, T> methodContext = (MethodContext<?, T>) thisMethod.toMethodContext();

        final CustomInvocation customInvocation = args$0 -> {
            if (this.manager.delegate().present()) {
                return this.invokeDelegate(this.manager.delegate().get(), thisMethod, args$0);
            }
            if (proceed == null) return TypeContext.of(proceed.getReturnType()).defaultOrNull();
            return proceed.invoke(callbackTarget, args$0);
        };

        final Callable<?> proceedCallable = () -> customInvocation.call(args);

        final Object[] arguments = this.resolveArgs(thisMethod, self, args);

        final Set<MethodWrapper<T>> wrappers = this.manager.wrappers(thisMethod.toMethod());

        for (final MethodWrapper<T> wrapper : wrappers) wrapper.acceptBefore(methodContext, callbackTarget, arguments);

        try {
            final Object result;
            final Result<MethodInterceptor<T>> interceptor = this.manager.interceptor(thisMethod.toMethod());
            if (interceptor.present()) {
                result = this.invokeInterceptor(interceptor.get(), callbackTarget, thisMethod.toMethod(), proceedCallable, customInvocation, arguments);
            }
            else {
                final Result<T> delegate = this.manager.delegate(thisMethod.toMethod());
                if (delegate.present()) {
                    result = this.invokeDelegate(delegate.get(), callbackTarget, thisMethod.toMethod(), arguments);
                }
                else {
                    if (callbackTarget == self && proceed != null) {
                        result = proceed.invoke(callbackTarget, arguments);
                    }
                    else {
                        result = this.invokeUnregistered(self, new MethodInvokable(thisMethod.toMethod()), proceed, arguments);
                    }
                }
            }

            for (final MethodWrapper<T> wrapper : wrappers) wrapper.acceptAfter(methodContext, callbackTarget, arguments);
            return result;
        }
        catch (final Throwable e) {
            for (final MethodWrapper<T> wrapper : wrappers) wrapper.acceptError(methodContext, callbackTarget, arguments, e);
            throw e;
        }
    }

    protected Object invokeInterceptor(final MethodInterceptor<T> interceptor, final Object self, final Method thisMethod, final Callable<?> proceed, final CustomInvocation customInvocation, final Object[] args) throws Throwable {
        final MethodInterceptorContext<T> context = new MethodInterceptorContext(thisMethod, args, self, proceed, customInvocation);
        return interceptor.intercept(context);
    }

    protected Object invokeDelegate(final T delegate, final Object self, final Method thisMethod, final Object[] args) throws Throwable {
        final Object result = thisMethod.invoke(delegate, args);
        if (result == delegate) return self;
        return result;
    }

    protected Object invokeUnregistered(final Object self, final Invokable thisMethod, final Invokable proceed, final Object[] args) throws Throwable {
        // If no handler is known, default to the original method. This is delegated to the instance
        // created, as it is typically created through Hartshorn's injectors and therefore DI dependent.
        Invokable target = thisMethod;
        if (this.manager.delegate().absent() && thisMethod == null)
            target = proceed;

        final Class<T> targetClass = this.manager.targetClass();

        if (target != null) {
            // If the target type is a class, whether it is abstract or not, we can invoke the native method directly. However, when
            // the target type is an interface, this method is not defined, requiring us to perform our own equality check.
            if (target.getName().equals("equals")
                    && target.getDeclaringClass().equals(Object.class)
                    && this.manager.delegate().absent()
                    && targetClass.isInterface()
            ) return this.proxyEquals(args[0]);

            return this.invokeTarget(self, thisMethod, target, args);
        }
        else {
            final StackTraceElement element = Thread.currentThread().getStackTrace()[3];
            final String name = element.getMethodName();
            final String className = targetClass == null ? "" : targetClass.getSimpleName() + ".";
            throw new AbstractMethodError("Cannot invoke method '" + className + name + "' because it is abstract. This type is proxied, but no proxy property was found for the method.");
        }
    }

    protected boolean proxyEquals(final Object obj) {
        if (obj == null) return false;
        if (this.manager.delegate().map(instance -> instance.equals(obj)).or(false)) return true;
        return this.manager.proxy() == obj;
    }

    protected Object invokeTarget(final Object self, final Invokable thisMethod, final Invokable target, final Object[] args) throws Throwable {
        final Class<T> targetClass = this.manager.targetClass();
        final TypeContext<T> targetType = TypeContext.of(targetClass);

        try {
            // If the proxy associated with this handler has a delegate, use it.
            if (this.manager.delegate().present()) return this.invokeDelegate(this.manager.delegate().get(), target, args);

                // If the method is default inside an interface, we cannot invoke it directly using a proxy instance. Instead we
                // need to lookup the method on the class and invoke it through the method handle directly.
            else if (thisMethod.isDefault()) return this.invokeDefault(targetClass, thisMethod, self, args);

                // If the current target instance (self) is not a proxy, we can invoke the method directly using reflections.
            else if (!(self instanceof Proxy || ProxyFactory.isProxyClass(self.getClass()))) return this.invokeSelf(self, target, args);

                // If none of the above conditions are met, we have no way to handle the method.
            else {
                final TypeContext<Object> selfContext = TypeContext.of(self);
                throw new IllegalStateException("Could not invoke local method " + thisMethod.getQualifiedName()
                        + " (targeting " + target.getQualifiedName() + ") on proxy "
                        + targetType.qualifiedName() + " of qualified type " + selfContext.qualifiedName() + "(isProxy=" + this.applicationContext().environment().manager().isProxy(self) + ")"
                        + " with arguments " + Arrays.toString(args) + ". " +
                        "This typically indicates that there is no appropriate proxy property (delegate or interceptor for the method.");
            }
        }
        catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

    protected Object[] resolveArgs(final MethodInvokable method, final Object instance, final Object[] args) {
        final MethodContext<?, ?> methodContext = method.toMethodContext();
        final ParameterLoaderContext context = new ParameterLoaderContext(methodContext, methodContext.parent(), instance, this.applicationContext());
        return this.parameterLoader.loadArguments(context, args).toArray();
    }

    protected Object invokeDelegate(final Object self, final Invokable target, final Object[] args) {
        return this.invokeAccessible(self, target, args, (method, instance, args$0) -> method.invoke(this.manager.delegate().get(), args$0));
    }

    protected Object invokeSelf(final Object self, final Invokable target, final Object[] args) {
        return this.invokeAccessible(self, target, args, (method, instance, args$0) -> method.invoke(self, args$0));
    }

    protected Object invokeAccessible(final Object self, final Invokable target, final Object[] args, final MethodInvoker function) {
        target.setAccessible(true);

        Object result;
        if (target instanceof MethodInvokable methodInvokable) {
            result =  function.invoke(methodInvokable.toMethodContext(), self, args)
                    .orElse(() -> TypeContext.of(target.getReturnType()).defaultOrNull())
                    .orNull();
        }
        else {
            try {
                result = target.invoke(self, args);
            }
            catch (final Throwable e) {
                result = TypeContext.of(target.getReturnType()).defaultOrNull();
            }
        }

        target.setAccessible(false);
        return result;
    }

    protected Object invokeDefault(final Class<T> declaringType, final Invokable thisMethod, final Object self, final Object[] args) throws Throwable {
        final MethodHandle handle;
        if (METHOD_HANDLE_CACHE.containsKey(thisMethod)) {
            handle = METHOD_HANDLE_CACHE.get(thisMethod);
        }
        else {
            handle = MethodHandles.lookup().findSpecial(
                    declaringType,
                    thisMethod.getName(),
                    MethodType.methodType(thisMethod.getReturnType(), thisMethod.getParameterTypes()),
                    declaringType
            ).bindTo(self);
            METHOD_HANDLE_CACHE.put(thisMethod, handle);
        }
        return handle.invokeWithArguments(args);
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
