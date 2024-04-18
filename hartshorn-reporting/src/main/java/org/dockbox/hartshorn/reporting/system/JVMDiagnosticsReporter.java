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
import java.util.List;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;

/**
 * A {@link Reportable} that reports information about the Java Virtual Machine.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class JVMDiagnosticsReporter implements Reportable {

    private final boolean includeJvmFlags;

    public JVMDiagnosticsReporter(boolean includeJvmFlags) {
        this.includeJvmFlags = includeJvmFlags;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("home").writeString(System.getProperty("java.home"));
        collector.property("version").writeString(System.getProperty("java.vm.version"));
        collector.property("vendor").writeString(System.getProperty("java.vm.vendor"));
        collector.property("name").writeString(System.getProperty("java.vm.name"));

        if (this.includeJvmFlags) {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            List<String> arguments = runtimeMXBean.getInputArguments();
            collector.property("flags").writeStrings(arguments.toArray(String[]::new));
        }
    }
}
