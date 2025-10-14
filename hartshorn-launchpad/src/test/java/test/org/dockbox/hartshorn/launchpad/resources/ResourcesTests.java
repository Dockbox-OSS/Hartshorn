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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

public class ResourcesTests {

    @Test
    void testGetResourceURLReturnsValidURL() {
        URL url = Assertions.assertDoesNotThrow(() -> Resources.getResourceURL("sample.txt"));
        Assertions.assertNotNull(url);
    }

    @Test
    void testGetResourceURLThrowsExceptionWhenResourceNotExists() {
        IOException exception = Assertions.assertThrows(IOException.class, () -> Resources.getResourceURL("not-exists.txt"));
        Assertions.assertEquals("Could not find resource not-exists.txt", exception.getMessage());
    }

    @Test
    void testGetResourceAsFileReturnsValidStream() {
        InputStream file = Assertions.assertDoesNotThrow(() -> Resources.getResourceAsInputStream("sample.txt"));
        Assertions.assertNotNull(file);
    }

    @Test
    void testGetResourceAsFileThrowsExceptionWhenResourceNotExists() {
        IOException exception = Assertions.assertThrows(IOException.class, () -> Resources.getResourceAsInputStream("not-exists.txt"));
        Assertions.assertEquals("Could not find resource not-exists.txt", exception.getMessage());
    }

    @Test
    void testGetResourceURLsReturnsValidURLs() {
        Set<URL> urls = Assertions.assertDoesNotThrow(() -> Resources.getResourceURLs("sample.txt"));
        Assertions.assertNotNull(urls);
        Assertions.assertFalse(urls.isEmpty());
        Assertions.assertEquals(1, urls.size());

        URL url = CollectionUtilities.first(urls);
        Assertions.assertNotNull(url);
    }

    @Test
    void testGetResourceURLsReturnsEmptyWhenResourceNotExists() {
        Set<URL> urls = Assertions.assertDoesNotThrow(() -> Resources.getResourceURLs("not-exists.txt"));
        Assertions.assertNotNull(urls);
        Assertions.assertTrue(urls.isEmpty());
    }

    @Test
    void testGetResourceAsFilesReturnsValidFiles() {
        Set<InputStream> files = Assertions.assertDoesNotThrow(() -> Resources.getResourcesAsInputStreams("sample.txt"));
        Assertions.assertNotNull(files);
        Assertions.assertFalse(files.isEmpty());
        Assertions.assertEquals(1, files.size());

        InputStream file = CollectionUtilities.first(files);
        Assertions.assertNotNull(file);
    }

    @Test
    void testGetResourceAsFilesReturnsEmptyWhenResourceNotExists() {
        Set<InputStream> files = Assertions.assertDoesNotThrow(() -> Resources.getResourcesAsInputStreams("not-exists.txt"));
        Assertions.assertNotNull(files);
        Assertions.assertTrue(files.isEmpty());
    }
}
