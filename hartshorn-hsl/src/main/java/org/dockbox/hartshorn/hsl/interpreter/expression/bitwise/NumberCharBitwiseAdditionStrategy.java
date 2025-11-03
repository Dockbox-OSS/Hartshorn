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
 * A {@link BitwiseAdditionStrategy} for adding {@link Number}s and {@link Character}s. No matter
 * the order of the operands, the result will always be a {@code double} representation of the
 * sum of the two operands.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class NumberCharBitwiseAdditionStrategy implements BitwiseAdditionStrategy {
    @Override
    public boolean supports(Object left, Object right) {
        return (left instanceof Number && right instanceof Character)
                || (left instanceof Character && right instanceof Number);
    }

    @Override
    public Object add(Object left, Object right) {
        if (left instanceof Character character) {
            int charValue = character;
            double numberValue = ((Number) right).doubleValue();
            return charValue + numberValue;
        }
        int charValue = ((Character) right);
        double numberValue = ((Number) left).doubleValue();
        return numberValue + charValue;
    }
}
