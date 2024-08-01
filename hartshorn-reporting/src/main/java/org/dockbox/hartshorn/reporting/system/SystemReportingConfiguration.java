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

package org.dockbox.hartshorn.reporting.system;

/**
 * A configuration object for {@link SystemDiagnosticsReporter}s that allows for fine-grained control over the
 * information that is reported.
 *
 * @see SystemDiagnosticsReporter
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SystemReportingConfiguration {

    private boolean includeUptime = true;
    private boolean includeJavaDiagnostics = true;
    private boolean includeJvmDiagnostics = true;
    private boolean includeOsDiagnostics = true;
    private boolean includeJvmFlags = true;
    private boolean includeDeviceName = true;
    private boolean includeProcessId = true;

    private boolean includeResponsibleServiceOrUser = true;
    private boolean includeMemoryUsage = true;

    /**
     * Whether the uptime of the system should be reported.
     *
     * @return {@code true} if the uptime of the system should be reported, {@code false} otherwise.
     */
    public boolean includeUptime() {
        return this.includeUptime;
    }

    /**
     * Sets whether the uptime of the system should be reported.
     *
     * @param includeUptime {@code true} if the uptime of the system should be reported, {@code false} otherwise.
     * @return this configuration object
     */
    public SystemReportingConfiguration includeUptime(boolean includeUptime) {
        this.includeUptime = includeUptime;
        return this;
    }

    /**
     * Whether information about the Java runtime should be reported.
     *
     * @return {@code true} if information about the Java runtime should be reported, {@code false} otherwise.
     */
    public boolean includeJavaDiagnostics() {
        return this.includeJavaDiagnostics;
    }

    /**
     * Sets whether information about the Java runtime should be reported.
     *
     * @param includeJavaDiagnostics {@code true} if information about the Java runtime should be reported, {@code false} otherwise.
     * @return this configuration object
     */
    public SystemReportingConfiguration includeJavaDiagnostics(boolean includeJavaDiagnostics) {
        this.includeJavaDiagnostics = includeJavaDiagnostics;
        return this;
    }

    /**
     * Whether information about the JVM should be reported.
     *
     * @return {@code true} if information about the JVM should be reported, {@code false} otherwise.
     */
    public boolean includeJvmDiagnostics() {
        return this.includeJvmDiagnostics;
    }

    /**
     * Sets whether information about the JVM should be reported.
     *
     * @param includeJvmDiagnostics {@code true} if information about the JVM should be reported, {@code false} otherwise.
     * @return this configuration object
     */
    public SystemReportingConfiguration includeJvmDiagnostics(boolean includeJvmDiagnostics) {
        this.includeJvmDiagnostics = includeJvmDiagnostics;
        return this;
    }

    /**
     * Whether information about the operating system should be reported.
     *
     * @return {@code true} if information about the operating system should be reported, {@code false} otherwise.
     */
    public boolean includeOsDiagnostics() {
        return this.includeOsDiagnostics;
    }

    /**
     * Sets whether information about the operating system should be reported.
     *
     * @param includeOsDiagnostics {@code true} if information about the operating system should be reported, {@code false} otherwise.
     * @return this configuration object
     */
    public SystemReportingConfiguration includeOsDiagnostics(boolean includeOsDiagnostics) {
        this.includeOsDiagnostics = includeOsDiagnostics;
        return this;
    }

    /**
     * Whether the JVM flags should be reported.
     *
     * @return {@code true} if the JVM flags should be reported, {@code false} otherwise.
     */
    public boolean includeJvmFlags() {
        return this.includeJvmFlags;
    }

    /**
     * Sets whether the JVM flags should be reported.
     *
     * @param includeJvmFlags {@code true} if the JVM flags should be reported, {@code false} otherwise.
     * @return this configuration object
     */
    public SystemReportingConfiguration includeJvmFlags(boolean includeJvmFlags) {
        this.includeJvmFlags = includeJvmFlags;
        return this;
    }

    /**
     * Whether the device name should be reported.
     *
     * @return {@code true} if the device name should be reported, {@code false} otherwise.
     */
    public boolean includeDeviceName() {
        return this.includeDeviceName;
    }

    /**
     * Sets whether the device name should be reported.
     *
     * @param includeDeviceName {@code true} if the device name should be reported, {@code false} otherwise.
     * @return this configuration object
     */
    public SystemReportingConfiguration includeDeviceName(boolean includeDeviceName) {
        this.includeDeviceName = includeDeviceName;
        return this;
    }

    /**
     * Whether the process ID should be reported.
     *
     * @return {@code true} if the process ID should be reported, {@code false} otherwise.
     */
    public boolean includeProcessId() {
        return this.includeProcessId;
    }

    /**
     * Sets whether the process ID should be reported.
     *
     * @param includeProcessId {@code true} if the process ID should be reported, {@code false} otherwise.
     * @return this configuration object
     */
    public SystemReportingConfiguration includeProcessId(boolean includeProcessId) {
        this.includeProcessId = includeProcessId;
        return this;
    }

    /**
     * Whether the responsible service or user should be reported.
     *
     * @return {@code true} if the responsible service or user should be reported, {@code false} otherwise.
     */
    public boolean includeResponsibleServiceOrUser() {
        return this.includeResponsibleServiceOrUser;
    }

    /**
     * Sets whether the responsible service or user should be reported.
     *
     * @param includeResponsibleServiceOrUser {@code true} if the responsible service or user should be reported, {@code false} otherwise.
     * @return this configuration object
     */
    public SystemReportingConfiguration includeResponsibleServiceOrUser(boolean includeResponsibleServiceOrUser) {
        this.includeResponsibleServiceOrUser = includeResponsibleServiceOrUser;
        return this;
    }

    /**
     * Whether the memory usage should be reported.
     *
     * @return {@code true} if the memory usage should be reported, {@code false} otherwise.
     */
    public boolean includeMemoryUsage() {
        return this.includeMemoryUsage;
    }

    /**
     * Sets whether the memory usage should be reported.
     *
     * @param includeMemoryUsage {@code true} if the memory usage should be reported, {@code false} otherwise.
     * @return this configuration object
     */
    public SystemReportingConfiguration includeMemoryUsage(boolean includeMemoryUsage) {
        this.includeMemoryUsage = includeMemoryUsage;
        return this;
    }
}
