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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ClassStatement extends FinalizableStatement implements NamedNode {

    private final Token name;
    private final VariableExpression superClass;
    private final ConstructorStatement constructor;
    private final List<FunctionStatement> methods;
    private final List<FieldStatement> fields;
    private final boolean isDynamic;

    public ClassStatement(Token name,
                          VariableExpression superClass, ConstructorStatement constructor,
                          List<FunctionStatement> methods, List<FieldStatement> fields,
                          boolean isDynamic) {
        this(name, false, name, superClass, constructor, methods, fields, isDynamic);
    }

    public ClassStatement(ASTNode at, boolean finalized, Token name,
                          VariableExpression superClass, ConstructorStatement constructor,
                          List<FunctionStatement> methods, List<FieldStatement> fields,
                          boolean isDynamic) {
        super(at, finalized);
        this.name = name;
        this.superClass = superClass;
        this.constructor = constructor;
        this.methods = methods;
        this.fields = fields;
        this.isDynamic = isDynamic;
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

    public List<FieldStatement> fields() {
        return this.fields;
    }

    public boolean isDynamic() {
        return this.isDynamic;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
