/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.kotlin

import jakarta.inject.Inject
import org.dockbox.hartshorn.application.context.ApplicationContext
import org.dockbox.hartshorn.application.environment.ApplicationManager
import org.dockbox.hartshorn.component.ComponentLocator
import org.dockbox.hartshorn.testsuite.HartshornTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * Tests compatibility with Kotlin classes, interfaces, objects and sealed interfaces.
 * This is similar to the tests seen in {@code GroovyComponentTests} and {@code ScalaComponentsTests}.
 * Instead of using a common test class for all three languages, each language has its own test class.
 * This is done to provide a simple example of how to use Hartshorn's Test Suite with each language.
 *
 * @author Guus Lieben
 * @since 22.5
 */
@HartshornTest
class KotlinComponentTests {

    @Inject
    private lateinit var applicationContext: ApplicationContext

    @Inject
    private lateinit var componentLocator: ComponentLocator

    @ParameterizedTest
    @MethodSource("components")
    fun <T> testComponent(componentType: Class<T>, applicationContextFunction: ((T) -> ApplicationContext)?, applicationManagerFunction: ((T) -> ApplicationManager)?) {
        val component: T = this.applicationContext.get(componentType)
        Assertions.assertNotNull(component)

        val container = this.componentLocator.container(componentType)
        Assertions.assertNotNull(container)
        Assertions.assertTrue(container.present())

        if (applicationContextFunction != null) {
            Assertions.assertEquals(this.applicationContext, applicationContextFunction(component))
        }

        if (applicationManagerFunction != null) {
            Assertions.assertEquals(this.applicationContext.environment().manager(), applicationManagerFunction(component))
        }

    }

    companion object {
        @Suppress("RedundantLambdaArrow") // 'it' in KotlinObjectComponent, required as type is not inferred.
        @JvmStatic
        fun components(): Stream<Arguments> = Stream.of(
                Arguments.of(KotlinClassComponent::class.java, KotlinClassComponent::applicationContext, KotlinClassComponent::applicationManager),
                Arguments.of(KotlinInterfaceComponent::class.java, null, null),
                Arguments.of(KotlinObjectComponent::class.java, { it: KotlinObjectComponent -> it.applicationContext() }, null),
                Arguments.of(KotlinSealedInterfaceComponent::class.java, null, null),
        )
    }
}