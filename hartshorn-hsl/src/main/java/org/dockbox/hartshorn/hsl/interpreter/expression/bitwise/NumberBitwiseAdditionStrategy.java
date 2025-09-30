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
