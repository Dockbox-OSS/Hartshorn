package org.dockbox.hartshorn.jms;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jms.annotations.UseActiveMQ;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.ExecutorService;

@HartshornTest
@UseActiveMQ
public class JMSThreadPoolTests {

    @InjectTest
    void testThreadPoolProviderIsSingleton(final ApplicationContext applicationContext) {
        final JMSThreadPoolProvider threadPoolProvider = applicationContext.get(JMSThreadPoolProvider.class);
        Assertions.assertNotNull(threadPoolProvider);

        // Test using sameness, and not using MetaProvider#singleton. This is because the provider may be a singleton, while
        // the provided type is not.
        final JMSThreadPoolProvider threadPoolProvider2 = applicationContext.get(JMSThreadPoolProvider.class);
        Assertions.assertSame(threadPoolProvider, threadPoolProvider2);
    }

    @InjectTest
    void testProvidedExecutorServiceIsShared(final ApplicationContext applicationContext) {
        final JMSThreadPoolProvider threadPoolProvider = applicationContext.get(JMSThreadPoolProvider.class);
        Assertions.assertNotNull(threadPoolProvider);

        final ExecutorService executorService = threadPoolProvider.executorService();
        Assertions.assertNotNull(executorService);

        final ExecutorService executorService2 = threadPoolProvider.executorService();
        Assertions.assertSame(executorService, executorService2);
    }
}
