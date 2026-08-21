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
import org.dockbox.hartshorn.inject.QualifierKey;
import org.dockbox.hartshorn.inject.ReflectionObjectFactory;
import org.dockbox.hartshorn.launchpad.launch.StandardApplicationBuilder;
import org.dockbox.hartshorn.util.types.GenericType;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = ArchitectureRuleConstants.HARTSHORN_PACKAGE)
public class ReflectionRulesTest {

    /**
     * Reflection should only be used in the implementation of the framework. Outside of specific
     * packages and implementations, reflection should not be used, instead relying on the
     * framework's abstractions and APIs to interact with objects and types.
     */
    @ArchTest
    static final ArchRule reflectionShouldOnlyBeInImplementation = noClasses()
            .that().resideOutsideOfPackages(
                    "org.dockbox.hartshorn.util.introspect..",
                    "org.dockbox.hartshorn.proxy..",
                    "org.dockbox.hartshorn.test..",
                    "org.dockbox.hartshorn.spi..",
                    "org.dockbox.hartshorn.util.types.."
            )
            .and().doNotBelongToAnyOf(
                    ReflectionObjectFactory.class,
                    GenericType.class,
                    QualifierKey.class,
                    StandardApplicationBuilder.class
            )
            .should().dependOnClassesThat()
            .resideInAnyPackage("java.lang.reflect..", "javassist..");
}
