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

package org.dockbox.hartshorn.reporting.system;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public class JVMDiagnosticsReporter implements Reportable {

    private final boolean includeJvmFlags;

    public JVMDiagnosticsReporter(boolean includeJvmFlags) {
        this.includeJvmFlags = includeJvmFlags;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("home").write(System.getProperty("java.home"));
        collector.property("version").write(System.getProperty("java.vm.version"));
        collector.property("vendor").write(System.getProperty("java.vm.vendor"));
        collector.property("name").write(System.getProperty("java.vm.name"));

        if (this.includeJvmFlags) {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            List<String> arguments = runtimeMXBean.getInputArguments();
            collector.property("flags").write(arguments.toArray(String[]::new));
        }
    }
}
