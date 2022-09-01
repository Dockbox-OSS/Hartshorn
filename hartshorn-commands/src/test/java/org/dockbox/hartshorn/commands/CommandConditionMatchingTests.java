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
import org.dockbox.hartshorn.commands.types.SampleCommand;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@HartshornTest
@UseCommands
@UseExpressionValidation
public class CommandConditionMatchingTests {

    @Inject
    private CommandGateway gateway;
    @Inject
    private JUnitSystemSubject subject;
    @Inject
    private SampleCommand sampleCommand;

    @Test
    void testMethodConditionMatchingProceedsIfMatched() throws ParsingException {
        gateway.accept(subject, "demo condition value");
        Assertions.assertEquals("value", sampleCommand.valueAfterCondition());
    }

    @Test
    void testMethodConditionMatchingFailsIfNotMatched() throws ParsingException {
        gateway.accept(subject, "demo condition");
        Assertions.assertNull(sampleCommand.valueAfterCondition());
    }
}
