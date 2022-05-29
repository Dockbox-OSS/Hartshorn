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

package org.dockbox.hartshorn.data.service;

import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.data.PersistentElement;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import javax.inject.Inject;

@HartshornTest
@UsePersistence
public class SerializationTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testToStringSerialization() {
        final PersistenceService service = this.applicationContext.get(PersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final String json = service.writeToString(element);

        Assertions.assertNotNull(json);
        Assertions.assertEquals("{\"name\":\"sample\"}", StringUtilities.strip(json));
    }

    @Test
    void testFromStringDeserialization() {
        final PersistenceService service = this.applicationContext.get(PersistenceService.class);
        final String json = "{\"name\":\"sample\"}";
        final PersistentElement element = service.readFromString(json);

        Assertions.assertNotNull(element);
        Assertions.assertEquals("sample", element.name());
    }

    @Test
    void testToPathSerialization() {
        final PathPersistenceService service = this.applicationContext.get(PathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final boolean result = service.writeToPath(element, this.path());

        Assertions.assertTrue(result);
    }

    private Path path() {
        return this.applicationContext.environment().manager().applicationPath().resolve(System.nanoTime() + "-persistence.tmp");
    }

    @Test
    void testFromPathDeserialization() {
        final PathPersistenceService service = this.applicationContext.get(PathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final Path path = this.path();

        final boolean result = service.writeToPath(element, path);
        Assertions.assertTrue(result);

        final PersistentElement out = service.readFromPath(path);
        Assertions.assertNotNull(out);
        Assertions.assertEquals("sample", out.name());
    }

    @Test
    void testToAnnotationPathSerialization() {
        final AnnotationPathPersistenceService service = this.applicationContext.get(AnnotationPathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final boolean result = service.writeToPath(element);

        Assertions.assertTrue(result);
    }

    @Test
    void testFromAnnotationPathDeserialization() {
        final AnnotationPathPersistenceService service = this.applicationContext.get(AnnotationPathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");

        final boolean result = service.writeToPath(element);
        Assertions.assertTrue(result);

        final PersistentElement out = service.readFromPath();
        Assertions.assertNotNull(out);
        Assertions.assertEquals("sample", out.name());
    }
}
