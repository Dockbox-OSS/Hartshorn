/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptorContext;
import org.dockbox.hartshorn.proxy.advice.registry.AdvisorRegistry;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The entrypoint for creating proxy objects. This class is responsible for creating proxy objects for
 * a given class, and is a default contract provided in all {@code ComponentProcessingContext}s for
 * components that permit the creation of proxies.
 *
 * <p>Proxy factories are responsible for creating proxy objects for a given class, after the proxy
 * has been created, the proxy object is passed to the responsible {@link ProxyManager} for further
 * life-cycle management. Proxy factories are able to create zero or more unique proxy objects for
 * a given class, and are responsible for ensuring that the proxy objects are unique.
 *
 * <p>To support the creation of proxies, the {@link ProxyFactory} exposes a set of methods that
 * can be used to modify the proxy object before it is created. This includes the delegation and
 * interception of methods.
 *
 * <p><b>Interception</b>
 * <p>Interception indicates the method is replaced by whichever implementation is chosen. Interception
 * can be done in two ways; full replacement, and wrapping.
 *
 * <p><b>Full replacement interception</b>
 * <p>A full replacement is done using a custom
 * {@link MethodInterceptor}, which accepts a {@link MethodInterceptorContext} to execute given functionality.
 * Within an interceptor it is possible to access all required information about the intercepted method,
 * as can be seen in the {@link MethodInterceptorContext} class.
 *
 * <p>Method interceptors are executed in series, allowing each step to re-use and/or modify the result of
 * another interceptor. To do so, the previous {@link MethodInterceptorContext#result()} is provided. If
 * the interceptor is the first one to execute, the result will be the default value of the return type.
 * The series are executed in no specific order.
 *
 * <pre>{@code
 * factory.intercept(greetingMethod, interceptorContext -> "Hello world!");
 * User user = factory.proxy().get();
 * String greeting = user.greeting(); // Returns 'Hello world!'
 * }</pre>
 *
 * <p><b>Wrapping interception</b>
 * <p>Wrapping interception is similar to the pre-existing method phasing
 * approach. It allows for specific callbacks to be executed before a method is performed, after it is finished,
 * and when an exception is thrown during the execution of the method. Wrappers will always be executed, even
 * if the method is intercepted or delegated. This allows for specific states to be prepared and closed around
 * a method's execution. For example, an annotation like {@code @Transactional} the wrapper can be used to:
 * <ul>
 *     <li>Open a transaction before the method is performed</li>
 *     <li>Commit the transaction after the method is finished</li>
 *     <li>Rollback the transaction if an exception is thrown</li>
 * </ul>
 *
 * <pre>{@code
 * public class UserMethodExecutionLogger implements MethodWrapper<User> {
 *     @Override
 *     public void acceptBefore(MethodView<?, User> method, User instance, Object[] args) {
 *         System.out.println("Before method!");
 *     }
 *
 *     @Override
 *     public void acceptAfter(MethodView<?, User> method, User instance, Object[] args) {
 *         System.out.println("After method!");
 *     }
 *
 *     @Override
 *     public void acceptError(MethodView<?, User> method, User instance, Object[] args, Throwable error) {
 *         System.out.println("Method caused an exception: " + error.getMessage());
 *     }
 * }
 * }</pre>
 * <pre>{@code
 * factory.intercept(greetingMethod, new UserMethodExecutionLogger());
 * User user = factory.proxy().get();
 * user.speakGreeting();
 * }</pre>
 *
 * <p>The above would then result in the following output:
 * <pre>{@code
 * Before method!
 * User says: Hello world!
 * After method!
 * }</pre>
 *
 * <p><b>Delegation</b>
 * <p>Like interception, delegation replaces the implementation of a proxy object. However, it does not carry the proxy's
 * context down to the implementation. Instead, it redirects the method call to another object. Delegation knows two different
 * delegate types; original instance, and backing implementations.
 *
 * <p><b>Original instance delegation</b>
 * <p>Original instance delegation indicates that the delegate is of the exact same type as the proxy type, or a sub-type of that
 * type. This allows all functionality to be delegated to this instance.
 *
 * <pre>{@code
 * public interface User {
 *     String greeting();
 * }
 * public class UserImpl implements User {
 *     @Override
 *     public String greeting() {
 *         return "Hello implementation!";
 *     }
 * }
 * }</pre>
 * <pre>{@code
 * StateAwareProxyFactory<User, ?> factory = applicationManager.factory(User.class);
 * factory.delegate(new UserImpl());
 * User user = factory.proxy().get();
 * user.greeting(); // Returns 'Hello implementation!'
 * }</pre>
 *
 * <p><b>Backing implementation delegation</b>
 * <p>Backing implementations follow the opposite rule of original instance delegation. Instead of requiring the exact type or a subtype to
 * be implemented, backing implementations delegate the behavior of a given parent of the type. This allows types like {@code JpaRepository}
 * implementations to specifically delegate to e.g. {@code HibernateJpaRepository}.
 *
 * <pre>{@code
 * public interface User extends ContextCarrier {
 *     String greeting();
 * }
 * public class ContextCarrierImpl implements ContextCarrier {
 *     @Override
 *     public ApplicationContext applicationContext() {
 *         return ...;
 *     }
 * }
 * }</pre>
 * <pre>{@code
 * StateAwareProxyFactory<User, ?> factory = applicationManager.factory(User.class);
 * factory.delegate(ContextCarrier.class, new ContextCarrierImpl());
 * User user = factory.proxy().get();
 * user.applicationContext(); // Returns a valid application context
 * user.greeting(); // Yields an exception as no implementation is assigned and the method is abstract
 * }</pre>
 *
 * <p>However, it is not unlikely a delegate returns itself in chained method calls. To avoid leaking the delegate, method handles always check if
 * the returned object is the delegate, and will replace it with the proxy instance if it is so.
 *
 * <pre>{@code
 * public interface Returner {
 *     Returner self();
 * }
 * public interface User extends Returner {
 *     String greeting();
 * }
 * public class ReturnerImpl implements Returner {
 *     @Override
 *     public Returner self() {
 *         return this;
 *     }
 * }
 * }</pre>
 * <pre>{@code
 * StateAwareProxyFactory<User, ?> factory = applicationManager.factory(User.class);
 * factory.delegate(Returner.class, new ReturnerImpl());
 * User user = factory.proxy().get();
 * user.self(); // Returns the user proxy object instead of the ReturnerImpl instance
 * }</pre>
 *
 * @param <T> The type of the proxy
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public interface ProxyFactory<T> {

    /**
     * Gets the registry of advisors that are currently active on this factory. This will return a registry
     * that can be used to add, remove, and replace advisors.
     *
     * @return The registry
     */
    AdvisorRegistry<T> advisors();

    /**
     * Applies the registry of advisors that are currently active to the given consumer. This will return
     * this factory, for easier chaining. Any changes made to the registry will be applied to the factory
     * either immediately or after the consumer has been executed.
     *
     * @param registryConsumer The consumer to apply the registry to
     * @return This factory
     */
    ProxyFactory<T> advisors(Consumer<? super AdvisorRegistry<T>> registryConsumer);

    /**
     * Implements the given interfaces on the proxy. This will add the given interfaces to the list of
     * interfaces that the proxy implements. This will not replace existing interfaces. This will not
     * affect the interfaces that the proxy implements directly. This will not affect implemented methods,
     * and new interface methods will have to be implemented manually either through delegation or
     * intercepting.
     *
     * @param interfaces The interfaces to implement
     * @return This factory
     */
    ProxyFactory<T> implement(Class<?>... interfaces);

    /**
     * Creates a proxy instance of the active {@link #type()} and returns it. This will create a new proxy,
     * as well as a new {@link ProxyManager} responsible for managing the proxy. The proxy will be created
     * with all currently known behaviors.
     *
     * <p>If the proxy could not be created, {@link Option#empty()} will be returned.
     *
     * @return A proxy instance
     * @throws ApplicationException If the proxy could not be created
     */
    Option<T> proxy() throws ApplicationException;

    /**
     * Creates a proxy instance of the given {@code type} and returns it. This will create a new proxy and
     * invokes the given {@link ConstructorView} to create the proxy instance. This also creates a new
     * {@link ProxyManager} responsible for managing the proxy. The proxy will be created with all currently
     * known behaviors.
     *
     * <p>If the proxy could not be created, {@link Option#empty()} will be returned.
     *
     * @param constructor The constructor to use
     * @param args The arguments to pass to the constructor
     * @return A proxy instance
     * @throws ApplicationException If the proxy could not be created
     */
    Option<T> proxy(ConstructorView<? extends T> constructor, Object[] args) throws ApplicationException;

    /**
     * Creates a proxy instance of the given {@code type} and returns it. This will create a new proxy and
     * invokes the given {@link Constructor} to create the proxy instance. This also creates a new
     * {@link ProxyManager} responsible for managing the proxy. The proxy will be created with all currently
     * known behaviors.
     *
     * @param constructor The constructor to use
     * @param args The arguments to pass to the constructor
     * @return A proxy instance
     * @throws ApplicationException If the proxy could not be created
     */
    Option<T> proxy(Constructor<? extends T> constructor, Object[] args) throws ApplicationException;

    /**
     * Gets the type of the proxy. This will return the original type, and not a proxy type.
     * @return The type of the proxy
     */
    Class<T> type();

    /**
     * Gets all currently known interfaces. This will return an empty set if no interfaces were set. This
     * will not include {@link Proxy}.
     *
     * @return All known interfaces, or an empty set
     */
    Set<Class<?>> interfaces();

    /**
     * Gets a temporary context for the current proxy factory. When a new proxy is created, this
     * context will be assigned to its {@link ProxyManager}.
     * @return The temporary context
     */
    ProxyContextContainer contextContainer();
}
