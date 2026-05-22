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

package test.org.dockbox.hartshorn.inject.conditions;

import org.assertj.core.api.AbstractAssert;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.condition.Condition;
import org.dockbox.hartshorn.inject.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.condition.ConditionResult;
import org.dockbox.hartshorn.inject.condition.RequiresCondition;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.classfile.AnnotationElement;
import java.lang.classfile.AnnotationValue;
import java.lang.classfile.AttributedElement;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HartshornIntegrationTest(includeBasePackages = false)
public class AbstractConditionTests {

    @Inject
    private ConditionMatcher conditionMatcher;

    protected ConditionAssert assertCondition(AnnotatedElementView element) {
        ConditionResult result = conditionMatcher.match(element);
        return new ConditionAssert(result);
    }

    protected ConditionAssert assertCondition(AttributedElement element) {
        ConditionResult result = conditionMatcher.match(element);
        return new ConditionAssert(result);
    }

    protected <A extends Annotation> ConditionAssert assertCondition(
            Class<? extends Condition> conditionType,
            A metaAnnotation
    ) {
        AnnotatedElementView elementView = createConditionalView(conditionType, metaAnnotation);
        return assertCondition(elementView);
    }

    protected ConditionAssert assertReferenceConditionMatches(
            Class<?> annotationType,
            Map<String, AnnotationValue> values
    ) {
        AnnotatedElementView elementView = createReferenceConditionalView(annotationType, values);
        return assertCondition(elementView);
    }

    private static <A extends Annotation> AnnotatedElementView createConditionalView(
            Class<? extends Condition> conditionType,
            A metaAnnotation
    ) {
        AnnotatedElementView elementView = mock(AnnotatedElementView.class);
        when(elementView.enclosingView()).thenReturn(Option.empty());
        when(elementView.classFileElement()).thenReturn(Option.empty());
        configureAnnotation(conditionType, metaAnnotation, elementView);
        return elementView;
    }

    private static AnnotatedElementView createReferenceConditionalView(
            Class<?> annotationType,
            Map<String, AnnotationValue> values
    ) {
        AnnotatedElementView elementView = mock(AnnotatedElementView.class);
        when(elementView.enclosingView()).thenReturn(Option.empty());
        ElementAnnotationsIntrospector annotationsIntrospector = mock(
                ElementAnnotationsIntrospector.class
        );
        when(annotationsIntrospector.all(RequiresCondition.class)).thenReturn(Set.of());
        when(elementView.annotations()).thenReturn(annotationsIntrospector);

        configureReferenceAnnotation(elementView, annotationType, values);
        return elementView;
    }

    private static void configureAnnotation(
            Class<? extends Condition> conditionType,
            Annotation metaAnnotation,
            AnnotatedElementView elementView
    ) {
        ElementAnnotationsIntrospector annotationsIntrospector = mock(
                ElementAnnotationsIntrospector.class
        );
        RequiresCondition requiresCondition = TypeUtils.annotation(RequiresCondition.class, Map.of(
                "condition", conditionType
        ));
        when(annotationsIntrospector.all(RequiresCondition.class))
                .thenReturn(Set.of(requiresCondition));

        Class<? extends Annotation> annotationType = metaAnnotation.annotationType();
        when(annotationsIntrospector.all(annotationType))
                .thenReturn(TypeUtils.unchecked(Set.of(metaAnnotation), Set.class));
        when(annotationsIntrospector.get(annotationType))
                .thenReturn(TypeUtils.unchecked(Option.of(metaAnnotation), Option.class));
        when(elementView.annotations()).thenReturn(annotationsIntrospector);
    }

    private static void configureReferenceAnnotation(
            AnnotatedElementView elementView,
            Class<?> annotationType,
            Map<String, AnnotationValue> values
    ) {
        java.lang.classfile.Annotation annotation = java.lang.classfile.Annotation.of(
                annotationType.describeConstable().orElseThrow(),
                values.entrySet().stream()
                        .map(e -> AnnotationElement.of(e.getKey(), e.getValue()))
                        .toList()
        );

        byte[] bytes = ClassFile.of().build(
                ClassDesc.of("ReferenceConditionalClass"),
                cb -> cb.with(RuntimeVisibleAnnotationsAttribute.of(List.of(annotation)))
        );
        ClassModel model = ClassFile.of().parse(bytes);
        when(elementView.classFileElement()).thenReturn(TypeUtils.unchecked(
                Option.of(model), Option.class
        ));
    }

    public static class ConditionAssert extends AbstractAssert<ConditionAssert, ConditionResult> {
        public ConditionAssert(ConditionResult actual) {
            super(actual, ConditionAssert.class);
        }

        public ConditionAssert matches() {
            return this.matches(ConditionResult::matches, "matches");
        }

        public ConditionAssert doesNotMatch() {
            return this.doesNotMatch(ConditionResult::matches, "does not match");
        }

        public ConditionAssert doesNotMatchWithMessage(String message) {
            return this.matches(result -> {
                return !result.matches() && result.message().equals(message);
            }, "does not match with message: " + message);
        }
    }
}
