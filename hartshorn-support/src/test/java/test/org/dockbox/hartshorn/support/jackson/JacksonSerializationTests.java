package test.org.dockbox.hartshorn.support.jackson;

import org.dockbox.hartshorn.support.jackson.modules.MultiMapDeserializer;
import org.dockbox.hartshorn.support.jackson.modules.MultiMapSerializer;
import org.dockbox.hartshorn.support.jackson.modules.OptionDeserializer;
import org.dockbox.hartshorn.support.jackson.modules.OptionSerializer;
import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.MultiMapComparator;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;
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
                MultiMap.class
        );
        MultiMapComparator comparator = MultiMapComparator.INSTANCE;
        assertThat(comparator.compare(map, deserialized)).isEqualTo(0);
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

        Option<String> deserialized = mapper.readValue(serialized, Option.class);
        assertThat(deserialized.present()).isTrue();
        assertThat(deserialized.get()).isEqualTo("value");
    }
}
