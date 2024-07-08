/*
 * Copyright 2019-2024 the original author or authors.
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

package test.org.dockbox.hartshorn.scope;

import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.ScopeAdapter;
import org.dockbox.hartshorn.component.ScopeAdapterKey;
import org.dockbox.hartshorn.inject.scope.DirectScopeKey;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScopeKeyTests {

    @Test
    void testScopeKeyTypeKeepsParameters() {
        ParameterizableType parameterType = ParameterizableType.create(String.class);
        ParameterizableType parameterizedType = ParameterizableType.builder(ScopeAdapter.class)
                .parameters(parameterType)
                .build();

        ScopeKey scopeKey = DirectScopeKey.of(parameterizedType);
        Assertions.assertEquals(parameterizedType, scopeKey.scopeType());
    }

    @Test
    void testRawScopeKeysEqual() {
        ScopeKey scopeKey1 = DirectScopeKey.of(Scope.class);
        ScopeKey scopeKey2 = DirectScopeKey.of(Scope.class);
        Assertions.assertEquals(scopeKey1, scopeKey2);
    }

    @Test
    void testParameterizedScopeKeysEqual() {
        ParameterizableType parameterType = ParameterizableType.create(String.class);
        ParameterizableType parameterizedType = ParameterizableType.builder(ScopeAdapter.class)
                .parameters(parameterType)
                .build();

        ScopeKey scopeKey1 = DirectScopeKey.of(parameterizedType);
        ScopeKey scopeKey2 = DirectScopeKey.of(parameterizedType);
        Assertions.assertEquals(scopeKey1, scopeKey2);
    }

    @Test
    void testCreateFromParameterizedTypeRequiresScopeType() {
        ParameterizableType parameterType = ParameterizableType.create(String.class);
        ParameterizableType parameterizedType = ParameterizableType.builder(ScopeAdapter.class)
                .parameters(parameterType)
                .build();

        Assertions.assertDoesNotThrow(() -> DirectScopeKey.of(parameterizedType));
        // String not a scope type
        Assertions.assertThrows(IllegalArgumentException.class, () -> DirectScopeKey.of(parameterType));
    }

    @Test
    void testScopeAdapterKeyParameterizableTypeRequiresScopeAdapter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ScopeAdapterKey.of(ParameterizableType.create(String.class)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ScopeAdapterKey.of(ParameterizableType.create(Scope.class)));
        Assertions.assertDoesNotThrow(() -> {
            ParameterizableType type = ParameterizableType.builder(ScopeAdapter.class)
                    .parameters(ParameterizableType.create(String.class))
                    .build();
            ScopeAdapterKey scopeAdapterKey = ScopeAdapterKey.of(type);
            Assertions.assertNotNull(scopeAdapterKey);
        });
    }

    @Test
    void testScopeAdapterKeysOfSameTypeEqual() {
        ScopeAdapter<Object> adapter1 = ScopeAdapter.of(new Object());
        ScopeAdapterKey key1 = ScopeAdapterKey.of(adapter1);

        ScopeAdapter<Object> adapter2 = ScopeAdapter.of(new Object());
        ScopeAdapterKey key2 = ScopeAdapterKey.of(adapter2);

        Assertions.assertNotEquals(adapter1, adapter2);
        Assertions.assertEquals(key1, key2);
    }
}
