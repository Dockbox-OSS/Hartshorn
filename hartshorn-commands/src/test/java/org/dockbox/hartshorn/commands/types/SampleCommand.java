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

package org.dockbox.hartshorn.commands.types;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.hsl.condition.RequiresExpression;
import org.dockbox.hartshorn.util.StringUtilities;

@Command("demo")
public class SampleCommand {

    private String valueAfterCondition;

    @Command(arguments = "<remaining{remainingString}>")
    public void parent(final CommandContext context) {
    }

    @Command(value = "sub", arguments = "<argument{Int}> --skip remainingInt")
    public void sub(final CommandContext context) {
    }

    @Command(value = "sub sub", arguments = "<service{Service}>")
    public void subsub(final CommandContext context) {
    }

    @Command(value = "complex", arguments = "<required{String}> [optional{String}]  [enum{org.dockbox.hartshorn.commands.types.CommandValueEnum}] --flag --vflag String -s")
    public void complex(final CommandContext context) {
    }

    @Command(value = "group", arguments = "[group <requiredA> <requiredB>]")
    public void group(final CommandContext context) {
    }

    @Command(value = "arguments", arguments = "<required{String}> [optional{String}] --flag String")
    public void arguments(final CommandSource source, final CommandContext context, final String required, final String optional, final String flag) {
    }

    @Command(value = "condition", arguments = "[optional{String}]")
    @RequiresExpression("optional != null && optional != \"\"")
    public void condition(final CommandContext context, final String optional) {
        if (StringUtilities.empty(optional)) {
            throw new IllegalArgumentException("optional is empty");
        }
        this.valueAfterCondition = optional;
    }

    public String valueAfterCondition() {
        return this.valueAfterCondition;
    }
}
