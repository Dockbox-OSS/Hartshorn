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
 * A virtual representation of a field member function. This is typically linked directly to a {@link
 * VirtualClass}. Field members are special in the sense that they can be accessed like properties, but
 * they are actually functions. This allows for lazy evaluation and computed properties.
 *
 * @see org.dockbox.hartshorn.hsl.ast.statement.FieldGetStatement
 * @see org.dockbox.hartshorn.hsl.ast.statement.FieldSetStatement
 * @see MemberStatement
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

    /**
     * Indicates whether this field member has a body.
     *
     * @return {@code true} if the field member has a body, {@code false} otherwise.
     */
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
