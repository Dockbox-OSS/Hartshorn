package org.dockbox.hartshorn.hsl.runtime;

public record FormattedDiagnostic(DiagnosticMessage message, Object... arguments) {
    public String format() {
        return message.format(arguments);
    }
}
