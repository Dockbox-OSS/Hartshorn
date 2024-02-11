/*
 * Copyright 2019-2024 the original author or authors.
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.CommentTokenList;
import org.dockbox.hartshorn.hsl.token.CommentTokenList.CommentType;
import org.dockbox.hartshorn.hsl.token.SharedTokenCharacter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenCharacterList;
import org.dockbox.hartshorn.hsl.token.TokenGraph;
import org.dockbox.hartshorn.hsl.token.TokenGraph.TokenNode;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.graph.ContainableGraphNode;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default lexer implementation. This lexer is designed to be customizable through
 * {@link TokenRegistry token registries}. This lexer can be re-used, but is not thread-safe.
 *
 * <p>Note that this lexer is not aware of any specific tokens on its own, and requires a
 * {@link TokenRegistry} to be provided. This registry should be configured with the
 * {@link TokenType token types} that are to be recognized by the lexer. As such, this lexer
 * is not aware of any specific language, and can be used to tokenize any language.
 *
 * <p>Tokens are matched by traversing a {@link TokenGraph}, which is built by the
 * {@link TokenRegistry}. This graph is traversed by matching the characters of available
 * {@link TokenType token types} to the characters of the source code. The most accurate
 * match is used to determine the token type. Keywords are matched individually based on their
 * {@link TokenType#tokenName() token name}.
 *
 * <p>Literals like strings, characters, and numbers are parsed separately, albeit with the
 * rules provided by the {@link TokenCharacterList} of the {@link TokenRegistry}. This allows
 * for a more flexible configuration of the lexer, as the {@link TokenCharacterList} can be
 * configured to match the needs of the language that is to be tokenized.
 *
 * <p>Comments are also parsed separately, and are matched based on their {@link CommentType}
 * as provided by the {@link TokenRegistry}. Comments are not added to the list of tokens, but
 * are instead added to the list of {@link Comment comments}.
 *
 * <p>Whitespace is ignored, except for newlines (unless the whitespace is inside a string
 * literal or comment). Newlines are used to determine the current line number, which is used to
 * report errors.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public class SimpleTokenRegistryLexer implements Lexer {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTokenRegistryLexer.class);

    private final List<Token> tokens = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final Map<String, TokenType> keywords = new HashMap<>();

    private final TokenRegistry tokenRegistry;
    private final String source;

    private int start;
    private int current;
    private int line = 1;
    private int column = -1;

    public SimpleTokenRegistryLexer(String source, TokenRegistry tokenRegistry) {
        this.source = source;
        this.tokenRegistry = tokenRegistry;
    }

    @Override
    public String source() {
        return this.source;
    }

    @Override
    public TokenRegistry tokenRegistry() {
        return this.tokenRegistry;
    }

    /**
     * The token graph that is used to match tokens. This graph is constructed based on the
     * {@link TokenRegistry} that is provided to this lexer.
     *
     * @return The token graph.
     */
    protected TokenGraph tokenGraph() {
        return this.tokenRegistry.tokenGraph();
    }

    /**
     * The index of the first character of the current token. This is typically the first character
     * of a keyword or literal that is currently being matched.
     *
     * @return The index of the first character of the current token.
     */
    protected int start() {
        return this.start;
    }

    /**
     * The current line number. This is the line number of the character that is currently being
     * matched.
     *
     * @return The current line number.
     */
    protected int line() {
        return this.line;
    }

    /**
     * The current column number. This is the column number of the character that is currently
     * being matched.
     *
     * @return The current column number.
     */
    protected int column() {
        return this.column;
    }

    /**
     * The index of the character that is currently being matched.
     *
     * @return The index of the character that is currently being matched.
     */
    protected int current() {
        return this.current;
    }

    /**
     * Increments the current index by one. This moves the index to the next character in the
     * source code. This does not check if the end of the source code has been reached, nor
     * does it update the current line and column numbers.
     */
    protected void incrementCurrent() {
        this.current++;
    }

    /**
     * Increments the current index by the provided delta. This moves the index to the next position
     * in the source code. This does not check if the end of the source code has been reached, nor
     * does it update the current line and column numbers.
     *
     * @param delta The number of characters to move the current index forward.
     */
    protected void incrementCurrent(int delta) {
        this.current += delta;
    }

    @Override
    public List<Token> scanTokens() {
        synchronized(this) {
            this.reset();

            while(!this.isAtEnd()) {
                this.start = this.current;
                this.scanToken();
            }

            Token token = Token.of(this.tokenRegistry.literals().eof())
                    .line(this.line)
                    .column(this.start)
                    .build();
            this.tokens.add(token);
            return this.tokens;
        }
    }

    /**
     * Resets the state of this lexer. This clears the list of tokens and comments, and resets
     * the line and column numbers. This method should always be called before a new tokenization
     * is started.
     */
    protected void reset() {
        this.tokens.clear();
        this.comments.clear();
        this.line = 1;
        this.column = -1;
        this.start = 0;
        this.current = 0;

        this.refreshKeywords();
    }

    /**
     * Refreshes the list of keywords. This method should be called whenever the list of
     * {@link TokenType#keyword() keywords} in the {@link TokenRegistry} has changed, or
     * a new tokenization is started.
     */
    protected void refreshKeywords() {
        this.keywords.clear();

        Map<String, TokenType> tokensByName = this.tokenRegistry.tokenTypes(TokenType::keyword)
                .stream()
                .collect(Collectors.toMap(TokenType::tokenName, Function.identity()));
        this.keywords.putAll(tokensByName);
    }

    /**
     * Scans the next token. This method is called for each character in the source code. The
     * character is matched against the {@link TokenRegistry} to determine the type of character
     * that is being scanned. Based on the type of character, the appropriate method is called
     * to scan the token.
     */
    protected void scanToken() {
        TokenCharacter tokenCharacter = this.pointToNextChar();
        if (tokenCharacter instanceof SharedTokenCharacter sharedTokenCharacter) {
            switch(sharedTokenCharacter) {
                case SPACE, CARRIAGE_RETURN, TAB -> { /* Ignore whitespace. */ }
                case NEWLINE -> this.nextLine();
                case NULL -> throw new ScriptEvaluationError("Unexpected null character", Phase.TOKENIZING, this.line(), this.column());
            }
        }
        else {
            TokenCharacterList characterList = this.tokenRegistry().characterList();
            if (!this.scanCharacterList(tokenCharacter, characterList)) {
                if (tokenCharacter.isStandaloneCharacter()) {
                    this.scanRegistryToken(tokenCharacter);
                }
                else {
                    this.scanOtherToken(tokenCharacter);
                }
            }
        }
    }

    /**
     * Compares the given character to special characters in the given {@link TokenCharacterList}.
     * If the character is a special character, the appropriate method is called to scan the token,
     * and this method returns {@code true}. If the character is not a special character, this
     * method returns {@code false}.
     *
     * <p>Certain validations are performed on the given character. If the character is invalid,
     * an exception is thrown.
     *
     * @param tokenCharacter The character to scan.
     * @param characterList The list of special characters.
     * @return {@code true} if the character is a special character, {@code false} otherwise.
     *
     * @throws ScriptEvaluationError If the character is invalid.
     */
    protected boolean scanCharacterList(TokenCharacter tokenCharacter, TokenCharacterList characterList) throws ScriptEvaluationError {
        if (tokenCharacter == characterList.nullCharacter()) {
            // Null character is not allowed. This is injected into the analysis when the source length is exceeded.
            throw new ScriptEvaluationError("Unexpected null character", Phase.TOKENIZING, this.line(), this.column());
        }
        else if(tokenCharacter == characterList.quoteCharacter()) {
            this.scanString();
            return true;
        }
        else if(tokenCharacter == characterList.charCharacter()) {
            this.scanChar();
            return true;
        }
        else if(tokenCharacter == characterList.numberSeparator()) {
            // Should only occur in #scanNumber(), so any other occurrence is an error.
            throw new ScriptEvaluationError("Unexpected dangling number separator", Phase.TOKENIZING, this.line(), this.column());
        }
        return false;
    }

    /**
     * Scans a token that is represented by one or more standalone characters. This will resolve
     * the appropriate token type by traversing the {@link TokenGraph} of the {@link TokenRegistry}.
     *
     * @param tokenCharacter The character that is being scanned.
     */
    protected void scanRegistryToken(TokenCharacter tokenCharacter) {
        ContainableGraphNode<TokenNode> next = this.findNext(tokenCharacter);
        int depth = 1;
        while (next != null) {
            boolean match = false;
            for(GraphNode<TokenNode> child : next.children()) {
                if (this.match(child.value().character())) {
                    next = (ContainableGraphNode<TokenNode>) child;
                    match = true;
                    break;
                }
            }
            // We're still in a valid token type, so if none of the children
            // match, we know we reached the end of the token. If the token
            // is incomplete, the token type will be null, so we can indicate
            // that the token is invalid.
            if (!match) {
                if (next.value().tokenType() != null) {
                    this.addMatchedToken(next.value());
                }
                else {
                    Option<GraphNode<TokenNode>> parent = this.tryFindValidParent(next, depth);
                    if (parent.present()) {
                        this.addMatchedToken(parent.get().value());
                    }
                    else {
                        String expectedTokens = CollectionUtilities.toString(next.children(), node -> {
                            TokenNode tokenNode = node.value();
                            return "'%s' (%s)".formatted(tokenNode.tokenType().representation(), tokenNode.tokenType().tokenName());
                        });
                        throw new ScriptEvaluationError("Unexpected end of token, expected any of: " + expectedTokens, Phase.TOKENIZING,
                                this.line(), this.column());
                    }
                }
                return;
            }

            depth++;
        }
        throw new ScriptEvaluationError("Unexpected character '" + this.currentChar().character() + "'", Phase.TOKENIZING, this.line(), this.column());
    }

    /**
     * Attempts to find a valid parent for the given node. This method is used to find a valid
     * token if we encounter a leaf node that does not have a token type. This method will traverse
     * the parents of the given node, and will return the first parent that has a valid token type.
     *
     * <p>This is used to find the correct token type when the token is incomplete as a combination,
     * but can be read as separate tokens. For example, the source "1 =++ 1" can be read as "1", "=", "++", "1"
     * but the initial matching would attempt to match "=++" as a single token. This method will then
     * attempt to find the valid parent, which is the "=" token, and will return that token type.
     *
     * <p>To account for the characters that were already matched, the depth of the current token
     * is provided. This is used to step back the correct number of characters to ensure that the
     * next character is matched correctly.
     *
     * @param node The node for which a valid parent is to be found.
     * @param depth The depth of the current token.
     * @return The first valid parent, if any.
     */
    protected Option<GraphNode<TokenNode>> tryFindValidParent(ContainableGraphNode<TokenNode> node, int depth) {
        Queue<GraphNode<TokenNode>> parents = new ArrayDeque<>(node.parents());
        while(!parents.isEmpty()) {
            GraphNode<TokenNode> parent = parents.poll();
            if (parent.value().tokenType() != null) {
                // We found a valid parent, so we can use that. Make sure to step back, to ensure
                // that the next character is matched correctly.
                int charactersToStepBack = depth - parent.value().tokenType().characters().length;
                this.incrementCurrent(-charactersToStepBack);
                this.column -= charactersToStepBack;
                return Option.of(parent);
            }
            if (parent instanceof ContainableGraphNode<TokenNode> containableGraphNode) {
                parents.addAll(containableGraphNode.parents());
            }
        }
        return Option.empty();
    }

    /**
     * Adds a token to the list of tokens. If the token matches a {@link CommentType comment type
     * definition} as provided by the active {@link TokenRegistry}, the token is added to the list
     * of comments. Otherwise, the token is added to the list of tokens.
     *
     * @param next The token that is matched.
     */
    protected void addMatchedToken(TokenNode next) {
        TokenType tokenType = next.tokenType();
        CommentTokenList commentTokenList = this.tokenRegistry().comments();
        Option<CommentType> commentType = commentTokenList.resolveFromOpenToken(tokenType);
        if (commentType.present()) {
            Option<TokenTypePair> tokenTypePair = commentTokenList.resolveTokenPairFromOpen(tokenType);
            if (tokenTypePair.absent()) {
                throw new ScriptEvaluationError("Invalid comment token pair", Phase.TOKENIZING, this.line(), this.column());
            }
            TokenType closeToken = tokenTypePair.get().close();
            switch(commentType.get()) {
            case LINE -> this.scanComment();
            case BLOCK -> this.scanMultilineComment(closeToken);
            default -> throw new ScriptEvaluationError("Invalid comment type", Phase.TOKENIZING, this.line(), this.column());
            }
        }
        else {
            this.addToken(tokenType);
        }
    }

    /**
     * Finds the next node in the token graph that matches the given character. If no such node
     * exists, an exception is thrown. This method will only discover root nodes, and should thus
     * only be used when scanning for a new token.
     *
     * @param tokenCharacter The character to match.
     * @return The next node in the token graph that matches the given character.
     *
     * @throws ScriptEvaluationError If the character is not matched.
     */
    protected ContainableGraphNode<TokenNode> findNext(TokenCharacter tokenCharacter) {
        Optional<ContainableGraphNode<TokenNode>> first = this.tokenGraph().roots().stream()
                .filter(node -> node.value().character() == tokenCharacter)
                .map(node -> (ContainableGraphNode<TokenNode>) node)
                .findFirst();
        return first.orElseThrow(() -> new ScriptEvaluationError("Unexpected character '" + tokenCharacter.character() + "'", Phase.TOKENIZING, this.line(), this.column()));
    }

    /**
     * Attempts to match the given character to a non-standalone token. Non-standalone means that
     * the character is not part of a pre-defined {@link TokenType}, but rather part of a literal
     * or identifier. If the character is matched, the appropriate method is called to scan the
     * token. If the character is not matched, an exception is thrown.
     *
     * @param character The character to match.
     *
     * @throws ScriptEvaluationError If the character is not matched.
     */
    protected void scanOtherToken(TokenCharacter character) {
        if(character.isDigit()) {
            this.scanNumber();
        }
        else if(character.isAlpha()) {
            this.scanIdentifier();
        }
        else {
            throw new ScriptEvaluationError("Unexpected character '" + character.character() + "'", Phase.TOKENIZING, this.line(), this.column());
        }
    }

    /**
     * Continues to match all text until the end of a block comment is found. The text is added to
     * the list of comments.
     *
     * @param closeToken The token that closes the block comment, or {@code null} if the comment is
     *                   closed by a newline.
     *
     * @see CommentTokenList#resolveTokenPairFromOpen(TokenType)
     * @see CommentTokenList#resolveFromOpenToken(TokenType)
     */
    protected void scanMultilineComment(TokenType closeToken) {
        StringBuilder text = new StringBuilder();
        int line = this.line();

        TokenCharacter[] characters = closeToken != null
                ? closeToken.characters()
                // If no explicit close token is provided, the comment is closed by a newline
                : new TokenCharacter[]{};

        while (!this.isAtEnd()) {
            // Ensure we don't match if there is no explicit close token
            boolean match = characters.length > 0;
            for(int i = 0; i < characters.length; i++) {
                if (this.peekChar(i) != characters[i]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                this.incrementCurrent(characters.length);
                break;
            }
            if (this.tokenRegistry().isLineSeparator(this.currentChar())) {
                this.nextLine();
                // If there is no explicit close token, treat line separators as the end of the comment
                if (characters.length == 0) {
                    break;
                }
            }
            text.append(this.pointToNextChar().character());
        }
        this.comments().add(new Comment(line, text.toString()));
    }

    /**
     * Continues to match all text until the end of a string literal is found. The text is added to
     * the list of tokens as a string literal.
     */
    protected void scanString() {
        while (this.currentChar() != this.tokenRegistry().characterList().quoteCharacter() && !this.isAtEnd()) {
            if (this.tokenRegistry().isLineSeparator(this.currentChar())) {
                this.nextLine();
            }
            this.pointToNextChar();
        }

        // Unterminated string
        if (this.isAtEnd()) {
            throw new ScriptEvaluationError("Unterminated string", Phase.TOKENIZING, this.line(), this.column());
        }

        // The closing quote
        this.pointToNextChar();

        // Trim the surrounding quotes
        String value = this.source().substring(this.start() + 1, this.current() - 1);
        this.addToken(this.tokenRegistry().literals().string(), value);
    }

    /**
     * Continues to match all text until the end of a character literal is found. The text is added
     * to the list of tokens as a character literal.
     */
    protected void scanChar() {
        String value = this.source().substring(this.start() + 1, this.start() + 2);
        this.pointToNextChar();
        if (this.currentChar() != this.tokenRegistry().characterList().charCharacter()) {
            throw new ScriptEvaluationError("Unterminated char variable", Phase.TOKENIZING, this.line(), this.column());
        }
        this.pointToNextChar();
        this.addToken(this.tokenRegistry().literals().character(), value.charAt(0));
    }

    @Override
    public List<Comment> comments() {
        return this.comments;
    }

    /**
     * Scans a single-line comment. This method is called when a single-line comment is encountered
     * in the source code. The comment is added to the list of comments.
     */
    protected void scanComment() {
        StringBuilder text = new StringBuilder();
        int line = this.line;
        while (!this.tokenRegistry.isLineSeparator(this.currentChar()) && !this.isAtEnd()) {
            text.append(this.pointToNextChar().character());
        }
        this.comments.add(new Comment(line, text.toString()));
    }

    /**
     * Continues to match all text until the end of a number literal is found. The text is added to
     * the list of tokens as a number literal. This method also matches fractional parts of a
     * number literal. For example, the source "123.456" is matched as a single number literal.
     * The fractional part is not matched as a separate token.
     *
     * <p>Numbers can contain underscores, which are ignored. For example, the source "1_000_000"
     * is matched as a single number literal with the value 1000000.
     */
    protected void scanNumber() {
        while (this.currentChar().isDigit() || this.tokenRegistry.characterList().numberSeparator() == this.currentChar()) {
            this.pointToNextChar();
        }

        // Look for a fractional part.
        if (this.tokenRegistry.characterList().numberDelimiter() == this.currentChar() && this.nextChar().isDigit()) {
            // Consume the delimiter
            this.pointToNextChar();
            while (this.currentChar().isDigit()) {
                this.pointToNextChar();
            }
        }

        String number = this.source.substring(this.start, this.current);
        number = number.replace("_", "");
        this.addToken(this.tokenRegistry.literals().number(), Double.parseDouble(number));
    }

    /**
     * Continues to match all text until the end of an identifier is found. The text is added to the
     * list of tokens as an identifier. An identifier is any sequence of alphanumeric characters
     * that does not match a reserved word. If the identifier matches a reserved word, the token
     * type of the reserved word is used instead.
     */
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

    /**
     * Looks up the token type that represents the given text as a literal (e.g. {@code null}, {@code true},
     * {@code false}). If the text does not match any known literal, the identifier token type is returned.
     *
     * @param text The text to look up.
     * @return The token type that represents the given text as a literal.
     */
    @NotNull
    protected TokenType lookupLiteralToken(String text) {
        Set<TokenType> literals = this.tokenRegistry.literals().literals();
        for(TokenType literal : literals) {
            if (literal.defaultLexeme() != null && literal.defaultLexeme().equals(text)) {
                return literal;
            }
        }
        return this.tokenRegistry.literals().identifier();
    }

    protected TokenCharacter pointToNextChar() {
        this.incrementCurrent();
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
        this.incrementCurrent();
        this.column++;
        return true;
    }

    protected TokenCharacter currentChar() {
        return this.peekChar(0);
    }

    protected TokenCharacter nextChar() {
        return this.peekChar(1);
    }

    protected TokenCharacter peekChar(int delta) {
        if (this.current + delta >= this.source.length()) {
            return this.tokenRegistry.characterList().nullCharacter();
        }
        char character = this.source.charAt(this.current + delta);
        return this.tokenRegistry.character(character);
    }

    protected void nextLine() {
        this.line++;
        this.column = -1;
    }
}
