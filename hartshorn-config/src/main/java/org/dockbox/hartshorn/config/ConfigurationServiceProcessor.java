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
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ServiceOrder;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.net.URI;
import java.util.Map;
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
public class ConfigurationServiceProcessor implements ServiceProcessor<UseConfigurations> {

    private static final Pattern STRATEGY_PATTERN = Pattern.compile("(.+):(.+)");
    private static final Map<String, ResourceLookupStrategy> strategies = HartshornUtils.emptyConcurrentMap();

    static {
        addStrategy(new ClassPathResourceLookupStrategy());
        addStrategy(new FileSystemLookupStrategy());
    }

    public static void addStrategy(ResourceLookupStrategy strategy) {
        strategies.put(strategy.name(), strategy);
    }

    @Override
    public Class<UseConfigurations> activator() {
        return UseConfigurations.class;
    }

    @Override
    public boolean preconditions(ApplicationContext context, TypeContext<?> type) {
        return type.annotation(Configuration.class).present();
    }

    @Override
    public <T> void process(ApplicationContext context, TypeContext<T> type) {
        final Configuration configuration = type.annotation(Configuration.class).get();

        String source = configuration.source();
        TypeContext<?> owner = TypeContext.of(configuration.owner());
        FileType filetype = configuration.filetype();

        final Matcher matcher = STRATEGY_PATTERN.matcher(source);
        ResourceLookupStrategy strategy = new FileSystemLookupStrategy();
        if (matcher.find()) {
            strategy = strategies.getOrDefault(matcher.group(1), strategy);
            source = matcher.group(2);
        }

        URI config = strategy.lookup(context, source, owner, filetype).orNull();

        if (config == null) config = new FileSystemLookupStrategy().lookup(context, source, owner, filetype).get();
        final Map<String, Object> cache = context.get(ObjectMapper.class)
                .fileType(filetype)
                .flat(config);

        context.properties(cache);
    }

    @Override
    public ServiceOrder order() {
        return ServiceOrder.FIRST;
    }
}
