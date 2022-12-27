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

import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ContextKeyTests {

    @Test
    void testContextKeyNameIsNullIfUndefined() {
        final ContextKey<Context> undefinedNameKey = ContextKey.builder(Context.class).build();
        Assertions.assertNull(undefinedNameKey.name());
    }

    @Test
    void testContextKeyNameIsNullIfEmpty() {
        final ContextKey<Context> emptyNameKey = ContextKey.builder(Context.class).name("").build();
        Assertions.assertNull(emptyNameKey.name());
    }

    @Test
    void testContextKeyNameIsNullIfNull() {
        // Ensure no NPE is thrown
        final ContextKey<Context> nullNameKey = Assertions.assertDoesNotThrow(() -> ContextKey.builder(Context.class).name(null).build());
        Assertions.assertNull(nullNameKey.name());
    }

    @Test
    void testRequiresApplicationContextIsTrueIfFunction() {
        final ContextKey<Context> key = ContextKey.builder(Context.class)
                .fallback(applicationContext -> null)
                .build();
        Assertions.assertTrue(key.requiresApplicationContext());
    }

    @Test
    void testRequiresApplicationContextIsFalseIfSupplier() {
        final ContextKey<Context> key = ContextKey.builder(Context.class)
                .fallback(() -> null)
                .build();
        Assertions.assertFalse(key.requiresApplicationContext());
    }

    @Test
    void testContextDoesNotYieldErrorIfFunctionFallbackProvidesNull() {
        final ContextKey<Context> key = ContextKey.builder(Context.class)
                .fallback(applicationContext -> null)
                .build();

        final ApplicationContext applicationContext = HartshornApplication.create();
        Assertions.assertNull(key.create(applicationContext));
    }

    @Test
    void testContextDoesNotYieldErrorIfSupplierFallbackProvidesNull() {
        final ContextKey<Context> key = ContextKey.builder(Context.class)
                .fallback(() -> null)
                .build();

        final ApplicationContext applicationContext = HartshornApplication.create();
        Assertions.assertNull(key.create(applicationContext));
    }

    @Test
    void testCreateYieldsExceptionIfNoFallbackAndApplication() {
        final ContextKey<Context> key = ContextKey.builder(Context.class).build();
        Assertions.assertThrows(IllegalStateException.class, () -> key.create(null));
    }

    @Test
    void testMutableKeyCreatesClone() {
        final ContextKey<Context> key = ContextKey.builder(Context.class).build();
        final ContextKey<Context> mutableKey = key.mutable().name("mutable").build();
        Assertions.assertNotSame(key, mutableKey);
        Assertions.assertNull(key.name());
        Assertions.assertEquals("mutable", mutableKey.name());
    }
}
