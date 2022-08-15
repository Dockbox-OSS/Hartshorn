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

import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;
import org.dockbox.hartshorn.util.reflect.MethodContext;

import java.util.List;

public class NativeFunctionStatement extends Function implements NamedNode {

    private final Token name;
    private final Token moduleName;
    private final MethodContext<?, ?> method;
    private final List<Token> params;

    public NativeFunctionStatement(final Token name, final Token moduleName, final MethodContext<?, ?> method, final List<Token> params) {
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

    public List<Token> params() {
        return this.params;
    }

    public MethodContext<?, ?> method() {
        return this.method;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
