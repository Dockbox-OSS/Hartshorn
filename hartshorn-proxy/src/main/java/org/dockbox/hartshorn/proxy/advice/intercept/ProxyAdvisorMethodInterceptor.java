/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.advice.IntrospectionProxyResultValidator;
import org.dockbox.hartshorn.proxy.advice.ProxyMethodInterceptHandler;
import org.dockbox.hartshorn.proxy.advice.ProxyMethodInvoker;
import org.dockbox.hartshorn.proxy.advice.ProxyResultValidator;
import org.dockbox.hartshorn.proxy.advice.ReflectionProxyMethodInterceptHandler;
import org.dockbox.hartshorn.proxy.advice.wrap.ProxyCallbackContext;
import org.dockbox.hartshorn.proxy.loaders.ProxyParameterLoaderContext;
import org.dockbox.hartshorn.proxy.loaders.UnproxyingParameterLoader;
import org.dockbox.hartshorn.util.TypeUtils;
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
@SuppressWarnings({ "OverlyBroadThrowsClause", "ProhibitedExceptionDeclared" })
public class ProxyAdvisorMethodInterceptor<T> implements ProxyMethodInterceptor<T> {

    private final ProxyManager<T> manager;
    private final Introspector introspector;
    private final ProxyMethodInvoker<T> methodInvoker;
    private final ProxyResultValidator resultValidator;
    private final ProxyMethodInterceptHandler<T> interceptHandler;
    private final ApplicationProxier applicationProxier;
    private final ParameterLoader<ProxyParameterLoaderContext> parameterLoader = new UnproxyingParameterLoader();

    public ProxyAdvisorMethodInterceptor(final ProxyManager<T> manager, final ApplicationProxier applicationProxier) {
        this.manager = manager;
        this.introspector = applicationProxier.introspector();
        this.applicationProxier = applicationProxier;
        this.resultValidator = new IntrospectionProxyResultValidator(introspector);
        this.interceptHandler = new ReflectionProxyMethodInterceptHandler<>(this);
        this.methodInvoker = this.interceptHandler.methodInvoker();
    }

    @Override
    public ProxyManager<T> manager() {
        return this.manager;
    }

    @Override
    public Object intercept(final Object self, final MethodInvokable source, final Invokable proxy, final Object[] args) throws Throwable {
        final T instance = this.manager().targetClass().cast(self);
        final T callbackTarget = this.manager().delegate().orElse(instance);
        final MethodView<T, ?> methodView = TypeUtils.adjustWildcards(source.toIntrospector(), MethodView.class);

        final CustomInvocation<?> customInvocation = this.createDefaultInvocation(source, proxy, callbackTarget);
        final Object[] arguments = this.resolveArgs(source, self, args);

        final Object result = this.interceptAndNotify(instance, source, proxy, callbackTarget, methodView, customInvocation, arguments);
        return this.resultValidator.validateResult(source, result);
    }

    protected Object interceptAndNotify(final T self, final MethodInvokable source, final Invokable proxy, final T callbackTarget,
                                      final MethodView<T, ?> methodView, final CustomInvocation<?> customInvocation,
                                      final Object[] arguments) throws Throwable {

        final ProxyCallbackContext<T> callbackContext = new ProxyCallbackContext<>(callbackTarget, TypeUtils.adjustWildcards(self, Object.class), methodView, arguments);
        return this.manager().advisor().safeWrapIntercept(callbackContext, () -> {
            final Option<MethodInterceptor<T, Object>> interceptor = this.manager()
                    .advisor()
                    .resolver()
                    .method(source.toMethod())
                    .interceptor();

            if (interceptor.present()) {
                return this.interceptHandler.handleInterceptedMethod(source, callbackTarget, customInvocation, arguments, interceptor.get());
            }
            else {
                return this.interceptHandler.handleNonInterceptedMethod(self, source, proxy, callbackTarget, arguments);
            }
        });
    }

    protected CustomInvocation<?> createDefaultInvocation(final Invokable source, final Invokable proxy, final T callbackTarget) {
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

    protected Object[] resolveArgs(final MethodInvokable method, final Object instance, final Object[] args) {
        final MethodView<?, ?> methodView = method.toIntrospector();
        final ProxyParameterLoaderContext context = new ProxyParameterLoaderContext(methodView, instance, applicationProxier);
        return this.parameterLoader().loadArguments(context, args).toArray();
    }

    protected ParameterLoader<ProxyParameterLoaderContext> parameterLoader() {
        return this.parameterLoader;
    }
}
