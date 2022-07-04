/*
 * Copyright 2019-2022 the original author or authors.
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
