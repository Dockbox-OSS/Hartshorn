package org.dockbox.hartshorn.hsl.interpreter.expression.bitwise;

public class CharacterBitwiseAdditionStrategy implements BitwiseAdditionStrategy {
    @Override
    public boolean supports(Object left, Object right) {
        return left instanceof Character && right instanceof Character;
    }

    @Override
    public Object add(Object left, Object right) {
        return String.valueOf(left) + right;
    }
}
