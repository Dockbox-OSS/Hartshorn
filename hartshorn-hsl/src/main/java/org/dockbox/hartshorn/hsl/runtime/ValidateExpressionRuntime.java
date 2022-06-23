package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.customizer.ExpressionCustomizer;
import org.dockbox.hartshorn.util.Result;

public class ValidateExpressionRuntime extends StandardLibraryHslRuntime {

    public ValidateExpressionRuntime(final ApplicationContext applicationContext) {
        super(applicationContext);
        this.customizer(new ExpressionCustomizer());
    }

    public boolean valid() {
        final Result<Boolean> result = this.result(ExpressionCustomizer.VALIDATION_ID);
        return result.or(false);
    }
}
