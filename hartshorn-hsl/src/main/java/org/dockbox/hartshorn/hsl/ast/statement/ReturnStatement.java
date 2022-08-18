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

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ReturnStatement extends Statement {

    public enum ReturnType {
        RETURN,
        YIELD,
    }

    private final Token keyword;
    private final Expression value;
    private final ReturnType returnType;

    public ReturnStatement(final Token keyword, final Expression value, final ReturnType returnType) {
        super(keyword);
        this.keyword = keyword;
        this.value = value;
        this.returnType = returnType;
    }

    public Token keyword() {
        return this.keyword;
    }

    public Expression value() {
        return this.value;
    }

    public ReturnType returnType() {
        return this.returnType;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
