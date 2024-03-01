/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.application;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;

import org.slf4j.Logger;

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

    private final RuntimeMXBean runtimeMXBean;
    private final ApplicationBuildContext buildContext;

    public ApplicationStartupLogger(ApplicationBuildContext buildContext) {
        this.buildContext = buildContext;
        this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    }

    /**
     * Returns the logger that is used by this startup logger.
     *
     * @return The logger used by this startup logger
     */
    public Logger logger() {
        return this.buildContext.logger();
    }

    /**
     * Logs the startup message of the application. This message includes practical information about the
     * application and its environment, such as its name, host, Java version, PID, and working directory.
     */
    public void logStartup() {
        this.logger().info(this.getStartupMessage().toString());
    }

    /**
     * Logs the started message of the application. This message includes practical information about the
     * application and its environment, such as its name, startup time, and JVM uptime.
     *
     * @param startupTime The duration it took for the application to start
     */
    public void logStarted(Duration startupTime) {
        this.logger().info(this.getStartedMessage(startupTime).toString());
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
        this.appendApplicationName(message);
        this.appendHost(message);
        this.appendJavaVersion(message);
        this.appendPID(message);
        this.appendExecutionContext(message);

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
        this.appendApplicationName(message);
        this.appendStartupTime(message, startupTime);
        this.appendJvmUptime(message);

        return message;
    }

    /**
     * Appends the application name to the given message.
     *
     * @param message The message to append the application name to
     */
    protected void appendApplicationName(StringBuilder message) {
        message.append(" ")
                .append(this.buildContext.mainClass().getSimpleName());
    }

    /**
     * Appends the startup time to the given message. The startup time is formatted as seconds.
     *
     * @param message The message to append the startup time to
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
                .append(this.runtimeMXBean.getUptime() / 1000.0d)
                .append(")");
    }

    /**
     * Appends the host name to the given message.
     *
     * @param message The message to append the host to
     */
    protected void appendHost(StringBuilder message) {
        final String host = this.runtimeMXBean.getName().split("@")[1];
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
                .append(this.runtimeMXBean.getVmVersion());
    }

    /**
     * Appends the process ID to the given message.
     *
     * @param message The message to append the PID to
     */
    protected void appendPID(StringBuilder message) {
        message.append(" with PID ")
                .append(this.runtimeMXBean.getPid());
    }

    /**
     * Appends the execution context to the given message. This includes the user name and working directory.
     *
     * @param message The message to append the execution context to
     */
    protected void appendExecutionContext(StringBuilder message) {
        message.append(" (")
                .append("Started by ")
                .append(System.getProperty("user.name"))
                .append(" in ")
                .append(System.getProperty("user.dir"))
                .append(")");
    }
}
