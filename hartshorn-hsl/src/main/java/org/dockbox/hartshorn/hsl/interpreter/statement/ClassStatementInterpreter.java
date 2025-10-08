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

package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldGetStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldSetStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClass;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClassBuilder;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFieldMemberFunction;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualProperty;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ClassStatementInterpreter implements StatementInterpreter<ClassStatement> {

    @Override
    public Void interpret(ClassStatement node, Interpreter interpreter) {
        Object superClass = null;
        VariableExpression superClassExpression = node.superClass();
        // Because super class is a variable expression ensure it's a class reference
        if (superClassExpression != null) {
            superClass = interpreter.evaluate(superClassExpression);
            if (!(superClass instanceof ClassReference classReference)) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                        .message(DiagnosticMessage.ILLEGAL_NON_CLASS_SUPER, superClass)
                        .at(superClassExpression)
                        .build();
            }
            if (classReference.isFinal()) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                        .message(DiagnosticMessage.ILLEGAL_FINAL_SUPER_TYPE, classReference.name())
                        .at(superClassExpression)
                        .build();
            }
        }

        interpreter.visitingScope().define(node.name().lexeme(), null);
        ClassReference superClassReference = (ClassReference) superClass;
        interpreter.withNextScope(() -> this.visitClassScope(node, interpreter, superClassReference));

        return null;
    }

    private void visitClassScope(ClassStatement node, Interpreter interpreter, ClassReference superClassReference) {
        if (node.superClass() != null) {
            interpreter.enterScope(new VariableScope(interpreter.visitingScope()));
            interpreter.visitingScope().define(ObjectTokenType.SUPER.representation(), superClassReference);
        }

        Map<String, VirtualFunction> methods = this.methodsToVirtualFunctions(node, interpreter);
        VirtualFunction constructor = this.constructorToVirtualFunction(node, interpreter);
        Map<String, VirtualProperty> fields = this.fieldsToVirtualProperties(node, interpreter);
        VirtualClass virtualClass = new VirtualClassBuilder(node.name(), interpreter.visitingScope())
                .superClass(superClassReference)
                .dynamic(node.isDynamic())
                .isFinal(node.isFinal())
                .constructor(constructor)
                .methods(methods)
                .fields(fields)
                .build();

        if (superClassReference != null) {
            interpreter.enterScope(interpreter.visitingScope().enclosing());
        }

        interpreter.visitingScope().enclosing().assign(node.name(), virtualClass);
    }

    private VirtualFunction constructorToVirtualFunction(ClassStatement node, Interpreter interpreter) {
        VirtualFunction constructor = null;
        if (node.constructor() != null) {
            constructor = new VirtualFunction(node.constructor(), interpreter.visitingScope(), true);
        }
        return constructor;
    }

    private Map<String, VirtualProperty> fieldsToVirtualProperties(ClassStatement node, Interpreter interpreter) {
        Map<String, VirtualProperty> properties = new LinkedHashMap<>();
        for (FieldStatement field : node.fields()) {
            VirtualProperty virtualProperty = new VirtualProperty(field);
            FieldGetStatement getter = field.getter();
            if (getter != null) {
                virtualProperty.getter(new VirtualFieldMemberFunction(getter, new VariableScope(interpreter.visitingScope())));
            }
            FieldSetStatement setter = field.setter();
            if (setter != null) {
                virtualProperty.setter(new VirtualFieldMemberFunction(setter, new VariableScope(interpreter.visitingScope())));
            }
            properties.put(field.name().lexeme(), virtualProperty);
        }
        return properties;
    }

    private Map<String, VirtualFunction> methodsToVirtualFunctions(ClassStatement node, Interpreter interpreter) {
        Map<String, VirtualFunction> methods = new LinkedHashMap<>();
        for (FunctionStatement method : node.methods()) {
            VirtualFunction function = new VirtualFunction(method, interpreter.visitingScope(), false);
            methods.put(method.name().lexeme(), function);
        }
        return methods;
    }
}
