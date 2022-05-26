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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationArgumentParser;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
import org.dockbox.hartshorn.application.environment.ApplicationManager;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.application.scan.PrefixContext;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.inject.MetaProvider;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.proxy.ApplicationProxier;

import java.util.Objects;

public class InitializingContext {

    private final ApplicationEnvironment environment;
    private final ApplicationContext applicationContext;
    private final ApplicationManager manager;
    private final ApplicationContextConfiguration configuration;

    public InitializingContext(final ApplicationEnvironment environment, final ApplicationContext applicationContext, final ApplicationManager manager, final ApplicationContextConfiguration configuration) {
        this.environment = environment;
        this.applicationContext = applicationContext;
        this.manager = manager;
        this.configuration = Objects.requireNonNull(configuration);
    }

    public ApplicationEnvironment environment() {
        return Objects.requireNonNull(this.environment, "Application environment has not been initialized yet");
    }

    public ApplicationContext applicationContext() {
        return Objects.requireNonNull(this.applicationContext, "Application context has not been initialized yet");
    }

    public ApplicationManager manager() {
        return Objects.requireNonNull(this.manager, "Application manager has not been initialized yet");
    }

    public ApplicationContextConfiguration configuration() {
        return this.configuration;
    }

    public ApplicationConfigurator applicationConfigurator() {
        return this.configuration.applicationConfigurator(this);
    }

    public ApplicationProxier applicationProxier() {
        return this.configuration.applicationProxier(this);
    }

    public ApplicationFSProvider applicationFSProvider() {
        return this.configuration.applicationFSProvider(this);
    }

    public ExceptionHandler exceptionHandler() {
        return this.configuration.exceptionHandler(this);
    }

    public ApplicationArgumentParser argumentParser() {
        return this.configuration.argumentParser(this);
    }

    public ApplicationLogger applicationLogger() {
        return this.configuration.applicationLogger(this);
    }

    public ApplicationEnvironment applicationEnvironment() {
        return this.configuration.applicationEnvironment(this);
    }

    public ComponentLocator componentLocator() {
        return this.configuration.componentLocator(this);
    }

    public ClasspathResourceLocator resourceLocator() {
        return this.configuration.resourceLocator(this);
    }

    public MetaProvider metaProvider() {
        return this.configuration.metaProvider(this);
    }

    public ComponentProvider componentProvider() {
        return this.configuration.componentProvider(this);
    }

    public ComponentPopulator componentPopulator() {
        return this.configuration.componentPopulator(this);
    }

    public PrefixContext prefixContext() {
        return this.configuration.prefixContext(this);
    }

    public ActivatorHolder activatorHolder() {
        return this.configuration.activatorHolder(this);
    }
}
