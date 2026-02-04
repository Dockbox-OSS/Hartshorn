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

package org.dockbox.hartshorn.inject.condition;

import java.lang.classfile.AttributedElement;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;

/**
 * A context that is used during the evaluation of a condition. This includes the type reference
 * that is being evaluated, and the {@link RequiresReferenceCondition} annotation that is used to
 * evaluate the condition.
 *
 * @see RequiresReferenceCondition
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public non-sealed class TypeReferenceConditionContext extends ConditionContext {

    private final TypeReference typeReference;
    private final AttributedElement element;

    public TypeReferenceConditionContext(
            ConditionDeclaration condition,
            TypeReference typeReference,
            AttributedElement element
    ) {
        super(condition);
        this.typeReference = typeReference;
        this.element = element;
    }

    /**
     * Returns the type reference in which the condition is being evaluated. This may match the
     * {@link #element()} if the element is a type, but may also represent a member or other
     * annotated element within the type.
     *
     * @return the type reference being evaluated
     */
    public TypeReference typeReference() {
        return this.typeReference;
    }

    /**
     * Returns the element being evaluated. This may be the type itself, but may also be a member or
     * other annotated element within the type.
     *
     * @return the element being evaluated
     */
    public AttributedElement element() {
        return this.element;
    }
}
