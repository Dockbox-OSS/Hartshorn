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

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.util.option.Option;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Represents a bundle of translations, which can be used to translate messages to a specific language.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface TranslationBundle {

    /**
     * Returns the primary language of the bundle. This is the language that is assigned to messages
     * looked up in- or registered to the bundle, if no other language is specified.
     *
     * @return the primary language of the bundle
     */
    Locale primaryLanguage();

    /**
     * Sets the primary language of the bundle. This is the language that is assigned to messages
     * looked up in- or registered to the bundle, if no other language is specified.
     *
     * @param language the primary language of the bundle
     * @return the current bundle
     */
    TranslationBundle primaryLanguage(Locale language);

    /**
     * Returns all messages registered to the bundle. The messages are not guaranteed to be in any
     * specific order, or use the default language of the bundle.
     *
     * @return all messages registered to the bundle
     */
    Set<Message> messages();

    /**
     * Returns the message with the provided key, if it exists in the bundle. If the message does not
     * exist, an empty {@link Option} is returned.
     *
     * <p>The returned message is detached from the bundle, and can be modified without affecting the
     * bundle.
     *
     * @param key the key of the message to return
     * @return the message with the provided key, if it exists in the bundle
     */
    Option<Message> message(String key);

    /**
     * Returns the message with the provided key, if it exists in the bundle. If the message does not
     * exist, an empty {@link Option} is returned. The message is translated to the provided language.
     *
     * <p>The returned message is detached from the bundle, and can be modified without affecting the
     * bundle.
     *
     * @param key the key of the message to return
     * @param language the language to translate the message to
     * @return the message with the provided key, if it exists in the bundle
     */
    Option<Message> message(String key, Locale language);

    /**
     * Combines the provided bundle with the current bundle. This will result in a new bundle, which
     * contains all messages from both bundles. If a message with the same key exists in both bundles,
     * the message from the provided bundle will be used.
     *
     * @param bundle the bundle to merge with the current bundle
     * @return a new bundle, which contains all messages from both bundles
     */
    TranslationBundle merge(TranslationBundle bundle);

    /**
     * Registers a new message to the bundle. If a message with the same key already exists in the
     * bundle, the translation will be merged into the existing message.
     *
     * @param key the key of the message to register
     * @param value the value of the message to register
     * @param language the language of the message to register
     * @return the registered message
     */
    Message register(String key, String value, Locale language);

    /**
     * Registers a new message to the bundle. If a message with the same key already exists in the
     * bundle, the translation will be merged into the existing message.
     *
     * @param message the message to register
     * @return the registered message
     */
    Message register(Message message);

    /**
     * Registers a new message to the bundle using the default language. If a message with the same key
     * already exists in the bundle, the translation will be merged into the existing message.
     *
     * @param key the key of the message to register
     * @param value the value of the message to register
     * @return the registered message
     */
    Message register(String key, String value);

    /**
     * Registers all messages from the provided map to the bundle. If a message with the same key
     * already exists in the bundle, the translation will be merged into the existing message.
     *
     * @param messages the messages to register
     * @param locale the language of the messages to register
     * @return the registered messages
     */
    Set<Message> register(Map<String, String> messages, Locale locale);

    /**
     * Registers all messages from the provided file to the bundle. This uses the provided file format
     * to determine how to read the file. If a message with the same key already exists in the bundle,
     * the translation will be merged into the existing message.
     *
     * @param source the file to read the messages from
     * @param locale the language of the messages to register
     * @param fileFormat the format of the file to read the messages from
     * @return the registered messages
     *
     * @deprecated since 0.6.0, for removal in 0.7.0. Use {@link #register(ResourceBundle)} instead.
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
    Set<Message> register(Path source, Locale locale, FileFormat fileFormat);

    /**
     * Registers all messages from the provided resource bundle to the bundle. If a message with the
     * same key already exists in the bundle, the translation will be merged into the existing message.
     *
     * @param resourceBundle the resource bundle to read the messages from
     * @return the registered messages
     */
    Set<Message> register(ResourceBundle resourceBundle);
}
