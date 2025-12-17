/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.launchpad.launch;

import org.dockbox.hartshorn.context.SimpleSingleElementContext;
import org.dockbox.hartshorn.util.configure.ContextualInitializer;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.slf4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;

/**
 * A logger that logs the startup phases of an application. This includes the startup message and the started
 * message. This is commonly used by {@link ApplicationBuilder application builders} to delegate the logging of
 * startup messages to a separate class.
 *
 * <p>Note that this does not include banners or other forms of visual startup messages. This is purely for
 * practical logging purposes.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class ApplicationStartupLogger {

    /**
     * Utility record that holds data about the application for logging purposes.
     *
     * @param runtimeMXBean runtime details of the application
     * @param buildContext build context of the application
     */
    public record ApplicationData(
            RuntimeMXBean runtimeMXBean,
            ApplicationBuildContext buildContext
    ) { }

    private final boolean includeApplicationName;
    private final boolean includeStartupTime;
    private final boolean includeJvmUptime;
    private final boolean includeHost;
    private final boolean includeJavaVersion;
    private final boolean includeProcessId;
    private final boolean includeResponsibleUser;
    private final boolean includeDirectory;

    private final ApplicationData applicationData;

    protected ApplicationStartupLogger(Configurer configurer, ApplicationBuildContext buildContext) {
        this.applicationData = new ApplicationData(
                ManagementFactory.getRuntimeMXBean(),
                buildContext
        );
        SimpleSingleElementContext<ApplicationData> context = SimpleSingleElementContext.create(this.applicationData);
        this.includeApplicationName = configurer.includeApplicationName.initialize(context);
        this.includeStartupTime = configurer.includeStartupTime.initialize(context);
        this.includeJvmUptime = configurer.includeJvmUptime.initialize(context);
        this.includeHost = configurer.includeHost.initialize(context);
        this.includeJavaVersion = configurer.includeJavaVersion.initialize(context);
        this.includeProcessId = configurer.includeProcessId.initialize(context);
        this.includeResponsibleUser = configurer.includeResponsibleUser.initialize(context);
        this.includeDirectory = configurer.includeDirectory.initialize(context);
    }

    protected boolean isEnabled() {
        return this.includeApplicationName ||
               this.includeStartupTime ||
               this.includeJvmUptime ||
               this.includeHost ||
               this.includeJavaVersion ||
               this.includeProcessId ||
               this.includeResponsibleUser ||
               this.includeDirectory;
    }

    /**
     * Returns the logger that is used by this startup logger.
     *
     * @return The logger used by this startup logger
     */
    public Logger logger() {
        return this.applicationData.buildContext().logger();
    }

    /**
     * Logs the startup message of the application. This message includes practical information about the
     * application and its environment, such as its name, host, Java version, PID, and working directory.
     */
    public void logStartup() {
        if (this.includeApplicationName
                || this.includeHost
                || this.includeJavaVersion
                || this.includeProcessId
                || this.includeResponsibleUser
                || this.includeDirectory
        ) {
            this.logger().info(this.getStartupMessage().toString());
        }
    }

    /**
     * Logs the started message of the application. This message includes practical information about the
     * application and its environment, such as its name, startup time, and JVM uptime.
     *
     * @param startupTime The duration it took for the application to start
     */
    public void logStarted(Duration startupTime) {
        if (this.includeApplicationName
                || this.includeStartupTime
                || this.includeJvmUptime) {
            this.logger().info(this.getStartedMessage(startupTime).toString());
        }
    }

    /**
     * Returns the startup message of the application. This message includes practical information about the
     * application and its environment, such as its name, host, Java version, PID, and working directory.
     *
     * @return The startup message of the application
     */
    protected CharSequence getStartupMessage() {
        final StringBuilder message = new StringBuilder();

        message.append("Starting");
        if (this.includeApplicationName) {
            this.appendApplicationName(message);
        }
        if (this.includeHost) {
            this.appendHost(message);
        }
        if (this.includeJavaVersion) {
            this.appendJavaVersion(message);
        }
        if (this.includeProcessId) {
            this.appendPID(message);
        }
        if (this.includeResponsibleUser || this.includeDirectory) {
            this.appendExecutionContext(message);
        }

        return message;
    }

    /**
     * Returns the started message of the application. This message includes practical information about the
     * application and its environment, such as its name, startup time, and JVM uptime.
     *
     * @param startupTime The duration it took for the application to start
     * @return The started message of the application
     */
    protected CharSequence getStartedMessage(Duration startupTime) {
        final StringBuilder message = new StringBuilder();

        message.append("Started");
        if (this.includeApplicationName) {
            this.appendApplicationName(message);
        }
        if (this.includeStartupTime) {
            this.appendStartupTime(message, startupTime);
        }
        if (this.includeJvmUptime) {
            this.appendJvmUptime(message);
        }

        return message;
    }

    /**
     * Appends the application name to the given message.
     *
     * @param message The message to append the application name to
     */
    protected void appendApplicationName(StringBuilder message) {
        message.append(" ")
                .append(this.applicationData.buildContext().applicationName());
    }

    /**
     * Appends the startup time to the given message. The startup time is formatted as seconds.
     *
     * @param message     The message to append the startup time to
     * @param startupTime The duration it took for the application to start
     */
    protected void appendStartupTime(StringBuilder message, Duration startupTime) {
        message.append(" in ")
                .append(startupTime.toMillis() / 1000.0d)
                .append(" seconds");
    }

    /**
     * Appends the JVM uptime to the given message. The JVM uptime is formatted as seconds.
     *
     * @param message The message to append the JVM uptime to
     */
    protected void appendJvmUptime(StringBuilder message) {
        message.append(" (JVM running for ")
                .append(this.applicationData.runtimeMXBean().getUptime() / 1000.0d)
                .append(")");
    }

    /**
     * Appends the hostname of the system on which the application is running. This is determined by the present
     * {@link RuntimeMXBean}. The hostname is extracted from the {@link RuntimeMXBean#getName()}.
     *
     * <p>For example, if the {@link RuntimeMXBean#getName()} returns {@code root@hartshorn-ci}, the returned value will be
     * {@code hartshorn-ci}.
     *
     * @param message The message to append the host to
     */
    protected void appendHost(StringBuilder message) {
        // Alternative to InetAddress.getLocalHost().getHostName()
        final String host = this.applicationData.runtimeMXBean().getName().split("@")[1];
        message.append(" on ")
                .append(host);
    }

    /**
     * Appends the Java (VM) version to the given message.
     *
     * @param message The message to append the Java version to
     */
    protected void appendJavaVersion(StringBuilder message) {
        message.append(" using Java ")
                .append(this.applicationData.runtimeMXBean().getVmVersion());
    }

    /**
     * Appends the process ID to the given message.
     *
     * @param message The message to append the PID to
     */
    protected void appendPID(StringBuilder message) {
        message.append(" with PID ")
                .append(this.applicationData.runtimeMXBean().getPid());
    }

    /**
     * Appends the execution context to the given message. This includes the user name and working directory.
     *
     * @param message The message to append the execution context to
     */
    protected void appendExecutionContext(StringBuilder message) {
        if (!this.includeDirectory && !this.includeResponsibleUser) {
            return;
        }
        message.append(" (Started");
        if (this.includeResponsibleUser) {
            message.append(" by ")
                    .append(System.getProperty("user.name"));
        }
        if (this.includeDirectory) {
            message.append(" in ")
                    .append(System.getProperty("user.dir"));
        }
        message.append(")");
    }

    public static ContextualInitializer<ApplicationBuildContext, ApplicationStartupLogger> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new ApplicationStartupLogger(configurer, context.input());
        };
    }

    /**
     * Configurer for the {@link ApplicationStartupLogger}.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        private ContextualInitializer<ApplicationData, Boolean> includeApplicationName = ContextualInitializer.of(true);
        private ContextualInitializer<ApplicationData, Boolean> includeStartupTime = ContextualInitializer.of(true);
        private ContextualInitializer<ApplicationData, Boolean> includeJvmUptime = ContextualInitializer.of(true);
        private ContextualInitializer<ApplicationData, Boolean> includeHost = ContextualInitializer.of(true);
        private ContextualInitializer<ApplicationData, Boolean> includeJavaVersion = ContextualInitializer.of(true);
        private ContextualInitializer<ApplicationData, Boolean> includeProcessId = ContextualInitializer.of(true);
        private ContextualInitializer<ApplicationData, Boolean> includeResponsibleUser = ContextualInitializer.of(true);
        private ContextualInitializer<ApplicationData, Boolean> includeDirectory = ContextualInitializer.of(true);

        public Configurer includeApplicationName(ContextualInitializer<ApplicationData, Boolean> include) {
            this.includeApplicationName = include;
            return this;
        }

        public Configurer includeApplicationName(boolean include) {
            return this.includeApplicationName(ContextualInitializer.of(include));
        }

        public Configurer includeStartupTime(ContextualInitializer<ApplicationData, Boolean> include) {
            this.includeStartupTime = include;
            return this;
        }

        public Configurer includeStartupTime(boolean include) {
            return this.includeStartupTime(ContextualInitializer.of(include));
        }

        public Configurer includeJvmUptime(ContextualInitializer<ApplicationData, Boolean> include) {
            this.includeJvmUptime = include;
            return this;
        }

        public Configurer includeJvmUptime(boolean include) {
            return this.includeJvmUptime(ContextualInitializer.of(include));
        }

        public Configurer includeHost(ContextualInitializer<ApplicationData, Boolean> include) {
            this.includeHost = include;
            return this;
        }

        public Configurer includeHost(boolean include) {
            return this.includeHost(ContextualInitializer.of(include));
        }

        public Configurer includeJavaVersion(ContextualInitializer<ApplicationData, Boolean> include) {
            this.includeJavaVersion = include;
            return this;
        }

        public Configurer includeJavaVersion(boolean include) {
            return this.includeJavaVersion(ContextualInitializer.of(include));
        }

        public Configurer includeProcessId(ContextualInitializer<ApplicationData, Boolean> include) {
            this.includeProcessId = include;
            return this;
        }

        public Configurer includeProcessId(boolean include) {
            return this.includeProcessId(ContextualInitializer.of(include));
        }

        public Configurer includeResponsibleUser(ContextualInitializer<ApplicationData, Boolean> include) {
            this.includeResponsibleUser = include;
            return this;
        }

        public Configurer includeResponsibleUser(boolean include) {
            return this.includeResponsibleUser(ContextualInitializer.of(include));
        }

        public Configurer includeDirectory(ContextualInitializer<ApplicationData, Boolean> include) {
            this.includeDirectory = include;
            return this;
        }

        public Configurer includeDirectory(boolean include) {
            return this.includeDirectory(ContextualInitializer.of(include));
        }

        public Configurer disableAll() {
            return this.includeApplicationName(false)
                    .includeStartupTime(false)
                    .includeJvmUptime(false)
                    .includeHost(false)
                    .includeJavaVersion(false)
                    .includeProcessId(false)
                    .includeResponsibleUser(false)
                    .includeDirectory(false);
        }

        public Configurer enableAll() {
            return this.includeApplicationName(true)
                    .includeStartupTime(true)
                    .includeJvmUptime(true)
                    .includeHost(true)
                    .includeJavaVersion(true)
                    .includeProcessId(true)
                    .includeResponsibleUser(true)
                    .includeDirectory(true);
        }
    }
}
