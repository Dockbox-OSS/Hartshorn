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

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.Message;
import org.dockbox.hartshorn.i18n.message.MessageTemplate;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

public class MessageTemplateServiceTests extends ApplicationAwareTest {

    private final ResourceService service = new ResourceServiceImpl() {
        static {
            for (final Language value : Language.values()) bundles.put(value, createDemoBundle("Demo:" + value.code()));
        }

        @Override
        public ApplicationContext applicationContext() {
            return MessageTemplateServiceTests.this.context();
        }
    };

    private static ResourceBundle createDemoBundle(final String value) {
        return new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][]{ { "demo", value } };
            }
        };
    }

    @Test
    public void testResourcesCanBeFormatted() {
        final Message entry = new MessageTemplate(this.context(), "Hello {0}!", "demo.formatted");
        final Message formatted = entry.format("world");

        Assertions.assertNotNull(formatted);
        Assertions.assertEquals("Hello world!", formatted.plain());
    }

    @Test
    public void testResourceReturnsCopyOnFormat() {
        final Message entry = new MessageTemplate(this.context(), "Hello {0}!", "demo.formatted");
        final Message formatted = entry.format("world");

        Assertions.assertNotSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsCopyOnTranslate() {
        final Exceptional<Message> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());

        final Message entry = demo.get();
        final Message formatted = entry.translate();

        Assertions.assertNotSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsCopyOnTranslateLanguage() {
        final Exceptional<Message> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());

        final Message entry = demo.get();
        final Message formatted = entry.translate(Language.NL_NL);

        Assertions.assertNotSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsCopyOnTranslateMessageReceiver() {
        final Exceptional<Message> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());

        final MessageReceiver mock = Mockito.mock(MessageReceiver.class);
        Mockito.when(mock.language()).thenReturn(Language.NL_NL);

        final Message entry = demo.get();
        final Message formatted = entry.translate(mock);

        Assertions.assertNotSame(entry, formatted);
    }

    @Test
    public void testResourceBundleUsesBundle() {
        final Exceptional<Message> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());
        Assertions.assertEquals("demo", demo.get().key());
    }

    @Test
    public void testResourceBundleKeepsTranslations() {
        final Exceptional<Message> demo = this.service.get("demo");
        Assertions.assertTrue(demo.present());
        final Message entry = demo.get();
        for (final Language value : Language.values()) {
            Assertions.assertEquals("Demo:" + value.code(), entry.translate(value).plain());
        }
    }

}
