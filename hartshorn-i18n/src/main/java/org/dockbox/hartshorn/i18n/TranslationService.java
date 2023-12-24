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

import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A service which provides translation functionality. It is used to retrieve {@link Message} instances,
 * which can be used to translate a message to a specific language.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface TranslationService extends ContextCarrier {

    /**
     * Returns the message registered to the provided key, if any.
     *
     * @param key the key of the message to retrieve
     * @return the message registered to the provided key
     */
    Option<Message> get(String key);

    /**
     * Returns the message registered to the provided key, if any. If no message is registered to the
     * provided key, a new message is created and registered to the provided key.
     *
     * @param key the key of the message to retrieve
     * @param value the value of the message to register if no message is registered to the provided key
     * @return the message registered to the provided key
     */
    Message getOrCreate(String key, String value);

    /**
     * Adds the provided bundle to the service. If the provided bundle contains messages that are already
     * registered to the service, the existing messages are updated.
     *
     * @param bundle the bundle to add to the service
     */
    void add(TranslationBundle bundle);

    /**
     * Adds the provided message to the service. If the provided message is already registered to the
     * service, the provided message is merged into the existing message.
     *
     * @param message the message to add to the service
     */
    void add(Message message);

    /**
     * Returns the bundle of translations that is managed by this service. The bundle is used to store
     * all messages that are registered to the service.
     *
     * @return the bundle of translations that is managed by this service
     */
    TranslationBundle bundle();
}
