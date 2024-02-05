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

package org.dockbox.hartshorn.component.condition;

import org.dockbox.hartshorn.application.context.ApplicationContext;

/**
 * Represents an annotation-based condition declaration. This may represent
 * a direct use of the {@link RequiresCondition} annotation, or a meta-annotation
 * that contains the {@link RequiresCondition} annotation.
 *
 * @see RequiresCondition
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class AnnotationConditionDeclaration implements ConditionDeclaration {

    private final RequiresCondition annotation;

    public AnnotationConditionDeclaration(RequiresCondition annotation) {
        this.annotation = annotation;
    }

    @Override
    public Condition condition(ApplicationContext applicationContext) {
        return applicationContext.get(this.annotation.condition());
    }

    @Override
    public boolean failOnNoMatch() {
        return this.annotation.failOnNoMatch();
    }
}
