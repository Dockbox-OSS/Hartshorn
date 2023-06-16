/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.parser.StandardTokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.expression.ComplexExpressionParserAdapter;
import org.dockbox.hartshorn.hsl.parser.expression.ExpressionParser;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;
import org.dockbox.hartshorn.hsl.semantic.Resolver;

@Service
@RequiresActivator(UseExpressionValidation.class)
public class HslLanguageProviders {

    @Binds
    private HslLanguageFactory languageFactory() {
        return new StandardHslLanguageFactory();
    }

    @Binds
    private Lexer lexer() {
        // TODO: Assisted inject? Perhaps @Assisted String source so dependency graph knows to skip it?
        return new Lexer(null);
    }

    @Binds
    private TokenParser tokenParser() {
        return new StandardTokenParser();
    }

    @Binds
    private ExpressionParser expressionParser() {
        return new ComplexExpressionParserAdapter();
    }

    @Binds
    private Resolver resolver(final Interpreter interpreter) {
        return new Resolver(interpreter);
    }

    @Binds
    private Interpreter interpreter() {
        // TODO: Assisted inject?
        return new Interpreter(null, null);
    }

    @Binds
    public ScriptRuntime runtime(final ApplicationContext applicationContext, final HslLanguageFactory factory) {
        return new StandardRuntime(applicationContext, factory);
    }
}
