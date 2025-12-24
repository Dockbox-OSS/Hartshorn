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

package test.org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false, binderPostProcessors = SampleBinderPostProcessor.class)
class BinderProcessorTests {

    @Test
    void binderPostProcessorIsCalled(
        @Inject HierarchicalBinder binder,
        @Inject ComponentProvider provider
    ) {
        BindingHierarchy<String> hierarchy = binder.hierarchy(ComponentKey.of(String.class));
        assertThat(hierarchy.size()).isPositive();

        String message = provider.get(String.class);
        assertThat(message).isEqualTo(SampleBinderPostProcessor.HELLO_WORLD);
    }
}
