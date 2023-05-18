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

package org.dockbox.hartshorn.reporting.component;

import java.util.EnumMap;
import java.util.Map;

public class ComponentReportingConfiguration {

    private ComponentAttribute groupBy = ComponentAttribute.STEREOTYPE;
    private boolean includeDependencies = true;
    private boolean includeRequiredConditions = true;
    private final EnumMap<ComponentAttribute, Boolean> attributes = new EnumMap<>(ComponentAttribute.class);

    public ComponentReportingConfiguration() {
        // Package is included by type definition, so we don't need to include it by default
        this.attributes.put(ComponentAttribute.STEREOTYPE, true);
    }

    public ComponentAttribute groupBy() {
        return this.groupBy;
    }

    public ComponentReportingConfiguration groupBy(
            final ComponentAttribute groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public boolean includeDependencies() {
        return this.includeDependencies;
    }

    public ComponentReportingConfiguration includeDependencies(final boolean includeDependencies) {
        this.includeDependencies = includeDependencies;
        return this;
    }

    public boolean includeRequiredConditions() {
        return this.includeRequiredConditions;
    }

    public ComponentReportingConfiguration includeRequiredConditions(final boolean includeRequiredConditions) {
        this.includeRequiredConditions = includeRequiredConditions;
        return this;
    }

    public Map<ComponentAttribute, Boolean> attributes() {
        return this.attributes;
    }

    public ComponentReportingConfiguration attribute(final ComponentAttribute attribute, final boolean enabled) {
        this.attributes.put(attribute, enabled);
        return this;
    }

    public ComponentReportingConfiguration enable(final ComponentAttribute attribute) {
        return this.attribute(attribute, true);
    }

    public ComponentReportingConfiguration disable(final ComponentAttribute attribute) {
        return this.attribute(attribute, false);
    }

    public enum ComponentAttribute {
        STEREOTYPE,
        PACKAGE,
        NONE,
    }
}
