package org.dockbox.hartshorn.jms;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jms.annotations.UseJMS;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@HartshornTest
@UseJMS
@Testcontainers
public abstract class JMSActiveMQIntegrationTests {

    private static final String ACTIVEMQ_IMAGE = "rmohr/activemq";
    private static final int ACTIVEMQ_PORT = 61616;

    @Container
    private static final GenericContainer activemq = new GenericContainer(ACTIVEMQ_IMAGE).withExposedPorts(ACTIVEMQ_PORT);

    @InjectTest
    void testActiveMqContainerIsAccessible(final ApplicationContext applicationContext) throws JMSException {
        final ConnectionFactory connectionFactory = applicationContext.get(ConnectionFactory.class);
        Assertions.assertNotNull(connectionFactory);

        final Connection connection = Assertions.assertDoesNotThrow((ThrowingSupplier<Connection>) connectionFactory::createConnection);
        Assertions.assertNotNull(connection);
        connection.close();
    }
}
