package org.dockbox.sample.reporting;

import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;

public class CustomDiagnosticsReporter implements CategorizedDiagnosticsReporter {

    @Override
    public String category() {
        return "my-custom-category";
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("messageToTheWorld").writeString("Hello from Hartshorn!");
        collector.property("pi").writeDouble(Math.PI);
        collector.property("answerToLife").writeInt(42);
    }
}
