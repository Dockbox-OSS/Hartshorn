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
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.conditions.ArchConditions;
import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.InfrastructurePriority;
import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.inject.annotations.SupportPriority;
import org.dockbox.hartshorn.inject.annotations.configuration.Binds;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Prototype;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.launch.ResourceConfigurationTypeReferenceCollector;
import org.dockbox.hartshorn.util.introspect.scan.TypeCollectionException;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;

import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.conditions.ArchConditions.beAnnotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static test.org.dockbox.hartshorn.architecture.ArchitectureUtilities.complyWith;

@AnalyzeClasses(packages = {
        ArchitectureRuleConstants.HARTSHORN_PACKAGE,
        ArchitectureRuleConstants.TEST_PACKAGE
})
public class ConfigurationRulesTest {

    /**
     * All binding methods should have a priority annotation, to ensure that default bindings
     * provided by Hartshorn are easily overridden by user-defined bindings.
     */
    @ArchTest
    static final ArchRule bindingsShouldHavePriority = methods()
            .that().areAnnotatedWith(Singleton.class)
            .or().areAnnotatedWith(Prototype.class)
            .or().areAnnotatedWith(Binds.class)
            .should(
                    beAnnotatedWith(InfrastructurePriority.class)
                            .or(beAnnotatedWith(SupportPriority.class))
                            .or(beAnnotatedWith(Priority.class))
                            // Composites are default priority to allow for easier composition of
                            // components, as such these are not configured with lower priorities
                            // even in cases where they are used as infrastructure or support
                            // components.
                            .or(beAnnotatedWith(CompositeMember.class))
            );

    /**
     * All binding methods should be declared in configuration classes, as they are otherwise not
     * discoverable by the framework.
     */
    @ArchTest
    static final ArchRule bindingsShouldOnlyBeInConfigurationClasses = methods()
            .that().areAnnotatedWith(Singleton.class)
            .or().areAnnotatedWith(Prototype.class)
            .or().areAnnotatedWith(Binds.class)
            .should().beDeclaredInClassesThat().areAnnotatedWith(Configuration.class);

    /**
     * All non-binding methods in configuration classes should be private, to prevent accidental
     * usage of these methods outside of the configuration class. This ensures that the
     * configuration classes are used as intended, and that the configuration classes are not
     * modified in unexpected ways.
     */
    @ArchTest
    static final ArchRule nonBindingMethodsInConfigurationClassesShouldBePrivate = methods()
            .that().areDeclaredInClassesThat().areAnnotatedWith(Configuration.class)
            .and().areNotAnnotatedWith(Singleton.class)
            .and().areNotAnnotatedWith(Prototype.class)
            .and().areNotAnnotatedWith(Binds.class)
            .should().bePrivate();

    /**
     * All binding methods should be public, to prevent the need for accessibility bypasses when
     * using the configuration classes. This has the additional benefit of making the configuration
     * classes easier to use, as the methods are more discoverable and can be used without needing
     * to use reflection or other means to access them.
     */
    @ArchTest
    static final ArchRule bindingMethodsShouldBePublic = methods()
            .that().areAnnotatedWith(Singleton.class)
            .or().areAnnotatedWith(Prototype.class)
            .or().areAnnotatedWith(Binds.class)
            .should().bePublic();

    /**
     * Composite members should not have a priority annotation, to allow for easier composition of
     * third-party components. This ensures that composite members are always treated as default
     * priority.
     */
    @ArchTest
    static final ArchRule compositeMembersShouldNotHavePriority = methods()
            .that().areAnnotatedWith(CompositeMember.class)
            .should().notBeAnnotatedWith(InfrastructurePriority.class)
            .andShould().notBeAnnotatedWith(SupportPriority.class)
            .andShould().notBeAnnotatedWith(Priority.class);

    /**
     * All configuration classes should not be extended. This ensures that the configuration classes
     * are used as intended, and that the configuration classes are not modified in unexpected ways.
     *
     * <p>Note that this rule does not prevent the use of composition, as configuration classes may
     * still be used as components in other configuration classes. However, it does prevent the use
     * of inheritance (outside of proxying) to modify the behavior of configuration classes, which
     * is not recommended.
     */
    @ArchTest
    static final ArchRule configurationsShouldNotBeExtended = classes()
            .that().areAnnotatedWith(Configuration.class)
            .should(complyWith("not be extended", (clazz, events) -> {
                clazz.getSubclasses().forEach(subclass ->
                        events.add(SimpleConditionEvent.violated(
                                clazz,
                                "%s is extended by %s".formatted(
                                        clazz.getName(),
                                        subclass.getName()
                                )
                        ))
                );
            }));

    /**
     * All configuration classes should not be final. This allows for easier testing and mocking of
     * configuration classes, as well as allowing for more flexible composition of configuration
     * classes.
     */
    @ArchTest
    static final ArchRule configurationClassesShouldNotBeFinal = noClasses()
            .that().areAnnotatedWith(Configuration.class)
            .should(ArchConditions.haveModifier(JavaModifier.FINAL));

    /**
     * All configuration classes should be registered in the hartshorn.environment.types resource of
     * their respective module. This ensures that the configuration classes are discoverable and can
     * be used without classpath scanning.
     */
    @ArchTest
    static final ArchRule configurationClassesShouldBeRegisteredInEnvironmentTypes = classes()
            .that().areAnnotatedWith(Configuration.class)
            .should(beRegisteredInEnvironmentTypeResource());

    private static ArchCondition<JavaClass> beRegisteredInEnvironmentTypeResource() {
        Set<String> registeredClasses = getKnownEnvironmentTypes();

        return complyWith("be registered in hartshorn.environment.types", (javaClass, events) -> {
            if (!registeredClasses.contains(javaClass.getName())) {
                events.add(SimpleConditionEvent.violated(
                        javaClass,
                        "%s is not registered in hartshorn.environment.types"
                                .formatted(javaClass.getName())
                ));
            }
        });
    }

    private static Set<String> getKnownEnvironmentTypes() {
        try {
            return new ResourceConfigurationTypeReferenceCollector().collect().stream()
                    .map(TypeReference::qualifiedName)
                    .collect(Collectors.toSet());
        }
        catch (TypeCollectionException e) {
            throw new RuntimeException("Failed to collect known environment types", e);
        }
    }
}
