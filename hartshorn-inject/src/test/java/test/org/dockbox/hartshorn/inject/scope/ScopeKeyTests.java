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

package test.org.dockbox.hartshorn.inject.scope;

import org.dockbox.hartshorn.inject.scope.DirectScopeKey;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeAdapter;
import org.dockbox.hartshorn.inject.scope.ScopeAdapterKey;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class ScopeKeyTests {

    @Test
    void scopeKeyTypeKeepsParameters() {
        ParameterizableType parameterType = ParameterizableType.create(String.class);
        ParameterizableType parameterizedType = ParameterizableType.builder(ScopeAdapter.class)
                .parameters(parameterType)
                .build();

        ScopeKey scopeKey = DirectScopeKey.of(parameterizedType);
        assertThat(scopeKey.scopeType()).isEqualTo(parameterizedType);
    }

    @Test
    void rawScopeKeysEqual() {
        ScopeKey scopeKey1 = DirectScopeKey.of(Scope.class);
        ScopeKey scopeKey2 = DirectScopeKey.of(Scope.class);
        assertThat(scopeKey2).isEqualTo(scopeKey1);
    }

    @Test
    void parameterizedScopeKeysEqual() {
        ParameterizableType parameterType = ParameterizableType.create(String.class);
        ParameterizableType parameterizedType = ParameterizableType.builder(ScopeAdapter.class)
                .parameters(parameterType)
                .build();

        ScopeKey scopeKey1 = DirectScopeKey.of(parameterizedType);
        ScopeKey scopeKey2 = DirectScopeKey.of(parameterizedType);
        assertThat(scopeKey2).isEqualTo(scopeKey1);
    }

    @Test
    void createFromParameterizedTypeRequiresScopeType() {
        ParameterizableType parameterType = ParameterizableType.create(String.class);
        ParameterizableType parameterizedType = ParameterizableType.builder(ScopeAdapter.class)
                .parameters(parameterType)
                .build();

        assertThatCode(() -> DirectScopeKey.of(parameterizedType)).doesNotThrowAnyException();
        // String not a scope type
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> DirectScopeKey.of(parameterType));
    }

    @Test
    void scopeAdapterKeyParameterizableTypeRequiresScopeAdapter() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> ScopeAdapterKey.of(ParameterizableType.create(String.class)));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> ScopeAdapterKey.of(ParameterizableType.create(Scope.class)));
        assertThatCode(() -> {
            ParameterizableType type = ParameterizableType.builder(ScopeAdapter.class)
                    .parameters(ParameterizableType.create(String.class))
                    .build();
            ScopeAdapterKey scopeAdapterKey = ScopeAdapterKey.of(type);
            assertThat(scopeAdapterKey).isNotNull();
        }).doesNotThrowAnyException();
    }

    @Test
    void scopeAdapterKeysOfSameTypeEqual() {
        ScopeAdapter<Object> adapter1 = ScopeAdapter.of(new Object());
        ScopeAdapterKey key1 = ScopeAdapterKey.of(adapter1);

        ScopeAdapter<Object> adapter2 = ScopeAdapter.of(new Object());
        ScopeAdapterKey key2 = ScopeAdapterKey.of(adapter2);

        assertThat(adapter2).isNotEqualTo(adapter1);
        assertThat(key2).isEqualTo(key1);
    }
}
