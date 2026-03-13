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
import java.util.Objects;

/**
 * A context that is used during the evaluation of a condition. This context provides access to the
 * {@link AttributedElement class file element} being evaluated. Unlike the
 * {@link IntrospectedConditionContext}, this context does not ensure that the element is fully
 * loaded by the JVM, and may therefore be used in earlier stages of the injection process.
 *
 * @see RequiresReferenceCondition
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public non-sealed class ReferenceConditionContext extends ConditionContext {

    private final AttributedElement element;

    public ReferenceConditionContext(
            ConditionDeclaration condition,
            AttributedElement element
    ) {
        super(condition);
        this.element = element;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReferenceConditionContext that = (ReferenceConditionContext) o;
        return Objects.equals(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(element);
    }
}
