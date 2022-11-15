/*
 * Copyright 2019-2022 the original author or authors.
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

package test.org.dockbox.hartshorn;

import org.dockbox.hartshorn.util.Triad;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TriadTests {

    @Test
    public void testFirstValueIsStored() {
        final Triad<?, ?, ?> triad = new Triad<>(1, "two", 3L);
        Assertions.assertEquals(1, triad.first());
        Assertions.assertTrue(triad.first() instanceof Integer);
    }

    @Test
    public void testSecondValueIsStored() {
        final Triad<?, ?, ?> triad = new Triad<>(1, "two", 3L);
        Assertions.assertEquals("two", triad.second());
        Assertions.assertTrue(triad.second() instanceof String);
    }

    @Test
    public void testThirdValueIsStored() {
        final Triad<?, ?, ?> triad = new Triad<>(1, "two", 3L);
        Assertions.assertEquals(3L, triad.third());
        Assertions.assertTrue(triad.third() instanceof Long);
    }

    @Test
    public void testFirstValueCanBeNull() {
        final Triad<?, ?, ?> triad = new Triad<>(null, "two", 3L);
        Assertions.assertNull(triad.first());
    }

    @Test
    public void testSecondValueCanBeNull() {
        final Triad<?, ?, ?> triad = new Triad<>(1, null, 3L);
        Assertions.assertNull(triad.second());
    }

    @Test
    public void testThirdValueCanBeNull() {
        final Triad<?, ?, ?> triad = new Triad<>(1, "two", null);
        Assertions.assertNull(triad.third());
    }

}
