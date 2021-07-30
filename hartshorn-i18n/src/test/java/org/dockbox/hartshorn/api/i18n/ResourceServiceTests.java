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

package org.dockbox.hartshorn.api.i18n;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.entry.Resource;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

@ExtendWith(HartshornRunner.class)
public class ResourceServiceTests {

    private final ResourceService service = new SimpleResourceService() {
        static {
            for (Language value : Language.values()) bundles.put(value, createDemoBundle("Demo:" + value.code()));
        }
    };

    private static ResourceBundle createDemoBundle(String value) {
        return new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][]{ { "demo", value} };
            }
        };
    }

    @Test
    public void testResourcesCanBeFormatted() {
        ResourceEntry entry = new Resource("Hello {0}!", "demo.formatted");
        ResourceEntry formatted = entry.format("world");

        Assertions.assertNotNull(formatted);
        Assertions.assertEquals("Hello world!", formatted.plain());
    }

    @Test
    public void testResourceReturnsCopyOnFormat() {
        ResourceEntry entry = new Resource("Hello {0}!", "demo.formatted");
        ResourceEntry formatted = entry.format("world");

        Assertions.assertNotSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsCopyOnTranslate() {
        Exceptional<ResourceEntry> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());

        ResourceEntry entry = demo.get();
        ResourceEntry formatted = entry.translate();

        Assertions.assertNotSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsCopyOnTranslateLanguage() {
        Exceptional<ResourceEntry> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());

        ResourceEntry entry = demo.get();
        ResourceEntry formatted = entry.translate(Language.NL_NL);

        Assertions.assertNotSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsCopyOnTranslateMessageReceiver() {
        Exceptional<ResourceEntry> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());

        MessageReceiver mock = Mockito.mock(MessageReceiver.class);
        Mockito.when(mock.language()).thenReturn(Language.NL_NL);

        ResourceEntry entry = demo.get();
        ResourceEntry formatted = entry.translate(mock);

        Assertions.assertNotSame(entry, formatted);
    }

    @Test
    public void testResourceBundleUsesBundle() {
        Exceptional<ResourceEntry> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());
        Assertions.assertEquals("demo", demo.get().key());
    }

    @Test
    public void testResourceBundleKeepsTranslations() {
        Exceptional<ResourceEntry> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());
        ResourceEntry entry = demo.get();
        for (Language value : Language.values()) {
            Assertions.assertEquals("Demo:"+ value.code(), entry.translate(value).plain());
        }
    }

}
