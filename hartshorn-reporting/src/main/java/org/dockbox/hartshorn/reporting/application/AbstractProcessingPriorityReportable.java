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
