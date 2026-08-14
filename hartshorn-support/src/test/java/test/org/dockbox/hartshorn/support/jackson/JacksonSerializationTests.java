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

package test.org.dockbox.hartshorn.support.jackson;

import org.dockbox.hartshorn.support.jackson.modules.MultiMapDeserializer;
import org.dockbox.hartshorn.support.jackson.modules.MultiMapSerializer;
import org.dockbox.hartshorn.support.jackson.modules.OptionDeserializer;
import org.dockbox.hartshorn.support.jackson.modules.OptionSerializer;
import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonSerializationTests {

    @Test
    void testMultiMapSerialization() {
        JsonMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(new MultiMapSerializer())
                        .addDeserializer(MultiMap.class, new MultiMapDeserializer())
                )
                .build();

        MultiMap<String, String> map = new ArrayListMultiMap<>();
        map.put("key1", "value1");
        map.put("key1", "value2");
        map.put("key2", "value3");

        String serialized = mapper.writeValueAsString(map);
        MultiMap<String, String> deserialized = mapper.readValue(
                serialized,
                new TypeReference<>() {}
        );

        assertThat(deserialized).isInstanceOf(ArrayListMultiMap.class);
        assertThat(deserialized.get("key1")).containsExactlyInAnyOrder("value1", "value2");
        assertThat(deserialized.get("key2")).containsExactly("value3");
    }

    @Test
    void testOptionSerialization() {
        JsonMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(new OptionSerializer())
                        .addDeserializer(Option.class, new OptionDeserializer())
                )
                .build();

        Option<String> option = Option.of("value");
        String serialized = mapper.writeValueAsString(option);

        Option<String> deserialized = mapper.readValue(serialized, new TypeReference<>() {});
        assertThat(deserialized.present()).isTrue();
        assertThat(deserialized.get()).isEqualTo("value");
    }
}
