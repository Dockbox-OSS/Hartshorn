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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class FunctionStatement extends Function {

    private final Token name;
    private final List<Token> params;
    private final List<Statement> funcBody;

    public FunctionStatement(final Token name,
                             final List<Token> params,
                             final List<Statement> funcBody) {
        super(name);
        this.name = name;
        this.params = params;
        this.funcBody = funcBody;
    }

    public Token name() {
        return this.name;
    }

    public List<Token> parameters() {
        return this.params;
    }

    public List<Statement> functionBody() {
        return this.funcBody;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
