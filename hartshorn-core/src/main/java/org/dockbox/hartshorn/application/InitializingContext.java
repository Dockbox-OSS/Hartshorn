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
import org.dockbox.hartshorn.application.environment.ApplicationArgumentParser;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentPostConstructor;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;

import java.util.Objects;

public final class InitializingContext extends DefaultApplicationAwareContext implements Reportable {

    private final ApplicationEnvironment environment;
    private final ApplicationBuilder<?, ?> configuration;

    public InitializingContext(final ApplicationEnvironment environment, final ApplicationContext applicationContext, final ApplicationBuilder<?, ?> builder) {
        super(applicationContext);
        this.environment = environment;
        this.configuration = Objects.requireNonNull(builder);
    }

    @Override
    protected boolean permitNullableApplicationContext() {
        return true;
    }

    public ApplicationEnvironment environment() {
        return Objects.requireNonNull(this.environment, "Application environment has not been initialized yet");
    }

    @Override
    public ApplicationContext applicationContext() {
        return Objects.requireNonNull(super.applicationContext(), "Application context has not been initialized yet");
    }

    public ConditionMatcher conditionMatcher() {
        return this.configuration.conditionMatcher(this);
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

    public AnnotationLookup annotationLookup() {
        return this.configuration.annotationLookup(this);
    }

    public ComponentProvider componentProvider() {
        return this.configuration.componentProvider(this);
    }

    public ComponentPostConstructor componentPostConstructor() {
        return this.configuration.componentPostConstructor(this);
    }

    public ComponentPopulator componentPopulator() {
        return this.configuration.componentPopulator(this);
    }

    public ViewContextAdapter viewContextAdapter() {
        return this.configuration.viewContextAdapter(this);
    }

    public ApplicationBuilder<?, ?> builder() {
        return this.configuration;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        final var that = (InitializingContext) obj;
        return Objects.equals(this.environment, that.environment) &&
                Objects.equals(super.applicationContext(), that.applicationContext()) &&
                Objects.equals(this.configuration, that.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.environment, this.applicationContext(), this.configuration);
    }

    @Override
    public String toString() {
        return "InitializingContext[" +
                "environment=" + this.environment + ", " +
                "applicationContext=" + this.applicationContext() + ", " +
                "configuration=" + this.configuration + ']';
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        this.configuration.report(collector);
    }
}
