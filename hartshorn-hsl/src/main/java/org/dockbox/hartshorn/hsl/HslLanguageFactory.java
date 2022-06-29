package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.factory.Factory;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.callable.module.NativeModule;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.parser.Parser;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.Token;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

@Service
@RequiresActivator(UseExpressionValidation.class)
public interface HslLanguageFactory {

    @Factory
    Lexer lexer(String source, ErrorReporter errorReporter);

    @Factory
    Parser parser(List<Token> tokens, ErrorReporter errorReporter);

    @Factory
    Resolver resolver(ErrorReporter errorReporter, Interpreter interpreter);

    @Factory
    Interpreter interpreter(ErrorReporter errorReporter, ResultCollector resultCollector, Logger logger, Map<String, NativeModule> modules);

}
