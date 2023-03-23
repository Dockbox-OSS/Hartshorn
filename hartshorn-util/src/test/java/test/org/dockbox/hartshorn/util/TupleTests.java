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

package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map.Entry;

public class TupleTests {

    @Test
    public void testFirstValueIsStored() {
        final Entry<?, ?> tuple = new Tuple<>(1, "two");
        Assertions.assertEquals(1, tuple.getKey());
        Assertions.assertTrue(tuple.getKey() instanceof Integer);
    }

    @Test
    public void testSecondValueIsStored() {
        final Entry<?, ?> tuple = new Tuple<>(1, "two");
        Assertions.assertEquals("two", tuple.getValue());
        Assertions.assertTrue(tuple.getValue() instanceof String);
    }

    @Test
    public void testEqualsUsesValues() {
        final Entry<?, ?> tuple = new Tuple<>(1, "two");
        final Entry<?, ?> second = new Tuple<>(1, "two");
        Assertions.assertNotSame(tuple, second);
        Assertions.assertEquals(tuple, second);
    }

}
