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

package test.org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.SingletonCache;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
public class SingletonCacheTests {

    @Inject
    private ComponentProvider componentProvider;

    @Inject
    private Binder binder;

    @Inject
    private SingletonCache cache;

    @Test
    @DisplayName("Lazy singletons should be cached in the scoped singleton cache after request")
    void testLazySingletonIsCachedAfterRequest() {
        // Given
        ComponentKey<String> key = ComponentKey.of(String.class);
        this.binder.bind(key).lazySingleton(() -> "Hello, World!");

        // Then
        Assertions.assertFalse(this.cache.contains(key));

        // When
        this.componentProvider.get(key);

        // Then
        Assertions.assertTrue(this.cache.contains(key));
    }

    @Test
    @DisplayName("Unprocessable non-lazy singletons should be cached in the scoped singleton cache after binding")
    void testNonLazySingletonWithoutProcessingIsCachedAfterBinding() {
        // Given
        ComponentKey<String> key = ComponentKey.of(String.class);
        this.binder.bind(key)
                .processAfterInitialization(false)
                .singleton("Hello, World!");

        // Then
        Assertions.assertTrue(this.cache.contains(key));
    }

    @Test
    @DisplayName("Singletons should not be cached in the scoped singleton cache if they require processing")
    void testSingletonIsNotCachedIfProcessingIsRequired() {
        // Given
        ComponentKey<String> key = ComponentKey.of(String.class);
        this.binder.bind(key).singleton("Hello, World!");

        // Then
        Assertions.assertFalse(this.cache.contains(key));

        // When
        this.componentProvider.get(key);

        // Then
        Assertions.assertTrue(this.cache.contains(key));
    }
}
