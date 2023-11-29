/*
 * Copyright 2019-2023 the original author or authors.
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

    public StandardJacksonObjectMapperConfigurator(Introspector introspector) {
        this.introspector = introspector;
    }

    @Override
    public MapperBuilder<?, ?> configure(MapperBuilder<?, ?> builder, FileFormat format, JsonInclusionRule inclusionRule) {
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
