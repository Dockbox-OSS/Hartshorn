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

package test.org.dockbox.hartshorn.config;

import java.nio.file.Path;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.annotations.UseSerialization;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.StringUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UseSerialization
public abstract class SerializationTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = PersistenceService.class)
    void testToStringSerialization() {
        PersistenceService service = this.applicationContext.get(PersistenceService.class);
        PersistentElement element = new PersistentElement("sample");
        String json = service.writeToString(element);

        Assertions.assertNotNull(json);
        Assertions.assertEquals("{\"name\":\"sample\"}", StringUtilities.strip(json));
    }

    @Test
    @TestComponents(components = PersistenceService.class)
    void testFromStringDeserialization() {
        PersistenceService service = this.applicationContext.get(PersistenceService.class);
        String json = "{\"name\":\"sample\"}";
        PersistentElement element = service.readFromString(json);

        Assertions.assertNotNull(element);
        Assertions.assertEquals("sample", element.name());
    }

    @Test
    @TestComponents(components = PathPersistenceService.class)
    void testToPathSerialization() {
        PathPersistenceService service = this.applicationContext.get(PathPersistenceService.class);
        PersistentElement element = new PersistentElement("sample");
        boolean result = service.writeToPath(element, this.path());

        Assertions.assertTrue(result);
    }

    private Path path() {
        return this.applicationContext.environment().fileSystem().applicationPath().resolve(System.nanoTime() + "-persistence.tmp");
    }

    @Test
    @TestComponents(components = PathPersistenceService.class)
    void testFromPathDeserialization() {
        PathPersistenceService service = this.applicationContext.get(PathPersistenceService.class);
        PersistentElement element = new PersistentElement("sample");
        Path path = this.path();

        boolean result = service.writeToPath(element, path);
        Assertions.assertTrue(result);

        PersistentElement out = service.readFromPath(path);
        Assertions.assertNotNull(out);
        Assertions.assertEquals("sample", out.name());
    }

    @Test
    @TestComponents(components = AnnotationPathPersistenceService.class)
    void testToAnnotationPathSerialization() {
        AnnotationPathPersistenceService service = this.applicationContext.get(AnnotationPathPersistenceService.class);
        PersistentElement element = new PersistentElement("sample");
        boolean result = service.writeToPath(element);

        Assertions.assertTrue(result);
    }

    @Test
    @TestComponents(components = AnnotationPathPersistenceService.class)
    void testFromAnnotationPathDeserialization() {
        AnnotationPathPersistenceService service = this.applicationContext.get(AnnotationPathPersistenceService.class);
        PersistentElement element = new PersistentElement("sample");

        boolean result = service.writeToPath(element);
        Assertions.assertTrue(result);

        PersistentElement out = service.readFromPath();
        Assertions.assertNotNull(out);
        Assertions.assertEquals("sample", out.name());
    }
}
