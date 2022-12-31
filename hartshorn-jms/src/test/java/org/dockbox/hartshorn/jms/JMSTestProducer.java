package org.dockbox.hartshorn.jms;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.jms.annotations.DestinationType;
import org.dockbox.hartshorn.jms.annotations.Producer;
import org.dockbox.hartshorn.jms.annotations.UseJMS;

@Service
@RequiresActivator(UseJMS.class)
public interface JMSTestProducer {

    @Producer(id = "test.queue", destination = DestinationType.QUEUE)
    void sendToQueue(String message);

    @Producer(id = "test.topic", destination = DestinationType.TOPIC)
    void sendToTopic(String message);

    @Producer(id = "test.json")
    void sendAsJSON(User user);
}
