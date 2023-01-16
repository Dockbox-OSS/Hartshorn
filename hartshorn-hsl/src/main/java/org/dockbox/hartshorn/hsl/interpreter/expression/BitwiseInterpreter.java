package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;

public abstract class BitwiseInterpreter<R, T extends ASTNode> implements ASTNodeInterpreter<R, T> {

    protected Object getBitwiseResult(final Token operator, final Object left, final Object right) {
        if (left instanceof Number && right instanceof Number) {
            final int iLeft = ((Number) left).intValue();
            final int iRight = ((Number) right).intValue();

            return switch (operator.type()) {
                case SHIFT_RIGHT -> iLeft >> iRight;
                case SHIFT_LEFT -> iLeft << iRight;
                case LOGICAL_SHIFT_RIGHT -> iLeft >>> iRight;
                case BITWISE_AND -> iLeft & iRight;
                case BITWISE_OR -> iLeft | iRight;
                case XOR -> this.xor(iLeft, iRight);
                default -> throw new RuntimeError(operator, "Unsupported bitwise operator.");
            };
        }
        final String leftType = left != null ? left.getClass().getSimpleName() : null;
        final String rightType = right != null ? right.getClass().getSimpleName() : null;
        throw new RuntimeError(operator, "Bitwise left and right must be a numbers, but got %s (%s) and %s (%s)".formatted(left, leftType, right, rightType));
    }

    protected Object xor(final Object left, final Object right) {
        if (left instanceof Number nleft && right instanceof Number nright) {
            final int iLeft = nleft.intValue();
            final int iRight = nright.intValue();
            return iLeft ^ iRight;
        }
        return InterpreterUtilities.isTruthy(left) ^ InterpreterUtilities.isTruthy(right);
    }
}
