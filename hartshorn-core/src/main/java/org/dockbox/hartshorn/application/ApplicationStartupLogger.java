/*
 * Copyright 2019-2023 the original author or authors.
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
import org.slf4j.LoggerFactory;

public class ApplicationStartupLogger {

    private final RuntimeMXBean runtimeMXBean;
    private final ApplicationBuildContext buildContext;

    public ApplicationStartupLogger(ApplicationBuildContext buildContext) {
        this.buildContext = buildContext;
        this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    }

    public Logger logger() {
        return this.buildContext.logger();
    }

    public void logStartup() {
        this.logger().info(this.getStartupMessage().toString());
    }

    public void logStarted(final Duration startupTime) {
        this.logger().info(this.getStartedMessage(startupTime).toString());
    }

    private CharSequence getStartupMessage() {
        final StringBuilder message = new StringBuilder();

        message.append("Starting");
        this.appendApplicationName(message);
        this.appendHost(message);
        this.appendJavaVersion(message);
        this.appendPID(message);
        this.appendExecutionContext(message);

        return message;
    }

    private CharSequence getStartedMessage(final Duration startupTime) {
        final StringBuilder message = new StringBuilder();

        message.append("Started");
        this.appendApplicationName(message);
        this.appendStartupTime(message, startupTime);
        this.appendJvmUptime(message);

        return message;
    }

    protected void appendApplicationName(final StringBuilder message) {
        message.append(" ")
                .append(this.buildContext.mainClass().getSimpleName());
    }

    protected void appendStartupTime(final StringBuilder message, final Duration startupTime) {
        message.append(" in ")
                .append(startupTime.toMillis() / 1000.0d)
                .append(" seconds");
    }

    protected void appendJvmUptime(final StringBuilder message) {
        message.append(" (JVM running for ")
                .append(this.runtimeMXBean.getUptime() / 1000.0d)
                .append(")");
    }

    protected void appendHost(final StringBuilder message) {
        final String host = this.runtimeMXBean.getName().split("@")[1];
        message.append(" on ")
                .append(host);
    }

    protected void appendJavaVersion(final StringBuilder message) {
        message.append(" using Java ")
                .append(this.runtimeMXBean.getVmVersion());
    }

    protected void appendPID(final StringBuilder message) {
        message.append(" with PID ")
                .append(this.runtimeMXBean.getPid());
    }

    protected void appendExecutionContext(final StringBuilder message) {
        message.append(" (")
                .append("Started by ")
                .append(System.getProperty("user.name"))
                .append(" in ")
                .append(System.getProperty("user.dir"))
                .append(")");
    }
}
