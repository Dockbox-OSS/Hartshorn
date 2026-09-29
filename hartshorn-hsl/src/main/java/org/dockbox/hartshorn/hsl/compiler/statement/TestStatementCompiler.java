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

package org.dockbox.hartshorn.hsl.compiler.statement;

import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.TypeKind;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

/**
 * Compiler for {@link TestStatement} nodes.
 *
 * @author Guus Lieben
 *
 * @since 0.7.0
 */
public class TestStatementCompiler implements StatementCompiler<TestStatement> {

    @Override
    public void compile(TestStatement node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        code.trying(
                tryBlock -> {
                    visitor.fork(tryBlock, () -> visitor.compile(node.body()));
                },
                catches -> {
                    catches.catching(CD_Yield, catchBlock -> {
                        int yieldSlot = catchBlock.allocateLocal(TypeKind.REFERENCE);

                        // addResult(ScriptContext, name, Boolean.valueOf(isTruthy(yield.value())).
                        catchBlock.astore(yieldSlot)
                                .aload(CompilationContext.CONTEXT_SLOT)
                                .ldc(node.name().lexeme())
                                .aload(yieldSlot)
                                .invokevirtual(CD_Yield, "value", MethodTypeDesc.of(ConstantDescs.CD_Object))
                                .invokestatic(CD_HslRuntimeSupport, "isTruthy", HslRuntimeSupport.MTD_IsTruthy)
                                // Wrap in Boolean.valueOf to explicitly convert primitive 0/1 to Boolean, otherwise it
                                // will be treated as an int.
                                .invokestatic(ConstantDescs.CD_Boolean, "valueOf", MethodTypeDesc.of(ConstantDescs.CD_Boolean, ConstantDescs.CD_boolean))
                                .invokestatic(CD_HslRuntimeSupport, "addResult", HslRuntimeSupport.MTD_AddResult);
                    });
                }
        );
    }
}
