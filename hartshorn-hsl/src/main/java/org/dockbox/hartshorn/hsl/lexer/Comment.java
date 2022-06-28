package org.dockbox.hartshorn.hsl.lexer;

public class Comment {

    private final int line;
    private final String text;

    public Comment(final int line, final String text) {
        this.line = line;
        this.text = text;
    }

    public int line() {
        return this.line;
    }

    public String text() {
        return this.text;
    }
}
