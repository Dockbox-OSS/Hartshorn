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

package org.dockbox.hartshorn.demo.commands.arguments;

import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.context.CommandContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple bean type which can be constructed through a {@link org.dockbox.hartshorn.commands.arguments.CustomParameterPattern}.
 *
 * @see org.dockbox.hartshorn.commands.arguments.HashtagParameterPattern
 * @see org.dockbox.hartshorn.demo.commands.services.CommandService#build(CommandContext)
 */
@Parameter("user")
@AllArgsConstructor
@Getter
public class User {
    private String name;
    private int age;
}
