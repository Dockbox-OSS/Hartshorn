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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.extension.CustomStatement;
import org.dockbox.hartshorn.hsl.extension.ResolverExtension;
import org.dockbox.hartshorn.hsl.extension.StatementModule;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.parser.statement.StatementParser;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.SimpleTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class CaptureModule implements StatementModule<CaptureModule.CaptureStatement> {

    public static final TokenType CAPTURE = SimpleTokenType.builder()
            .keyword(true)
            .standaloneStatement(true)
            .tokenName("capture")
            .build();

    private Object capturedValue;

    @Override
    public StatementParser<CaptureStatement> parser() {
        return new StatementParser<>() {
            @Override
            public Option<? extends CaptureStatement> parse(TokenParser parser, TokenStepValidator validator) throws ScriptEvaluationError {
                if (parser.match(CAPTURE)) {
                    Token capture = parser.previous();
                    Expression expression = parser.expression();
                    return Option.of(new CaptureStatement(capture, CaptureModule.this, expression));
                }
                return Option.empty();
            }

            @Override
            public Set<Class<? extends CaptureStatement>> types() {
                return Set.of(CaptureStatement.class);
            }
        };
    }

    @Override
    public ResolverExtension<CaptureStatement> resolver() {
        return (node, resolver) -> {
            resolver.beginScope();
            resolver.resolve(node.expression());
            resolver.endScope();
        };
    }

    @Override
    public <U> U accept(StatementVisitor<U> visitor) {
        return null;
    }

    @Override
    public TokenType tokenType() {
        return CAPTURE;
    }

    @Override
    public ASTNodeInterpreter<Void, CaptureStatement> interpreter() {
        return (node, interpreter) -> {
            CaptureModule.this.capturedValue = interpreter.evaluate(node.expression());
            return null;
        };
    }

    public Object capturedValue() {
        return capturedValue;
    }


    public static class CaptureStatement extends CustomStatement<CaptureStatement> {

        private final Expression expression;

        public CaptureStatement(
                ASTNode at,
                StatementModule<CaptureStatement> module,
                Expression expression
        ) {
            super(at, module);
            this.expression = expression;
        }

        public Expression expression() {
            return expression;
        }
    }
}
