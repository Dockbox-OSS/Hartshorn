package org.dockbox.hartshorn.hsl.token;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.hsl.token.TokenGraph.TokenNode;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.AssertTokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.BitwiseAssignmentTokenType;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ClassTokenType;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;
import org.dockbox.hartshorn.hsl.token.type.ControlTokenType;
import org.dockbox.hartshorn.hsl.token.type.ExceptionTokenType;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.hsl.token.type.ImportTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.LoopTokenType;
import org.dockbox.hartshorn.hsl.token.type.MemberModifierTokenType;
import org.dockbox.hartshorn.hsl.token.type.ObjectTokenType;
import org.dockbox.hartshorn.hsl.token.type.PairTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TypeTokenType;
import org.dockbox.hartshorn.hsl.token.type.VariableTokenType;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.jetbrains.annotations.NotNull;

public final class DefaultTokenRegistry implements MutableTokenRegistry {

    private final Set<TokenType> types = new HashSet<>();

    private Map<Character, TokenCharacter> characterMapping;
    private TokenGraph tokenGraph;

    private DefaultTokenRegistry() {
        // Should use factory methods
    }

    public static DefaultTokenRegistry createDefault() {
        return new DefaultTokenRegistry().loadDefaults();
    }

    @Override
    public void addTokens(TokenType... types) {
        Collections.addAll(this.types, types);
        tokenGraph = null;
        characterMapping = null;
    }

    @Override
    public Set<TokenCharacter> characters() {
        return Set.of(DefaultTokenCharacter.values());
    }

    @Override
    public boolean isLineSeparator(TokenCharacter character) {
        return character.character() == SharedTokenCharacter.NEWLINE.character();
    }

    @Override
    public TokenCharacter character(char character) {
        return characterMapping().computeIfAbsent(character, c -> SimpleTokenCharacter.of(character, false));
    }

    @Override
    public TokenCharacterList characterList() {
        return DefaultCharacterList.INSTANCE;
    }

    @Override
    public Set<TokenType> tokenTypes() {
        return CollectionUtilities.merge(this.types, this.comments().commentTypes().allValues());
    }

    @Override
    public Set<TokenType> tokenTypes(Predicate<TokenType> predicate) {
        return this.tokenTypes().stream()
                .filter(predicate)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public LiteralTokenList literals() {
        return DefaultLiteralTokenList.INSTANCE;
    }

    @Override
    public CommentTokenList comments() {
        return DefaultCommentTokenList.INSTANCE;
    }

    @Override
    public TokenPairList tokenPairs() {
        return DefaultTokenPairList.INSTANCE;
    }

    @Override
    public TokenGraph tokenGraph() {
        if (this.tokenGraph == null) {
            this.tokenGraph = TokenGraph.of(this);
        }
        return this.tokenGraph;
    }

    private Map<Character, TokenCharacter> characterMapping() {
        if (this.characterMapping == null) {
            this.characterMapping = this.buildCharacterMapping();
        }
        return this.characterMapping;
    }

    @NotNull
    private Map<Character, TokenCharacter> buildCharacterMapping() {
        Map<Character, TokenCharacter> allCharacters = new HashMap<>();

        // Token graph roots are the first characters of each token type
        for(GraphNode<TokenNode> node : tokenGraph().roots()) {
            TokenCharacter character = node.value().character();
            allCharacters.put(character.character(), character);
        }

        // Always shared, contains basic characters such as spaces, tabs, newlines, etc
        for(SharedTokenCharacter character : SharedTokenCharacter.values()) {
            allCharacters.put(character.character(), character);
        }

        // Special characters, such as quotes, null, etc
        TokenCharacterList characterList = this.characterList();
        Set<TokenCharacter> specialCharacters = Set.of(
                characterList.nullCharacter(),
                characterList.charCharacter(),
                characterList.quoteCharacter(),
                characterList.numberDelimiter(),
                characterList.numberSeparator()
        );
        for(TokenCharacter character : specialCharacters) {
            allCharacters.put(character.character(), character);
        }

        return allCharacters;
    }

    public DefaultTokenRegistry loadDefaults() {
        this.addTokens(ArithmeticTokenType.values());
        this.addTokens(PairTokenType.values());
        this.addTokens(MemberModifierTokenType.values());
        this.addTokens(ImportTokenType.values());
        this.addTokens(ObjectTokenType.values());
        this.addTokens(TypeTokenType.values());
        this.addTokens(ClassTokenType.values());
        this.addTokens(ConditionTokenType.values());
        this.addTokens(BitwiseTokenType.values());
        this.addTokens(LoopTokenType.values());
        this.addTokens(VariableTokenType.values());
        this.addTokens(LiteralTokenType.values());
        this.addTokens(FunctionTokenType.values());
        this.addTokens(AssertTokenType.values());
        this.addTokens(ExceptionTokenType.values());
        this.addTokens(BaseTokenType.values());
        this.addTokens(ControlTokenType.values());
        this.addTokens(BitwiseAssignmentTokenType.values());
        return this;
    }
}
