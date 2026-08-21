/*
 * Copyright 2019-2026 the original author or authors.
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
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.TypeUtils;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

/**
 * Configuration for Jackson support in Hartshorn. This configuration provides various bindings and
 * customizations for Jackson's {@link ObjectMapper} and related components.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresClass(classes = ObjectMapper.class)
public class JacksonSupportConfiguration {

    /**
     * Creates an {@link ObjectMapper} using the provided {@link MapperBuilder} and applies any
     * customizations from the given {@link Customizer} collection.
     *
     * @param builder the {@link MapperBuilder} to use for creating the {@link ObjectMapper}
     * @param customizers a collection of {@link Customizer} instances to apply to the builder
     *
     * @return a configured {@link ObjectMapper} instance
     */
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

    /**
     * Creates a {@link Customizer} for the {@link MapperBuilder} that adds all provided
     * {@link JacksonModule} instances to the builder.
     *
     * @param modules a collection of {@link JacksonModule} instances to add to the builder
     * @return a {@link Customizer} that adds the provided modules to the {@link MapperBuilder}
     */
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

    /**
     * Creates a {@link JacksonModule} that registers all known {@link ValueSerializer} and
     * {@link ValueDeserializer} instances.
     *
     * @param serializers a collection of {@link ValueSerializer} instances to register
     * @param deserializers a collection of {@link ValueDeserializer} instances to register
     * @return a {@link JacksonModule} that registers the provided serializers and deserializers
     */
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

    /**
     * Configuration for Jackson JSON support.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    @Configuration
    @RequiresClass(classes = JsonMapper.class)
    public static class JacksonJsonSupportConfiguration {

        /**
         * Creates a {@link MapperBuilder} for JSON using Jackson's {@link JsonMapper}. This builder
         * will automatically find and add any available modules through the service loader
         * mechanism.
         *
         * @return a {@link MapperBuilder} for JSON with auto-discovered modules
         */
        @Singleton
        public MapperBuilder<?, ?> jsonObjectMapperBuilder() {
            return JsonMapper.builder().findAndAddModules();
        }
    }

    /**
     * Configuration for Jackson support modules, including serializers and deserializers for
     * specific types introduced by Hartshorn.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    @Configuration
    @RequiresProperty(
            name = "hartshorn.jackson.support.include-defaults",
            withValue = "true",
            matchIfMissing = true
    )
    public static class JacksonSupportModuleConfiguration {

        /**
         * Creates a {@link ValueSerializer} for Hartshorn's {@link MultiMap}.
         *
         * @return a {@link ValueSerializer} for {@link MultiMap}
         */
        @Singleton
        @CompositeMember
        public ValueSerializer<?> multiMapSerializer() {
            return new MultiMapSerializer();
        }

        /**
         * Creates a {@link ValueDeserializer} for Hartshorn's {@link MultiMap}.
         *
         * @return a {@link ValueDeserializer} for {@link MultiMap}
         */
        @Singleton
        @CompositeMember
        public ValueDeserializer<?> multiMapDeserializer() {
            return new MultiMapDeserializer();
        }

        /**
         * Creates a {@link ValueSerializer} for Hartshorn's {@link Option}.
         *
         * @return a {@link ValueSerializer} for {@link Option}
         */
        @Singleton
        @CompositeMember
        public ValueSerializer<?> optionSerializer() {
            return new OptionSerializer();
        }

        /**
         * Creates a {@link ValueDeserializer} for Hartshorn's {@link Option}.
         *
         * @return a {@link ValueDeserializer} for {@link Option}
         */
        @Singleton
        @CompositeMember
        public ValueDeserializer<?> optionDeserializer() {
            return new OptionDeserializer();
        }
    }
}
