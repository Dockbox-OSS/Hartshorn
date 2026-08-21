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

/**
 * An abstract base class for {@link Reportable} implementations that need to report a processing
 * priority. This class provides a utility method to report the priority in a human-readable manner,
 * mapping the integer priority values to their corresponding display names, if applicable.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class AbstractProcessingPriorityReportable implements Reportable {

    /**
     * Reports the given processing priority to the provided {@link DiagnosticsPropertyCollector}.
     * If the given priority has a corresponding display name, it will be included in the report.
     * Otherwise, only the integer value will be reported.
     *
     * @param collector the diagnostics property collector to report to
     * @param priority the processing priority to report
     */
    protected void reportPriority(DiagnosticsPropertyCollector collector, int priority) {
        collector.property("priority").writeDelegate(priorityCollector -> {
            priorityCollector.property("value").writeInt(priority);
            String priorityDisplayName = priorityDisplayName(priority);
            priorityCollector.property("name").writeString(priorityDisplayName);
        });
    }

    /**
     * Returns the display name for the given processing priority. If the priority does not have a
     * corresponding display name, this method returns {@code null}.
     *
     * @param priority the processing priority to get the display name for
     * @return the display name for the given processing priority, or {@code null} if none exists
     */
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
