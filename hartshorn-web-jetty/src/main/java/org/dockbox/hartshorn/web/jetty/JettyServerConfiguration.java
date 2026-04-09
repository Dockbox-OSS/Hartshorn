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

import jakarta.servlet.Filter;
import org.dockbox.hartshorn.inject.annotations.Fuzzy;
import org.dockbox.hartshorn.inject.annotations.Named;
import org.dockbox.hartshorn.inject.annotations.Priority;
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
import org.dockbox.hartshorn.web.ServerPortProvider;
import org.dockbox.hartshorn.web.UseWebServer;
import org.dockbox.hartshorn.web.WebServer;
import org.dockbox.hartshorn.web.route.HandlerMappingResolver;
import org.dockbox.hartshorn.web.route.support.RequestRoutingServlet;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
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
    @Priority(Priority.SUPPORT_PRIORITY)
    public WebServer webServer(Server server) {
        return new JettyWebServer(server);
    }

    /**
     * Creates a prototype instance of the Jetty {@link Server}. Unlike {@link WebServer}, the
     * backing Jetty Server is not a singleton, allowing for multiple server instances if needed.
     *
     * @param serverHandler The main (servlet) handler for the server.
     * @param threadPool The thread pool for the server.
     * @param scheduler The scheduler for the server.
     * @param bufferPool The byte buffer pool for the server.
     * @param customizers A collection of customizers to configure the server.
     * @param portProvider The provider for the server port.
     *
     * @return The Jetty Server instance.
     */
    @Prototype
    @Priority(Priority.SUPPORT_PRIORITY)
    public Server jettyServer(
            Handler serverHandler,
            @Named("jettyServerThreadPool") ThreadPool threadPool,
            @Named("jettyServerScheduler") Scheduler scheduler,
            @Named("jettyServerBufferPool") ByteBufferPool bufferPool,
            @Fuzzy ComponentCollection<Customizer<Server>> customizers,
            ServerPortProvider portProvider
    ) {
        Server server = new Server(threadPool, scheduler, bufferPool);
        server.setHandler(serverHandler);

        HttpConfiguration httpConfig = new HttpConfiguration();
        ServerConnector connector = new ServerConnector(
                server,
                new HttpConnectionFactory(httpConfig)
        );

        // Jetty defines 0 as dynamic port selection, but we want to allow for -1 as well, as it is
        // a common convention for dynamic port selection in other frameworks. If a higher value is
        // provided, it will be used as the port number instead of dynamic selection.
        connector.setPort(Math.max(0, portProvider.port()));
        server.addConnector(connector);

        customizers.forEach(customizer -> customizer.configure(server));

        return server;
    }

    /**
     * Creates a prototype instance of the main server handler, which is a
     * {@link ServletContextHandler} that routes requests to the appropriate handlers based on the
     * provided {@link HandlerMappingResolver}. Additionally, any filters configured within the
     * application are added to the context handler, allowing for additional request processing and
     * manipulation before reaching the main handlers.
     *
     * @param handlerMappingResolver The resolver for mapping requests to handlers.
     * @param filters A collection of filters to be added to the context handler.
     *
     * @return The configured server handler instance.
     */
    @Prototype
    @Priority(Priority.SUPPORT_PRIORITY)
    public Handler serverHandler(
            HandlerMappingResolver handlerMappingResolver,
            @Fuzzy ComponentCollection<Filter> filters
    ) {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(
                new RequestRoutingServlet(handlerMappingResolver)
        ), "/*");

        context.addServlet();
        filters.forEach(filter -> context.addFilter(new FilterHolder(filter), "/*", null));
        return context;
    }

    /**
     * Default Jetty thread pool configuration, active when no custom thread pool is provided.
     *
     * @return The default Jetty ThreadPool instance.
     */
    @Prototype
    @Named("jettyServerThreadPool")
    @RequiresAbsentBinding(value = ThreadPool.class, name = "jettyServerThreadPool")
    @Priority(Priority.SUPPORT_PRIORITY)
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
    @Priority(Priority.SUPPORT_PRIORITY)
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
    @Named("jettyServerScheduler")
    @RequiresAbsentBinding(value = Scheduler.class, name = "jettyServerScheduler")
    @Priority(Priority.SUPPORT_PRIORITY)
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
    @Priority(Priority.SUPPORT_PRIORITY)
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
    @Named("jettyServerBufferPool")
    @RequiresAbsentBinding(value = ByteBufferPool.class, name = "jettyServerBufferPool")
    @Priority(Priority.SUPPORT_PRIORITY)
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
    @Priority(Priority.SUPPORT_PRIORITY)
    public ByteBufferPool jettyServerCustomBufferPool(
            ComponentProvider componentProvider,
            @PropertyValue(name = "hartshorn.web.jetty.bufferpool")
            Class<? extends ByteBufferPool> bufferPool
    ) {
        return componentProvider.get(bufferPool);
    }
}
