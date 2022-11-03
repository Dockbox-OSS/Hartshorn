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

import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

import java.nio.file.Path;

@HartshornTest(includeBasePackages = false)
public class FSProviderTests {

    @InjectTest
    void testApplicationPathIsAbsolute(final ApplicationFSProvider fsProvider) {
        final Path path = fsProvider.applicationPath();

        Assertions.assertNotNull(path);
        Assertions.assertTrue(path.isAbsolute());
        Assertions.assertNotNull(path.getParent());
    }
}
