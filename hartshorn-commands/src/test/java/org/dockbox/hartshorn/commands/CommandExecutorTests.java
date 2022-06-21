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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

@HartshornTest
@UseCommands
@UseMethodCancelling
public class CommandExecutorTests {

    @InjectTest
    void testMethodCancelling(final ApplicationContext applicationContext) throws ParsingException {
        final CommandGateway gateway = applicationContext.get(CommandGateway.class);
        final JUnitSystemSubject subject = applicationContext.get(JUnitSystemSubject.class);
        Assertions.assertTrue(subject.received().isEmpty());

        gateway.accept(subject, "demo sub 1 --skip 1 2 3 4");
        Assertions.assertFalse(subject.received().isEmpty());

        final Message message = subject.received().get(0);
        Assertions.assertEquals("command.cancelled", message.key());
    }
}
