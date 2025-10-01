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

import java.math.BigDecimal;

public class NumberBitwiseAdditionStrategy implements BitwiseAdditionStrategy {
    @Override
    public boolean supports(Object left, Object right) {
        return left instanceof Number && right instanceof Number;
    }

    @Override
    public Object add(Object left, Object right) {
        Number leftNumber = (Number) left;
        Number rightNumber = (Number) right;
        BigDecimal sum = BigDecimal.valueOf(leftNumber.doubleValue())
                .add(BigDecimal.valueOf(rightNumber.doubleValue()));
        return sum.doubleValue();
    }
}
