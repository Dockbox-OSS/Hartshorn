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

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static test.org.dockbox.hartshorn.architecture.ArchitectureUtilities.complyWith;

@AnalyzeClasses(packages = ArchitectureRuleConstants.TEST_PACKAGE)
public class IntegrationTestRulesTest {

    @ArchTest
    static final ArchRule integrationTestsShouldNotIncludeBasePackages = classes()
            .that().areAnnotatedWith(HartshornIntegrationTest.class)
            .should(complyWith("have includeBasePackages set to false", (javaClass, events) -> {
                HartshornIntegrationTest annotation =
                        javaClass.getAnnotationOfType(HartshornIntegrationTest.class);

                if (annotation.includeBasePackages()) {
                    events.add(SimpleConditionEvent.violated(
                            javaClass,
                            "%s sets includeBasePackages to true".formatted(javaClass.getName())
                    ));
                }
            }));
}
