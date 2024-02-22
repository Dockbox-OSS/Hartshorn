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

package org.dockbox.hartshorn.hsl.ast.statement;

import java.util.List;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ConstructorStatement extends ParametricExecutableStatement {

    private final Token keyword;

    public ConstructorStatement(Token keyword,
                                List<Parameter> params,
                                BlockStatement body) {
        super(keyword, params, body);
        this.keyword = keyword;
    }

    public Token keyword() {
        return this.keyword;
    }

    public Token initializerIdentifier() {
        return Token.of(this.type())
                .literal(this.keyword().line())
                .position(this.keyword())
                .build();
    }

    protected TokenType type() {
        return FunctionTokenType.CONSTRUCTOR;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
