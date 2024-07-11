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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.launchpad.launch.ApplicationBuilder;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;

public class InvalidFactoryModifierException extends ApplicationRuntimeException {
    public InvalidFactoryModifierException(String what, Class<?> actual) {
        super("Invalid " + what + " for @HartshornFactory modifier, expected " + ApplicationBuilder.class.getSimpleName() + " but got " + actual.getSimpleName());
    }

    public InvalidFactoryModifierException(String message) {
        super(message);
    }
}
