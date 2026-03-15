/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.hsl.ast.statement;

import java.util.List;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

/**
 * A constructor statement, which defines a special method used to initialize new objects of a
 * class. Unlike Java, constructors in HSL are defined using the {@code constructor} keyword,
 * followed by a parameter list and a body.
 *
 * <p>Constructors cannot carry access modifiers (e.g., public, private), and are thus public
 * by default.
 *
 * <p>For example, the statement below defines a constructor that takes two parameters,
 * {@code param1} and {@code param2}, and initializes the object with these values:
 * <pre>{@code
 * constructor(param1, param2) {
 *    // constructor body
 * }
 * }</pre>
 *
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
public class ConstructorStatement extends ParametricExecutableBodyStatement {

    private final Token keyword;

    public ConstructorStatement(
        Token keyword,
        List<Parameter> params,
        BlockStatement body
    ) {
        super(keyword, params, body);
        this.keyword = keyword;
    }

    /**
     * Returns the token representing the {@code constructor} keyword.
     *
     * @return the {@code constructor} keyword token
     */
    public Token keyword() {
        return this.keyword;
    }

    /**
     * Returns a token that identifies this constructor initializer. This is the equivalent of
     * {@link #keyword()}, but with the type explicitly set to {@link FunctionTokenType#CONSTRUCTOR}
     * to allow for a stricter lexical meaning when defining or referencing constructors.
     *
     * @return the initializer identifier token
     */
    public Token initializerIdentifier() {
        return Token.of(this.type())
            .literal(this.keyword().line())
            .position(this.keyword())
            .build();
    }

    /**
     * Returns the token type representing a constructor.
     *
     * @return the constructor token type
     */
    protected TokenType type() {
        return FunctionTokenType.CONSTRUCTOR;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
