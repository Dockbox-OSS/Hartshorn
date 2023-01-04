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

    public boolean includeUptime() {
        return this.includeUptime;
    }

    public SystemReportingConfiguration includeUptime(final boolean includeUptime) {
        this.includeUptime = includeUptime;
        return this;
    }

    public boolean includeJavaDiagnostics() {
        return this.includeJavaDiagnostics;
    }

    public SystemReportingConfiguration includeJavaDiagnostics(final boolean includeJavaDiagnostics) {
        this.includeJavaDiagnostics = includeJavaDiagnostics;
        return this;
    }

    public boolean includeJvmDiagnostics() {
        return this.includeJvmDiagnostics;
    }

    public SystemReportingConfiguration includeJvmDiagnostics(final boolean includeJvmDiagnostics) {
        this.includeJvmDiagnostics = includeJvmDiagnostics;
        return this;
    }

    public boolean includeOsDiagnostics() {
        return this.includeOsDiagnostics;
    }

    public SystemReportingConfiguration includeOsDiagnostics(final boolean includeOsDiagnostics) {
        this.includeOsDiagnostics = includeOsDiagnostics;
        return this;
    }

    public boolean includeJvmFlags() {
        return this.includeJvmFlags;
    }

    public SystemReportingConfiguration includeJvmFlags(final boolean includeJvmFlags) {
        this.includeJvmFlags = includeJvmFlags;
        return this;
    }

    public boolean includeDeviceName() {
        return this.includeDeviceName;
    }

    public SystemReportingConfiguration includeDeviceName(final boolean includeDeviceName) {
        this.includeDeviceName = includeDeviceName;
        return this;
    }

    public boolean includeProcessId() {
        return this.includeProcessId;
    }

    public SystemReportingConfiguration includeProcessId(final boolean includeProcessId) {
        this.includeProcessId = includeProcessId;
        return this;
    }

    public boolean includeResponsibleServiceOrUser() {
        return this.includeResponsibleServiceOrUser;
    }

    public SystemReportingConfiguration includeResponsibleServiceOrUser(final boolean includeResponsibleServiceOrUser) {
        this.includeResponsibleServiceOrUser = includeResponsibleServiceOrUser;
        return this;
    }

    public boolean includeMemoryUsage() {
        return this.includeMemoryUsage;
    }

    public SystemReportingConfiguration includeMemoryUsage(final boolean includeMemoryUsage) {
        this.includeMemoryUsage = includeMemoryUsage;
        return this;
    }
}
