/*
 * Copyright 2019-2026 the original author or authors.
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

package test.org.dockbox.hartshorn.launchpad.launch.scanning;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Test;

import test.org.dockbox.hartshorn.launchpad.launch.scanning.discover.ScanSpecificPackageActivator;
import test.org.dockbox.hartshorn.launchpad.launch.scanning.components.CountingComponentPreProcessor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test is associated with <a
 * href="https://github.com/GuusLieben/Hartshorn/issues/609">#609</a>. It tests that overly specific
 * packages like {@code com.specific.sub} are not processed twice if a broader package like
 * {@code com.specific} is bound to the same application context.
 */
@ScanSpecificPackageActivator
@HartshornIntegrationTest(
    includeBasePackages = false,
    includePackages = "test.org.dockbox.hartshorn.launchpad.launch.scanning",
    componentPreProcessors = {CountingComponentPreProcessor.class}
)
class SpecificPackageTests {

    @Test
    void specificPackageFilterIsApplied(@Inject CountingComponentPreProcessor processor) {
        assertThat(processor.processed()).isOne();
    }
}
