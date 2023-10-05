package org.dockbox.hartshorn.hsl.token;

public enum SharedTokenCharacter implements SimpleTokenCharacter {
    SPACE(' '),
    TAB('\t'),
    NEWLINE('\n'),
    CARRIAGE_RETURN('\r'),
    NULL('\0'),

    ;

    private final char character;

    SharedTokenCharacter(final char character) {
        this.character = character;
    }

    @Override
    public char character() {
        return this.character;
    }

    @Override
    public boolean isStandaloneCharacter() {
        return true;
    }
}
