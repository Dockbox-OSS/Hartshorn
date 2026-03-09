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

import org.dockbox.hartshorn.inject.InjectionApplicationAwareContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;

import java.util.Objects;

/**
 * A context that is used during the evaluation of a condition. This includes the annotated element
 * that is being evaluated, and the {@link RequiresCondition} annotation that is used to evaluate
 * the condition. Note that this annotation is not necessarily present on the annotated element, but
 * may be composed from extending annotations.
 *
 * @see RequiresCondition
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public non-sealed class IntrospectedConditionContext extends ConditionContext
    implements InjectionApplicationAwareContext {

    private final InjectionCapableApplication application;
    private final AnnotatedElementView annotatedElement;

    public IntrospectedConditionContext(
        InjectionCapableApplication application,
        AnnotatedElementView annotatedElement,
        ConditionDeclaration condition
    ) {
        super(condition);
        this.application = application;
        this.annotatedElement = annotatedElement;
    }

    /**
     * Returns the annotated element that is being evaluated.
     *
     * @return the annotated element that is being evaluated
     */
    public AnnotatedElementView annotatedElement() {
        return this.annotatedElement;
    }

    @Override
    public InjectionCapableApplication application() {
        return this.application;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IntrospectedConditionContext that = (IntrospectedConditionContext) o;
        return Objects.equals(annotatedElement, that.annotatedElement);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(annotatedElement);
    }
}
