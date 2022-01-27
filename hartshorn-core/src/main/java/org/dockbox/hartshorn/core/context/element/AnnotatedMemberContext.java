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

package org.dockbox.hartshorn.core.context.element;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.List;

/**
 * AnnotatedMemberContext is a context for annotated members. It is used as a base class for all contexts that are both {@link ModifierCarrier}s
 * and {@link AnnotatedElementContext}s.
 *
 * @param <A> The type of the annotated element.
 * @see AnnotatedElement
 * @see Member
 * @author Guus Lieben
 * @since 21.5
 */
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
