package org.dockbox.hartshorn.jms.demo;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.jms.annotations.Producer;
import org.dockbox.hartshorn.jms.annotations.Subscriber;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Service
public abstract class DemoSub {

    @Subscriber(id = "demo")
    public void sub(final TextMessage message) throws JMSException {
        System.out.println("Received message: " + message.getText());
    }

    @Producer(id = "demo")
    public abstract void publish(final String message);
}
