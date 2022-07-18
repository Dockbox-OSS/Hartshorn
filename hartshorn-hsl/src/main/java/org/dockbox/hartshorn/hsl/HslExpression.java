package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.runtime.ValidateExpressionRuntime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class HslExpression extends HslScript {

    protected HslExpression(final ApplicationContext context, final String source) {
        super(context, source);
    }

    public static HslExpression of(final ApplicationContext context, final String source) {
        return new HslExpression(context, source);
    }

    public static HslExpression of(final ApplicationContext context, final Path path) throws IOException {
        return of(context, sourceFromPath(path));
    }

    public static HslExpression of(final ApplicationContext context, final File file) throws IOException {
        return of(context, file.toPath());
    }

    public boolean valid() {
        final ScriptContext context = this.evaluate();
        return ValidateExpressionRuntime.valid(context);
    }

    @Override
    protected ScriptRuntime createRuntime() {
        return this.applicationContext().get(ValidateExpressionRuntime.class);
    }
}
