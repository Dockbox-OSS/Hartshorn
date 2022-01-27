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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ProcessingOrder;
import org.dockbox.hartshorn.core.services.ServicePreProcessor;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processes all services annotated with {@link Configuration} by loading the indicated file and registering the
 * properties to {@link ApplicationContext#property(String, Object)}. To support different file sources
 * {@link ResourceLookupStrategy strategies} are used. Each strategy is able to define behavior specific to sources
 * defined with its name. Strategies can be indicated in the {@link Configuration#source()} of a {@link Configuration}
 * in the format {@code strategy_name:source_name}. If a strategy is not registered, or no name is defined, behavior
 * defaults to {@link FileSystemLookupStrategy}.
 */
@AutomaticActivation
public class ConfigurationServicePreProcessor implements ServicePreProcessor<UseConfigurations> {

    private final Pattern STRATEGY_PATTERN = Pattern.compile("(.+):(.+)");
    private final Map<String, ResourceLookupStrategy> strategies = new ConcurrentHashMap<>();

    public ConfigurationServicePreProcessor() {
        this.addStrategy(new ClassPathResourceLookupStrategy());
        this.addStrategy(new FileSystemLookupStrategy());
    }

    public void addStrategy(final ResourceLookupStrategy strategy) {
        this.strategies.put(strategy.name(), strategy);
    }

    @Override
    public Class<UseConfigurations> activator() {
        return UseConfigurations.class;
    }

    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        return key.type().annotation(Configuration.class).present();
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final Configuration configuration = key.type().annotation(Configuration.class).get();

        String source = configuration.source();
        final TypeContext<?> owner = TypeContext.of(configuration.owner());
        final FileFormats filetype = configuration.filetype();

        final Matcher matcher = this.STRATEGY_PATTERN.matcher(source);
        ResourceLookupStrategy strategy = new FileSystemLookupStrategy();
        if (matcher.find()) {
            strategy = this.strategies.getOrDefault(matcher.group(1), strategy);
            source = matcher.group(2);
        }

        context.log().debug("Determined strategy " + TypeContext.of(strategy).name() + " for " + filetype.asFileName(source) + ", declared by " + key.type().name());

        URI config = strategy.lookup(context, source, owner, filetype).orNull();

        if (config == null) config = new FileSystemLookupStrategy().lookup(context, source, owner, filetype).get();
        final Map<String, Object> cache = context.get(ObjectMapper.class)
                .fileType(filetype)
                .flat(config);

        context.log().debug("Located " + cache.size() + " in source " + filetype.asFileName(source));
        context.properties(cache);
    }

    @Override
    public ProcessingOrder order() {
        return ProcessingOrder.FIRST;
    }
}
