/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.hsl;

import java.util.List;
import java.util.Map;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;

/**
 * The {@link ScriptComponentFactory} is a service that provides the necessary components to
 * create and execute a script, which cannot be directly provided by the IoC container due to
 * them requiring additional context or state.
 *
 * @since 0.5.0
 *
 * @see Lexer
 * @see TokenParser
 * @see Resolver
 * @see Interpreter
 *
 * @author Guus Lieben
 */
public interface ScriptComponentFactory {

    /**
     * Creates a new lexer instance for the given source and token registry.
     *
     * @param tokenRegistry the token registry to use
     * @param source the source to tokenize
     * @return a new lexer instance
     */
    Lexer lexer(TokenRegistry tokenRegistry, String source);

    /**
     * Creates a new token parser instance for the given token registry and tokens.
     *
     * @param tokenRegistry the token registry to use
     * @param tokens the tokens to parse
     * @return a new token parser instance
     */
    TokenParser parser(TokenRegistry tokenRegistry, List<Token> tokens);

    /**
     * Creates a new resolver instance for the given interpreter.
     *
     * @param interpreter the interpreter to use
     * @return a new resolver instance
     */
    Resolver resolver(Interpreter interpreter);

    /**
     * Creates a new interpreter instance for the given result collector, modules, token registry and application context.
     *
     * @param resultCollector the result collector to use
     * @param modules the modules to use
     * @param tokenRegistry the token registry to use
     * @param applicationContext the application context to use
     * @return a new interpreter instance
     */
    Interpreter interpreter(ResultCollector resultCollector, Map<String, NativeModule> modules, TokenRegistry tokenRegistry, ApplicationContext applicationContext);
}
