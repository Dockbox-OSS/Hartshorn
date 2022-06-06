package org.dockbox.hartshorn.proxy.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.proxy.CustomInvocation;
import org.dockbox.hartshorn.proxy.DefaultProxyFactory;
import org.dockbox.hartshorn.proxy.LazyProxyManager;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.proxy.MethodWrapper;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.MultiMap;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.TypeMap;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BytebuddyProxyFactory<T> implements StateAwareProxyFactory<T, BytebuddyProxyFactory<T>> {

    private static final String MANAGER_FIELD = "$__manager";

    // Delegates and interceptors
    private final Map<Method, Object> delegates = new ConcurrentHashMap<>();
    private final Map<Method, MethodInterceptor<T>> interceptors = new ConcurrentHashMap<>();
    private final TypeMap<Object> typeDelegates = new TypeMap<>();
    private T typeDelegate;

    // Proxy data
    private final Class<T> type;
    private Builder<T> builder;
    private boolean trackState = true;
    private boolean modified;

    public BytebuddyProxyFactory(final TypeContext<T> type) {
        this(type.type());
    }

    public BytebuddyProxyFactory(final Class<T> type) {
        this.type = type;
        this.builder = this.prepareBuilder();
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public @Nullable T typeDelegate() {
        return this.typeDelegate;
    }

    @Override
    public Map<Method, Object> delegates() {
        return this.delegates;
    }

    @Override
    public Map<Method, MethodInterceptor<T>> interceptors() {
        return this.interceptors;
    }

    @Override
    public MultiMap<Method, MethodWrapper<T>> wrappers() {
        return null; // TODO
    }

    @Override
    public TypeMap<Object> typeDelegates() {
        return this.typeDelegates;
    }

    @Override
    public Set<Class<?>> interfaces() {
        return null; // TODO
    }

    public boolean trackState() {
        return this.trackState;
    }

    @Override
    public StateAwareProxyFactory<T, BytebuddyProxyFactory<T>> trackState(final boolean trackState) {
        return null;
    }

    @Override
    public boolean modified() {
        return this.modified;
    }

    protected Builder<T> prepareBuilder() {
        try {
            final Constructor<?> declaredConstructor;
            if (this.type.isInterface()) declaredConstructor = Object.class.getDeclaredConstructor();
            else declaredConstructor = this.type.getDeclaredConstructor();

            final Builder<T> builder;
            if (this.type.isInterface()) {
                builder = (Builder<T>) new ByteBuddy()
                        .subclass(Object.class, Default.NO_CONSTRUCTORS)
                        .defineMethod("equals", boolean.class, Modifier.PUBLIC)
                        .withParameters(Object.class)
                        // TODO: Compare the types equally
                        .intercept(FixedValue.value(true))

                        .defineMethod("hashCode", int.class, Modifier.PUBLIC)
                        .intercept(FixedValue.value(this.type().hashCode()))

                        .implement(this.type());
            }
            else {
                builder = new ByteBuddy()
                        .subclass(this.type, Default.NO_CONSTRUCTORS);
            }

            return builder.name(DefaultProxyFactory.NAME_GENERATOR.get(this.type))

                    .implement(Proxy.class)
                    .method(ElementMatchers.is(Proxy.class.getMethod("manager")))
                    .intercept(FieldAccessor.ofField(MANAGER_FIELD))

                    .defineConstructor(Modifier.PUBLIC)
                    .withParameters(ProxyManager.class)
                    .intercept(MethodCall.invoke(declaredConstructor).andThen(FieldAccessor.ofField(MANAGER_FIELD).setsArgumentAt(0)))

                    .defineField(MANAGER_FIELD, ProxyManager.class, Modifier.PRIVATE | Modifier.FINAL | Modifier.TRANSIENT);
        }
        catch (final NoSuchMethodException e) {
            return ExceptionHandler.unchecked(e);
        }
    }

    protected void updateState() {
        if (this.trackState) this.modified = true;
    }

    @Override
    public BytebuddyProxyFactory<T> delegate(final T delegate) {
        if (delegate != null) {
            this.updateState();
            this.builder = this.builder.method(ElementMatchers.isDeclaredBy(this.type))
                    .intercept(MethodCall.invokeSelf().on(delegate).withAllArguments());
            for (final Method declaredMethod : this.type.getDeclaredMethods()) {
                this.delegates.put(declaredMethod, delegate);
            }
            this.typeDelegate = delegate;
        }
        return this;
    }

    @Override
    public BytebuddyProxyFactory<T> delegateAbstract(final T delegate) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public <S> BytebuddyProxyFactory<T> delegate(final Class<S> type, final S delegate) {
        if (type.isAssignableFrom(this.type)) {
            this.updateState();
            this.builder = this.builder.method(ElementMatchers.isDeclaredBy(type))
                    .intercept(MethodDelegation.to(delegate));
            for (final Method declaredMethod : type.getDeclaredMethods()) {
                this.delegates.put(declaredMethod, delegate);
            }
            this.typeDelegates.put((Class<Object>) type, delegate);
        }
        else {
            throw new IllegalArgumentException(this.type.getName() + " does not " + (type.isInterface() ? "implement " : "extend ") + type);
        }
        return this;
    }

    @Override
    public BytebuddyProxyFactory<T> delegate(final MethodContext<?, T> method, final T delegate) {
        return this.delegate(method.method(), delegate);
    }

    @Override
    public BytebuddyProxyFactory<T> delegate(final Method method, final T delegate) {
        this.updateState();
        this.builder = this.builder
                .method(ElementMatchers.named(method.getName()).and(ElementMatchers.takesArguments(method.getParameterTypes())))
                .intercept(MethodDelegation.to(delegate));
        this.delegates.put(method, delegate);
        return this;
    }

    @Override
    public BytebuddyProxyFactory<T> intercept(final MethodContext<?, T> method, final MethodInterceptor<T> interceptor) {
        return this.intercept(method.method(), interceptor);
    }

    @Override
    public BytebuddyProxyFactory<T> intercept(final Method method, final MethodInterceptor<T> interceptor) {
        final MethodInterceptor<T> methodInterceptor;
        if (this.interceptors.containsKey(method)) {
            methodInterceptor = this.interceptors.get(method).andThen(interceptor);
        }
        else {
            methodInterceptor = interceptor;
        }
        this.updateState();
        this.interceptors.put(method, methodInterceptor);
        this.builder = this.builder.method(ElementMatchers.is(method))
                .intercept(MethodDelegation
                        .withDefaultConfiguration()
                        .withBinders(Morph.Binder.install(CustomInvocation.class))
                        .to(new BytebuddyInterceptor(ctx -> methodInterceptor.intercept((MethodInterceptorContext<T>) ctx)))
                );
        return this;
    }

    @Override
    public BytebuddyProxyFactory<T> intercept(final MethodContext<?, T> method, final MethodWrapper<T> wrapper) {
        return this.intercept(method.method(), wrapper);
    }

    @Override
    public BytebuddyProxyFactory<T> intercept(final Method method, final MethodWrapper<T> wrapper) {
        return null; // TODO
    }

    @Override
    public BytebuddyProxyFactory<T> implement(final Class<?>... interfaces) {
        this.builder.implement(interfaces);
        return this;
    }

    protected Object methodDelegate(final Method method) {
        return this.delegates.get(method);
    }

    @Override
    public Result<T> proxy() throws ApplicationException {
        try {
            final Unloaded<T> unloaded = this.builder.make();
            unloaded.saveIn(new File("generated/bytebuddy"));
            final Class<? extends T> proxy = unloaded.load(this.type.getClassLoader()).getLoaded();

            // Initialize manager and provide it to the proxy constructor
            final LazyProxyManager<T> proxyManager = new LazyProxyManager<>(
                    null,
                    (Class<T>) proxy,
                    this.type,
                    this.typeDelegate,
                    this.delegates,
                    this.typeDelegates,
                    this.interceptors,
                    this.wrappers());
            final T instance = proxy.getConstructor(ProxyManager.class).newInstance(proxyManager);
            proxyManager.proxy(instance);

            return Result.of(instance);
        }
        catch (final Exception e) {
            throw new ApplicationException("Encountered exception while building proxy instance for " + this.type().getCanonicalName(), e);
        }
    }
}
