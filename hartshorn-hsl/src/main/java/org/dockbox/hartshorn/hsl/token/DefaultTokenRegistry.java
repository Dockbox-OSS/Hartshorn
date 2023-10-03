package org.dockbox.hartshorn.hsl.token;

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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class DefaultTokenRegistry implements TokenRegistry {

    private final Set<TokenType> types = new HashSet<>();

    private DefaultTokenRegistry() {
        // Should use factory methods
    }

    public static DefaultTokenRegistry createDefault() {
        return new DefaultTokenRegistry().loadDefaults();
    }

    public void addTokens(TokenType... types) {
        Collections.addAll(this.types, types);
    }

    @Override
    public Set<TokenCharacter> characters() {
        return Set.of(DefaultTokenCharacter.values());
    }

    @Override
    public boolean isNumberSeparator(TokenCharacter character) {
        return character.character() == DefaultTokenCharacter.UNDERSCORE.character();
    }

    @Override
    public boolean isNumberDelimiter(TokenCharacter character) {
        return character.character() == DefaultTokenCharacter.DOT.character();
    }

    @Override
    public boolean isLineSeparator(TokenCharacter character) {
        return character.character() == DefaultTokenCharacter.NEWLINE.character();
    }

    @Override
    public TokenCharacter character(char character) {
        TokenCharacter tokenCharacter = DefaultTokenCharacter.of(character);
        if (tokenCharacter == null) {
            return (SimpleTokenCharacter) () -> character;
        }
        return tokenCharacter;
    }

    @Override
    public DefaultTokenCharacter nullCharacter() {
        return DefaultTokenCharacter.NULL;
    }

    @Override
    public Set<TokenType> tokenTypes(Predicate<TokenType> predicate) {
        return this.types.stream()
                .filter(predicate)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<TokenType> literals() {
        return Set.of(LiteralTokenType.values());
    }

    @Override
    public TokenPairList tokenPairs() {
        return new DefaultTokenPairList();
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
