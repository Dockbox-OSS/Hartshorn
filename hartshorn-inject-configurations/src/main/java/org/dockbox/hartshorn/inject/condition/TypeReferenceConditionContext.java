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

import org.dockbox.hartshorn.util.introspect.scan.TypeReference;

import java.lang.classfile.ClassModel;

/**
 * A context that is used during the evaluation of a condition. This includes the type reference
 * that is being evaluated, and the {@link RequiresReferenceCondition} annotation that is used to
 * evaluate the condition. Note that this annotation is not necessarily present on the annotated
 * element, but may be composed from extending annotations.
 *
 * @see RequiresReferenceCondition
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public non-sealed class TypeReferenceConditionContext extends ConditionContext {

    private final TypeReference typeReference;
    private final ClassModel classModel;

    public TypeReferenceConditionContext(
            ConditionDeclaration condition,
            TypeReference typeReference,
            ClassModel classModel
    ) {
        super(condition);
        this.typeReference = typeReference;
        this.classModel = classModel;
    }

    /**
     * Returns the annotated element that is being evaluated.
     *
     * @return the annotated element that is being evaluated
     */
    public TypeReference typeReference() {
        return this.typeReference;
    }

    /**
     * Returns the class model of the type reference being evaluated.
     *
     * @return the class model of the type reference being evaluated
     */
    public ClassModel classModel() {
        return this.classModel;
    }
}
