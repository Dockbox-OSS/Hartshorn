/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.commands.CommandParameterResources;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.i18n.Message;

import jakarta.inject.Inject;

/**
 * Converts Hashtag-patterns into type instances used by command executors. The pattern follows the HashtagPatternParser from WorldEdit.
 * Patterns are expected to start with a hashtag (#) followed by the alias of the type which is ignored while parsing due to the
 * availability of the Class for the type. Arguments are to be surrounded with square brackets.
 *
 * <p>An example of this pattern is as follows: if we have a constructor for a Shape type
 * <pre>{@code
 * public Shape(String name, int sides) { ... }
 * }</pre>
 * The pattern for this type is expected to be <pre>#shape[square][4]</pre>
 */
@Component
public class HashtagParameterPattern extends PrefixedParameterPattern {

    private final CommandParameterResources resources;

    @Inject
    public HashtagParameterPattern(final CommandParameterResources resources) {
        this.resources = resources;
    }

    @Override
    protected char opening() {
        return '[';
    }

    @Override
    protected char closing() {
        return ']';
    }

    @Override
    protected char prefix() {
        return '#';
    }

    @Override
    protected boolean requiresTypeName() {
        return true;
    }

    @Override
    protected Message wrongFormat() {
        return this.resources.wrongHashtagPatternFormat();
    }
}
