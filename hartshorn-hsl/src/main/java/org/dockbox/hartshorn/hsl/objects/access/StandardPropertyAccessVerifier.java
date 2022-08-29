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
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

public class StandardPropertyAccessVerifier implements PropertyAccessVerifier {

    @Override
    public FormattedDiagnostic read(final Token at, final VirtualProperty property, final InstanceReference instance, final VariableScope fromScope) {
        if (property.readModifier() == null || property.readModifier().type() == TokenType.PUBLIC) return null;
        return this.permittedScope("read", property, instance, fromScope);
    }

    @Override
    public FormattedDiagnostic write(final Token at, final VirtualProperty property, final InstanceReference instance, final VariableScope fromScope) {
        if (property.writeModifier() == null || property.writeModifier().type() == TokenType.PUBLIC) return null;
        return this.permittedScope("assign to", property, instance, fromScope);
    }

    private FormattedDiagnostic permittedScope(final String action, final VirtualProperty property, final InstanceReference instance, final VariableScope fromScope) {
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

        final String name = property.fieldStatement().name().lexeme();
        final String parent = instance.type().name();

        final Token modifierToken = property.fieldStatement().modifier();
        final String modifier = modifierToken == null ? TokenType.PUBLIC.representation() : modifierToken.lexeme();

        final String members;
        final VirtualMemberFunction setter = property.setter();
        final VirtualMemberFunction getter = property.getter();

        if (setter == null && getter == null) {
            members = "no members.";
        } else {
            final StringBuilder memberBuilder = new StringBuilder();
            if (setter != null) {
                memberBuilder.append("a ");
                memberBuilder.append(setter.modifier() == null ? "public" : setter.modifier().lexeme());
                memberBuilder.append(" setter");
            }
            if (getter != null) {
                if (setter != null) {
                    memberBuilder.append(" and a ");
                } else {
                    memberBuilder.append("a ");
                }
                memberBuilder.append(getter.modifier() == null ? "public" : getter.modifier().lexeme());
                memberBuilder.append(" getter");
            }
            memberBuilder.append(".");
            members = memberBuilder.toString();
        }

        return new FormattedDiagnostic(DiagnosticMessage.INVALID_PROPERTY_ACCESS, action, name, parent, modifier, members);
    }
}
