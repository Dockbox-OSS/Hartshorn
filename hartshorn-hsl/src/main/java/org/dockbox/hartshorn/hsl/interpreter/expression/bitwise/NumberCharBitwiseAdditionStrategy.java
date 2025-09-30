package org.dockbox.hartshorn.hsl.interpreter.expression.bitwise;

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
