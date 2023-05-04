package org.dockbox.hartshorn.config.jackson.beta;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter.Value;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.IncludeRule;
import org.dockbox.hartshorn.config.beta.SerializerConfigurer;
import org.dockbox.hartshorn.config.jackson.JacksonObjectMapperConfigurator;
import org.dockbox.hartshorn.config.jackson.introspect.HartshornJacksonAnnotationIntrospector;
import org.dockbox.hartshorn.util.introspect.Introspector;

public class StandardJacksonObjectMapperConfigurator implements JacksonObjectMapperConfigurator {

    private final Introspector introspector;
    private final SerializerConfigurer configurer;

    public StandardJacksonObjectMapperConfigurator(final Introspector introspector, final SerializerConfigurer configurer) {
        this.introspector = introspector;
        this.configurer = configurer;
    }

    @Override
    public MapperBuilder<?, ?> configure(final MapperBuilder<?, ?> builder, final FileFormat format) {
        MapperBuilder<?, ?> mapperBuilder = builder;
        mapperBuilder = this.applyDefaultConfiguration(builder);
        mapperBuilder = this.applyMapperConfiguration(mapperBuilder, this.configurer);
        return mapperBuilder;
    }

    protected MapperBuilder<?, ?> applyMapperConfiguration(final MapperBuilder<?, ?> builder, final SerializerConfigurer configurer) {
        return builder
                .serializationInclusion(this.getJsonIncludeRule(configurer.includeRule()));
    }

    protected MapperBuilder<?, ?> applyDefaultConfiguration(final MapperBuilder<?, ?> builder) {
        return builder
                .annotationIntrospector(this.getJacksonAnnotationIntrospector())
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .enable(Feature.ALLOW_COMMENTS)
                .enable(Feature.ALLOW_YAML_COMMENTS)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .defaultSetterInfo(Value.forContentNulls(Nulls.AS_EMPTY))
                // Hartshorn convention uses fluent style getters/setters, these are not picked up by Jackson
                // which would otherwise cause it to fail due to it recognizing the object as an empty bean,
                // even if it is not empty.
                .visibility(PropertyAccessor.FIELD, Visibility.ANY);
    }

    protected HartshornJacksonAnnotationIntrospector getJacksonAnnotationIntrospector() {
        return new HartshornJacksonAnnotationIntrospector(this.introspector);
    }

    protected JsonInclude.Include getJsonIncludeRule(final IncludeRule rule) {
        if (rule == null) {
            return Include.ALWAYS;
        }
        return switch (rule) {
            case SKIP_EMPTY -> Include.NON_EMPTY;
            case SKIP_NULL -> Include.NON_NULL;
            case SKIP_DEFAULT -> Include.NON_DEFAULT;
            case SKIP_NONE -> Include.ALWAYS;
        };
    }
}
