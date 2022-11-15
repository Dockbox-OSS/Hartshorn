/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.util.option.Option;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public interface TranslationBundle {

    Locale primaryLanguage();

    TranslationBundle primaryLanguage(Locale language);

    Set<Message> messages();

    Option<Message> message(String key);

    Option<Message> message(String key, Locale language);

    TranslationBundle merge(TranslationBundle bundle);

    Message register(String key, String value, Locale language);

    Message register(Message message);

    Message register(String key, String value);

    Set<Message> register(Map<String, String> messages, Locale locale);

    Set<Message> register(Path source, Locale locale, FileFormat fileFormat);

    Set<Message> register(ResourceBundle resourceBundle);
}
