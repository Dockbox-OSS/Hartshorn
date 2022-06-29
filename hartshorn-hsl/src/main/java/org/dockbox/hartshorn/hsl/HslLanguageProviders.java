package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.parser.Parser;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;
import org.dockbox.hartshorn.hsl.semantic.Resolver;

@Service
@RequiresActivator(UseExpressionValidation.class)
public abstract class HslLanguageProviders {

    @Provider
    private final Class<? extends Lexer> lexer = Lexer.class;

    @Provider
    private final Class<? extends Parser> parser = Parser.class;

    @Provider
    private final Class<? extends Resolver> resolver = Resolver.class;

    @Provider
    private final Class<? extends Interpreter> interpreter = Interpreter.class;

    @Provider
    public StandardRuntime runtime(final ApplicationContext applicationContext, final HslLanguageFactory factory) {
        return new StandardRuntime(applicationContext, factory);
    }
}
