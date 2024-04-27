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

package org.dockbox.hartshorn.config;

import java.net.URI;
import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.config.annotations.IncludeResourceConfiguration;
import org.dockbox.hartshorn.config.properties.PropertyHolder;
import org.dockbox.hartshorn.util.resources.FileSystemLookupStrategy;
import org.dockbox.hartshorn.util.resources.MissingSourceException;
import org.dockbox.hartshorn.util.resources.ResourceLookup;
import org.dockbox.hartshorn.util.resources.ResourceLookupStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes all services annotated with {@link IncludeResourceConfiguration} by loading the indicated file and registering the
 * properties to {@link PropertyHolder#set(String, Object)}. To support different file sources
 * {@link ResourceLookupStrategy strategies} are used. Each strategy is able to define behavior specific to sources
 * defined with its name. Strategies can be indicated in the {@link IncludeResourceConfiguration#value()} of a {@link IncludeResourceConfiguration}
 * in the format {@code strategy_name:source_name}. If a strategy is not registered, or no name is defined, behavior
 * defaults to {@link FileSystemLookupStrategy}.
 *
 * @since 0.4.7
 *
 * @author Guus Lieben
 */
public class ConfigurationServicePreProcessor extends ComponentPreProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationServicePreProcessor.class);

    @Override
    public <T> void process(ApplicationContext context, ComponentProcessingContext<T> processingContext) {
        if (processingContext.type().annotations().has(IncludeResourceConfiguration.class)) {
            IncludeResourceConfiguration configuration = processingContext.type().annotations().get(IncludeResourceConfiguration.class).get();
            String[] sources = configuration.value();

            for (String source : sources) {
                if (this.processSource(source, context, processingContext.key())) {
                    return;
                }
                LOG.debug("Skipped configuration source '{}', proceeding to next source if available", source);
            }

            if (configuration.failOnMissing()) {
                throw new MissingSourceException("None of the configured sources in " + processingContext.type().name() + " were found");
            }
            else {
                LOG.warn("None of the configured sources in {} were found, proceeding without configuration", processingContext.type().name());
            }
        }
    }

    private <T> boolean processSource(String source, ApplicationContext context, ComponentKey<T> key) {
        ResourceLookup resourceLookup = context.get(ResourceLookup.class);
        Set<URI> config = resourceLookup.lookup(source);

        if (config.isEmpty()) {
            LOG.warn("Configuration source '%s' was not found for %s".formatted(source, key.type().getSimpleName()));
            return false;
        }
        else if (config.size() > 1) {
            LOG.warn("Found multiple configuration files for " + key.type().getSimpleName() + ": " + config);
        }

        ConfigurationURIContextList uriContextList = context.firstContext(ConfigurationURIContextList.CONTEXT_KEY).get();
        for (URI uri : config) {
            ConfigurationURIContext uriContext = new ConfigurationURIContext(uri, key, source);
            uriContextList.add(uriContext);
        }

        return true;
    }

    @Override
    public int priority() {
        // Run before the ProviderServicePreProcessor, so this context is available for configuration objects
        return ProcessingPriority.HIGH_PRECEDENCE;
    }
}
