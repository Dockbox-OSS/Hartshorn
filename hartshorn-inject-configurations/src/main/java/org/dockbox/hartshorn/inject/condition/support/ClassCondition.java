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

package org.dockbox.hartshorn.inject.condition.support;

import org.dockbox.hartshorn.inject.condition.ReferenceConditionContext;
import org.dockbox.hartshorn.inject.condition.ConditionResult;
import org.dockbox.hartshorn.inject.condition.ReferenceCondition;
import org.dockbox.hartshorn.util.types.ClassFileUtilities;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.lang.classfile.Annotation;
import java.lang.classfile.AnnotationValue;
import java.lang.classfile.AttributedElement;
import java.util.List;

/**
 * A condition that matches when a class is present on the classpath.
 *
 * @see RequiresClass
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ClassCondition implements ReferenceCondition {

    @Override
    public ConditionResult matches(ReferenceConditionContext context) {
        AttributedElement element = context.element();
        Annotation annotation = ClassFileUtilities.getAnnotation(
                element,
                RequiresClass.class
        ).orElseThrow(() -> new IllegalStateException(
                "ClassModel does not represent a type annotated with RequiresClass"
        ));

        List<String> classes = ClassFileUtilities.getAnnotationValues(
                        annotation,
                        "classes",
                        AnnotationValue.OfClass.class
                ).stream()
                .map(AnnotationValue.OfClass::className)
                .map(ClassFileUtilities::constantPoolNameToQualifiedName)
                .toList();
        // Potentially return early if a class is not found
        ConditionResult result = matchRequiredClasses(classes);
        if (result != null) return result;

        List<String> classNames = ClassFileUtilities.getAnnotationValues(
                        annotation,
                        "classNames",
                        AnnotationValue.OfString.class
                ).stream()
                .map(AnnotationValue.OfString::stringValue)
                .toList();
        result = matchRequiredClasses(classNames);
        if (result != null) return result;

        return ConditionResult.matched();
    }

    private static ConditionResult matchRequiredClasses(List<String> classNames) {
        for (String requiredClassName : classNames) {
            if (TypeUtils.exists(requiredClassName)) {
                continue;
            }
            return ConditionResult.notFound("class", requiredClassName);
        }
        return null;
    }
}
