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

package test.org.dockbox.hartshorn.hsl.extension;

import java.util.concurrent.atomic.AtomicBoolean;

import org.dockbox.hartshorn.hsl.extension.ResolverExtension;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.extension.ExpressionModule;
import org.dockbox.hartshorn.hsl.token.SimpleTokenCharacter;
import org.dockbox.hartshorn.hsl.token.type.SimpleTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;
import org.junit.jupiter.api.Assertions;

public class AtNameModule implements ExpressionModule<AtNameExpression> {

    public static final TokenType TOKEN_TYPE = SimpleTokenType.builder()
            .characters(SimpleTokenCharacter.of('@', true))
            .tokenName("at")
            .build();

    private final AtomicBoolean resolverAccessed = new AtomicBoolean(false);

    public boolean resolverAccessed() {
        return this.resolverAccessed.get();
    }

    @Override
    public TokenType tokenType() {
        return TOKEN_TYPE;
    }

    @Override
    public ASTNodeParser<AtNameExpression> parser() {
        return new AtNameExpressionParser(this);
    }

    @Override
    public ASTNodeInterpreter<Object, AtNameExpression> interpreter() {
        return (node, adapter) -> node.identifier().lexeme();
    }

    @Override
    public ResolverExtension<AtNameExpression> resolver() {
        return (node, resolver) -> {
            // This is where you would resolve the expression to a value
            resolverAccessed.set(true);
        };
    }

    @Override
    public <U> U accept(ExpressionVisitor<U> visitor) {
        // Default runtime has two visitors:
        // - ResolverVisitor for semantics, which should delegate to #resolver()
        // - InterpreterVisitor for runtime, which should delegate to #interpreter()
        // Other visitors should not be in the default runtime, so we fail the test if
        // one is encountered. In regular implementations this method should be overridden
        // to handle visitor delegation.
        return Assertions.fail("Expression should not be visited by default runtime");
    }
}
