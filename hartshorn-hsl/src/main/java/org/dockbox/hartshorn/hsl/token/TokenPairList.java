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

package org.dockbox.hartshorn.hsl.token;

import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;

/**
 * Represents a list of token pairs. This is used to define the various types of code blocks that
 * may be present in a script. A token pair is a pair of tokens that are used to define the start
 * and end of a block of code.
 *
 * @see TokenTypePair
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface TokenPairList {

    /**
     * Returns the token pair for a block of code. Code blocks are typically allowed to contain
     * zero or more statements, and are defined by a start and end token.
     *
     * @return the token pair for a block of code
     */
    TokenTypePair block();

    /**
     * Returns the token pair for a collection of parameters, typically used in conditions or
     * function definitions.
     *
     * @return the token pair for a collection of parameters
     */
    TokenTypePair parameters();

    /**
     * Returns the token pair for a list or array of items. This is typically not used directly
     * in statements, but rather as a part of an array literal expression.
     *
     * @return the token pair for a list or array of items
     */
    TokenTypePair array();

}
