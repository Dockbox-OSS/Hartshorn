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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ServiceOrder;
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
    public boolean preconditions(final ApplicationContext context, final TypeContext<?> type) {
        return type.annotation(Configuration.class).present();
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        final Configuration configuration = type.annotation(Configuration.class).get();

        String source = configuration.source();
        final TypeContext<?> owner = TypeContext.of(configuration.owner());
        final FileFormats filetype = configuration.filetype();

        final Matcher matcher = this.STRATEGY_PATTERN.matcher(source);
        ResourceLookupStrategy strategy = new FileSystemLookupStrategy();
        if (matcher.find()) {
            strategy = this.strategies.getOrDefault(matcher.group(1), strategy);
            source = matcher.group(2);
        }

        context.log().debug("Determined strategy " + TypeContext.of(strategy).name() + " for " + filetype.asFileName(source) + ", declared by " + type.name());

        URI config = strategy.lookup(context, source, owner, filetype).orNull();

        if (config == null) config = new FileSystemLookupStrategy().lookup(context, source, owner, filetype).get();
        final Map<String, Object> cache = context.get(ObjectMapper.class)
                .fileType(filetype)
                .flat(config);

        context.log().debug("Located " + cache.size() + " in source " + filetype.asFileName(source));
        context.properties(cache);
    }

    @Override
    public ServiceOrder order() {
        return ServiceOrder.FIRST;
    }
}