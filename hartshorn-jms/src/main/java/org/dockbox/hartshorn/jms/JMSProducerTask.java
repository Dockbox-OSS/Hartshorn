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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.option.Option;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

@Component
public class JMSProducerTask {

    @Inject
    private ApplicationContext applicationContext;
    @Inject
    private ObjectMapper mapper;

    private final JMSSessionContext sessionContext;

    private Session session;
    private MessageProducer producer;

    @Bound
    public JMSProducerTask(final JMSSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public void send(final Object message) {
        final Option<String> output = this.mapper.write(message);
        final String text = output.orElseGet(() -> String.valueOf(message));

        try {
            final TextMessage textMessage = this.session.createTextMessage(text);
            this.producer.send(textMessage);
        }
        catch (final JMSException e) {
            this.applicationContext.handle(e);
        }
    }

    @PostConstruct
    public void enable() throws ApplicationException {
        try {
            // TODO: Resolve NPE on session lookup
            this.session = this.applicationContext.get(ComponentKey.of(Session.class, this.sessionContext.session()));

            final Destination destination = switch (this.sessionContext.destinationType()) {
                case QUEUE -> this.session.createQueue(this.sessionContext.id());
                case TOPIC -> this.session.createTopic(this.sessionContext.id());
            };

            this.producer = this.session.createProducer(destination);
            this.producer.setDeliveryMode(this.sessionContext.deliveryMode());
            this.producer.setPriority(this.sessionContext.priority());
            this.producer.setTimeToLive(this.sessionContext.timeToLive());
            this.producer.setDisableMessageID(this.sessionContext.disableMessageId());
            this.producer.setDisableMessageTimestamp(this.sessionContext.disableMessageTimestamp());
        }
        catch (final JMSException e) {
            throw new ApplicationException(e);
        }
    }
}
