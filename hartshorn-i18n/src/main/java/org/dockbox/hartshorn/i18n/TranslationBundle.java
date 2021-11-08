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
import org.dockbox.hartshorn.persistence.FileType;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public interface TranslationBundle {

    Locale primaryLanguage();

    TranslationBundle primaryLanguage(Locale language);

    Set<Message> messages();

    Exceptional<Message> message(String key);

    Exceptional<Message> message(String key, Locale language);

    TranslationBundle merge(TranslationBundle bundle);

    Message register(String key, String value, Locale language);

    Message register(Message message);

    Message register(String key, String value);

    Set<Message> register(Map<String, String> messages, Locale locale);

    Set<Message> register(Path source, Locale locale, FileType fileType);

    Set<Message> register(ResourceBundle resourceBundle);
}
