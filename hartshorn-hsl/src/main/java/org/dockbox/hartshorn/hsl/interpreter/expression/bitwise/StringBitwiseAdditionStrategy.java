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

package org.dockbox.hartshorn.hsl.interpreter.expression.bitwise;

/**
 * A {@link BitwiseAdditionStrategy} that supports addition of any object to a {@link String} by
 * converting the non-string operand to a string using {@link String#valueOf(Object)}.
 *
 * @since 0.7.0
 * 
 * @author Guus Lieben
 */
public class StringBitwiseAdditionStrategy implements BitwiseAdditionStrategy {
    @Override
    public boolean supports(Object left, Object right) {
        return left instanceof String || right instanceof String;
    }

    @Override
    public Object add(Object left, Object right) {
        return String.valueOf(left) + right;
    }
}
