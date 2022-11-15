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

package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.context.DefaultContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FunctionParserContext extends DefaultContext {

    private final Set<String> prefixFunctions = new HashSet<>();
    private final Set<String> infixFunctions = new HashSet<>();

    public void addPrefixFunction(final String name) {
        this.prefixFunctions.add(name);
    }

    public void addInfixFunction(final String name) {
        this.infixFunctions.add(name);
    }

    public Set<String> prefixFunctions() {
        return Collections.unmodifiableSet(this.prefixFunctions);
    }

    public Set<String> infixFunctions() {
        return Collections.unmodifiableSet(this.infixFunctions);
    }
}
