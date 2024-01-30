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

package org.dockbox.hartshorn.reporting.component;

/**
 * General configuration for the {@link ComponentDiagnosticsReporter} that allows for fine-grained control over the
 * information that is reported.
 *
 * @since 0.5.0
 *
 * @see ComponentDiagnosticsReporter
 *
 * @author Guus Lieben
 */
public class ComponentReportingConfiguration {

    private ComponentAttribute groupBy = ComponentAttribute.STEREOTYPE;
    private boolean includeDependencies = true;
    private boolean includeRequiredConditions = true;

    /**
     * The attribute by which components should be grouped. Defaults to {@link ComponentAttribute#STEREOTYPE}.
     *
     * @return the attribute by which components should be grouped
     */
    public ComponentAttribute groupBy() {
        return this.groupBy;
    }

    /**
     * Sets the attribute by which components should be grouped.
     *
     * @param groupBy the attribute by which components should be grouped
     * @return this configuration object
     */
    public ComponentReportingConfiguration groupBy(ComponentAttribute groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    /**
     * Whether dependencies should be included in the report.
     *
     * @return {@code true} if dependencies should be included in the report, {@code false} otherwise
     */
    public boolean includeDependencies() {
        return this.includeDependencies;
    }

    /**
     * Sets whether dependencies should be included in the report.
     *
     * @param includeDependencies {@code true} if dependencies should be included in the report, {@code false} otherwise
     * @return this configuration object
     */
    public ComponentReportingConfiguration includeDependencies(boolean includeDependencies) {
        this.includeDependencies = includeDependencies;
        return this;
    }

    /**
     * Whether required conditions should be included in the report.
     *
     * @return {@code true} if required conditions should be included in the report, {@code false} otherwise
     */
    public boolean includeRequiredConditions() {
        return this.includeRequiredConditions;
    }

    /**
     * Sets whether required conditions should be included in the report.
     *
     * @param includeRequiredConditions {@code true} if required conditions should be included in the report, {@code false} otherwise
     * @return this configuration object
     */
    public ComponentReportingConfiguration includeRequiredConditions(boolean includeRequiredConditions) {
        this.includeRequiredConditions = includeRequiredConditions;
        return this;
    }
}
