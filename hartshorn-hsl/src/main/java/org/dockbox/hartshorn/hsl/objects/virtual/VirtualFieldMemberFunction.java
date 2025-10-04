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

package org.dockbox.hartshorn.hsl.objects.virtual;

import org.dockbox.hartshorn.hsl.ast.statement.FieldMemberStatement;
import org.dockbox.hartshorn.hsl.ast.statement.MemberStatement;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class VirtualFieldMemberFunction extends VirtualFunction implements MemberStatement {

    private final Token name;
    private final Token modifier;

    public VirtualFieldMemberFunction(FieldMemberStatement declaration, VariableScope closure) {
        super(declaration, closure, false);
        this.name = declaration.name();
        this.modifier = declaration.modifier();
    }

    public boolean hasBody() {
        return this.declaration().body() != null;
    }

    @Override
    public FieldMemberStatement declaration() {
        return (FieldMemberStatement) super.declaration();
    }

    @Override
    public Token name() {
        return this.name;
    }

    @Override
    public Token modifier() {
        return this.modifier;
    }
}
