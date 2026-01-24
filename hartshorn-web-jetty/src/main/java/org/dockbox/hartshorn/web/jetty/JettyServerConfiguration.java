package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.inject.annotations.Fuzzy;
import org.dockbox.hartshorn.inject.annotations.Named;
import org.dockbox.hartshorn.inject.annotations.PropertyValue;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Prototype;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.condition.support.RequiresAbsentBinding;
import org.dockbox.hartshorn.inject.condition.support.RequiresProperty;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.launchpad.condition.RequiresActivator;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.UseWebServer;
import org.dockbox.hartshorn.web.WebServer;
import org.dockbox.hartshorn.web.chain.RequestFilterChain;
import org.dockbox.hartshorn.web.jetty.route.JettyRequestHandler;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;

@Configuration
@RequiresActivator(UseWebServer.class)
public class JettyServerConfiguration {

    @Singleton
    public WebServer webServer(Server server) {
        return new JettyWebServer(server);
    }

    @Prototype
    public Server jettyServer(
            RequestFilterChain chain,
            ThreadPool threadPool,
            Scheduler scheduler,
            ByteBufferPool bufferPool,
            @Fuzzy ComponentCollection<Customizer<Server>> customizers,
            @PropertyValue(name = "hartshorn.web.port", defaultValue = "8080") int port
    ) {
        Server server = new Server(threadPool, scheduler, bufferPool);
        server.setHandler(new JettyRequestHandler(chain));

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        customizers.forEach(customizer -> customizer.configure(server));

        return server;
    }

    @Prototype
    @RequiresAbsentBinding(value = ThreadPool.class, name = "jettyServerThreadPool")
    public ThreadPool jettyServerThreadPool() {
        return new QueuedThreadPool();
    }

    @Prototype
    @Named("jettyServerThreadPool")
    @RequiresProperty(name = "hartshorn.web.jetty.threadpool")
    public ThreadPool jettyServerCustomThreadPool(
            ComponentProvider componentProvider,
            @PropertyValue(name = "hartshorn.web.jetty.threadpool")
            Class<? extends ThreadPool> threadPool
    ) {
        return componentProvider.get(threadPool);
    }

    @Prototype
    @RequiresAbsentBinding(value = Scheduler.class, name = "jettyServerScheduler")
    public Scheduler jettyServerScheduler() {
        return new ScheduledExecutorScheduler();
    }

    @Prototype
    @Named("jettyServerScheduler")
    @RequiresProperty(name = "hartshorn.web.jetty.scheduler")
    public Scheduler jettyServerCustomScheduler(
            ComponentProvider componentProvider,
            @PropertyValue(name = "hartshorn.web.jetty.scheduler")
            Class<? extends Scheduler> scheduler
    ) {
        return componentProvider.get(scheduler);
    }

    @Prototype
    @RequiresAbsentBinding(value = ByteBufferPool.class, name = "jettyServerBufferPool")
    public ByteBufferPool jettyServerBufferPool() {
        return new ArrayByteBufferPool();
    }

    @Prototype
    @Named("jettyServerBufferPool")
    @RequiresProperty(name = "hartshorn.web.jetty.bufferpool")
    public ByteBufferPool jettyServerCustomBufferPool(
            ComponentProvider componentProvider,
            @PropertyValue(name = "hartshorn.web.jetty.bufferpool")
            Class<? extends ByteBufferPool> bufferPool
    ) {
        return componentProvider.get(bufferPool);
    }
}
