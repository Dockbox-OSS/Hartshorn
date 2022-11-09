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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.beans.BeanContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.StandardTokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.expression.ExpressionParser;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.inject.Context;

import java.util.Set;

import jakarta.inject.Inject;
import jakarta.inject.Named;

@Service
@RequiresActivator(UseExpressionValidation.class)
public class HslLanguageProviders {

    @Inject
    @Named(HslStatementBeans.STATEMENT_BEAN)
    private Set<ASTNodeParser<? extends Statement>> statementParsers;

    @Inject
    @Named(HslExpressionBeans.EXPRESSION_BEAN)
    private Set<ExpressionParser<?>> expressionParsers;

    @Provider
    private final Class<? extends Lexer> lexer = Lexer.class;

    @Provider
    private final Class<? extends Resolver> resolver = Resolver.class;

    @Provider
    private final Class<? extends Interpreter> interpreter = Interpreter.class;

    @Provider
    public ScriptRuntime runtime(final ApplicationContext applicationContext, final HslLanguageFactory factory) {
        return new StandardRuntime(applicationContext, factory);
    }

    @Provider
    public TokenParser tokenParser(@Context final BeanContext beanContext) {
        final StandardTokenParser parser = new StandardTokenParser();
        this.statementParsers.forEach(parser::statementParser);
        this.expressionParsers.forEach(parser::expressionParser);
        return parser;
    }
}
