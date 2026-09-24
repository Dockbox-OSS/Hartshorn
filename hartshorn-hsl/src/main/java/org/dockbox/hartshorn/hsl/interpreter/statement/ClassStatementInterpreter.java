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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.statement.ClassMemberStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClass;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualClassBuilder;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Interpreter for {@link ClassStatement} nodes.
 *
 * @since 0.5.0
 * 
 * @author Guus Lieben
 */
public class ClassStatementInterpreter implements StatementInterpreter<ClassStatement> {

    private final Set<ClassMemberInterpreter<?>> memberInterpreters =
        Collections.synchronizedSet(new LinkedHashSet<>());

    public ClassStatementInterpreter() {
        this.memberInterpreter(new ConstructorMemberInterpreter());
        this.memberInterpreter(new MethodMemberInterpreter());
        this.memberInterpreter(new FieldMemberInterpreter());
    }

    /**
     * Registers a class member interpreter to be used when interpreting class body members.
     *
     * @param memberInterpreter the member interpreter to register
     * @return this interpreter, for chaining
     */
    public ClassStatementInterpreter memberInterpreter(
        ClassMemberInterpreter<?> memberInterpreter
    ) {
        if (memberInterpreter != null) {
            this.memberInterpreters.add(memberInterpreter);
        }
        return this;
    }

    /**
     * Returns the set of registered class member interpreters.
     *
     * @return the set of registered class member interpreters
     */
    public Set<ClassMemberInterpreter<?>> memberInterpreters() {
        return Collections.unmodifiableSet(this.memberInterpreters);
    }

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
        interpreter.withNextScope(() -> this.visitClassScope(node,
            interpreter,
            superClassReference));

        return null;
    }

    private void visitClassScope(
        ClassStatement node,
        Interpreter interpreter,
        ClassReference superClassReference
    ) {
        if (node.superClass() != null) {
            interpreter.enterScope(new VariableScope(interpreter.visitingScope()));
            interpreter.visitingScope()
                .define(ObjectTokenType.SUPER.representation(), superClassReference);
        }

        VirtualClassBuilder builder =
            new VirtualClassBuilder(node.name(), interpreter.visitingScope())
                .superClass(superClassReference)
                .dynamic(node.isDynamic())
                .isFinal(node.isFinal());

        for (ClassMemberStatement member : node.members()) {
            this.interpretMember(member, interpreter, builder);
        }

        VirtualClass virtualClass = builder.build();

        if (superClassReference != null) {
            interpreter.enterScope(interpreter.visitingScope().enclosing());
        }

        interpreter.visitingScope().enclosing().assign(node.name(), virtualClass);
    }

    @SuppressWarnings("unchecked")
    private <T extends ClassMemberStatement> void interpretMember(
        T member,
        Interpreter interpreter,
        VirtualClassBuilder builder
    ) {
        for (ClassMemberInterpreter<?> memberInterpreter : this.memberInterpreters) {
            if (memberInterpreter.types().contains(member.getClass())) {
                ((ClassMemberInterpreter<T>) memberInterpreter)
                    .interpret(member, interpreter, builder);
                return;
            }
        }
        for (ClassMemberInterpreter<?> memberInterpreter : this.memberInterpreters) {
            for (Class<?> type : memberInterpreter.types()) {
                if (type.isInstance(member)) {
                    ((ClassMemberInterpreter<T>) memberInterpreter)
                        .interpret(member, interpreter, builder);
                    return;
                }
            }
        }
        if (interpreter.state() != null) {
            for (ClassMemberInterpreter<?> memberInterpreter :
                    interpreter.state().memberInterpreters()) {
                if (memberInterpreter.types().contains(member.getClass())) {
                    ((ClassMemberInterpreter<T>) memberInterpreter)
                        .interpret(member, interpreter, builder);
                    return;
                }
            }
            for (ClassMemberInterpreter<?> memberInterpreter :
                    interpreter.state().memberInterpreters()) {
                for (Class<?> type : memberInterpreter.types()) {
                    if (type.isInstance(member)) {
                        ((ClassMemberInterpreter<T>) memberInterpreter)
                            .interpret(member, interpreter, builder);
                        return;
                    }
                }
            }
        }
    }
}
