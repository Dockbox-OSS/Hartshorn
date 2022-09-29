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

import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;

@HartshornTest
@UseCommands
@UseMethodCancelling
public class CommandExecutorTests {

    @Inject
    private CommandGateway gateway;
    @Inject
    private JUnitSystemSubject subject;

    @InjectTest
    void testMethodCancelling() throws ParsingException {
        Assertions.assertTrue(this.subject.received().isEmpty());

        this.gateway.accept(this.subject, "demo sub 1 --skip 1 2 3 4");
        Assertions.assertFalse(this.subject.received().isEmpty());

        final Message message = this.subject.received().get(0);
        Assertions.assertEquals("command.cancelled", message.key());
    }
}
