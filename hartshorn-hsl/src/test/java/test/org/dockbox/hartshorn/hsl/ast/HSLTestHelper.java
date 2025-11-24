/*
 * Copyright 2019-2025 the original author or authors.
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

package test.org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.ScriptComponentFactory;
import org.dockbox.hartshorn.hsl.StandardScriptComponentFactory;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.CacheOnlyResultCollector;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.ResultCollector;
import org.dockbox.hartshorn.hsl.lexer.Lexer;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.expression.ExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.StatementParser;
import org.dockbox.hartshorn.hsl.runtime.Return;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.DefaultTokenRegistry;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

public class HSLTestHelper {

    private final ScriptComponentFactory factory;
    private final TokenRegistry tokenRegistry;
    private final String source;

    private Lexer lexer;
    private TokenParser parser;
    private Resolver resolver;
    private Interpreter interpreter;

    private List<Statement> statements;

    public HSLTestHelper(ScriptComponentFactory factory, TokenRegistry tokenRegistry, String source) {
        this.factory = factory;
        this.tokenRegistry = tokenRegistry;
        this.source = source;
    }

    public static HSLTestHelper of(ScriptComponentFactory factory, TokenRegistry tokenRegistry, String source) {
        return new HSLTestHelper(factory, tokenRegistry, source);
    }

    public static HSLTestHelper of(String source) {
        return new HSLTestHelper(
                new StandardScriptComponentFactory(),
                DefaultTokenRegistry.createDefault(),
                source
        );
    }

    public static ExpressionTestHelper ofExpression(String source) {
        DefaultTokenRegistry tokenRegistry = DefaultTokenRegistry.createDefault();
        tokenRegistry.addTokens(CaptureModule.CAPTURE);
        CaptureModule module = new CaptureModule();
        return new ExpressionTestHelper(
                new StandardScriptComponentFactory(),
                tokenRegistry,
                "%s %s".formatted(CaptureModule.CAPTURE.tokenName(), source),
                module
        ).statementParser(module.parser());
    }

    public Lexer lexer() {
        if (this.lexer == null) {
            this.lexer = this.factory.lexer(this.tokenRegistry, this.source);

        }
        return this.lexer;
    }

    public List<Token> tokenize() {
        Lexer lexer = this.lexer();
        return lexer.scanTokens();
    }

    public TokenParser tokenParser() {
        if (this.parser == null) {
            List<Token> tokens = this.tokenize();
            this.parser = this.factory.parser(tokenRegistry, tokens);
        }
        return this.parser;
    }

    public HSLTestHelper statementParser(StatementParser<? extends Statement> parser) {
        this.parser = this.tokenParser().statementParser(parser);
        return this;
    }

    public HSLTestHelper expressionParser(ExpressionParser parser) {
        this.parser = this.tokenParser().expressionParser(parser);
        return this;
    }

    public List<Statement> parse() {
        if (this.statements == null) {
            this.statements = this.tokenParser().parse();
        }
        return this.statements;
    }

    public Resolver resolver() {
        if (this.resolver == null) {
            this.resolver = new Resolver(this.interpreter());
        }
        return this.resolver;
    }

    public Interpreter interpreter() {
        if (this.interpreter == null) {
            ResultCollector resultCollector = new CacheOnlyResultCollector(null);
            this.interpreter = this.factory.interpreter(
                    resultCollector, Map.of(), this.tokenRegistry, null
            );
        }
        return this.interpreter;
    }

    public ResultCollector interpret() {
        Interpreter interpreter = this.interpreter();
        List<Statement> statements = this.parse();
        this.resolver().resolve(statements);
        interpreter.interpret(statements);
        return interpreter.resultCollector();
    }

    public Option<?> captureReturn() {
        try {
            this.interpret();
            return Option.empty();
        }
        catch (Return returnValue) {
            return Option.of(returnValue.value());
        }
    }

    public HSLTestHelper defineVariable(String name, Object value) {
        this.interpreter().visitingScope().define(name, value);
        return this;
    }

    public <T extends ASTNode, R> R interpret(T node, ASTNodeInterpreter<R, T> interpreter) {
        return interpreter.interpret(node, interpreter());
    }

    public static class ExpressionTestHelper extends HSLTestHelper {

        private final CaptureModule module;

        public ExpressionTestHelper(
                ScriptComponentFactory factory,
                TokenRegistry tokenRegistry,
                String source,
                CaptureModule module
        ) {
            super(factory, tokenRegistry, source);
            this.module = module;
        }

        @Override
        public ExpressionTestHelper statementParser(StatementParser<? extends Statement> parser) {
            return (ExpressionTestHelper) super.statementParser(parser);
        }

        @Override
        public ExpressionTestHelper expressionParser(ExpressionParser parser) {
            return (ExpressionTestHelper) super.expressionParser(parser);
        }

        @Override
        public ExpressionTestHelper defineVariable(String name, Object value) {
            return (ExpressionTestHelper) super.defineVariable(name, value);
        }

        public Object interpretValue() {
            this.interpret();
            return this.module.capturedValue();
        }

        public Expression parseExpression() {
            List<Statement> statements = this.parse();
            if (statements.size() != 1) {
                Assertions.fail("Expected exactly one statement but got " + statements.size());
            }
            Statement statement = statements.getFirst();
            CaptureModule.CaptureStatement captureStatement = Assertions.assertInstanceOf(
                    CaptureModule.CaptureStatement.class,
                    statement
            );
            return captureStatement.expression();
        }

        public <T extends Expression> T parseExpression(Class<T> type) {
            Expression expression = this.parseExpression();
            return Assertions.assertInstanceOf(type, expression);
        }

        public <T extends Expression, R> R interpret(Class<T> type, ASTNodeInterpreter<R, T> interpreter) {
            T expression = parseExpression(type);
            Resolver resolver = new Resolver(this.interpreter());
            resolver.resolve(expression);
            return interpreter.interpret(expression, this.interpreter());
        }
    }
}
