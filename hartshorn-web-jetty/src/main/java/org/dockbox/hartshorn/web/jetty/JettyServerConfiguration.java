/*
 * Copyright 2019-2026 the original author or authors.
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

/**
 * Configuration class for setting up a Jetty web server within the Hartshorn framework.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseWebServer.class)
public class JettyServerConfiguration {

    /**
     * Creates a singleton instance of {@link WebServer}, wrapping the Jetty web server.
     *
     * @param server The Jetty server instance.
     *
     * @return The WebServer instance.
     */
    @Singleton
    public WebServer webServer(Server server) {
        return new JettyWebServer(server);
    }

    /**
     * Creates a prototype instance of the Jetty {@link Server}. Unlike {@link WebServer}, the
     * backing Jetty Server is not a singleton, allowing for multiple server instances if needed.
     *
     * @param chain The request filter chain.
     * @param threadPool The thread pool for the server.
     * @param scheduler The scheduler for the server.
     * @param bufferPool The byte buffer pool for the server.
     * @param customizers A collection of customizers to configure the server.
     * @param port The port on which the server will listen.
     *
     * @return The Jetty Server instance.
     */
    @Prototype
    public Server jettyServer(
            RequestFilterChain chain,
            @Named("jettyServerThreadPool") ThreadPool threadPool,
            @Named("jettyServerScheduler") Scheduler scheduler,
            @Named("jettyServerBufferPool") ByteBufferPool bufferPool,
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

    /**
     * Default Jetty thread pool configuration, active when no custom thread pool is provided.
     *
     * @return The default Jetty ThreadPool instance.
     */
    @Prototype
    @RequiresAbsentBinding(value = ThreadPool.class, name = "jettyServerThreadPool")
    public ThreadPool jettyServerThreadPool() {
        return new QueuedThreadPool();
    }

    /**
     * Custom Jetty thread pool configuration, activated when a custom thread pool class is
     * specified.
     *
     * @param componentProvider The component provider to instantiate the custom thread pool.
     * @param threadPool The custom thread pool class.
     *
     * @return The custom Jetty ThreadPool instance.
     */
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

    /**
     * Default Jetty scheduler configuration, active when no custom scheduler is provided.
     *
     * @return The default Jetty Scheduler instance.
     */
    @Prototype
    @RequiresAbsentBinding(value = Scheduler.class, name = "jettyServerScheduler")
    public Scheduler jettyServerScheduler() {
        return new ScheduledExecutorScheduler();
    }

    /**
     * Custom Jetty scheduler configuration, activated when a custom scheduler class is specified.
     *
     * @param componentProvider The component provider to instantiate the custom scheduler.
     * @param scheduler The custom scheduler class.
     *
     * @return The custom Jetty Scheduler instance.
     */
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

    /**
     * Default Jetty byte buffer pool configuration, active when no custom buffer pool is provided.
     *
     * @return The default Jetty ByteBufferPool instance.
     */
    @Prototype
    @RequiresAbsentBinding(value = ByteBufferPool.class, name = "jettyServerBufferPool")
    public ByteBufferPool jettyServerBufferPool() {
        return new ArrayByteBufferPool();
    }

    /**
     * Custom Jetty byte buffer pool configuration, activated when a custom buffer pool class is
     * specified.
     *
     * @param componentProvider The component provider to instantiate the custom buffer pool.
     * @param bufferPool The custom buffer pool class.
     *
     * @return The custom Jetty ByteBufferPool instance.
     */
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
