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

package org.dockbox.hartshorn.hsl.compiler;

import java.lang.classfile.Label;
import java.util.ArrayDeque;
import java.util.Deque;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.semantic.SymbolTable;

/**
 * Context object tracking compiler state, loop targets, local variable slots, and AST resolution
 * distances during bytecode compilation.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class CompilationContext {

    public static final int CONTEXT_SLOT = 1;
    public static final int SCOPE_SLOT = 2;

    private final ScriptContext scriptContext;
    private final SymbolTable symbolTable;
    private final String className;

    private final Deque<Label> breakTargets = new ArrayDeque<>();
    private final Deque<Label> continueTargets = new ArrayDeque<>();
    private int nextLocalSlot = 3;

    public CompilationContext(ScriptContext scriptContext, SymbolTable symbolTable, String className) {
        this.scriptContext = scriptContext;
        this.symbolTable = symbolTable;
        this.className = className;
    }

    public ScriptContext scriptContext() {
        return this.scriptContext;
    }

    public SymbolTable symbolTable() {
        return this.symbolTable;
    }

    public String className() {
        return this.className;
    }

    public Integer distance(Expression expression) {
        return this.symbolTable != null ? this.symbolTable.distance(expression) : null;
    }

    public void pushLoop(Label breakTarget, Label continueTarget) {
        this.breakTargets.push(breakTarget);
        this.continueTargets.push(continueTarget);
    }

    public void popLoop() {
        this.breakTargets.pop();
        this.continueTargets.pop();
    }

    public Label currentBreakTarget() {
        return this.breakTargets.peek();
    }

    public Label currentContinueTarget() {
        return this.continueTargets.peek();
    }

    public int allocateSlot() {
        return this.nextLocalSlot++;
    }
}
