/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClassBuilder;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;

import java.util.Set;

/**
 * An interpreter for {@link ConstructorStatement} class members.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ConstructorMemberInterpreter implements ClassMemberInterpreter<ConstructorStatement> {

    @Override
    public void interpret(
        ConstructorStatement constructor,
        Interpreter interpreter,
        VirtualClassBuilder builder
    ) {
        VirtualFunction function =
            new VirtualFunction(constructor, interpreter.visitingScope(), true);
        builder.constructor(function);
    }

    @Override
    public Set<Class<? extends ConstructorStatement>> types() {
        return Set.of(ConstructorStatement.class);
    }
}
