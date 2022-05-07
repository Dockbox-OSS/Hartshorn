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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.util.reflect.Extends;
import org.dockbox.hartshorn.inject.Populate;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Inject;

/**
 * Annotation for test classes that should be run with the Hartshorn test suite. This will automatically
 * provide the test class with a {@link HartshornExtension} that will provide an active
 * {@link ApplicationContext} for the test class. The provided {@link ApplicationContext} is refreshed
 * according to the lifecycle of the test class.
 *
 * <p>Additionally, the {@link HartshornExtension} will inject fields annotated with {@link Inject}
 * within the test class. Like the active {@link ApplicationContext}, this will be refreshed according to
 * the lifecycle of the test class.
 *
 * @see HartshornExtension
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(HartshornExtension.class)
@Extends(Populate.class)
@Populate(executables = false)
public @interface HartshornTest {
    Class<? extends Annotation>[] activators() default {};
}
