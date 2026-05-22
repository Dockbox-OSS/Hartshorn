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

package test.org.dockbox.hartshorn.inject.condition;

import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.inject.condition.AnnotationConditionDeclaration;
import org.dockbox.hartshorn.inject.condition.CacheableCondition;
import org.dockbox.hartshorn.inject.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.condition.ConditionResult;
import org.dockbox.hartshorn.inject.condition.ReferenceCondition;
import org.dockbox.hartshorn.inject.condition.ReferenceConditionContext;
import org.dockbox.hartshorn.inject.condition.ReferenceConditionDeclaration;
import org.dockbox.hartshorn.inject.condition.RequiresCondition;
import org.dockbox.hartshorn.inject.condition.RequiresReferenceCondition;
import org.dockbox.hartshorn.util.types.ClassFileUtilities;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.junit.jupiter.api.Test;

import java.lang.classfile.Annotation;
import java.lang.classfile.ClassModel;
import java.lang.classfile.MethodModel;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConditionCacheTests {

    @Test
    void cachedConditionMarkerIsRespectedByReferenceDeclarations() {
        RequiresReferenceCondition annotation = TypeUtils.annotation(
                RequiresReferenceCondition.class,
                Map.of(
                        "condition", TestCondition.class
                ));
        Annotation classFileAnnotation = ClassFileUtilities.toClassfileAnnotation(annotation);
        ReferenceConditionDeclaration declaration = new ReferenceConditionDeclaration(
                classFileAnnotation
        );
        assertThat(declaration.cacheable()).isTrue();
    }

    @Test
    void cachedConditionMarkerIsRespectedByIntrospectionDeclarations() {
        RequiresCondition annotation = TypeUtils.annotation(
                RequiresCondition.class,
                Map.of(
                        "condition", TestCondition.class
                ));
        AnnotationConditionDeclaration declaration = new AnnotationConditionDeclaration(annotation);
        assertThat(declaration.cacheable()).isTrue();
    }

    @Test
    void cacheableConditionResultsAreCachedByMatcher() {
        ConditionMatcher matcher = new ConditionMatcher(() -> null);
        // Have to use reference matching, as application is unavailable for introspection.
        ClassModel classModel = ClassFileUtilities.getClassModel(ConditionCacheTests.class).get();
        MethodModel methodModel = ClassFileUtilities.getMethod(
                classModel,
                "conditionalMethod"
        ).get();

        boolean matchedOnce = matcher.match(methodModel).matches();
        assertThat(matchedOnce).isTrue();

        Assertions.assertThatCode(() -> {
            boolean matchedTwice = matcher.match(methodModel).matches();
            assertThat(matchedTwice).isTrue();
        }).doesNotThrowAnyException();
    }

    @RequiresReferenceCondition(condition = TestCondition.class)
    public void conditionalMethod() {}

    public static class TestCondition implements CacheableCondition, ReferenceCondition {

        private static volatile boolean evaluated = false;

        @Override
        public ConditionResult matches(ReferenceConditionContext context) {
            if (evaluated) {
                throw new IllegalStateException("Condition should not be evaluated more than once");
            }
            evaluated = true;
            return ConditionResult.matched();
        }
    }
}
