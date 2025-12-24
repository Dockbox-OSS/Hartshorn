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

import org.dockbox.hartshorn.launchpad.resources.Resources;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class ResourcesTests {

    @Test
    void getResourceURLReturnsValidURL() throws Exception {
        URL url = Resources.getResourceURL("sample.txt");
        assertThat(url).isNotNull();
    }

    @Test
    void getResourceURLThrowsExceptionWhenResourceNotExists() {
        IOException exception = assertThatExceptionOfType(IOException.class)
                .isThrownBy(() -> Resources.getResourceURL("not-exists.txt"))
                .actual();
        assertThat(exception.getMessage()).isEqualTo("Could not find resource not-exists.txt");
    }

    @Test
    void getResourceAsFileReturnsValidStream() throws Exception {
        InputStream file = Resources.getResourceAsInputStream("sample.txt");
        assertThat(file).isNotNull();
    }

    @Test
    void getResourceAsFileThrowsExceptionWhenResourceNotExists() {
        IOException exception = assertThatExceptionOfType(IOException.class)
                .isThrownBy(() -> Resources.getResourceAsInputStream("not-exists.txt"))
                .actual();
        assertThat(exception.getMessage()).isEqualTo("Could not find resource not-exists.txt");
    }

    @Test
    void getResourceURLsReturnsValidURLs() throws Exception {
        Set<URL> urls = Resources.getResourceURLs("sample.txt");
        assertThat(urls)
                .isNotEmpty()
                .hasSize(1)
                .first()
                .isNotNull();
    }

    @Test
    void getResourceURLsReturnsEmptyWhenResourceNotExists() throws Exception {
        Set<URL> urls = Resources.getResourceURLs("not-exists.txt");
        assertThat(urls).isNotNull();
        assertThat(urls).isEmpty();
    }

    @Test
    void getResourceAsFilesReturnsValidFiles() throws Exception {
        Set<InputStream> files = Resources.getResourcesAsInputStreams("sample.txt");
        assertThat(files)
                .isNotEmpty()
                .hasSize(1);

        InputStream file = CollectionUtilities.first(files);
        assertThat(file).isNotNull();
    }

    @Test
    void getResourceAsFilesReturnsEmptyWhenResourceNotExists() throws Exception {
        Set<InputStream> files = Resources.getResourcesAsInputStreams("not-exists.txt");
        assertThat(files).isNotNull();
        assertThat(files).isEmpty();
    }
}
