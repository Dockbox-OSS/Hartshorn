package org.dockbox.hartshorn.hsl.lexer;

import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenConstants;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.inject.binding.Bound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lexer {

    private static final Map<String, TokenType> keywords = TokenType.keywords();

    private final List<Token> tokens = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final ErrorReporter errorReporter;
    private final String source;

    private int start = 0;
    private int current = 0;
    private int line = 1;

    @Bound
    public Lexer(final String source, final ErrorReporter errorReporter) {
        this.source = source;
        this.errorReporter = errorReporter;
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
            case TokenConstants.ARRAY_OPEN:
                this.addToken(TokenType.ARRAY_OPEN);
                break;
            case TokenConstants.ARRAY_CLOSE:
                this.addToken(TokenType.ARRAY_CLOSE);
                break;
            case TokenConstants.LEFT_PAREN:
                this.addToken(TokenType.LEFT_PAREN);
                break;
            case TokenConstants.RIGHT_PAREN:
                this.addToken(TokenType.RIGHT_PAREN);
                break;
            case TokenConstants.LEFT_BRACE:
                this.addToken(TokenType.LEFT_BRACE);
                break;
            case TokenConstants.RIGHT_BRACE:
                this.addToken(TokenType.RIGHT_BRACE);
                break;
            case TokenConstants.COMMA:
                this.addToken(TokenType.COMMA);
                break;
            case TokenConstants.DOT:
                this.addToken(TokenType.DOT);
                break;
            case TokenConstants.MINUS:
                this.addToken(this.match(TokenConstants.MINUS)
                        ? TokenType.MINUS_MINUS
                        : TokenType.MINUS
                );
                break;
            case TokenConstants.PLUS:
                this.addToken(this.match(TokenConstants.PLUS)
                        ? TokenType.PLUS_PLUS
                        : TokenType.PLUS
                );
                break;
            case TokenConstants.SEMICOLON:
                this.addToken(TokenType.SEMICOLON);
                break;
            case TokenConstants.STAR:
                this.addToken(TokenType.STAR);
                break;
            case TokenConstants.QUESTION_MARK:
                this.addToken(this.match(TokenConstants.COLON)
                        ? TokenType.ELVIS
                        : TokenType.QUESTION_MARK
                );
                break;
            case TokenConstants.COLON:
                this.addToken(TokenType.COLON);
                break;
            case TokenConstants.BANG:
                this.addToken(this.match(TokenConstants.EQUAL)
                        ? TokenType.BANG_EQUAL
                        : TokenType.BANG
                );
                break;
            case TokenConstants.EQUAL:
                this.addToken(this.match(TokenConstants.EQUAL)
                        ? TokenType.EQUAL_EQUAL :
                        TokenType.EQUAL
                );
                break;
            case TokenConstants.LESS:
                this.addToken(this.match(TokenConstants.EQUAL)
                        ? TokenType.LESS_EQUAL
                        : this.match(TokenConstants.LESS)
                            ? TokenType.SHIFT_LEFT
                            : TokenType.LESS
                );
                break;
            case TokenConstants.GREATER:
                this.addToken(this.match(TokenConstants.EQUAL)
                        ? TokenType.GREATER_EQUAL
                        : this.match(TokenConstants.GREATER)
                            ? this.match(TokenConstants.GREATER)
                                ? TokenType.LOGICAL_SHIFT_RIGHT
                                : TokenType.SHIFT_RIGHT
                            : TokenType.GREATER);
                break;
            case TokenConstants.SLASH:
                if (this.match(TokenConstants.SLASH)) {
                    this.scanComment();
                }
                else if (this.match(TokenConstants.STAR)) {
                    this.scanMultilineComment();
                }
                else {
                    this.addToken(TokenType.SLASH);
                }
                break;
            case TokenConstants.AMPERSAND:
                this.addToken(this.match(TokenConstants.AMPERSAND)
                        ? TokenType.AND
                        : TokenType.BITWISE_AND);
                break;
            case TokenConstants.PIPE:
                this.addToken(this.match(TokenConstants.PIPE)
                        ? TokenType.OR
                        : TokenType.BITWISE_OR);
                break;
            case TokenConstants.CARET:
                this.addToken(TokenType.XOR);
                break;
            case TokenConstants.TILDE:
                this.addToken(TokenType.COMPLEMENT);
                break;
            case TokenConstants.HASH:
                this.scanComment();
                break;
            case TokenConstants.SPACE:
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                this.line++;
                break;
            case TokenConstants.QUOTE:
                this.scanString();
                break;
            case TokenConstants.SINGLE_QUOTE:
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
            if (this.currentChar() == TokenConstants.STAR && this.nextChar() == TokenConstants.SLASH) {
                this.current += 2;
                break;
            }
            if (this.currentChar() == '\n') this.line++;
            text.append(this.pointToNextChar());
        }
        this.comments.add(new Comment(line, text.toString()));
    }

    private void scanNumber() {
        while (this.isDigit(this.currentChar()) || this.currentChar() == '_') {
            this.pointToNextChar();
        }

        // Look for a fractional part.
        if (this.currentChar() == TokenConstants.DOT && this.isDigit(this.nextChar())) {
            // Consume the "."
            this.pointToNextChar();
            while (this.isDigit(this.currentChar())) this.pointToNextChar();
        }

        String number = this.source.substring(this.start, this.current);
        number = number.replaceAll("_", "");
        this.addToken(TokenType.NUMBER, Double.parseDouble(number));
    }

    private void scanString() {
        while (this.currentChar() != TokenConstants.QUOTE && !this.isAtEnd()) {
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
        if (this.currentChar() != TokenConstants.SINGLE_QUOTE) {
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
