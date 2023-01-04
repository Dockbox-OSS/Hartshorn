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

package org.dockbox.hartshorn.reporting.application;

public class ApplicationReportingConfiguration {

    private boolean includeVersion = true;
    private boolean includeJarLocation = true;
    private boolean includeEnvironmentVariables = true;
    private boolean includeServiceActivators = true;
    private boolean includeObservers = true;
    private boolean includeContexts = true;

    public boolean includeVersion() {
        return this.includeVersion;
    }

    public ApplicationReportingConfiguration includeVersion(final boolean includeVersion) {
        this.includeVersion = includeVersion;
        return this;
    }

    public boolean includeJarLocation() {
        return this.includeJarLocation;
    }

    public ApplicationReportingConfiguration includeJarLocation(final boolean includeJarLocation) {
        this.includeJarLocation = includeJarLocation;
        return this;
    }

    public boolean includeApplicationProperties() {
        return this.includeEnvironmentVariables;
    }

    public ApplicationReportingConfiguration includeApplicationProperties(final boolean includeEnvironmentVariables) {
        this.includeEnvironmentVariables = includeEnvironmentVariables;
        return this;
    }

    public boolean includeServiceActivators() {
        return this.includeServiceActivators;
    }

    public ApplicationReportingConfiguration includeServiceActivators(final boolean includeServiceActivators) {
        this.includeServiceActivators = includeServiceActivators;
        return this;
    }

    public boolean includeObservers() {
        return this.includeObservers;
    }

    public ApplicationReportingConfiguration includeObservers(final boolean includeObservers) {
        this.includeObservers = includeObservers;
        return this;
    }

    public boolean includeContexts() {
        return this.includeContexts;
    }

    public ApplicationReportingConfiguration includeContexts(final boolean includeContexts) {
        this.includeContexts = includeContexts;
        return this;
    }
}
