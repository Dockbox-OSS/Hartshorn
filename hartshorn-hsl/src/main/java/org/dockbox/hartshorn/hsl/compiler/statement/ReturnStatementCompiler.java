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

import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.compiler.ASTNodeCompiler;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

/**
 * Compiler for {@link ReturnStatement} nodes.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public class ReturnStatementCompiler implements StatementCompiler<ReturnStatement> {

    @Override
    public void compile(ReturnStatement node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        if (node.returnType() == ReturnStatement.ReturnType.YIELD) {
            code.new_(ASTNodeCompiler.CD_Yield).dup();
        }
        if (node.expression() != null) {
            visitor.compile(node.expression());
        } else {
            code.aconst_null();
        }
        switch (node.returnType()) {
            case RETURN -> code.areturn();
            case YIELD -> code.invokespecial(
                            CD_Yield,
                            ConstantDescs.INIT_NAME,
                            MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_Object)
                    )
                    .athrow();
        }
    }
}
