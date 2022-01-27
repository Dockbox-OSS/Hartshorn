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

package org.dockbox.hartshorn.jms;

import org.dockbox.hartshorn.jms.annotations.DestinationType;
import org.dockbox.hartshorn.jms.annotations.Producer;

public class JMSSessionContext extends JMSDestinationContext {

    private final int deliveryMode;
    private final long timeToLive;
    private final int priority;
    private final boolean disableMessageId;
    private final boolean disableMessageTimestamp;

    public JMSSessionContext(
            final String session,
            final String id,
            final DestinationType destinationType,
            final int deliveryMode,
            final long timeToLive,
            final int priority,
            final boolean disableMessageId,
            final boolean disableMessageTimestamp
    ) {
        super(session, id, destinationType);
        this.deliveryMode = deliveryMode;
        this.timeToLive = timeToLive;
        this.priority = priority;
        this.disableMessageId = disableMessageId;
        this.disableMessageTimestamp = disableMessageTimestamp;
    }

    public static JMSSessionContext of(final Producer producer) {
        return new JMSSessionContext(
                producer.session(),
                producer.id(),
                producer.destination(),
                producer.deliveryMode(),
                producer.timeToLive(),
                producer.priority(),
                producer.disableMessageId(),
                producer.disableMessageTimestamp()
        );
    }

    public int deliveryMode() {
        return this.deliveryMode;
    }

    public long timeToLive() {
        return this.timeToLive;
    }

    public int priority() {
        return this.priority;
    }

    public boolean disableMessageId() {
        return this.disableMessageId;
    }

    public boolean disableMessageTimestamp() {
        return this.disableMessageTimestamp;
    }
}
