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

package org.dockbox.hartshorn.launchpad.test;

import org.dockbox.hartshorn.inject.condition.RequiresCondition;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A condition that requires the application to be a test application. This condition is met if the
 * application context contains a binding for {@link ApplicationTestManager}, which is always
 * provided by the Hartshorn Test Suite.
 *
 * <p>This condition is the preferred way to verify whether the application is being executed in a
 * test environment. Usage of environment variables or system properties is discouraged, as these
 * can be easily manipulated and may not be set in all environments.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Extends(RequiresCondition.class)
@RequiresCondition(condition = TestApplicationCondition.class)
public @interface RequiresTestApplication {
}
