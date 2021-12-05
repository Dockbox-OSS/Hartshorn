/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.HartshornUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.List;

// skipcq: JAVA-W0100
public abstract class AnnotatedMemberContext <A extends AnnotatedElement & Member> extends AnnotatedElementContext<A> implements ModifierCarrier {

    private List<AccessModifier> modifiers;

    @Override
    public List<AccessModifier> modifiers() {
        if (this.modifiers == null) {
            this.modifiers = AccessModifier.from(this.element().getModifiers());
        }
        return this.modifiers;
    }
}
