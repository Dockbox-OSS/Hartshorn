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

package org.dockbox.hartshorn.i18n.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

/**
 * A {@link Converter} that converts a {@link String} to a {@link Message} using the {@link TranslationService}.
 * The provided {@link String} is used as the key to retrieve the {@link Message} from the {@link TranslationService}.
 * If no {@link Message} is found, {@code null} is returned.
 *
 * @see TranslationService#get(String)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class StringToMessageConverter implements Converter<String, Message> {

    private final TranslationService translationService;

    public StringToMessageConverter(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public @Nullable Message convert(@Nullable String input) {
        return this.translationService.get(input).orNull();
    }
}
