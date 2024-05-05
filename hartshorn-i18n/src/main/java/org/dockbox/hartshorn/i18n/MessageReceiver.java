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

import org.dockbox.hartshorn.util.Subject;

import java.util.Locale;

/**
 * Represents a receiver of messages, which can be used to send messages to a specific target.
 *
 * @see Message
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface MessageReceiver extends Subject {

    /**
     * Returns the preferred or primary language of the receiver.
     *
     * @return the preferred or primary language of the receiver
     */
    Locale language();

    /**
     * Sets the preferred or primary language of the receiver.
     *
     * @param language the preferred or primary language of the receiver
     */
    void language(Locale language);

    /**
     * Sends the provided message to the receiver. This will result in the message
     * being displayed to the receiver in the receiver's preferred language, in
     * whichever way the receiver is able to display messages.
     *
     * @param text the message to send
     */
    void send(Message text);
}
