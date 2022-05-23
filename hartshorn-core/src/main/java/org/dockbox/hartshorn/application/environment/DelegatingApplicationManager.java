/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.application.environment;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.Hartshorn;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationManager;
import org.dockbox.hartshorn.logging.LogExclude;
import org.dockbox.hartshorn.context.ModifiableContextCarrier;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The default implementation of the {@link ApplicationManager} interface. This implementation delegates most functionality
 * to concrete implementations of each of the {@link ApplicationManager} parent interfaces. If any of these implementations
 * also implement {@link ApplicationManaged}, this implementation will ensure {@link ApplicationManaged#applicationManager(ApplicationManager)}
 * is called on them.
 *
 * @author Guus Lieben
 * @since 21.9
 */
@LogExclude
public class DelegatingApplicationManager implements ObservableApplicationManager, ModifiableContextCarrier {

    private static final String BANNER = """
                 _   _            _       _                     \s
                | | | | __ _ _ __| |_ ___| |__   ___  _ __ _ __ \s
                | |_| |/ _` | '__| __/ __| '_ \\ / _ \\| '__| '_ \\\s
                |  _  | (_| | |  | |_\\__ \\ | | | (_) | |  | | | |
            ====|_| |_|\\__,_|_|===\\__|___/_|=|_|\\___/|_|==|_|=|_|====
                                             -- Hartshorn v%s --
            """.formatted(Hartshorn.VERSION);

    private final Set<LifecycleObserver> observers = ConcurrentHashMap.newKeySet();
    private final ApplicationFSProvider applicationFSProvider;
    private final ApplicationLogger applicationLogger;
    private final ApplicationProxier applicationProxier;
    private final boolean isCI;

    private ApplicationContext applicationContext;
    private ExceptionHandler exceptionHandler;

    public DelegatingApplicationManager(
            final TypeContext<?> activator,
            final ApplicationLogger applicationLogger,
            final ApplicationProxier applicationProxier,
            final ApplicationFSProvider applicationFSProvider,
            final ExceptionHandler exceptionHandler) {
        if (applicationLogger instanceof ApplicationManaged applicationManaged)
            applicationManaged.applicationManager(this);
        this.exceptionHandler = exceptionHandler;

        if (applicationLogger instanceof ApplicationManaged applicationManaged)
            applicationManaged.applicationManager(this);
        this.applicationLogger = applicationLogger;

        if (applicationProxier instanceof ApplicationManaged applicationManaged)
            applicationManaged.applicationManager(this);
        this.applicationProxier = applicationProxier;

        if (applicationFSProvider instanceof ApplicationManaged applicationManaged)
            applicationManaged.applicationManager(this);
        this.applicationFSProvider = applicationFSProvider;

        this.isCI = this.checkCI();

        if (!this.isCI()) this.printHeader(activator);
    }

    @Override
    public Set<LifecycleObserver> observers() {
        return this.observers;
    }

    public ApplicationFSProvider applicationFSProvider() {
        return this.applicationFSProvider;
    }

    public ApplicationLogger applicationLogger() {
        return this.applicationLogger;
    }

    public ApplicationProxier applicationProxier() {
        return this.applicationProxier;
    }

    public boolean CI() {
        return this.isCI;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    public ExceptionHandler exceptionHandler() {
        return this.exceptionHandler;
    }

    public DelegatingApplicationManager exceptionHandler(final ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    protected boolean checkCI() {
        for (final StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) return true;
        }

        return System.getenv().containsKey("GITLAB_CI")
                || System.getenv().containsKey("JENKINS_HOME")
                || System.getenv().containsKey("TRAVIS")
                || System.getenv().containsKey("GITHUB_ACTIONS")
                || System.getenv().containsKey("APPVEYOR");
    }

    private void printHeader(final TypeContext<?> activator) {
        final Logger logger = LoggerFactory.getLogger(activator.type());
        for (final String line : BANNER.split("\n")) {
            logger.info(line);
        }
        logger.info("");
    }

    @Override
    public boolean isCI() {
        return this.isCI;
    }

    @Override
    public Logger log() {
        return this.applicationLogger.log();
    }

    @Override
    public <T> Result<TypeContext<T>> real(final T instance) {
        return this.applicationProxier.real(instance);
    }

    @Override
    public <T> Result<ProxyManager<T>> manager(final T instance) {
        return this.applicationProxier.manager(instance);
    }

    @Override
    public <D, T extends D> Result<D> delegate(final TypeContext<D> type, final T instance) {
        return this.applicationProxier.delegate(type, instance);
    }

    @Override
    public <T> StateAwareProxyFactory<T, ?> factory(final TypeContext<T> type) {
        return this.applicationProxier.factory(type);
    }

    @Override
    public <T> StateAwareProxyFactory<T, ?> factory(final Class<T> type) {
        return this.applicationProxier.factory(type);
    }

    public DelegatingApplicationManager applicationContext(final ApplicationContext applicationContext) {
        if (this.applicationContext == null) this.applicationContext = applicationContext;
        else throw new IllegalArgumentException("Application context has already been configured");
        return this;
    }

    @Override
    public void register(final LifecycleObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public Path applicationPath() {
        return this.applicationFSProvider.applicationPath();
    }

    @Override
    public <T> Class<T> unproxy(final T instance) {
        return this.applicationProxier.unproxy(instance);
    }

    @Override
    public boolean isProxy(final Object instance) {
        return this.applicationProxier.isProxy(instance);
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return this.applicationProxier.isProxy(candidate);
    }

    @Override
    public void handle(final Throwable throwable) {
        this.exceptionHandler.handle(throwable);
    }

    @Override
    public void handle(final String message, final Throwable throwable) {
        this.exceptionHandler.handle(message, throwable);
    }

    @Override
    public ExceptionHandler stacktraces(final boolean stacktraces) {
        return this.exceptionHandler.stacktraces(stacktraces);
    }
}
