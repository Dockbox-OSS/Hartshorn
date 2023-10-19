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

package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.support.CollectionToArrayConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CollectionToArrayConverterTests {

    @Test
    void testConversionKeepsOrderAndElements() {
        List<Object> list = List.of("test", 1, 2.0, true, new Object());

        Object converted = new CollectionToArrayConverter().convert(list, List.class, Object[].class);
        Assertions.assertNotNull(converted);

        Assertions.assertTrue(converted instanceof Object[]);
        Object[] array = (Object[]) converted;

        Assertions.assertEquals(list.size(), array.length);
        for (int i = 0; i < list.size(); i++) {
            Assertions.assertSame(list.get(i), array[i]);
        }
    }

    @Test
    void testComponentTypeIsRetained() {
        List<String> list = List.of("test", "test2", "test3");

        Object converted = new CollectionToArrayConverter().convert(list, List.class, String[].class);
        Assertions.assertNotNull(converted);

        Assertions.assertTrue(converted instanceof String[]);
        String[] array = (String[]) converted;

        Assertions.assertEquals(list.size(), array.length);
        for (int i = 0; i < list.size(); i++) {
            Assertions.assertEquals(list.get(i), array[i]);
        }
    }
}
