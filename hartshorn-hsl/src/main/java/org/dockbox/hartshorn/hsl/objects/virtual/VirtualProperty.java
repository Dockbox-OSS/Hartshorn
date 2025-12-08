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

package org.dockbox.hartshorn.hsl.objects.virtual;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * A virtual property, representing a field on a virtual class.
 *
 * @see VirtualClass
 * @see VirtualFieldMemberFunction
 * @see FieldStatement
 * @see org.dockbox.hartshorn.hsl.ast.statement.FieldGetStatement
 * @see org.dockbox.hartshorn.hsl.ast.statement.FieldSetStatement
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class VirtualProperty {

    private final FieldStatement fieldStatement;

    private VirtualFieldMemberFunction getter;
    private VirtualFieldMemberFunction setter;

    private Token readModifier;
    private Token writeModifier;

    public VirtualProperty(FieldStatement fieldStatement) {
        this.fieldStatement = fieldStatement;
        this.readModifier = fieldStatement.modifier();
        this.writeModifier = fieldStatement.modifier();
    }

    /**
     * The field statement backing this property.
     *
     * @return the field statement
     */
    public FieldStatement fieldStatement() {
        return this.fieldStatement;
    }

    /**
     * The getter function for this property. Getters are optional; if no getter is defined, the
     * property is read directly.
     *
     * @return the getter function, or null if none is defined
     */
    public VirtualFieldMemberFunction getter() {
        return this.getter;
    }

    /**
     * Sets the getter function for this property.
     *
     * @param getter the getter function
     *
     * @return this property
     */
    public VirtualProperty getter(VirtualFieldMemberFunction getter) {
        this.getter = getter;
        if (getter.modifier() != null) {
            this.readModifier = getter.modifier();
        }
        return this;
    }

    /**
     * The setter function for this property. Setters are optional; if no setter is defined, the
     * property is written directly.
     *
     * @return the setter function, or null if none is defined
     */
    public VirtualFieldMemberFunction setter() {
        return this.setter;
    }

    /**
     * Sets the setter function for this property.
     *
     * @param setter the setter function
     *
     * @return this property
     */
    public VirtualProperty setter(VirtualFieldMemberFunction setter) {
        this.setter = setter;
        if (setter.modifier() != null) {
            this.writeModifier = setter.modifier();
        }
        return this;
    }

    /**
     * The read modifier for this property. This will return {@code null} if no getter is defined.
     *
     * @return the read modifier
     */
    public Token readModifier() {
        return this.readModifier;
    }

    /**
     * The write modifier for this property. This will return {@code null} if no setter is defined.
     *
     * @return the write modifier
     */
    public Token writeModifier() {
        return this.writeModifier;
    }
}
