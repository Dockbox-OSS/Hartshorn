/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.data.service;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.data.PersistentElement;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

@UsePersistence
public class SerialisationTests extends ApplicationAwareTest {

    @Test
    void testToStringSerialisation() {
        final PersistenceService service = this.context().get(PersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final String json = service.writeToString(element);

        Assertions.assertNotNull(json);
        Assertions.assertEquals("{\"name\":\"sample\"}", HartshornUtils.strip(json));
    }

    @Test
    void testFromStringDeserialisation() {
        final PersistenceService service = this.context().get(PersistenceService.class);
        final String json = "{\"name\":\"sample\"}";
        final PersistentElement element = service.readFromString(json);

        Assertions.assertNotNull(element);
        Assertions.assertEquals("sample", element.name());
    }

    @Test
    void testToPathSerialisation() {
        final PathPersistenceService service = this.context().get(PathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final boolean result = service.writeToPath(element, this.path());

        Assertions.assertTrue(result);
    }

    private Path path() {
        return this.context().environment().manager().applicationPath().resolve(System.nanoTime() + "-persistence.tmp");
    }

    @Test
    void testFromPathDeserialisation() {
        final PathPersistenceService service = this.context().get(PathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final Path path = this.path();

        final boolean result = service.writeToPath(element, path);
        Assertions.assertTrue(result);

        final PersistentElement out = service.readFromPath(path);
        Assertions.assertNotNull(out);
        Assertions.assertEquals("sample", out.name());
    }

    @Test
    void testToAnnotationPathSerialisation() {
        final AnnotationPathPersistenceService service = this.context().get(AnnotationPathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final boolean result = service.writeToPath(element);

        Assertions.assertTrue(result);
    }

    @Test
    void testFromAnnotationPathDeserialisation() {
        final AnnotationPathPersistenceService service = this.context().get(AnnotationPathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");

        final boolean result = service.writeToPath(element);
        Assertions.assertTrue(result);

        final PersistentElement out = service.readFromPath();
        Assertions.assertNotNull(out);
        Assertions.assertEquals("sample", out.name());
    }
}
