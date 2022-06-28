package org.dockbox.hartshorn.hsl.lexer;

import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HslLexer {

    private final List<Token> tokens = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final ErrorReporter errorReporter;
    private static final Map<String, TokenType> keywords = TokenType.keywords();

    private String source;
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public HslLexer(final String source, final ErrorReporter errorReporter) {
        this.source = source;
        this.errorReporter = errorReporter;
    }

    public void source(final String source) {
        this.source = source;
    }

    public String source() {
        return this.source;
    }

    public List<Token> scanTokens() {
        while (!this.isAtEnd()) {
            this.start = this.current;
            this.scanToken();
        }
        this.tokens.add(new Token(TokenType.EOF, "", null, this.line));
        return this.tokens;
    }

    public List<Comment> comments() {
        return this.comments;
    }

    private void scanToken() {
        final char c = this.pointToNextChar();
        switch (c) {
            case '[':
                this.addToken(TokenType.ARRAY_OPEN);
                break;
            case ']':
                this.addToken(TokenType.ARRAY_CLOSE);
                break;
            case '(':
                this.addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                this.addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                this.addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                this.addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                this.addToken(TokenType.COMMA);
                break;
            case '.':
                this.addToken(TokenType.DOT);
                break;
            case '-':
                this.addToken(this.match('-') ? TokenType.MINUS_MINUS : TokenType.MINUS);
                break;
            case '+':
                this.addToken(this.match('+') ? TokenType.PLUS_PLUS : TokenType.PLUS);
                break;
            case ';':
                this.addToken(TokenType.SEMICOLON);
                break;
            case '*':
                this.addToken(TokenType.STAR);
                break;
            case '?':
                this.addToken(this.match(':') ? TokenType.ELVIS : TokenType.QUESTION_MARK);
                break;
            case ':':
                this.addToken(TokenType.COLON);
                break;
            case '!':
                this.addToken(this.match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                this.addToken(this.match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                this.addToken(this.match('=') ? TokenType.LESS_EQUAL : this.match('<') ? TokenType.SHIFT_LEFT : TokenType.LESS);
                break;
            case '>':
                this.addToken(this.match('=') ? TokenType.GREATER_EQUAL :
                        (this.match('>') ? this.match('>') ? TokenType.LOGICAL_SHIFT_RIGHT : TokenType.SHIFT_RIGHT : TokenType.GREATER));
                break;
            case '/':
                if (this.match('/')) {
                    this.scanComment();
                }
                else if (this.match('*')) {
                    this.scanMultilineComment();
                }
                else {
                    this.addToken(TokenType.SLASH);
                }
                break;
            case '#':
                this.scanComment();
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                this.line++;
                break;
            case '"':
                this.scanString();
                break;
            case '\'':
                this.scanChar();
                break;
            default:
                if (this.isDigit(c)) {
                    this.scanNumber();
                }
                else if (this.isAlpha(c)) {
                    this.scanIdentifier();
                }
                else {
                    this.errorReporter.error(Phase.TOKENIZING, this.line, "Unexpected character.");
                }
        }
    }

    private void scanComment() {
        final StringBuilder text = new StringBuilder();
        final int line = this.line;
        while (this.currentChar() != '\n' && !this.isAtEnd()) {
            text.append(this.pointToNextChar());
        }
        this.comments.add(new Comment(line, text.toString()));
    }

    private void scanMultilineComment() {
        final StringBuilder text = new StringBuilder();
        final int line = this.line;
        while (!this.isAtEnd()) {
            if (this.currentChar() == '*' && this.nextChar() == '/') {
                this.current += 2;
                break;
            }
            if (this.currentChar() == '\n') {
                this.line++;
            }
            text.append(this.pointToNextChar());
        }
        this.comments.add(new Comment(line, text.toString()));
    }

    private void scanNumber() {
        while (this.isDigit(this.currentChar()) || this.currentChar() == '_') {
            this.pointToNextChar();
        }

        // Look for a fractional part.
        if (this.currentChar() == '.' && this.isDigit(this.nextChar())) {
            // Consume the "."
            this.pointToNextChar();
            while (this.isDigit(this.currentChar())) this.pointToNextChar();
        }

        String number = this.source.substring(this.start, this.current);
        number = number.replaceAll("_", "");
        this.addToken(TokenType.NUMBER, Double.parseDouble(number));
    }

    private void scanString() {
        while (this.currentChar() != '"' && !this.isAtEnd()) {
            if (this.currentChar() == '\n') this.line++;
            this.pointToNextChar();
        }

        // Unterminated scanString.
        if (this.isAtEnd()) {
            this.errorReporter.error(Phase.TOKENIZING, this.line, "Unterminated scanString.");
            return;
        }

        // The closing ".
        this.pointToNextChar();

        // Trim the surrounding quotes.
        final String value = this.source.substring(this.start + 1, this.current - 1);
        this.addToken(TokenType.STRING, value);
    }

    private void scanChar() {
        final String value = this.source.substring(this.start + 1, this.start + 2);
        this.pointToNextChar();
        if (this.currentChar() != '\'') {
            this.errorReporter.error(Phase.TOKENIZING, this.line, "Unterminated char variable.");
            return;
        }
        this.pointToNextChar();
        this.addToken(TokenType.CHAR, value.charAt(0));
    }

    private void scanIdentifier() {
        while (this.isAlphaNumeric(this.currentChar())) this.pointToNextChar();

        // See if the scanIdentifier is a reserved word.
        final String text = this.source.substring(this.start, this.current);

        TokenType type = keywords.get(text);

        if (type == null) type = TokenType.IDENTIFIER;
        this.addToken(type);
    }

    private char pointToNextChar() {
        this.current++;
        return this.source.charAt(this.current - 1);
    }

    private void addToken(final TokenType type) {
        this.addToken(type, null);
    }

    private void addToken(final TokenType type, final Object literal) {
        final String text = this.source.substring(this.start, this.current);
        this.tokens.add(new Token(type, text, literal, this.line));
    }

    private boolean isAtEnd() {
        return this.current >= this.source.length();
    }

    private boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    private boolean match(final char expected) {
        if (this.isAtEnd()) return false;
        if (this.source.charAt(this.current) != expected) return false;
        this.current++;
        return true;
    }

    private char currentChar() {
        if (this.isAtEnd()) return '\0';
        return this.source.charAt(this.current);
    }

    private char nextChar() {
        if (this.current + 1 >= this.source.length()) return '\0';
        return this.source.charAt(this.current + 1);
    }

    private boolean isAlpha(final char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(final char c) {
        return this.isAlpha(c) || this.isDigit(c);
    }
}
