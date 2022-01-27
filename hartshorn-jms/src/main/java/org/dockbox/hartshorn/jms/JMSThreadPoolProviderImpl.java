package org.dockbox.hartshorn.jms;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JMSThreadPoolProviderImpl implements JMSThreadPoolProvider {

    private ExecutorService executorService;

    @Override
    public ExecutorService executorService() {
        if (this.executorService == null) {
            this.executorService = Executors.newCachedThreadPool();
        }
        return this.executorService;
    }
}
