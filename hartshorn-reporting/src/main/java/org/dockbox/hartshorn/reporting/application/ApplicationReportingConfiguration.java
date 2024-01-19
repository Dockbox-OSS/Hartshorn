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

package org.dockbox.hartshorn.reporting.application;

/**
 * Configuration for the {@link ApplicationDiagnosticsReporter}. This configuration allows for enabling/disabling
 * of specific diagnostics.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ApplicationReportingConfiguration {

    private boolean includeVersion = true;
    private boolean includeJarLocation = true;
    private boolean includeEnvironmentVariables = true;
    private boolean includeServiceActivators = true;
    private boolean includeObservers = true;
    private boolean includeContexts = true;

    /**
     * Returns {@code true} if the version of the application should be included in the diagnostics report.
     *
     * @return {@code true} if the version of the application should be included in the diagnostics report.
     */
    public boolean includeVersion() {
        return this.includeVersion;
    }

    /**
     * Configures if the version of the application should be included in the diagnostics report.
     *
     * @param includeVersion {@code true} if the version of the application should be included in the diagnostics report.
     *
     * @return this, for chaining.
     */
    public ApplicationReportingConfiguration includeVersion(boolean includeVersion) {
        this.includeVersion = includeVersion;
        return this;
    }

    /**
     * Returns {@code true} if the location of the application jar should be included in the diagnostics report.
     *
     * @return {@code true} if the location of the application jar should be included in the diagnostics report.
     */
    public boolean includeJarLocation() {
        return this.includeJarLocation;
    }

    /**
     * Configures if the location of the application jar should be included in the diagnostics report.
     *
     * @param includeJarLocation {@code true} if the location of the application jar should be included in the diagnostics report.
     *
     * @return this, for chaining.
     */
    public ApplicationReportingConfiguration includeJarLocation(boolean includeJarLocation) {
        this.includeJarLocation = includeJarLocation;
        return this;
    }

    /**
     * Returns {@code true} if the environment variables should be included in the diagnostics report.
     *
     * @return {@code true} if the environment variables should be included in the diagnostics report.
     */
    public boolean includeApplicationProperties() {
        return this.includeEnvironmentVariables;
    }

    /**
     * Configures if the environment variables should be included in the diagnostics report.
     *
     * @param includeEnvironmentVariables {@code true} if the environment variables should be included in the diagnostics report.
     *
     * @return this, for chaining.
     */
    public ApplicationReportingConfiguration includeApplicationProperties(boolean includeEnvironmentVariables) {
        this.includeEnvironmentVariables = includeEnvironmentVariables;
        return this;
    }

    /**
     * Returns {@code true} if the service activators should be included in the diagnostics report.
     *
     * @return {@code true} if the service activators should be included in the diagnostics report.
     */
    public boolean includeServiceActivators() {
        return this.includeServiceActivators;
    }

    /**
     * Configures if the service activators should be included in the diagnostics report.
     *
     * @param includeServiceActivators {@code true} if the service activators should be included in the diagnostics report.
     *
     * @return this, for chaining.
     */
    public ApplicationReportingConfiguration includeServiceActivators(boolean includeServiceActivators) {
        this.includeServiceActivators = includeServiceActivators;
        return this;
    }

    /**
     * Returns {@code true} if the observers should be included in the diagnostics report.
     *
     * @return {@code true} if the observers should be included in the diagnostics report.
     */
    public boolean includeObservers() {
        return this.includeObservers;
    }

    /**
     * Configures if the observers should be included in the diagnostics report.
     *
     * @param includeObservers {@code true} if the observers should be included in the diagnostics report.
     *
     * @return this, for chaining.
     */
    public ApplicationReportingConfiguration includeObservers(boolean includeObservers) {
        this.includeObservers = includeObservers;
        return this;
    }

    /**
     * Returns {@code true} if the contexts should be included in the diagnostics report.
     *
     * @return {@code true} if the contexts should be included in the diagnostics report.
     */
    public boolean includeContexts() {
        return this.includeContexts;
    }

    /**
     * Configures if the contexts should be included in the diagnostics report.
     *
     * @param includeContexts {@code true} if the contexts should be included in the diagnostics report.
     *
     * @return this, for chaining.
     */
    public ApplicationReportingConfiguration includeContexts(boolean includeContexts) {
        this.includeContexts = includeContexts;
        return this;
    }
}
