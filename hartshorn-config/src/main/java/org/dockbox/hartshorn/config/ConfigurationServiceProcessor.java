package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public class ConfigurationServiceProcessor implements ServiceProcessor<UseConfigurations> {

    private static final Set<ResourceLookupStrategy> strategies = HartshornUtils.emptyConcurrentSet();

    static {
        addStrategy(new ClassPathResourceLookupStrategy());
    }

    public static void addStrategy(ResourceLookupStrategy strategy) {
        strategies.add(strategy);
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

        URI config = null;
        for (ResourceLookupStrategy strategy : strategies) {
            if (strategy.accepts(context, source, owner)) {
                config = strategy.lookup(context, source, owner, filetype).orNull();
                break;
            }
        }

        if (config == null) config = new DataPathLookupStrategy().lookup(context, source, owner, filetype).get();
        final Map<String, Object> cache = context.get(ObjectMapper.class)
                .fileType(filetype)
                .flat(config);

        context.properties(cache);
    }
}
