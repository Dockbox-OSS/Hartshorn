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

package test.org.dockbox.hartshorn.launchpad.resources;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.environment.FileSystemProvider;
import org.dockbox.hartshorn.launchpad.resources.ResourceLookup;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
class ResourceLookupTests {

    @Inject
    private ResourceLookup resourceLookup;

    @Inject
    private FileSystemProvider fileSystemProvider;

    @Test
    void classpathLookup() {
        this.testResourceLookup("classpath:sample.txt");
    }

    @Test
    void filesystemLookup() throws Exception {
        this.createLocalFile();
        this.testResourceLookup("fs:sample.txt");
    }

    @Test
    void unnamedResourceLookup() throws Exception {
        this.createLocalFile();
        this.testResourceLookup("sample.txt");
    }

    private void testResourceLookup(String path) {
        Set<URI> lookup = this.resourceLookup.lookup(path);
        assertThat(lookup)
                .isNotEmpty()
                .hasSize(1);

        URI uri = CollectionUtilities.first(lookup);
        File file = new File(uri);

        assertThat(file)
                .hasName("sample.txt")
                .exists();
    }

    private void createLocalFile() throws IOException {
        Path path = this.fileSystemProvider.applicationPath();
        Files.createFile(path.resolve("sample.txt"));
    }
}
