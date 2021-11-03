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
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MessageTemplateServiceTests extends ApplicationAwareTest {

    private TranslationBundle bundle() {
        final Message english = new MessageTemplate("value", "demo", Languages.EN_US);
        final Message dutch = new MessageTemplate("waarde", "demo", Languages.NL_NL);
        final TranslationBundle bundle = new DefaultTranslationBundle();
        bundle.register(english);
        bundle.register(dutch);
        return bundle;
    }

    @Test
    public void testResourcesCanBeFormatted() {
        final Message entry = new MessageTemplate("Hello {0}!", "demo.formatted", Languages.EN_US);
        final Message formatted = entry.format("world");

        Assertions.assertNotNull(formatted);
        Assertions.assertEquals("Hello world!", formatted.string());
    }

    @Test
    public void testResourceReturnsCopyOnFormat() {
        final Message entry = new MessageTemplate("Hello {0}!", "demo.formatted", Languages.EN_US);
        final Message formatted = entry.format("world");

        Assertions.assertSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsCopyOnTranslateLanguage() {
        final Exceptional<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        final Message entry = demo.get();
        final Message formatted = entry.translate(Languages.NL_NL);

        Assertions.assertSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsSelfOnTranslateMessageReceiver() {
        final Exceptional<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        final MessageReceiver mock = Mockito.mock(MessageReceiver.class);
        Mockito.when(mock.language()).thenReturn(Languages.NL_NL);

        final Message entry = demo.get();
        final Message formatted = entry.translate(mock);

        Assertions.assertSame(entry, formatted);
    }

    @Test
    void testMessageReturnsCloneOnDetach() {
        final Exceptional<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        final Message message = demo.get();
        final Message detach = message.detach();

        Assertions.assertNotSame(message, detach);
    }

    @Test
    public void testResourceBundleUsesBundle() {
        final Exceptional<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());
        Assertions.assertEquals("demo", demo.get().key());
    }

    @Test
    public void testResourceBundleKeepsTranslations() {
        final Exceptional<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());
        final Message entry = demo.get();

        Assertions.assertEquals("value", entry.translate(Languages.EN_US).string());
        Assertions.assertEquals("waarde", entry.translate(Languages.NL_NL).string());
    }

    @Test
    void testMergedMessageKeepsAllLanguages() {
        final Message messageA = new MessageTemplate("English", "message.a", Languages.EN_US);
        final Message messageB = new MessageTemplate("Dutch", "message.a", Languages.NL_NL);
        final Message merged = messageA.merge(Languages.EN_US, messageB);
        Assertions.assertNotSame(messageA, merged);
        Assertions.assertNotSame(messageB, merged);
        Assertions.assertEquals("English", merged.string());
        Assertions.assertEquals("English", merged.translate(Languages.EN_US).string());
        Assertions.assertEquals("Dutch", merged.translate(Languages.NL_NL).string());
    }

    @Test
    void testMergedMessageKeepsAllFormatting() {
        final Message messageA = new MessageTemplate("Value: {0}", "message.a", Languages.EN_US, "Property");
        final Message messageB = new MessageTemplate("Waarde: {0}", "message.a", Languages.NL_NL, "Eigenschap");
        final Message merged = messageA.merge(Languages.EN_US, messageB);
        Assertions.assertNotSame(messageA, merged);
        Assertions.assertNotSame(messageB, merged);
        Assertions.assertEquals("Value: Property", merged.translate(Languages.EN_US).string());
        Assertions.assertEquals("Waarde: Eigenschap", merged.translate(Languages.NL_NL).string());

        new MessageTemplate("Value: {0}", "key", Languages.EN_US, "Property")
                .translate(Languages.NL_NL).format("Eigenschap");

        final Message message = new MessageTemplate("Value: {0}", "key", Languages.EN_US)
                .format(Languages.NL_NL, "Eigenschap")
                .format(Languages.EN_US, "Property");
        message.translate(Languages.NL_NL).string(); // Value: Eigenschap
        message.translate(Languages.EN_US).string(); // Value: Property
    }
}
