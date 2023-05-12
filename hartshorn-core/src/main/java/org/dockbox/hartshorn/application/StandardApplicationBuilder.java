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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.contextual.UseStaticBinding;
import org.dockbox.hartshorn.inject.processing.UseServiceProvision;
import org.dockbox.hartshorn.component.UseProxying;
import org.dockbox.hartshorn.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.function.Function;

public class StandardApplicationBuilder extends DefaultApplicationBuilder<StandardApplicationBuilder, ApplicationContext> {

    private enum FactoryState {
        WAITING,
        CREATING,
    }

    private FactoryState state = FactoryState.WAITING;
    protected Function<Logger, ApplicationContextConstructor> constructor;

    public StandardApplicationBuilder constructor(final Function<Logger, ApplicationContextConstructor> constructor) {
        this.constructor = constructor;
        return this.self();
    }

    @Override
    public ApplicationContext create() {
        if (this.state == FactoryState.CREATING) {
            throw new IllegalStateException("Application factory is already creating a new application context");
        }
        this.state = FactoryState.CREATING;

        final Logger logger = LoggerFactory.getLogger(this.mainClass());

        final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        logger.info("Starting application " + this.mainClass().getSimpleName() + " on " + this.host(runtimeMXBean) + " using Java " + runtimeMXBean.getVmVersion() + " with PID " + runtimeMXBean.getPid());

        final long applicationStartTimestamp = System.currentTimeMillis();
        final ApplicationContext applicationContext = this.constructor.apply(logger).createContext(this);
        final long applicationStartedTimestamp = System.currentTimeMillis();

        final double startupTime = ((double) (applicationStartedTimestamp - applicationStartTimestamp)) / 1000;
        final double jvmUptime = ((double) runtimeMXBean.getUptime()) / 1000;

        logger.info("Started " + Hartshorn.PROJECT_NAME + " in " + startupTime + " seconds (JVM running for " + jvmUptime + ")");

        this.state = FactoryState.WAITING;

        return applicationContext;
    }

    protected String host(final RuntimeMXBean runtimeMXBean) {
        // Alternative to InetAddress.getLocalHost().getHostName()
        return runtimeMXBean.getName().split("@")[1];
    }

    public StandardApplicationBuilder loadDefaults() {
        return this.constructor(StandardApplicationContextConstructor::new)
                .serviceActivator(TypeUtils.annotation(UseBootstrap.class))
                .serviceActivator(TypeUtils.annotation(UseProxying.class))
                .serviceActivator(TypeUtils.annotation(UseServiceProvision.class))
                .serviceActivator(TypeUtils.annotation(UseStaticBinding.class));
    }

    @Override
    public StandardApplicationBuilder self() {
        return this;
    }
}
