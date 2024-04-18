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

package org.dockbox.hartshorn.i18n.support;

import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.processing.CompositeMember;
import org.dockbox.hartshorn.component.processing.Singleton;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

/**
 * Configuration for utility {@link Converter}s that affect {@link Message}s.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@Configuration
public class MessageConverterConfiguration {

    /**
     * Converter to convert a {@link String} to a {@link Message}. If possible this will delegate to the {@link
     * TranslationService} to retrieve the {@link Message} for the provided {@link String}. If no {@link Message} is
     * found, {@code null} is returned. Note that strings provided to this converter are expected to represent keys
     * that are compatible with the active {@link TranslationService}.
     *
     * @param translationService the {@link TranslationService} to delegate to
     * @return a {@link Converter} that converts a {@link String} to a {@link Message}
     */
    @Singleton
    @CompositeMember
    public Converter<String, Message> stringToMessageConverter(TranslationService translationService) {
        return new StringToMessageConverter(translationService);
    }

    /**
     * Converter to convert a {@link Message} to a {@link String}. This converter will attempt to retrieve the string
     * representation of the provided {@link Message}, translating it to the current default locale of the message.
     *
     * @return a {@link Converter} that converts a {@link Message} to a {@link String}
     */
    @Singleton
    @CompositeMember
    public Converter<Message, String> messageToStringConverter() {
        return message -> message.string();
    }
}
