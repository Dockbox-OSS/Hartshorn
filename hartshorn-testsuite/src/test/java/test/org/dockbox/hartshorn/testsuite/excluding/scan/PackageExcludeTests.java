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

package test.org.dockbox.hartshorn.testsuite.excluding.scan;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.org.dockbox.hartshorn.testsuite.excluding.scan.exclude.ExcludedComponent;
import test.org.dockbox.hartshorn.testsuite.excluding.scan.include.IncludedComponent;

@HartshornIntegrationTest(
    excludePackages = {
        "test.org.dockbox.hartshorn.testsuite.excluding.scan.exclude"
    },
    includePackages = {
        "test.org.dockbox.hartshorn.testsuite.excluding.scan"
    },
    includeBasePackages = false
)
public class PackageExcludeTests {

    @Test
    void componentInExcludedPackageIsNotPresentInRegistry(@Inject ComponentRegistry registry) {
        Assertions.assertTrue(registry.container(IncludedComponent.class).present());
        Assertions.assertFalse(registry.container(ExcludedComponent.class).present());
    }
}
