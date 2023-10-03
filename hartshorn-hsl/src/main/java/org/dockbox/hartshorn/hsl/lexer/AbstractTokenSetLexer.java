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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Requires updating
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public abstract class AbstractTokenSetLexer implements Lexer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTokenSetLexer.class);

    private final List<Token> tokens = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final Map<String, TokenType> keywords = new HashMap<>();

    private final TokenRegistry tokenRegistry;
    private final String source;

    private int start;
    private int current;
    private int line = 1;
    private int column = -1;


    protected AbstractTokenSetLexer(String source, TokenRegistry tokenRegistry) {
        this.source = source;
        this.tokenRegistry = tokenRegistry;
    }

    protected String source() {
        return this.source;
    }

    public TokenRegistry tokenSet() {
        return this.tokenRegistry;
    }

    protected int start() {
        return this.start;
    }

    protected int line() {
        return this.line;
    }

    protected int column() {
        return this.column;
    }

    protected int current() {
        return this.current;
    }

    protected void incrementCurrent() {
        this.incrementCurrent(1);
    }

    protected void incrementCurrent(int delta) {
        this.current += delta;
    }
    
    @Override
    public List<Token> scanTokens() {
        this.refreshKeywords();

        while (!this.isAtEnd()) {
            this.start = this.current;
            this.scanToken();
        }

        Token token = Token.of(LiteralTokenType.EOF)
                .line(this.line)
                .column(this.start)
                .build();
        this.tokens.add(token);
        return this.tokens;
    }

    protected void refreshKeywords() {
        this.keywords.clear();

        Map<String, TokenType> tokensByName = this.tokenRegistry.tokenTypes(TokenType::keyword)
                .stream()
                .collect(Collectors.toMap(TokenType::tokenName, Function.identity()));
        this.keywords.putAll(tokensByName);
    }

    protected abstract void scanToken();

    @Override
    public List<Comment> comments() {
        return this.comments;
    }

    protected void scanComment() {
        StringBuilder text = new StringBuilder();
        int line = this.line;
        while (!this.tokenRegistry.isLineSeparator(this.currentChar()) && !this.isAtEnd()) {
            text.append(this.pointToNextChar().character());
        }
        this.comments.add(new Comment(line, text.toString()));
    }

    protected void scanNumber() {
        while (this.currentChar().isDigit() || this.tokenRegistry.isNumberSeparator(this.currentChar())) {
            this.pointToNextChar();
        }

        // Look for a fractional part.
        if (this.tokenRegistry.isNumberDelimiter(this.currentChar()) && this.nextChar().isDigit()) {
            // Consume the "."
            this.pointToNextChar();
            while (this.currentChar().isDigit()) {
                this.pointToNextChar();
            }
        }

        String number = this.source.substring(this.start, this.current);
        number = number.replaceAll("_", "");
        this.addToken(LiteralTokenType.NUMBER, Double.parseDouble(number));
    }

    protected void scanIdentifier() {
        while (this.currentChar().isAlphaNumeric()) {
            this.pointToNextChar();
        }

        // See if the scanIdentifier is a reserved word.
        String text = this.source.substring(this.start, this.current);

        TokenType type = this.keywords.get(text);
        if (type == null) {
            type = this.lookupLiteralToken(text);
        }
        this.addToken(type);
    }

    @NotNull
    private TokenType lookupLiteralToken(String text) {
        Set<TokenType> literals = this.tokenRegistry.literals();
        for(TokenType literal : literals) {
            if (literal.defaultLexeme() != null && literal.defaultLexeme().equals(text)) {
                return literal;
            }
        }
        return LiteralTokenType.IDENTIFIER;
    }

    protected TokenCharacter pointToNextChar() {
        this.current++;
        this.column++;
        char character = this.source.charAt(this.current - 1);
        return this.tokenRegistry.character(character);
    }

    protected void addToken(TokenType type) {
        if (type.reserved()) {
            LOG.warn("Reserved token type used: " + type + " at line " + this.line + ", column " + this.column + ". " +
                    "Reserved tokens are not supported and may not be implemented yet. " +
                    "This may cause unexpected behavior.");
        }
        this.addToken(type, null);
    }

    protected void addToken(TokenType type, Object literal) {
        String text = this.source.substring(this.start, this.current);
        Token token = Token.of(type, text)
                .literal(literal)
                .line(this.line)
                .column(Math.min(this.start, this.column))
                .build();
        this.tokens.add(token);
    }

    protected boolean isAtEnd() {
        return this.current >= this.source.length();
    }

    protected boolean match(TokenCharacter expected) {
        if (this.isAtEnd()) {
            return false;
        }
        if (this.currentChar() != expected) {
            return false;
        }
        this.current++;
        this.column++;
        return true;
    }

    protected TokenCharacter currentChar() {
        if (this.isAtEnd()) {
            return this.tokenRegistry.nullCharacter();
        }
        char character = this.source.charAt(this.current);
        return this.tokenRegistry.character(character);
    }

    protected TokenCharacter nextChar() {
        if (this.current + 1 >= this.source.length()) {
            return this.tokenRegistry.nullCharacter();
        }
        char character = this.source.charAt(this.current + 1);
        return this.tokenRegistry.character(character);
    }

    protected void nextLine() {
        this.line++;
        this.column = -1;
    }
}
