/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.DefaultTranslationBundle;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.MessageReceiver;
import org.dockbox.hartshorn.i18n.MessageTemplate;
import org.dockbox.hartshorn.i18n.TranslationBundle;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Locale;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UseTranslations
public class MessageTemplateServiceTests {

    private static final Locale NL_NL = new Locale("nl", "NL");
    private static final Locale EN_US = Locale.US;

    @Inject
    private ApplicationContext applicationContext;

    private TranslationBundle bundle() {
        final Message english = new MessageTemplate("value", "demo", EN_US);
        final Message dutch = new MessageTemplate("waarde", "demo", NL_NL);
        final TranslationBundle bundle = new DefaultTranslationBundle(this.applicationContext);
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
        final Option<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        final Message entry = demo.get();
        final Message formatted = entry.translate(NL_NL);

        Assertions.assertSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsSelfOnTranslateMessageReceiver() {
        final Option<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        final MessageReceiver mock = Mockito.mock(MessageReceiver.class);
        Mockito.when(mock.language()).thenReturn(NL_NL);

        final Message entry = demo.get();
        final Message formatted = entry.translate(mock);

        Assertions.assertSame(entry, formatted);
    }

    @Test
    void testMessageReturnsCloneOnDetach() {
        final Option<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        final Message message = demo.get();
        final Message detach = message.detach();

        Assertions.assertNotSame(message, detach);
    }

    @Test
    public void testResourceBundleUsesBundle() {
        final Option<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());
        Assertions.assertEquals("demo", demo.get().key());
    }

    @Test
    public void testResourceBundleKeepsTranslations() {
        final Option<Message> demo = this.bundle().message("demo");
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
    @TestComponents(components = TranslationProviderService.class)
    void testTranslationProvidersGetRegistered(final ApplicationContext applicationContext) {
        final TranslationService translationService = applicationContext.get(TranslationService.class);
        final Option<Message> message = translationService.get("lang.name");
        Assertions.assertTrue(message.present());
        Assertions.assertEquals("English", message.get().translate(EN_US).string());
        Assertions.assertEquals("Nederlands", message.get().translate(NL_NL).string());
    }
}
