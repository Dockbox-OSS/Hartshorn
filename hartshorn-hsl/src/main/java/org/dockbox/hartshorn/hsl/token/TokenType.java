package org.dockbox.hartshorn.hsl.token;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public enum TokenType {
    // Literals
    IDENTIFIER, STRING, NUMBER, CHAR, NATIVE, EOF,

    // Single-character tokens
    LEFT_PAREN("("), RIGHT_PAREN(")"),
    LEFT_BRACE("{"), RIGHT_BRACE("}"),
    ARRAY_OPEN("["), ARRAY_CLOSE("]"),
    COMMA(","), DOT("."),
    MINUS("-"), PLUS("+"),
    SEMICOLON(";"), SLASH("/"), STAR("*"),
    EQUAL("="), BANG("!"),
    GREATER(">"), LESS("<"),
    QUESTION_MARK("?"), COLON(":"),

    // Two character tokens combining single character tokens
    ELVIS(builder -> builder.combines(QUESTION_MARK, COLON).ok()),
    EQUAL_EQUAL(builder -> builder.repeats(EQUAL).ok()),
    BANG_EQUAL(builder -> builder.combines(BANG, EQUAL).ok()),
    GREATER_EQUAL(builder -> builder.combines(GREATER, EQUAL).ok()),
    LESS_EQUAL(builder -> builder.combines(LESS, EQUAL).ok()),
    PLUS_PLUS(builder -> builder.repeats(PLUS).ok()),
    MINUS_MINUS(builder -> builder.repeats(MINUS).ok()),
    SHIFT_RIGHT(builder -> builder.repeats(GREATER).ok()),
    SHIFT_LEFT(builder -> builder.repeats(LESS).ok()),
    LOGICAL_SHIFT_RIGHT(builder -> builder.combines(GREATER, GREATER, GREATER).ok()),

    // Keywords
    PREFIX(builder -> builder.keyword(true).ok()),
    INFIX(builder -> builder.keyword(true).ok()),
    CLASS(builder -> builder.keyword(true).ok()),
    FUN(builder -> builder.keyword(true).ok()),
    EXTENDS(builder -> builder.keyword(true).ok()),
    ELSE(builder -> builder.keyword(true).ok()),
    TRUE(builder -> builder.keyword(true).ok()),
    FALSE(builder -> builder.keyword(true).ok()),
    FOR(builder -> builder.keyword(true).ok()),
    OR(builder -> builder.keyword(true).ok()),
    AND(builder -> builder.keyword(true).ok()),
    XOR(builder -> builder.keyword(true).ok()),
    SUPER(builder -> builder.keyword(true).ok()),
    THIS(builder -> builder.keyword(true).ok()),
    VAR(builder -> builder.keyword(true).ok()),
    NULL(builder -> builder.keyword(true).ok()),
    ARRAY(builder -> builder.keyword(true).ok()),

    // Standalone statements
    IF(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    REPEAT(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    DO(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    WHILE(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    BREAK(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    CONTINUE(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    RETURN(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    PRINT(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    TEST(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    MODULE(builder -> builder.keyword(true).standaloneStatement(true).ok()),
    ;

    private final TokenMetaData metaData;

    /**
     * Creates a new {@link TokenType} with the standard representation. The created {@link TokenType}
     * will not be a keyword or standalone statement.
     */
    TokenType() {
        this(TokenMetaDataBuilder::ok);
    }

    /**
     * Creates a new {@link TokenType} with the given static representation. The created {@link TokenType}
     * will not be a keyword or standalone statement.
     * @param representation
     */
    TokenType(final String representation) {
        this(b -> b.representation(representation).ok());
    }

    /**
     * Creates a new {@link TokenType} and allows the instantiating source to customize the metadata of
     * the new {@link TokenType}.
     * @param builder
     */
    TokenType(final Function<TokenMetaDataBuilder, TokenMetaData> builder) {
        this.metaData = builder.apply(TokenMetaData.builder(this));
    }

    public String representation() {
        return this.metaData.representation();
    }

    public boolean keyword() {
        return this.metaData.keyword();
    }

    public boolean standaloneStatement() {
        return this.metaData.standaloneStatement();
    }

    public static Map<String, TokenType> keywords() {
        final Map<String, TokenType> keywords = new ConcurrentHashMap<>();
        for (final TokenType tokenType : TokenType.values()) {
            if (tokenType.keyword()) {
                keywords.put(tokenType.representation(), tokenType);
            }
        }
        return Map.copyOf(keywords);
    }
}
