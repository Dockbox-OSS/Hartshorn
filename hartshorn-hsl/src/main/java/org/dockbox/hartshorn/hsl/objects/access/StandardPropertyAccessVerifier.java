/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualInstance;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualMemberFunction;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualProperty;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

public class StandardPropertyAccessVerifier implements PropertyAccessVerifier {

    @Override
    public String read(final Token at, final VirtualProperty property, final InstanceReference instance, final VariableScope fromScope) {
        if (property.readModifier() == null || property.readModifier().type() == TokenType.PUBLIC) return null;
        return this.permittedScope("read", property, instance, fromScope);
    }

    @Override
    public String write(final Token at, final VirtualProperty property, final InstanceReference instance, final VariableScope fromScope) {
        if (property.writeModifier() == null || property.writeModifier().type() == TokenType.PUBLIC) return null;
        return this.permittedScope("assign to", property, instance, fromScope);
    }

    private String permittedScope(final String action, final VirtualProperty property, final InstanceReference instance, final VariableScope fromScope) {
        if (instance instanceof VirtualInstance virtualInstance) {
            final VariableScope classScope = virtualInstance.type().variableScope();
            VariableScope currentScope = fromScope;
            while (currentScope != null) {
                if (currentScope == classScope) {
                    return null;
                }
                currentScope = currentScope.enclosing();
            }
        }

        final StringBuffer message = new StringBuffer();
        message.append("Cannot ");
        message.append(action);
        message.append(" property ");
        message.append(property.fieldStatement().name().lexeme());
        message.append(" of ");
        message.append(instance.type().name());
        message.append(" because it is not accessible from the current scope. ");
        message.append("The property is declared ");
        final Token modifier = property.fieldStatement().modifier();
        message.append(modifier == null ? "public" : modifier.lexeme());
        message.append(" and has ");
        final VirtualMemberFunction setter = property.setter();
        final VirtualMemberFunction getter = property.getter();
        if (setter == null && getter == null) {
            message.append("no members.");
        } else {
            if (setter != null) {
                message.append("a ");
                message.append(setter.modifier() == null ? "public" : setter.modifier().lexeme());
                message.append(" setter");
            }
            if (getter != null) {
                if (setter != null) {
                    message.append(" and a ");
                } else {
                    message.append("a ");
                }
                message.append(getter.modifier() == null ? "public" : getter.modifier().lexeme());
                message.append(" getter");
            }
            message.append(".");
        }
        return message.toString();
    }
}
