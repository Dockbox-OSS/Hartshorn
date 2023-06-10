package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.parser.StandardTokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;
import java.util.Map;

@Service
@RequiresActivator(UseExpressionValidation.class)
public class StandardHslLanguageFactory implements HslLanguageFactory {

    @Override
    public Lexer lexer(final String source) {
        return new Lexer(source);
    }

    @Override
    public TokenParser parser(final List<Token> tokens) {
        return new StandardTokenParser(tokens);
    }

    @Override
    public Resolver resolver(final Interpreter interpreter) {
        return new Resolver(interpreter);
    }

    @Override
    public Interpreter interpreter(final ResultCollector resultCollector, final Map<String, NativeModule> modules) {
        return this.interpreter(resultCollector, modules, new ExecutionOptions());
    }

    @Override
    public Interpreter interpreter(final ResultCollector resultCollector, final Map<String, NativeModule> modules, final ExecutionOptions options) {
        return new Interpreter(resultCollector, modules, options);
    }
}
