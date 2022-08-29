package org.dockbox.hartshorn.hsl.runtime;

public class FormattedDiagnostic {

    private final DiagnosticMessage message;
    private final Object[] arguments;

    public FormattedDiagnostic(final DiagnosticMessage message, final Object... arguments) {
        this.message = message;
        this.arguments = arguments;
    }

    public DiagnosticMessage errorIndex() {
        return message;
    }

    public Object[] arguments() {
        return arguments;
    }
}
