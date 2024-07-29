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

package test.org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.launchpad.InvalidActivationSourceException;
import test.org.dockbox.hartshorn.boot.activators.AbstractActivator;
import test.org.dockbox.hartshorn.boot.activators.InterfaceActivator;
import test.org.dockbox.hartshorn.boot.activators.ValidActivator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HartshornApplicationTests {

    @Test
    void testCreationFailsWithAbstractActivator() {
        Assertions.assertThrows(InvalidActivationSourceException.class, () -> HartshornApplication.create(AbstractActivator.class));
    }

    @Test
    void testCreationFailsWithInterfaceActivator() {
        Assertions.assertThrows(InvalidActivationSourceException.class, () -> HartshornApplication.create(InterfaceActivator.class));
    }

    @Test
    void testCreationSucceedsWithValidActivator() {
        Assertions.assertDoesNotThrow(() -> HartshornApplication.create(ValidActivator.class));
    }

    @Test
    void testCreationSucceedsWithValidDeducedActivator() {
        Assertions.assertDoesNotThrow(() -> ValidActivator.main());
    }
}
