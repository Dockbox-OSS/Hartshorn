package org.dockbox.hartshorn.reporting.support;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;

public interface DiagnosticsPropertyWriterConsumer {

    void writeTo(DiagnosticsPropertyWriter writer);
}
