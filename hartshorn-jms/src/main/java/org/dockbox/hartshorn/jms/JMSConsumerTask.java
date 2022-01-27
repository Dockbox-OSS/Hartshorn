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
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.inject.binding.ComponentBinding;
import org.dockbox.hartshorn.util.ApplicationException;

import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

@ComponentBinding(JMSConsumerTask.class)
public class JMSConsumerTask extends DefaultContext implements Runnable, LifecycleObserver {

    @Inject
    private JMSExceptionListener exceptionListener;
    @Inject
    private ApplicationContext applicationContext;

    private boolean closed = false;

    private Session session;
    private MessageConsumer messageConsumer;

    private final JMSConsumer consumer;
    private final JMSDestinationContext destinationContext;

    @Bound
    public JMSConsumerTask(final JMSConsumer consumer, final JMSDestinationContext destinationContext) {
        this.consumer = consumer;
        this.destinationContext = destinationContext;
    }

    @Override
    public void run() {
        if (this.consumer != null) {
            try {
                this.session = this.applicationContext.get(Key.of(Session.class, this.destinationContext.session()));

                final Destination destination = switch (this.destinationContext.destinationType()) {
                    case QUEUE -> this.session.createQueue(this.destinationContext.id());
                    case TOPIC -> this.session.createTopic(this.destinationContext.id());
                };

                this.messageConsumer = this.session.createConsumer(destination);
            }
            catch (final JMSException e) {
                this.exceptionListener.onException(e);
                return;
            }

            while (!this.closed) {
                try {
                    final Message message = this.messageConsumer.receive(1_000);
                    if (message != null) {
                        this.consumer.consume(message);
                    }
                } catch (final JMSException e) {
                    this.exceptionListener.onException(e);
                }
                catch (final ApplicationException e) {
                    this.applicationContext.handle(e);
                }
            }
        }
    }

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        // Nothing happens
    }

    @Override
    public void onExit(final ApplicationContext applicationContext) {
        this.closed = true;
        if (this.session != null) {
            try {
                this.messageConsumer.close();
                this.session.close();
            }
            catch (final JMSException e) {
                applicationContext.handle(e);
            }
        }
    }
}
