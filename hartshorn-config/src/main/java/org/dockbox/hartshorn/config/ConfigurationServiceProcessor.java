package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.di.GenericType;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.FileTypeAttribute;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationServiceProcessor implements ServiceProcessor<UseConfigurations> {

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

        String file = configuration.source();
        Class<?> owner = configuration.owner();

        final FileManager fileManager = context.get(FileManager.class, FileTypeAttribute.of(FileType.YAML));
        final Path config = fileManager.configFile(owner, file);

        final Exceptional<HashMap<String, Object>> cache = fileManager.read(config, new GenericType<>() {
        });

        if (cache.absent()) return;

        final Map<String, Object> localCache = cache.map(read -> (Map<String, Object>) read).get();
        context.properties(localCache);
    }
}
