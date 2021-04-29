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

package org.dockbox.selene.api.i18n;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.Resource;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

@ExtendWith(SeleneJUnit5Runner.class)
public class ResourceServiceTests {

    private final ResourceService service = new SimpleResourceService() {
        @Override
        public void init() {
            super.init();
            for (Language value : Language.values()) bundles.put(value, createBundle("demo", "Demo:" + value.getCode()));
        }

        {
            this.init();
        }
    };

    private static ResourceBundle createBundle(String key, String value) {
        return new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][]{ {key, value} };
            }
        };
    }

    @Test
    public void testResourceBundleUsesBundle() {
        Exceptional<ResourceEntry> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());
        Assertions.assertEquals("demo", demo.get().getKey());
    }

    @Test
    public void testResourceBundleKeepsTranslations() {
        Exceptional<ResourceEntry> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());
        ResourceEntry entry = demo.get();
        for (Language value : Language.values()) {
            Assertions.assertEquals("Demo:"+ value.getCode(), entry.translate(value).plain());
        }
    }

}
