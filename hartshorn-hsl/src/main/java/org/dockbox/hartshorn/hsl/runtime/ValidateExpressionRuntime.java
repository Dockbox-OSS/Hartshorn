package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.HslLanguageFactory;
import org.dockbox.hartshorn.hsl.customizer.ExpressionCustomizer;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.util.Result;

import jakarta.inject.Inject;

public class ValidateExpressionRuntime extends StandardRuntime {

    @Inject
    public ValidateExpressionRuntime(final ApplicationContext applicationContext, final HslLanguageFactory factory) {
        super(applicationContext, factory);
        this.customizer(new ExpressionCustomizer());
    }

    public static boolean valid(final ScriptContext context) {
        final Result<Boolean> result = context.result(ExpressionCustomizer.VALIDATION_ID);
        return result.or(false);
    }
}
