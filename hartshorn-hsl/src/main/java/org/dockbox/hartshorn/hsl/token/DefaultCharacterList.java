package org.dockbox.hartshorn.hsl.token;

public class DefaultCharacterList implements TokenCharacterList {

    public static final DefaultCharacterList INSTANCE = new DefaultCharacterList();

    @Override
    public TokenCharacter nullCharacter() {
        return SharedTokenCharacter.NULL;
    }

    @Override
    public TokenCharacter quoteCharacter() {
        return DefaultTokenCharacter.QUOTE;
    }

    @Override
    public TokenCharacter charCharacter() {
        return DefaultTokenCharacter.SINGLE_QUOTE;
    }

    @Override
    public TokenCharacter numberSeparator() {
        return DefaultTokenCharacter.UNDERSCORE;
    }

    @Override
    public TokenCharacter numberDelimiter() {
        return DefaultTokenCharacter.DOT;
    }
}
