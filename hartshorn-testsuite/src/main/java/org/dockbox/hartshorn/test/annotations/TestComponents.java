/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.test.annotations;

import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test method or class as requiring specific components to be registered in the test context.
 * These components will be available for injection in the test method or class.
 *
 * <p>Test components are always treated as managed components, meaning they will be registered in the
 * {@link org.dockbox.hartshorn.inject.component.ComponentRegistry}, and should be annotated with
 * {@link Component} or a component stereotype annotation. For non-managed components, you can provide
 * them as bindings in a {@link Configuration} class.
 *
 * <p>Test components can be registered at both the method and class level. If a test class is annotated with
 * {@code @TestComponents}, all test methods in that class will have access to the specified components.
 *
 * <pre>{@code
 * @TestComponents(MyTestComponent.class)
 * public class MyTest {
 *     @Inject
 *     private MyTestComponent myTestComponent;
 *
 *     @Test
 *     public void testMyComponent() {
 *     // Use myTestComponent in the test
 *     }
 * }
 * }</pre>
 *
 * <pre>{@code
 * public class MyTest {
 *
 *     @Test
 *     @TestComponents(MyTestComponent.class)
 *     public void testMyComponent(@Inject MyTestComponent myTestComponent) {
 *     // Use myTestComponent in the test
 *     }
 * }
 * }</pre>
 *
 * @since 0.4.11
 *
 * @author Guus Lieben
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TestComponents {

    /**
     * Components to register for the test. These components will be registered in the test context
     * and will be available for injection in the test method or class.
     *
     * @return The components to register for the test
     */
    Class<?>[] value() default {};
}
