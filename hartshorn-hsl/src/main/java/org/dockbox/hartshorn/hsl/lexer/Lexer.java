/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.hsl.lexer;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenConstants;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The lexer takes in HSL sources and transforms it into tokens. This implementation
 * uses the constants defined in {@link TokenConstants} to identify individual tokens,
 * and matches them to logical {@link TokenType}s.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public class Lexer {

    private static final Logger LOG = LoggerFactory.getLogger(Lexer.class);
    private static final Map<String, TokenType> KEYWORDS = TokenType.keywords();

    private final List<Token> tokens = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final String source;

    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = -1;

    public Lexer(final String source) {
        this.source = source;
    }

    /**
     * Transforms the configured source into valid {@link Token}s. If an invalid token is
     * encountered, an error is reported. When an error is reported, the lexer will attempt
     * to proceed to the next token, skipping the invalid token(s). The collection of
     * tokens will always end with a single {@link TokenType#EOF EndOfFile token}.
     *
     * @return The scanned tokens.
     */
    public List<Token> scanTokens() {
        while (!this.isAtEnd()) {
            this.start = this.current;
            this.scanToken();
        }
        final Token token = Token.of(TokenType.EOF)
                .lexeme("")
                .line(this.line)
                .column(this.start)
                .build();
        this.tokens.add(token);
        return this.tokens;
    }

    /**
     * Gets all comments, if any.
     * @return All comments, or an empty {@link List}.
     */
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
                this.addToken(this.match(TokenConstants.DOT)
                        ? TokenType.RANGE
                        : TokenType.DOT
                );
                break;
            case TokenConstants.MINUS:
                if (this.match(TokenConstants.MINUS)) {
                    this.addToken(TokenType.MINUS_MINUS);
                } else if (this.match(TokenConstants.GREATER)) {
                    this.addToken(TokenType.ARROW);
                } else {
                    this.addToken(TokenType.MINUS);
                }
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
            case TokenConstants.MODULO:
                this.addToken(TokenType.MODULO);
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
                if (this.match(TokenConstants.EQUAL)) {
                    this.addToken(TokenType.LESS_EQUAL);
                }
                else {
                    if (this.match(TokenConstants.LESS)) {
                        if (this.match(TokenConstants.EQUAL)) {
                            this.addToken(TokenType.SHIFT_LEFT_EQUAL);
                        }
                        else {
                            this.addToken(TokenType.SHIFT_LEFT);
                        }
                    }
                    else this.addToken(TokenType.LESS);
                }
                break;
            case TokenConstants.GREATER:
                if (this.match(TokenConstants.EQUAL)) {
                    this.addToken(TokenType.GREATER_EQUAL);
                }
                else {
                    if (this.match(TokenConstants.GREATER)) {
                        if (this.match(TokenConstants.GREATER)) {
                            this.addToken(TokenType.LOGICAL_SHIFT_RIGHT);
                        }
                        else if (this.match(TokenConstants.EQUAL)) {
                            this.addToken(TokenType.SHIFT_RIGHT_EQUAL);
                        }
                        else {
                            this.addToken(TokenType.SHIFT_RIGHT);
                        }
                    }
                    else this.addToken(TokenType.GREATER);
                }
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
                if (this.match(TokenConstants.AMPERSAND)) this.addToken(TokenType.AND);
                else if (this.match(TokenConstants.EQUAL)) this.addToken(TokenType.BITWISE_AND_EQUAL);
                else this.addToken(TokenType.BITWISE_AND);
                break;
            case TokenConstants.PIPE:
                if (this.match(TokenConstants.PIPE)) this.addToken(TokenType.OR);
                else if (this.match(TokenConstants.EQUAL)) this.addToken(TokenType.BITWISE_OR_EQUAL);
                else this.addToken(TokenType.BITWISE_OR);
                break;
            case TokenConstants.CARET:
                this.addToken(this.match(TokenConstants.EQUAL)
                        ? TokenType.XOR_EQUAL
                        : TokenType.XOR);
                break;
            case TokenConstants.TILDE:
                this.addToken(this.match(TokenConstants.EQUAL)
                        ? TokenType.COMPLEMENT_EQUAL
                        : TokenType.COMPLEMENT);
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
                this.nextLine();
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
                    throw new ScriptEvaluationError("Unexpected character '" + c + "'", Phase.TOKENIZING, this.line, this.column);
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
            if (this.currentChar() == '\n') {
                this.nextLine();
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
            if (this.currentChar() == '\n') {
                this.nextLine();
            }
            this.pointToNextChar();
        }

        // Unterminated scanString.
        if (this.isAtEnd()) {
            throw new ScriptEvaluationError("Unterminated string", Phase.TOKENIZING, this.line, this.column);
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
            throw new ScriptEvaluationError("Unterminated char variable", Phase.TOKENIZING, this.line, this.column);
        }
        this.pointToNextChar();
        this.addToken(TokenType.CHAR, value.charAt(0));
    }

    private void scanIdentifier() {
        while (this.isAlphaNumeric(this.currentChar())) this.pointToNextChar();

        // See if the scanIdentifier is a reserved word.
        final String text = this.source.substring(this.start, this.current);

        TokenType type = KEYWORDS.get(text);

        if (type == null) type = TokenType.IDENTIFIER;
        this.addToken(type);
    }

    private char pointToNextChar() {
        this.current++;
        this.column++;
        return this.source.charAt(this.current - 1);
    }

    private void addToken(final TokenType type) {
        if (type.reserved()) {
            LOG.warn("Reserved token type used: " + type + " at line " + this.line + ", column " + this.column + ". " +
                    "Reserved tokens are not supported and may not be implemented yet. " +
                    "This may cause unexpected behavior.");
        }
        this.addToken(type, null);
    }

    private void addToken(final TokenType type, final Object literal) {
        final String text = this.source.substring(this.start, this.current);
        final Token token = Token.of(type)
                .literal(literal)
                .lexeme(text)
                .line(this.line)
                .column(Math.min(this.start, this.column))
                .build();
        this.tokens.add(token);
    }

    private boolean isAtEnd() {
        return this.current >= this.source.length();
    }

    private boolean isDigit(final char character) {
        return character >= '0' && character <= '9';
    }

    private boolean match(final char expected) {
        if (this.isAtEnd()) return false;
        if (this.source.charAt(this.current) != expected) return false;
        this.current++;
        this.column++;
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

    private boolean isAlpha(final char character) {
        return (character >= 'a' && character <= 'z') ||
                (character >= 'A' && character <= 'Z') ||
                character == '_';
    }

    private boolean isAlphaNumeric(final char character) {
        return this.isAlpha(character) || this.isDigit(character);
    }

    private void nextLine() {
        this.line++;
        this.column = -1;
    }

    private int column() {
        // Get current line from complete source index
        final int lineStart = this.source.lastIndexOf('\n', this.current) + 1;
        return this.current - lineStart - 1;
    }
}
