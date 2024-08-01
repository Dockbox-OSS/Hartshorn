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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.ConfigurableDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;

/**
 * A {@link ConfigurableDiagnosticsReporter} that reports information about the system. The information that is
 * reported can be configured using {@link SystemReportingConfiguration}. The following information is reported by
 * default:
 * <ul>
 *     <li>Uptime</li>
 *     <li>Java runtime, through {@link JavaDiagnosticsReporter}</li>
 *     <li>JVM, through {@link JVMDiagnosticsReporter}</li>
 *     <li>Operating system, through {@link OSDiagnosticsReporter}</li>
 *     <li>Device name</li>
 *     <li>Process ID</li>
 *     <li>Responsible service or user</li>
 *     <li>Memory usage, through {@link MemoryUsageDiagnosticsReporter}</li>
 * </ul>
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SystemDiagnosticsReporter implements ConfigurableDiagnosticsReporter<SystemReportingConfiguration>, CategorizedDiagnosticsReporter {

    /**
     * Default category for {@link SystemDiagnosticsReporter} instances.
     */
    public static final String SYSTEM_CATEGORY = "system";

    private final SystemReportingConfiguration configuration = new SystemReportingConfiguration();

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        if (this.configuration.includeUptime()) {
            collector.property("uptime").writeLong(runtimeMXBean.getUptime());
        }
        if (this.configuration.includeJavaDiagnostics()) {
            collector.property("java").writeDelegate(new JavaDiagnosticsReporter());
        }
        if (this.configuration.includeJvmDiagnostics()) {
            collector.property("jvm").writeDelegate(new JVMDiagnosticsReporter(this.configuration.includeJvmFlags()));
        }
        if (this.configuration.includeOsDiagnostics()) {
            collector.property("os").writeDelegate(new OSDiagnosticsReporter());
        }
        if (this.configuration.includeDeviceName()) {
            collector.property("device").writeString(runtimeMXBean.getName().split("@")[1]);
        }
        if (this.configuration.includeProcessId()) {
            collector.property("pid").writeLong(runtimeMXBean.getPid());
        }
        if (this.configuration.includeResponsibleServiceOrUser()) {
            String user = System.getProperty("user.name");
            collector.property("user").writeString(user);
        }
        if (this.configuration.includeMemoryUsage()) {
            collector.property("memory").writeDelegate(new MemoryUsageDiagnosticsReporter());
        }
    }

    @Override
    public SystemReportingConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public String category() {
        return SYSTEM_CATEGORY;
    }
}
