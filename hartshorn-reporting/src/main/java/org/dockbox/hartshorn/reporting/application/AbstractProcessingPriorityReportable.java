/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;

public abstract class AbstractProcessingPriorityReportable implements Reportable {

    protected void reportPriority(DiagnosticsPropertyCollector collector, int priority) {
        collector.property("priority").writeDelegate(priorityCollector -> {
            priorityCollector.property("value").writeInt(priority);
            String priorityDisplayName = priorityDisplayName(priority);
            priorityCollector.property("name").writeString(priorityDisplayName);
        });
    }

    protected String priorityDisplayName(int priority) {
        return switch (priority) {
            case ProcessingPriority.HIGHEST_PRECEDENCE -> "highest";
            case ProcessingPriority.HIGH_PRECEDENCE -> "high";
            case ProcessingPriority.NORMAL_PRECEDENCE -> "normal";
            case ProcessingPriority.LOW_PRECEDENCE -> "low";
            case ProcessingPriority.LOWEST_PRECEDENCE -> "lowest";
            default -> null;
        };
    }
}
