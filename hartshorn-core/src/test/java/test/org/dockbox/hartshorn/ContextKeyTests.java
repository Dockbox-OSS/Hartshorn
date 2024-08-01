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

package test.org.dockbox.hartshorn;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.testsuite.HartshornTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ContextKeyTests {

    @Test
    void testContextKeyNameIsNullIfUndefined() {
        ContextIdentity<ContextView> undefinedNameKey = ContextKey.builder(ContextView.class).build();
        Assertions.assertNull(undefinedNameKey.name());
    }

    @Test
    void testContextKeyNameIsNullIfEmpty() {
        ContextIdentity<ContextView> emptyNameKey = ContextKey.builder(ContextView.class).name("").build();
        Assertions.assertNull(emptyNameKey.name());
    }

    @Test
    void testContextKeyNameIsNullIfNull() {
        ContextIdentity<ContextView> nullNameKey = Assertions.assertDoesNotThrow(() -> ContextKey.builder(ContextView.class).name(null).build());
        // Ensure no NPE is thrown
        Assertions.assertNull(nullNameKey.name());
    }

    @Test
    void testRequiresApplicationContextIsTrueIfFunction() {
        ContextIdentity<ContextView> key = ContextKey.builder(ContextView.class)
                .fallback(applicationContext -> null)
                .build();
        Assertions.assertTrue(key.requiresApplicationContext());
    }

    @Test
    void testRequiresApplicationContextIsFalseIfSupplier() {
        ContextIdentity<ContextView> key = ContextKey.builder(ContextView.class)
                .fallback(() -> null)
                .build();
        Assertions.assertFalse(key.requiresApplicationContext());
    }

    @Test
    void testContextDoesNotYieldErrorIfFunctionFallbackProvidesNull() {
        ContextKey<ContextView> key = ContextKey.builder(ContextView.class)
                .fallback(applicationContext -> null)
                .build();

        ApplicationContext applicationContext = HartshornTestApplication.createWithoutBasePackages();
        Assertions.assertNull(key.create(applicationContext));
    }

    @Test
    void testContextDoesNotYieldErrorIfSupplierFallbackProvidesNull() {
        ContextKey<ContextView> key = ContextKey.builder(ContextView.class)
                .fallback(() -> null)
                .build();

        ApplicationContext applicationContext = HartshornTestApplication.createWithoutBasePackages();
        Assertions.assertNull(key.create(applicationContext));
    }

    @Test
    void testCreateYieldsExceptionIfNoFallbackAndApplication() {
        ContextKey<ContextView> key = ContextKey.builder(ContextView.class).build();
        Assertions.assertThrows(IllegalStateException.class, () -> key.create(null));
    }

    @Test
    void testMutableKeyCreatesClone() {
        ContextKey<ContextView> key = ContextKey.builder(ContextView.class).build();
        ContextIdentity<ContextView> mutableKey = key.mutable().name("mutable").build();
        Assertions.assertNotSame(key, mutableKey);
        Assertions.assertNull(key.name());
        Assertions.assertEquals("mutable", mutableKey.name());
    }
}
