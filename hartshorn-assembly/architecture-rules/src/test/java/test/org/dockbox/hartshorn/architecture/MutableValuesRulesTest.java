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

package test.org.dockbox.hartshorn.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packages = ArchitectureRuleConstants.HARTSHORN_PACKAGE)
public class MutableValuesRulesTest {

    /**
     * No public methods should return or accept concrete collection types, as this can lead to
     * unexpected behavior and makes it difficult to change the implementation of the method in the
     * future.
     */
    @ArchTest
    static final ArchRule publicMethodsShouldNotExposeConcreteCollectionTypes = methods()
            .that().arePublic()
            .should(notReturnConcreteCollectionTypes())
            .andShould(notAcceptConcreteCollectionTypes());

    /**
     * No public constructors should accept concrete collection types, as this can lead to
     * unexpected behavior and makes it difficult to change the implementation of the constructor in
     * the future.
     */
    @ArchTest
    static final ArchRule publicConstructorsShouldNotExposeConcreteCollectionTypes = constructors()
            .that().arePublic()
            .should(notAcceptConcreteCollectionTypes());

    /**
     * No public fields should have concrete collection types, as this can lead to unexpected
     * behavior and makes it difficult to change the implementation of the field in the future.
     */
    @ArchTest
    static final ArchRule publicFieldsShouldNotExposeConcreteCollectionTypes = fields()
            .that().arePublic()
            .should(notHaveConcreteCollectionType());

    private static ArchCondition<JavaMethod> notReturnConcreteCollectionTypes() {
        return new ArchCondition<>("not return concrete collection types") {

            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                JavaClass returnType = method.getRawReturnType();

                if (isConcreteCollectionType(returnType)) {
                    events.add(SimpleConditionEvent.violated(
                            method,
                            "%s returns concrete collection type %s"
                                    .formatted(
                                            method.getFullName(),
                                            returnType.getName()
                                    )
                    ));
                }
            }
        };
    }

    private static ArchCondition<JavaCodeUnit> notAcceptConcreteCollectionTypes() {
        return new ArchCondition<>("not accept concrete collection types") {

            @Override
            public void check(JavaCodeUnit codeUnit, ConditionEvents events) {
                for (JavaClass parameterType : codeUnit.getRawParameterTypes()) {
                    if (isConcreteCollectionType(parameterType)) {
                        events.add(SimpleConditionEvent.violated(
                                codeUnit,
                                "%s accepts concrete collection type %s"
                                        .formatted(
                                                codeUnit.getFullName(),
                                                parameterType.getName()
                                        )
                        ));
                    }
                }
            }
        };
    }

    private static ArchCondition<JavaField> notHaveConcreteCollectionType() {
        return new ArchCondition<>("not have concrete collection types") {

            @Override
            public void check(JavaField field, ConditionEvents events) {
                JavaClass fieldType = field.getRawType();

                if (isConcreteCollectionType(fieldType)) {
                    events.add(SimpleConditionEvent.violated(
                            field,
                            "%s has concrete collection type %s"
                                    .formatted(
                                            field.getFullName(),
                                            fieldType.getName()
                                    )
                    ));
                }
            }
        };
    }

    private static boolean isConcreteCollectionType(JavaClass type) {
        return isCollectionType(type)
                && !type.isInterface()
                && !type.isEquivalentTo(Properties.class);
    }

    private static boolean isCollectionType(JavaClass type) {
        return type.isAssignableTo(Collection.class)
                || type.isAssignableTo(Map.class);
    }
}
