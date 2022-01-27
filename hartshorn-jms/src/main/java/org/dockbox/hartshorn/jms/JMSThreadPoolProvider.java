package org.dockbox.hartshorn.jms;

import java.util.concurrent.ExecutorService;

public interface JMSThreadPoolProvider {
    ExecutorService executorService();
}
