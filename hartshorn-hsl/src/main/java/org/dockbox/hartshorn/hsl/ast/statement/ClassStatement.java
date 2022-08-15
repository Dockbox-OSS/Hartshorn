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

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class ClassStatement extends FinalizableStatement implements NamedNode {

    private final Token name;
    private final VariableExpression superClass;
    private final ConstructorStatement constructor;
    private final List<FunctionStatement> methods;

    public ClassStatement(final Token name, final VariableExpression superClass, final ConstructorStatement constructor, final List<FunctionStatement> methods) {
        this(name, false, name, superClass, constructor, methods);
    }

    public ClassStatement(final ASTNode at, final boolean finalized, final Token name, final VariableExpression superClass, final ConstructorStatement constructor, final List<FunctionStatement> methods) {
        super(at, finalized);
        this.name = name;
        this.superClass = superClass;
        this.constructor = constructor;
        this.methods = methods;
    }

    @Override
    public Token name() {
        return this.name;
    }

    public VariableExpression superClass() {
        return this.superClass;
    }

    public ConstructorStatement constructor() {
        return this.constructor;
    }

    public List<FunctionStatement> methods() {
        return this.methods;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
