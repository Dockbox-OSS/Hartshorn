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

package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.IOUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class IOUtilitiesTests {

    @TempDir
    private Path testDirectory;

    @Test
    void hasFileExtensionForPresentExtension() {
        Path path = this.testDirectory.resolve("file.txt");
        assertThat(IOUtilities.hasFileExtension(path)).isTrue();
    }

    @Test
    void hasFileExtensionForAbsentExtension() {
        Path path = this.testDirectory.resolve("file");
        assertThat(IOUtilities.hasFileExtension(path)).isFalse();
    }

    @Test
    void getFileExtensionForPresentExtension() {
        Path path = this.testDirectory.resolve("file.txt");
        String extension = IOUtilities.getFileExtension(path);
        assertThat(extension).isEqualTo("txt");
    }

    @Test
    void getFileExtensionForAbsentExtension() {
        Path path = this.testDirectory.resolve("file");
        String extension = IOUtilities.getFileExtension(path);
        assertThat(extension).isNull();
    }

    @Test
    void existsForExistingFile() throws Exception {
        Path path = this.testDirectory.resolve("file");
        assertThat(path.toFile().createNewFile()).isTrue();
        assertThat(IOUtilities.exists(path)).isTrue();
    }

    @Test
    void existsForNotExistingFile() {
        Path path = this.testDirectory.resolve("doesnotexist");
        assertThat(IOUtilities.exists(path)).isFalse();
    }
}
