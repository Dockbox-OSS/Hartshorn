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

package org.dockbox.hartshorn.i18n;

import java.util.Locale;

/**
 * A message is a string resource that can be translated into a specific language. It can also be
 * formatted with custom, or default arguments. A message is mutable, but can be detached from its
 * original source through the {@link #detach()} method.
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public interface Message {

    /**
     * Translates the message into the language of the provided {@link MessageReceiver}. This modifies
     * the current instance, and returns it.
     *
     * @param receiver the receiver to translate the message for
     * @return the current message
     */
    Message translate(MessageReceiver receiver);

    /**
     * Translates the message into the provided language. This modifies the current instance, and
     * returns it.
     *
     * @param lang the language to translate the message into
     * @return the current message
     */
    Message translate(Locale lang);

    /**
     * Adds the provided arguments to the message as default formatting arguments for the given
     * language. This will ensure that any calls to {@link #string()} will use these arguments if
     * the language matches. This modifies the current instance, and returns it.
     *
     * @param language the language to add the arguments for
     * @param args the arguments to add
     * @return the current message
     */
    Message format(Locale language, Object... args);

    /**
     * Adds the provided arguments to the message as default formatting arguments for the default
     * language. This will ensure that any calls to {@link #string()} will use these arguments.
     * This modifies the current instance, and returns it.
     *
     * @param args the arguments to add
     * @return the current message
     */
    Message format(Object... args);

    /**
     * Returns the key of the message. This is the identifier that is used to retrieve the message
     * from the {@link TranslationBundle}.
     *
     * @return the key of the message
     */
    String key();

    /**
     * Returns the string value of the message. This uses the current language, and any formatting
     * arguments that have been configured for that language.
     *
     * @return the string value of the message
     */
    String string();

    /**
     * Returns the current default language of the message.
     *
     * @return the current default language of the message
     */
    Locale language();

    /**
     * Merges the current and provided message into a new message. The provided message will
     * contain all formatting arguments and resources of the current message. If both the current
     * and provided message contain formatting arguments or resources for the same language, the
     * provided message will take precedence.
     *
     * @param primary the primary language of the message
     * @param message the message to merge with
     * @return the merged message
     */
    Message merge(Locale primary, Message message);

    /**
     * Detaches the current message from its original source. This will return a new message
     * instance, with the same key, language, formatting arguments and resources as the current
     * message, but without any references to the original source. This allows the message to be
     * used outside the context of the {@link TranslationBundle} that it was retrieved from.
     *
     * <p>This has the added benefit of being able to configure a default language for the message,
     * without affecting the original source.
     *
     * @return a new message instance, with the same key, language, formatting arguments and
     *         resources as the current message
     */
    Message detach();

}
