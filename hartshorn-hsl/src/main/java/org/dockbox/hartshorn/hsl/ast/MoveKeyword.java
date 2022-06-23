package org.dockbox.hartshorn.hsl.ast;


public class MoveKeyword extends RuntimeException {

    public enum ScopeType {
        NONE, LOOP
    }

    public enum MoveType {
        BREAK,
        CONTINUE
    }

    private final MoveType moveType;

    public MoveKeyword(final MoveType type) {
        super(null, null, false, false);
        this.moveType = type;
    }

    public MoveType moveType() {
        return this.moveType;
    }
}
