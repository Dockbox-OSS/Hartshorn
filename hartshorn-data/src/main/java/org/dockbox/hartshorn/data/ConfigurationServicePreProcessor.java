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

package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.data.annotations.Configuration;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.data.context.ConfigurationURIContext;
import org.dockbox.hartshorn.data.context.ConfigurationURIContextList;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processes all services annotated with {@link Configuration} by loading the indicated file and registering the
 * properties to {@link PropertyHolder#set(String, Object)}. To support different file sources
 * {@link ResourceLookupStrategy strategies} are used. Each strategy is able to define behavior specific to sources
 * defined with its name. Strategies can be indicated in the {@link Configuration#value()} of a {@link Configuration}
 * in the format {@code strategy_name:source_name}. If a strategy is not registered, or no name is defined, behavior
 * defaults to {@link FileSystemLookupStrategy}.
 */
public class ConfigurationServicePreProcessor implements ComponentPreProcessor {

    private final Pattern STRATEGY_PATTERN = Pattern.compile("(.+):(.+)");
    private final Map<String, ResourceLookupStrategy> strategies = new ConcurrentHashMap<>();

    public ConfigurationServicePreProcessor() {
        this.registerDefaultStrategies();
    }

    /**
     * Registers the default strategies for this pre-processor. Protected to allow subclasses to
     * override the default strategies.
     */
    protected void registerDefaultStrategies() {
        this.addStrategy(new ClassPathResourceLookupStrategy());
        this.addStrategy(new FileSystemLookupStrategy());
    }

    public void addStrategy(final ResourceLookupStrategy strategy) {
        this.strategies.put(strategy.name(), strategy);
    }

    @Override
    public boolean modifies(final ApplicationContext context, final Key<?> key) {
        return key.type().annotation(Configuration.class).present();
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final Configuration configuration = key.type().annotation(Configuration.class).get();

        final String[] sources = configuration.value();

        for (final String source : sources) {
            if (this.processSource(source, context, key)) return;
            context.log().debug("Skipped configuration source '{}', proceeding to next source if available", source);
        }

        if (configuration.failOnMissing()) {
            throw new IllegalStateException("None of the configured sources in " + key.type().name() + " were found");
        } else {
            context.log().warn("None of the configured sources in {} were found, proceeding without configuration", key.type().name());
        }
    }

    private <T> boolean processSource(final String source, final ApplicationContext context, final Key<T> key) {
        String matchedSource = source;
        final Matcher matcher = this.STRATEGY_PATTERN.matcher(matchedSource);
        ResourceLookupStrategy strategy = new FileSystemLookupStrategy();
        if (matcher.find()) {
            strategy = this.strategies.getOrDefault(matcher.group(1), strategy);
            matchedSource = matcher.group(2);
        }
        context.log().debug("Determined strategy " + TypeContext.of(strategy).name() + " for " + matchedSource + ", declared by " + key.type().name());

        final Set<URI> config = strategy.lookup(context, matchedSource);
        if (config.isEmpty()) {
            context.log().warn("No configuration file found for " + key.type().name());
            return false;
        }
        else if (config.size() > 1) {
            context.log().warn("Found multiple configuration files for " + key.type().name() + ": " + config);
        }

        final ConfigurationURIContextList uriContextList = context.first(ConfigurationURIContextList.class).get();
        final ConfigurationURIContext uriContext = new ConfigurationURIContext(config.iterator().next(), key, matchedSource, strategy);
        uriContextList.add(uriContext);

        return true;
    }

    @Override
    public Integer order() {
        // Run before the ProviderServicePreProcessor, so this context is available for configuration objects
        return (Integer.MIN_VALUE / 2) - 256;
    }
}
