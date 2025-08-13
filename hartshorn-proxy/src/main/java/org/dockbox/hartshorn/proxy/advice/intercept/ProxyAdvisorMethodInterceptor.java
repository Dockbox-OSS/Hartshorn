/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.proxy.advice.intercept;

import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.advice.IntrospectionProxyResultValidator;
import org.dockbox.hartshorn.proxy.advice.ProxyMethodInterceptHandler;
import org.dockbox.hartshorn.proxy.advice.ProxyMethodInvoker;
import org.dockbox.hartshorn.proxy.advice.ProxyResultValidator;
import org.dockbox.hartshorn.proxy.advice.ReflectionProxyMethodInterceptHandler;
import org.dockbox.hartshorn.proxy.advice.wrap.ProxyCallbackContext;
import org.dockbox.hartshorn.proxy.loaders.ProxyParameterLoaderContext;
import org.dockbox.hartshorn.proxy.loaders.UnproxyingParameterLoader;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoader;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * The default method interceptor used by the {@link org.dockbox.hartshorn.proxy.JDKInterfaceProxyFactory}. This
 * interceptor is responsible for invoking the {@link ProxyMethodInterceptHandler} and validating the results of the
 * invocation.
 *
 * <p>This interceptor ensures a state is prepared for the {@link ProxyMethodInterceptHandler} to use. This state
 * includes the {@link ProxyCallbackContext} and the {@link CustomInvocation} which is used to invoke the method
 * on the target instance. The actual invocation is delegated to the {@link ProxyMethodInterceptHandler}, which will
 * typically handle the invocation using configured advisors in its {@link ProxyMethodInvoker}.
 *
 * @param <T> the type of the target instance
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
@SuppressWarnings("ProhibitedExceptionDeclared")
public class ProxyAdvisorMethodInterceptor<T> implements ProxyMethodInterceptor<T> {

    private final ProxyManager<T> manager;
    private final Introspector introspector;
    private final ProxyMethodInvoker<T> methodInvoker;
    private final ProxyResultValidator resultValidator;
    private final ProxyMethodInterceptHandler<T> interceptHandler;
    private final ProxyOrchestrator proxyOrchestrator;
    private final ParameterLoader parameterLoader = new UnproxyingParameterLoader();

    public ProxyAdvisorMethodInterceptor(ProxyManager<T> manager, ProxyOrchestrator proxyOrchestrator) {
        this.manager = manager;
        this.introspector = proxyOrchestrator.introspector();
        this.proxyOrchestrator = proxyOrchestrator;
        this.resultValidator = new IntrospectionProxyResultValidator(this.introspector);
        this.interceptHandler = new ReflectionProxyMethodInterceptHandler<>(this);
        this.methodInvoker = this.interceptHandler.methodInvoker();
    }

    @Override
    public ProxyManager<T> manager() {
        return this.manager;
    }

    @Override
    public Object intercept(Object self, MethodInvokable source, Invokable proxy, Object[] args) throws Throwable {
        T instance = this.manager().targetClass().cast(self);
        T callbackTarget = this.manager().delegate().orElse(instance);
        MethodView<T, ?> methodView = TypeUtils.unchecked(source.toIntrospector(), MethodView.class);

        CustomInvocation<?> customInvocation = this.createDefaultInvocation(source, proxy, callbackTarget);
        Object[] arguments = this.resolveArgs(source, self, args);

        Object result = this.interceptAndNotify(instance, source, proxy, callbackTarget, methodView, customInvocation, arguments);
        return this.resultValidator.validateResult(source, result);
    }

    /**
     * Intercepts the method invocation and delegates the invocation to the {@link ProxyMethodInterceptHandler}. This
     * may involve invoking an advised interceptor, or simply invoking the method on the target instance, depending on
     * the configuration of the {@link ProxyManager#advisor() proxy manager's advisor}.
     *
     * @param self the instance on which the method is invoked
     * @param source the method being invoked, represented as a {@link MethodInvokable}
     * @param proxy the proxy method being invoked, which may be null if the method is not proxied
     * @param callbackTarget the target instance on which the method is invoked, which may differ from `self` if a delegate is used
     * @param methodView the method view representing the method being invoked, used for introspection
     * @param defaultInvocation the default invocation to use if no interceptor is present
     * @param arguments the arguments passed to the method invocation
     * @return the result of the method invocation, which may be modified by the interceptor
     * @throws Throwable if an error occurs during the method invocation or interception
     */
    protected Object interceptAndNotify(T self, MethodInvokable source, Invokable proxy, T callbackTarget,
                                      MethodView<T, ?> methodView, CustomInvocation<?> defaultInvocation,
                                      Object[] arguments) throws Throwable {

        ProxyCallbackContext<T> callbackContext = new ProxyCallbackContext<>(callbackTarget, TypeUtils.unchecked(self, Object.class), methodView, arguments);
        return this.manager().advisor().safeWrapIntercept(callbackContext, () -> {
            Option<MethodInterceptor<T, Object>> interceptor = this.manager()
                    .advisor()
                    .resolver()
                    .method(source.toMethod())
                    .interceptor();

            if (interceptor.present()) {
                return this.interceptHandler.handleInterceptedMethod(source, callbackTarget, defaultInvocation, arguments, interceptor.get());
            }
            else {
                return this.interceptHandler.handleNonInterceptedMethod(self, source, proxy, callbackTarget, arguments);
            }
        });
    }

    /**
     * Creates a default invocation for the method. This is used when no interceptor is present, or when the
     * interceptor does not handle the method. The default invocation can either invoke the method on the
     * delegate instance (if present), or return a default value based on the method's return type.
     *
     * @param source the method being invoked, represented as a {@link MethodInvokable}
     * @param proxy the proxy method being invoked, which may be null if the method is not proxied
     * @param callbackTarget the target instance on which the method is invoked, which may differ from the `self` instance
     * @return a {@link CustomInvocation} that defines how the method should be invoked
     */
    protected CustomInvocation<?> createDefaultInvocation(Invokable source, Invokable proxy, T callbackTarget) {
        return interceptorArgs -> {
            if (this.manager().delegate().present()) {
                return this.methodInvoker.invokeDelegate(this.manager().delegate().get(), source, interceptorArgs);
            }
            if (proxy == null) {
                return this.introspector.introspect(source.returnType()).defaultOrNull();
            }
            return proxy.invoke(callbackTarget, interceptorArgs);
        };
    }

    /**
     * Resolves the arguments for the method invocation, using the {@link #parameterLoader configured parameter loader}.
     * This typically only involves unproxying the arguments, but may also involve additional processing if the
     * method has parameters that require special handling (e.g., annotations, default values).
     *
     * @param method the method being invoked, represented as a {@link MethodInvokable}
     * @param instance the instance on which the method is invoked, which may be a proxy or a concrete instance
     * @param args the arguments passed to the method invocation, which may include proxies or other objects
     * @return an array of resolved arguments, which may be modified from the original `args` array
     */
    protected Object[] resolveArgs(MethodInvokable method, Object instance, Object[] args) {
        MethodView<?, ?> methodView = method.toIntrospector();
        ProxyParameterLoaderContext context = new ProxyParameterLoaderContext(methodView, instance, this.proxyOrchestrator);
        return this.parameterLoader().loadArguments(context, args).toArray();
    }

    /**
     * Returns the {@link ParameterLoader} used to load the parameters for the method invocation. This loader is
     * responsible for unproxying the arguments and resolving any special handling required for the method's parameters.
     *
     * @return the parameter loader used for resolving method arguments
     */
    protected ParameterLoader parameterLoader() {
        return this.parameterLoader;
    }
}
