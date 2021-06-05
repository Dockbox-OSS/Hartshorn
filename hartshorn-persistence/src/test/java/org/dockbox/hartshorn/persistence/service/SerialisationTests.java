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

package org.dockbox.hartshorn.persistence.service;

import org.dockbox.hartshorn.persistence.PersistentElement;
import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;

@ExtendWith(HartshornRunner.class)
public class SerialisationTests {

    @Test
    void testToStringSerialisation() {
        final PersistenceService service = Hartshorn.context().get(PersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final String json = service.writeToString(element);

        Assertions.assertNotNull(json);
        Assertions.assertEquals("{\"name\":\"sample\"}", HartshornUtils.strip(json));
    }

    @Test
    void testFromStringDeserialisation() {
        final PersistenceService service = Hartshorn.context().get(PersistenceService.class);
        final String json = "{\"name\":\"sample\"}";
        final PersistentElement element = service.readFromString(json);

        Assertions.assertNotNull(element);
        Assertions.assertEquals("sample", element.getName());
    }

    @Test
    void testToPathSerialisation() {
        final PathPersistenceService service = Hartshorn.context().get(PathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final boolean result = service.writeToPath(element, this.getPath());

        Assertions.assertTrue(result);
    }

    @Test
    void testFromPathDeserialisation() {
        final PathPersistenceService service = Hartshorn.context().get(PathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final Path path = this.getPath();

        final boolean result = service.writeToPath(element, path);
        Assertions.assertTrue(result);

        final PersistentElement out = service.readFromPath(path);
        Assertions.assertNotNull(out);
        Assertions.assertEquals("sample", out.getName());
    }

    @Test
    void testToAnnotationPathSerialisation() {
        final AnnotationPathPersistenceService service = Hartshorn.context().get(AnnotationPathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");
        final boolean result = service.writeToPath(element);

        Assertions.assertTrue(result);
    }

    @Test
    void testFromAnnotationPathDeserialisation() {
        final AnnotationPathPersistenceService service = Hartshorn.context().get(AnnotationPathPersistenceService.class);
        final PersistentElement element = new PersistentElement("sample");

        final boolean result = service.writeToPath(element);
        Assertions.assertTrue(result);

        final PersistentElement out = service.readFromPath();
        Assertions.assertNotNull(out);
        Assertions.assertEquals("sample", out.getName());
    }

    private Path getPath() {
        return Hartshorn.context().get(FileManager.class).getDataFile(Hartshorn.class, System.nanoTime() + "-persistence.tmp");
    }
}
