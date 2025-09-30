package org.dockbox.hartshorn.hsl.interpreter.expression.bitwise;

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
