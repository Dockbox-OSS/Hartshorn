package org.dockbox.hartshorn.config.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
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
import org.dockbox.hartshorn.config.JsonInclusionRule;
import org.dockbox.hartshorn.config.jackson.introspect.HartshornJacksonAnnotationIntrospector;
import org.dockbox.hartshorn.util.introspect.Introspector;

import java.util.EnumMap;
import java.util.Map;

public class StandardJacksonObjectMapperConfigurator implements JacksonObjectMapperConfigurator {

    private static final Map<JsonInclusionRule, Include> RULE_MAPPINGS = new EnumMap<>(JsonInclusionRule.class);

    static {
        RULE_MAPPINGS.put(JsonInclusionRule.SKIP_EMPTY, Include.NON_EMPTY);
        RULE_MAPPINGS.put(JsonInclusionRule.SKIP_NULL, Include.NON_NULL);
        RULE_MAPPINGS.put(JsonInclusionRule.SKIP_DEFAULT, Include.NON_DEFAULT);
        RULE_MAPPINGS.put(JsonInclusionRule.SKIP_NONE, Include.ALWAYS);
    }

    private final Introspector introspector;

    public StandardJacksonObjectMapperConfigurator(final Introspector introspector) {
        this.introspector = introspector;
    }

    @Override
    public MapperBuilder<?, ?> configure(final MapperBuilder<?, ?> builder, final FileFormat format, final JsonInclusionRule inclusionRule) {
        MapperBuilder<?, ?> mb = builder
                .annotationIntrospector(new HartshornJacksonAnnotationIntrospector(this.introspector))
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
        if (inclusionRule != null) {
            assert RULE_MAPPINGS.containsKey(inclusionRule) : "Unknown inclusion rule " + inclusionRule;
            mb = mb.serializationInclusion(RULE_MAPPINGS.get(inclusionRule));
        }
        return mb;
    }
}
