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

package test.org.dockbox.hartshorn.i18n;

import java.util.Locale;

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.ObjectMapper;
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

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UseTranslations
public class MessageTemplateServiceTests {

    private static final Locale EN_US = Locale.US;
    private static final Locale NL_NL = new Locale.Builder()
            .setLanguageTag("nl")
            .setRegion("NL")
            .build();

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ExceptionHandler exceptionHandler;

    private TranslationBundle bundle() {
        Message english = new MessageTemplate("value", "demo", EN_US);
        Message dutch = new MessageTemplate("waarde", "demo", NL_NL);
        TranslationBundle bundle = new DefaultTranslationBundle(this.objectMapper, this.exceptionHandler);
        bundle.register(english);
        bundle.register(dutch);
        return bundle;
    }

    @Test
    public void testResourcesCanBeFormatted() {
        Message entry = new MessageTemplate("Hello {0}!", "demo.formatted", EN_US);
        Message formatted = entry.format("world");

        Assertions.assertNotNull(formatted);
        Assertions.assertEquals("Hello world!", formatted.string());
    }

    @Test
    public void testResourceReturnsCopyOnFormat() {
        Message entry = new MessageTemplate("Hello {0}!", "demo.formatted", EN_US);
        Message formatted = entry.format("world");

        Assertions.assertSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsCopyOnTranslateLanguage() {
        Option<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        Message entry = demo.get();
        Message formatted = entry.translate(NL_NL);

        Assertions.assertSame(entry, formatted);
    }

    @Test
    public void testResourceReturnsSelfOnTranslateMessageReceiver() {
        Option<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        MessageReceiver mock = Mockito.mock(MessageReceiver.class);
        Mockito.when(mock.language()).thenReturn(NL_NL);

        Message entry = demo.get();
        Message formatted = entry.translate(mock);

        Assertions.assertSame(entry, formatted);
    }

    @Test
    void testMessageReturnsCloneOnDetach() {
        Option<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());

        Message message = demo.get();
        Message detach = message.detach();

        Assertions.assertNotSame(message, detach);
    }

    @Test
    public void testResourceBundleUsesBundle() {
        Option<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());
        Assertions.assertEquals("demo", demo.get().key());
    }

    @Test
    public void testResourceBundleKeepsTranslations() {
        Option<Message> demo = this.bundle().message("demo");
        Assertions.assertTrue(demo.present());
        Message entry = demo.get();

        Assertions.assertEquals("value", entry.translate(EN_US).string());
        Assertions.assertEquals("waarde", entry.translate(NL_NL).string());
    }

    @Test
    void testMergedMessageKeepsAllLanguages() {
        Message messageA = new MessageTemplate("English", "message.a", EN_US);
        Message messageB = new MessageTemplate("Dutch", "message.a", NL_NL);
        Message merged = messageA.merge(EN_US, messageB);
        Assertions.assertNotSame(messageA, merged);
        Assertions.assertNotSame(messageB, merged);
        Assertions.assertEquals("English", merged.string());
        Assertions.assertEquals("English", merged.translate(EN_US).string());
        Assertions.assertEquals("Dutch", merged.translate(NL_NL).string());
    }

    @Test
    void testMergedMessageKeepsAllFormatting() {
        Message messageA = new MessageTemplate("Value: {0}", "message.a", EN_US, "Property");
        Message messageB = new MessageTemplate("Waarde: {0}", "message.a", NL_NL, "Eigenschap");
        Message merged = messageA.merge(EN_US, messageB);
        Assertions.assertNotSame(messageA, merged);
        Assertions.assertNotSame(messageB, merged);
        Assertions.assertEquals("Value: Property", merged.translate(EN_US).string());
        Assertions.assertEquals("Waarde: Eigenschap", merged.translate(NL_NL).string());

        new MessageTemplate("Value: {0}", "key", EN_US, "Property")
                .translate(NL_NL).format("Eigenschap");

        Message message = new MessageTemplate("Value: {0}", "key", EN_US)
                .format(NL_NL, "Eigenschap")
                .format(EN_US, "Property");

        message.translate(NL_NL).string(); // Value: Eigenschap
        message.translate(EN_US).string(); // Value: Property
    }

    @InjectTest
    @TestComponents(components = TranslationProviderService.class)
    void testTranslationProvidersGetRegistered(ApplicationContext applicationContext) {
        TranslationService translationService = applicationContext.get(TranslationService.class);
        Option<Message> message = translationService.get("lang.name");
        Assertions.assertTrue(message.present());
        Assertions.assertEquals("English", message.get().translate(EN_US).string());
        Assertions.assertEquals("Nederlands", message.get().translate(NL_NL).string());
    }
}
