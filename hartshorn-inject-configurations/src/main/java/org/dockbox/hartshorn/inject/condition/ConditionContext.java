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

package org.dockbox.hartshorn.inject.condition;

import org.dockbox.hartshorn.inject.DefaultInjectionApplicationAwareContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;

/**
 * A context that is used during the evaluation of a condition. This includes the annotated element that is being
 * evaluated, and the {@link RequiresCondition} annotation that is used to evaluate the condition. Note that this
 * annotation is not necessarily present on the annotated element, but may be composed from extending annotations.
 *
 * @see RequiresCondition
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ConditionContext extends DefaultInjectionApplicationAwareContext {

    private final AnnotatedElementView annotatedElementContext;
    private final ConditionDeclaration condition;

    public ConditionContext(InjectionCapableApplication application, AnnotatedElementView annotatedElementContext, ConditionDeclaration condition) {
        super(application);
        this.annotatedElementContext = annotatedElementContext;
        this.condition = condition;
    }

    /**
     * Returns the annotated element that is being evaluated.
     *
     * @return the annotated element that is being evaluated
     */
    public AnnotatedElementView annotatedElement() {
        return this.annotatedElementContext;
    }

    /**
     * Returns the {@link ConditionDeclaration declaration} that is used to evaluate the condition.
     *
     * @return the {@link ConditionDeclaration declaration} that is used to evaluate the condition
     */
    public ConditionDeclaration condition() {
        return this.condition;
    }
}
