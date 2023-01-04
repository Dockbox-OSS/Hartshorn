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

package test.org.dockbox.hartshorn;

import org.dockbox.hartshorn.util.Vector3N;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VectorTests {

    @Test
    void testXCanConvertType() {
        final Vector3N vec = Vector3N.of(1, 2, 3);
        Assertions.assertEquals(1, vec.xI());
        Assertions.assertEquals(1D, vec.xD());
        Assertions.assertEquals(1F, vec.xF());
        Assertions.assertEquals(1L, vec.xL());
    }

    @Test
    void testYCanConvertType() {
        final Vector3N vec = Vector3N.of(1, 2, 3);
        Assertions.assertEquals(2, vec.yI());
        Assertions.assertEquals(2D, vec.yD());
        Assertions.assertEquals(2F, vec.yF());
        Assertions.assertEquals(2L, vec.yL());
    }

    @Test
    void testZCanConvertType() {
        final Vector3N vec = Vector3N.of(1, 2, 3);
        Assertions.assertEquals(3, vec.zI());
        Assertions.assertEquals(3D, vec.zD());
        Assertions.assertEquals(3F, vec.zF());
        Assertions.assertEquals(3L, vec.zL());
    }

    @Test
    void testEqualsUsesValues() {
        final Vector3N vec = Vector3N.of(1, 2, 3);
        final Vector3N sec = Vector3N.of(1, 2, 3);

        Assertions.assertNotSame(vec, sec);
        Assertions.assertEquals(vec, sec);
    }
}
