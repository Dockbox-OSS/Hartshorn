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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class FieldMemberStatement extends ParametricExecutableBodyStatement implements MemberStatement {

    private final Token modifier;
    private final Token keyword;
    private final FieldStatement fieldStatement;

    protected FieldMemberStatement(Token modifier, Token keyword, FieldStatement fieldStatement, List<Parameter> parameters, BlockStatement body) {
        super(modifier != null ? modifier : keyword, parameters, body);
        this.modifier = modifier;
        this.keyword = keyword;
        this.fieldStatement = fieldStatement;
    }

    public boolean hasBody() {
        return this.statements() != null;
    }

    public Token keyword() {
        return this.keyword;
    }

    public FieldStatement field() {
        return this.fieldStatement;
    }

    @Override
    public Token name() {
        return this.fieldStatement.name();
    }

    @Override
    public Token modifier() {
        return this.modifier;
    }
}
