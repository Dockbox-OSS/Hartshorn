package org.dockbox.hartshorn.support.jackson;

import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.Fuzzy;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.condition.support.RequiresClass;
import org.dockbox.hartshorn.inject.condition.support.RequiresProperty;
import org.dockbox.hartshorn.support.jackson.modules.MultiMapDeserializer;
import org.dockbox.hartshorn.support.jackson.modules.MultiMapSerializer;
import org.dockbox.hartshorn.support.jackson.modules.OptionDeserializer;
import org.dockbox.hartshorn.support.jackson.modules.OptionSerializer;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.types.TypeUtils;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

@Configuration
@RequiresClass(classes = ObjectMapper.class)
public class JacksonSupportConfiguration {

    @Singleton
    public ObjectMapper objectMapper(
            MapperBuilder<?, ?> builder,
            @Fuzzy ComponentCollection<Customizer<MapperBuilder<?, ?>>> customizers
    ) {
        for (Customizer<MapperBuilder<?, ?>> customizer : customizers) {
            customizer.configure(builder);
        }
        return builder.build();
    }

    @Singleton
    @CompositeMember
    public Customizer<MapperBuilder<?, ?>> jacksonMapperCustomizer(
            @Fuzzy ComponentCollection<JacksonModule> modules
    ) {
        return builder -> {
            for (JacksonModule module : modules) {
                builder.addModule(module);
            }
        };
    }

    @Singleton
    @CompositeMember
    public JacksonModule hartshornSupportModule(
            @Fuzzy ComponentCollection<ValueSerializer<?>> serializers,
            @Fuzzy ComponentCollection<ValueDeserializer<?>> deserializers
    ) {
        SimpleModule module = new SimpleModule();
        for (ValueSerializer<?> serializer : serializers) {
            module.addSerializer(serializer);
        }
        for (ValueDeserializer<?> deserializer : deserializers) {
            module.addDeserializer(
                    deserializer.handledType(),
                    TypeUtils.unchecked(deserializer, ValueDeserializer.class)
            );
        }
        return module;
    }

    @Configuration
    @RequiresClass(classes = JsonMapper.class)
    public static class JacksonJsonSupportConfiguration {

        @Singleton
        public MapperBuilder<?, ?> jsonObjectMapperBuilder() {
            return JsonMapper.builder().findAndAddModules();
        }
    }

    @Configuration
    @RequiresProperty(
            name = "hartshorn.jackson.support.include-defaults",
            withValue = "true",
            matchIfMissing = true
    )
    public static class JacksonSupportModuleConfiguration {

        @Singleton
        @CompositeMember
        public ValueSerializer<?> multiMapSerializer() {
            return new MultiMapSerializer();
        }

        @Singleton
        @CompositeMember
        public ValueDeserializer<?> multiMapDeserializer() {
            return new MultiMapDeserializer();
        }

        @Singleton
        @CompositeMember
        public ValueSerializer<?> optionSerializer() {
            return new OptionSerializer();
        }

        @Singleton
        @CompositeMember
        public ValueDeserializer<?> optionDeserializer() {
            return new OptionDeserializer();
        }
    }
}
