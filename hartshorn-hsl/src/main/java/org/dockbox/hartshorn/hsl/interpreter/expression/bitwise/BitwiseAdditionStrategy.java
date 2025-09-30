package org.dockbox.hartshorn.hsl.interpreter.expression.bitwise;

public interface BitwiseAdditionStrategy {

    boolean supports(Object left, Object right);

    Object add(Object left, Object right);
}
