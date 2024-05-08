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

import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.util.List;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class NativeFunctionStatement extends Function implements NamedNode {

    private final Token name;
    private final Token moduleName;
    private final MethodView<?, ?> method;
    private final List<Parameter> params;

    public NativeFunctionStatement(Token name, Token moduleName, MethodView<?, ?> method, List<Parameter> params) {
        super(name);
        this.name = name;
        this.moduleName = moduleName;
        this.method = method;
        this.params = params;
    }

    @Override
    public Token name() {
        return this.name;
    }

    public Token moduleName() {
        return this.moduleName;
    }

    public List<Parameter> params() {
        return this.params;
    }

    public MethodView<?, ?> method() {
        return this.method;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
