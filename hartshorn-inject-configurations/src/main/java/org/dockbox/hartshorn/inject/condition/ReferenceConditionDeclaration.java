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

import java.lang.classfile.Annotation;
import java.lang.classfile.AnnotationValue;
import java.lang.classfile.ClassModel;
import org.dockbox.hartshorn.inject.ObjectFactory;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.ClassFileUtilities;
import org.dockbox.hartshorn.util.types.TypeUtils;

/**
 * Represents a condition declaration that is based on an annotation reference. The type on which
 * the annotation is declared is not ensured to be loaded, and should not be loaded during the
 * evaluation of the condition.
 *
 * @see RequiresReferenceCondition
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ReferenceConditionDeclaration implements ConditionDeclaration {

    private final Annotation annotation;

    public ReferenceConditionDeclaration(Annotation annotation) {
        this.annotation = annotation;
    }

    /**
     * Creates a new {@link ReferenceConditionDeclaration} from a meta-annotation. Only the
     * {@link RequiresReferenceCondition} annotation is retained, while the original meta-annotation
     * is discarded.
     *
     * @param metaAnnotation the meta-annotation to create the declaration from
     * @return the created reference condition declaration
     */
    public static ReferenceConditionDeclaration createFromMetaAnnotation(
        Annotation metaAnnotation
    ) {
        ClassModel model = ClassFileUtilities.getClassModel(metaAnnotation.classSymbol());
        Option<Annotation> declaration = ClassFileUtilities.getAnnotation(
            model,
            RequiresReferenceCondition.class
        );
        return new ReferenceConditionDeclaration(declaration.orElseThrow(() -> {
            return new IllegalStateException(
                "Could not create ReferenceConditionDeclaration from "
                    + "non-ReferenceCondition annotation"
            );
        }));
    }

    @Override
    public Condition condition(ObjectFactory objectFactory) {
        return ClassFileUtilities.getAnnotationValue(
                this.annotation,
                "condition",
                AnnotationValue.OfClass.class
            )
            .map(AnnotationValue.OfClass::className)
            .map(ClassFileUtilities::constantPoolNameToQualifiedName)
            .flatMap(TypeUtils::forName)
            .map(objectFactory::create)
            .ofType(Condition.class)
            .orElseThrow(() -> new IllegalStateException(
                "Could not instantiate condition for reference condition declaration"
            ));
    }

    @Override
    public boolean failOnNoMatch() {
        // Default to false if the value is not explicitly set (matching default
        // value of annotation).
        return ClassFileUtilities.getAnnotationValue(
            this.annotation,
            "failOnNoMatch",
            AnnotationValue.OfBoolean.class
        ).test(AnnotationValue.OfBoolean::booleanValue, false);
    }
}
