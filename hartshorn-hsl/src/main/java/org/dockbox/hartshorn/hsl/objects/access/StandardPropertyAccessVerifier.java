/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.hsl.objects.access;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualInstance;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class StandardPropertyAccessVerifier implements PropertyAccessVerifier {

    @Override
    public boolean verify(Token at, FieldStatement field, InstanceReference instance, VariableScope fromScope) {
        if (field.isPublic()) {
            return true;
        }

        if (instance instanceof VirtualInstance virtualInstance) {
            VariableScope classScope = virtualInstance.type().variableScope();
            VariableScope currentScope = fromScope;
            while (currentScope != null) {
                if (currentScope == classScope) {
                    return true;
                }
                currentScope = currentScope.enclosing();
            }
        }
        return false;
    }
}
