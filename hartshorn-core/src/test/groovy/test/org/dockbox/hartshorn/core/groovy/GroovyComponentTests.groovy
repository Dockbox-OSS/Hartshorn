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
package test.org.dockbox.hartshorn.core.groovy

import jakarta.inject.Inject
import org.dockbox.hartshorn.application.context.ApplicationContext
import org.dockbox.hartshorn.component.ComponentRegistry
import org.dockbox.hartshorn.testsuite.HartshornTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.util.stream.Stream
/**
 * Tests compatibility with Groovy classes, traits and interfaces.
 * This is similar to the tests seen in {@code KotlinComponentTests} and {@code ScalaComponentsTests}.
 * Instead of using a common test class for all three languages, each language has its own test class.
 * This is done to provide a simple example of how to use Hartshorn's Test Suite with each language.
 *
 * @author Guus Lieben
 * @since 0.4.13
 */
@HartshornTest(includeBasePackages = false, scanPackages = ["test.org.dockbox.hartshorn.core.groovy"])
class GroovyComponentTests {

    @Inject
    private ApplicationContext applicationContext

    @Inject
    private ComponentRegistry componentRegistry

    @ParameterizedTest
    @MethodSource("components")
    <T> void testComponent(Class<T> componentType, applicationContextFunction, applicationManagerFunction) {
        def component = this.applicationContext.get(componentType)
        Assertions.assertNotNull(component)

        def container = this.componentRegistry.container(componentType)
        Assertions.assertNotNull(container)
        Assertions.assertTrue(container.present())

        if (applicationContextFunction != null) {
            Assertions.assertSame(this.applicationContext, applicationContextFunction(component))
        }

        if (applicationManagerFunction != null) {
            Assertions.assertSame(this.applicationContext.environment(), applicationManagerFunction(component))
        }
    }

    static Stream<Arguments> components() {
        return Stream.of(
                Arguments.of(GroovyInterfaceComponent.class, null, null),
                Arguments.of(GroovyClassComponent.class,
                        { final GroovyClassComponent component -> component.applicationContext() },
                        { final GroovyClassComponent component -> component.applicationManager() },
                ),
        )
    }
}
