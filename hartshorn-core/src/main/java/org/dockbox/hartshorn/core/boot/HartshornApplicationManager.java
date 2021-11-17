/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.context.LogExclude;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Set;

import lombok.Getter;

@LogExclude
public class HartshornApplicationManager implements ApplicationManager {

    private static final String BANNER = """
                 _   _            _       _                     \s
                | | | | __ _ _ __| |_ ___| |__   ___  _ __ _ __ \s
                | |_| |/ _` | '__| __/ __| '_ \\ / _ \\| '__| '_ \\\s
                |  _  | (_| | |  | |_\\__ \\ | | | (_) | |  | | | |
            ====|_| |_|\\__,_|_|===\\__|___/_|=|_|\\___/|_|==|_|=|_|====
                                             -- Hartshorn v%s --
            """.formatted(Hartshorn.VERSION);

    @Getter
    private final Set<LifecycleObserver> observers = HartshornUtils.emptyConcurrentSet();
    private final ApplicationFSProvider applicationFSProvider;

    @Getter
    private ApplicationContext applicationContext;

    private final ApplicationLogger applicationLogger;
    private final ApplicationProxier applicationProxier;

    public HartshornApplicationManager(
            final ApplicationLogger applicationLogger,
            final ApplicationProxier applicationProxier,
            final ApplicationFSProvider applicationFSProvider
    ) {
        if (applicationLogger instanceof ApplicationManaged applicationManaged)
            applicationManaged.applicationManager(this);
        this.applicationLogger = applicationLogger;

        if (applicationProxier instanceof ApplicationManaged applicationManaged)
            applicationManaged.applicationManager(this);
        this.applicationProxier = applicationProxier;

        if (applicationFSProvider instanceof ApplicationManaged applicationManaged)
            applicationManaged.applicationManager(this);
        this.applicationFSProvider = applicationFSProvider;

        if (!this.isCI()) this.printHeader();
    }

    private void printHeader() {
        for (final String line : BANNER.split("\n")) {
            Hartshorn.log().info(line);
        }
        Hartshorn.log().info("");
    }

    @Override
    public boolean isCI() {
        return HartshornUtils.isCI();
    }

    @Override
    public Logger log() {
        return this.applicationLogger.log();
    }

    @Override
    public <T> Exceptional<T> proxy(final TypeContext<T> type, final T instance) {
        return this.applicationProxier.proxy(type, instance);
    }

    @Override
    public <T> Exceptional<TypeContext<T>> real(final T instance) {
        return this.applicationProxier.real(instance);
    }

    @Override
    public <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, final P instance) {
        return this.applicationProxier.delegator(type, instance);
    }

    @Override
    public <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, final ProxyHandler<P> handler) {
        return this.applicationProxier.delegator(type, handler);
    }

    @Override
    public <T> ProxyHandler<T> handler(final TypeContext<T> type, final T instance) {
        return this.applicationProxier.handler(type, instance);
    }

    public void applicationContext(final ApplicationContext applicationContext) {
        if (this.applicationContext == null) this.applicationContext = applicationContext;
        else throw new IllegalArgumentException("Application context has already been configured");
    }

    @Override
    public void register(final LifecycleObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public Path applicationPath() {
        return this.applicationFSProvider.applicationPath();
    }
}
