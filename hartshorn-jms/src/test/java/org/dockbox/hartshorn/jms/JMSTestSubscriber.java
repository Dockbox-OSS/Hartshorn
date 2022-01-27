package org.dockbox.hartshorn.jms;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.jms.annotations.DestinationType;
import org.dockbox.hartshorn.jms.annotations.Subscriber;
import org.dockbox.hartshorn.jms.annotations.UseJMS;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.jms.Message;

@Service(activators = UseJMS.class, permitProxying = false)
public class JMSTestSubscriber {

    @Inject
    private Logger logger;

    private Message queueMessage;
    private Message topicMessage;
    private User user;

    @Subscriber(id = "test.queue", destination = DestinationType.QUEUE)
    public void onQueueMessage(final Message message) {
        this.queueMessage = message;
        this.logger.info("Received queue message: {}", message);
    }

    @Subscriber(id = "test.topic", destination = DestinationType.TOPIC)
    public void onTopicMessage(final Message message) {
        this.topicMessage = message;
        this.logger.info("Received queue message: {}", message);
    }

    @Subscriber(id = "test.json")
    public void onJsonMessage(final User user) {
        this.user = user;
        this.logger.info("Received json message: {}", user);
    }

    public Message queueMessage() {
        return this.queueMessage;
    }

    public Message topicMessage() {
        return this.topicMessage;
    }

    public User user() {
        return this.user;
    }
}
