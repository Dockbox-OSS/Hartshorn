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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Locale;

@HartshornTest
@UseTranslations
public class MessageTemplateServiceTests {

    private static final Locale NL_NL = new Locale("nl", "NL");
    private static final Locale EN_US = Locale.US;
    
    private TranslationBundle bundle() {
        final Message english = new MessageTemplate("value", "demo", EN_US);
        final Message dutch = new MessageTemplate("waarde", "demo", NL_NL);
        final TranslationBundle bundle = new DefaultTranslationBundle();
        bundle.register(english);
        bundle.register(dutch);
        return bundle;
    }

    @Test
    public void testResourcesCanBeFormatted() {
        final Message entry = new MessageTemplate("Hello {0}!", "demo.formatted", EN_US);
        final Message formatted = entry.format("world");

        Assertions.assertNotNull(formatted);
        Assertions.assertEquals("Hello world!", formatted.string());
    }

    @Test
    public void testResourceReturnsCopyOnFormat() {
        final Message entry = new MessageTemplate("Hello {0}!", "demo.formatted", EN_US);
        final Message formatted = entry.format("world");

        Assertions.assertSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsCopyOnTranslateLanguage() {
        final Exceptional<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        final Message entry = demo.get();
        final Message formatted = entry.translate(NL_NL);

        Assertions.assertSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsSelfOnTranslateMessageReceiver() {
        final Exceptional<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        final MessageReceiver mock = Mockito.mock(MessageReceiver.class);
        Mockito.when(mock.language()).thenReturn(NL_NL);

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

        Assertions.assertEquals("value", entry.translate(EN_US).string());
        Assertions.assertEquals("waarde", entry.translate(NL_NL).string());
    }

    @Test
    void testMergedMessageKeepsAllLanguages() {
        final Message messageA = new MessageTemplate("English", "message.a", EN_US);
        final Message messageB = new MessageTemplate("Dutch", "message.a", NL_NL);
        final Message merged = messageA.merge(EN_US, messageB);
        Assertions.assertNotSame(messageA, merged);
        Assertions.assertNotSame(messageB, merged);
        Assertions.assertEquals("English", merged.string());
        Assertions.assertEquals("English", merged.translate(EN_US).string());
        Assertions.assertEquals("Dutch", merged.translate(NL_NL).string());
    }

    @Test
    void testMergedMessageKeepsAllFormatting() {
        final Message messageA = new MessageTemplate("Value: {0}", "message.a", EN_US, "Property");
        final Message messageB = new MessageTemplate("Waarde: {0}", "message.a", NL_NL, "Eigenschap");
        final Message merged = messageA.merge(EN_US, messageB);
        Assertions.assertNotSame(messageA, merged);
        Assertions.assertNotSame(messageB, merged);
        Assertions.assertEquals("Value: Property", merged.translate(EN_US).string());
        Assertions.assertEquals("Waarde: Eigenschap", merged.translate(NL_NL).string());

        new MessageTemplate("Value: {0}", "key", EN_US, "Property")
                .translate(NL_NL).format("Eigenschap");

        final Message message = new MessageTemplate("Value: {0}", "key", EN_US)
                .format(NL_NL, "Eigenschap")
                .format(EN_US, "Property");

        message.translate(NL_NL).string(); // Value: Eigenschap
        message.translate(EN_US).string(); // Value: Property
    }

    @InjectTest
    void testTranslationProvidersGetRegistered(final ApplicationContext applicationContext) {
        final TranslationService translationService = applicationContext.get(TranslationService.class);
        final Exceptional<Message> message = translationService.get("lang.name");
        Assertions.assertTrue(message.present());
        Assertions.assertEquals("English", message.get().translate(EN_US).string());
        Assertions.assertEquals("Nederlands", message.get().translate(NL_NL).string());
    }
}
