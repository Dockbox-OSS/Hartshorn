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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.dockbox.hartshorn.application.environment.FileSystemProvider;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.resources.ResourceLookup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.Inject;

@HartshornTest(includeBasePackages = false)
public class ResourceLookupTests {

    @Inject
    private ResourceLookup resourceLookup;

    @Inject
    private FileSystemProvider fileSystemProvider;

    @Test
    void testClasspathLookup() {
        this.testResourceLookup("classpath:sample.txt");
    }

    @Test
    void testFilesystemLookup() throws IOException {
        this.createLocalFile();
        this.testResourceLookup("fs:sample.txt");
    }

    @Test
    void testUnnamedResourceLookup() throws IOException {
        this.createLocalFile();
        this.testResourceLookup("sample.txt");
    }

    private void testResourceLookup(String path) {
        Set<URI> lookup = this.resourceLookup.lookup(path);
        Assertions.assertFalse(lookup.isEmpty());
        Assertions.assertEquals(1, lookup.size());

        URI uri = CollectionUtilities.first(lookup);
        File file = new File(uri);

        Assertions.assertEquals("sample.txt", file.getName());
        Assertions.assertTrue(file.exists());
    }

    private void createLocalFile() throws IOException {
        Path path = this.fileSystemProvider.applicationPath();
        Files.createFile(path.resolve("sample.txt"));
    }
}
