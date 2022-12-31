package org.dockbox.hartshorn.jms;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jms.annotations.UseJMS;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.jms.Message;

@HartshornTest
@UseJMS
@Testcontainers(disabledWithoutDocker = true)
public class JMSCommunicationTests {

    private static final String ACTIVEMQ_IMAGE = "rmohr/activemq";
    private static final int ACTIVEMQ_PORT = 61616;

    @Container
    private static final GenericContainer activemq = new GenericContainer(ACTIVEMQ_IMAGE).withExposedPorts(ACTIVEMQ_PORT);

    @InjectTest
    void subscriberCanStart(final ApplicationContext applicationContext) {
        final JMSThreadPoolProvider threadPoolProvider = applicationContext.get(JMSThreadPoolProvider.class);
        final ExecutorService executorService = threadPoolProvider.executorService();

        if (executorService instanceof ThreadPoolExecutor threadPoolExecutor) {
            Assertions.assertTrue(threadPoolExecutor.getActiveCount() > 1);
        } else {
            Assertions.fail("Cannot analyse executor service as it is not a ThreadPoolExecutor");
        }

        applicationContext.get(JMSThreadPoolProvider.class).executorService().shutdown();
    }

    @InjectTest
    void testSubscribersCanReceiveQueueMessages(final ApplicationContext applicationContext) throws InterruptedException {
        final JMSTestSubscriber subscriber = applicationContext.get(JMSTestSubscriber.class);
        final JMSTestProducer producer = applicationContext.get(JMSTestProducer.class);

        producer.sendToQueue("Hello World");
        Thread.sleep(100);

        final Message message = subscriber.queueMessage();
        Assertions.assertNotNull(message);

        applicationContext.get(JMSThreadPoolProvider.class).executorService().shutdown();
    }

    @InjectTest
    void testSubscribersCanReceiveTopicMessages(final ApplicationContext applicationContext) throws InterruptedException {
        final JMSTestSubscriber subscriber = applicationContext.get(JMSTestSubscriber.class);
        final JMSTestProducer producer = applicationContext.get(JMSTestProducer.class);

        producer.sendToTopic("Hello World");
        Thread.sleep(100);

        final Message message = subscriber.topicMessage();
        Assertions.assertNotNull(message);

        applicationContext.get(JMSThreadPoolProvider.class).executorService().shutdown();
    }

    @InjectTest
    void testSubscribersCanReceiveJSONMessages(final ApplicationContext applicationContext) throws InterruptedException {
        final JMSTestSubscriber subscriber = applicationContext.get(JMSTestSubscriber.class);
        final JMSTestProducer producer = applicationContext.get(JMSTestProducer.class);

        producer.sendAsJSON(new User("John", 32));
        Thread.sleep(250);

        final User user = subscriber.user();
        Assertions.assertNotNull(user);
        Assertions.assertEquals("John", user.name());
        Assertions.assertEquals(32, user.age());

        applicationContext.get(JMSThreadPoolProvider.class).executorService().shutdown();
    }
}
