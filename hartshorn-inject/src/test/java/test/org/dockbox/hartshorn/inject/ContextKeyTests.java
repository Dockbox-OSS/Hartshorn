/*
 * Copyright 2019-2025 the original author or authors.
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

package test.org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.inject.ContextKey;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class ContextKeyTests {

    @Test
    void contextKeyNameIsNullIfUndefined() {
        ContextIdentity<ContextView> undefinedNameKey =
            ContextKey.builder(ContextView.class).build();
        assertThat(undefinedNameKey.name()).isNull();
    }

    @Test
    void contextKeyNameIsNullIfEmpty() {
        ContextIdentity<ContextView> emptyNameKey =
            ContextKey.builder(ContextView.class).name("").build();
        assertThat(emptyNameKey.name()).isNull();
    }

    @Test
    void contextKeyNameIsNullIfNull() {
        ContextIdentity<ContextView> nullNameKey = ContextKey.builder(ContextView.class)
                .name(null)
                .build();
        // Ensure no NPE is thrown
        assertThat(nullNameKey.name()).isNull();
    }

    @Test
    void createYieldsExceptionIfNoFallbackAndApplication() {
        ContextKey<ContextView> key = ContextKey.builder(ContextView.class).build();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(key::create);
    }

    @Test
    void mutableKeyCreatesClone() {
        ContextKey<ContextView> key = ContextKey.builder(ContextView.class).build();
        ContextIdentity<ContextView> mutableKey = key.mutable().name("mutable").build();
        assertThat(mutableKey).isNotSameAs(key);
        assertThat(key.name()).isNull();
        assertThat(mutableKey.name()).isEqualTo("mutable");
    }
}
