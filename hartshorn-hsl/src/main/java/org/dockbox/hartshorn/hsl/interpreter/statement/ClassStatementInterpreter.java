/*
 * Copyright 2019-2023 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClass;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;

public class ClassStatementInterpreter implements ASTNodeInterpreter<Void, ClassStatement> {

    @Override
    public Void interpret(final ClassStatement node, final InterpreterAdapter adapter) {
        Object superClass = null;
        // Because super class is a variable expression assert it's a class
        if (node.superClass() != null) {
            superClass = adapter.evaluate(node.superClass());
            if (!(superClass instanceof ClassReference virtualClass)) {
                throw new RuntimeError(node.superClass().name(), "Superclass must be a class.");
            }
            if (virtualClass.isFinal()) {
                throw new ScriptEvaluationError("Cannot extend final class '" + virtualClass.name() + "'.", Phase.INTERPRETING, node.superClass().name());
            }
        }

        adapter.visitingScope().define(node.name().lexeme(), null);

        final ClassReference superClassReference = (ClassReference) superClass;
        adapter.withNextScope(() -> visitClassScope(node, adapter, superClassReference));

        return null;
    }

    private static void visitClassScope(final ClassStatement node, final InterpreterAdapter adapter, final ClassReference superClassReference) {
        if (node.superClass() != null) {
            adapter.enterScope(new VariableScope(adapter.visitingScope()));
            adapter.visitingScope().define(ObjectTokenType.SUPER.representation(), superClassReference);
        }

        final Map<String, VirtualFunction> methods = new HashMap<>();

        // Bind all method into the class
        for (final FunctionStatement method : node.methods()) {
            final VirtualFunction function = new VirtualFunction(method, adapter.visitingScope(), false);
            methods.put(method.name().lexeme(), function);
        }

        VirtualFunction constructor = null;
        if (node.constructor() != null) {
            constructor = new VirtualFunction(node.constructor(), adapter.visitingScope(), true);
        }

        final Map<String, FieldStatement> fields = node.fields().stream().collect(Collectors.toUnmodifiableMap(field -> field.name().lexeme(), f -> f));

        final VirtualClass virtualClass = new VirtualClass(node.name().lexeme(),
                superClassReference, constructor, adapter.visitingScope(),
                methods, fields,
                node.isFinal(), node.isDynamic());

        if (superClassReference != null) {
            adapter.enterScope(adapter.visitingScope().enclosing());
        }

        adapter.visitingScope().enclosing().assign(node.name(), virtualClass);
    }
}
