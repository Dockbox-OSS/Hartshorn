/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.inject.DefaultFallbackCompatibleContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Context for function parsers, holding the names of registered prefix and infix functions.
 *
 * @since 0.4.13
 * 
 * @author Guus Lieben
 */
public class FunctionParserContext extends DefaultFallbackCompatibleContext {

    private final Set<String> prefixFunctions = new HashSet<>();
    private final Set<String> infixFunctions = new HashSet<>();

    /**
     * Registers a new prefix function by its name.
     *
     * @param name the name of the prefix function
     */
    public void addPrefixFunction(String name) {
        this.prefixFunctions.add(name);
    }

    /**
     * Registers a new infix function by its name.
     *
     * @param name the name of the infix function
     */
    public void addInfixFunction(String name) {
        this.infixFunctions.add(name);
    }

    /**
     * Retrieves an unmodifiable set of registered prefix function names.
     *
     * @return the set of prefix function names
     */
    public Set<String> prefixFunctions() {
        return Collections.unmodifiableSet(this.prefixFunctions);
    }

    /**
     * Retrieves an unmodifiable set of registered infix function names.
     *
     * @return the set of infix function names
     */
    public Set<String> infixFunctions() {
        return Collections.unmodifiableSet(this.infixFunctions);
    }
}
