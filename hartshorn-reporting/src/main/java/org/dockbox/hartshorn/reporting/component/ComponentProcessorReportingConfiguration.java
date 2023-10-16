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

public class ComponentProcessorReportingConfiguration {

    private ComponentProcessorAttribute groupBy = ComponentProcessorAttribute.STEREOTYPE;
    private final EnumMap<ComponentProcessorAttribute, Boolean> attributes = new EnumMap<>(ComponentProcessorAttribute.class);

    public ComponentProcessorReportingConfiguration() {
        for (ComponentProcessorAttribute attribute : ComponentProcessorAttribute.values()) {
            this.attributes.put(attribute, true);
        }
    }

    public ComponentProcessorAttribute groupBy() {
        return this.groupBy;
    }

    public ComponentProcessorReportingConfiguration groupBy(ComponentProcessorAttribute groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public EnumMap<ComponentProcessorAttribute, Boolean> attributes() {
        return this.attributes;
    }

    public ComponentProcessorReportingConfiguration attribute(ComponentProcessorAttribute attribute, boolean enabled) {
        this.attributes.put(attribute, enabled);
        return this;
    }

    public ComponentProcessorReportingConfiguration enable(ComponentProcessorAttribute attribute) {
        return this.attribute(attribute, true);
    }

    public ComponentProcessorReportingConfiguration disable(ComponentProcessorAttribute attribute) {
        return this.attribute(attribute, false);
    }

    public enum ComponentProcessorAttribute {
        STEREOTYPE,
        PACKAGE,
        PHASE,
        NONE,
    }
}
