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
import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;
import org.dockbox.hartshorn.hsl.token.type.ControlTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.LoopTokenType;

public class DefaultTokenSetLexer extends AbstractTokenSetLexer {

    public DefaultTokenSetLexer(String source, TokenRegistry tokenRegistry) {
        super(source, tokenRegistry);
    }

    @Override
    protected void scanToken() {
        final TokenCharacter tokenCharacter = this.pointToNextChar();
        if (tokenCharacter instanceof DefaultTokenCharacter defaultTokenCharacter) {
            scanDefaultToken(defaultTokenCharacter);
        }
        else {
            scanOtherToken(tokenCharacter);
        }
    }

    private void scanDefaultToken(DefaultTokenCharacter defaultTokenCharacter) {
        switch(defaultTokenCharacter) {
        case ARRAY_OPEN:
            this.addToken(tokenSet().tokenPairs().array().open());
            break;
        case ARRAY_CLOSE:
            this.addToken(tokenSet().tokenPairs().array().close());
            break;
        case LEFT_PAREN:
            this.addToken(tokenSet().tokenPairs().parameters().open());
            break;
        case RIGHT_PAREN:
            this.addToken(tokenSet().tokenPairs().parameters().close());
            break;
        case LEFT_BRACE:
            this.addToken(tokenSet().tokenPairs().block().open());
            break;
        case RIGHT_BRACE:
            this.addToken(tokenSet().tokenPairs().block().close());
            break;
        case COMMA:
            this.addToken(BaseTokenType.COMMA);
            break;
        case DOT:
            this.addToken(this.match(DefaultTokenCharacter.DOT)
                    ? LoopTokenType.RANGE
                    : BaseTokenType.DOT
            );
            break;
        case MINUS:
            if(this.match(DefaultTokenCharacter.MINUS)) {
                this.addToken(ArithmeticTokenType.MINUS_MINUS);
            }
            else if(this.match(DefaultTokenCharacter.GREATER)) {
                this.addToken(ControlTokenType.ARROW);
            }
            else {
                this.addToken(ArithmeticTokenType.MINUS);
            }
            break;
        case PLUS:
            this.addToken(this.match(DefaultTokenCharacter.PLUS)
                    ? ArithmeticTokenType.PLUS_PLUS
                    : ArithmeticTokenType.PLUS
            );
            break;
        case SEMICOLON:
            this.addToken(BaseTokenType.SEMICOLON);
            break;
        case STAR:
            this.addToken(ArithmeticTokenType.STAR);
            break;
        case MODULO:
            this.addToken(ArithmeticTokenType.MODULO);
            break;
        case QUESTION_MARK:
            this.addToken(this.match(DefaultTokenCharacter.COLON)
                    ? ConditionTokenType.ELVIS
                    : BaseTokenType.QUESTION_MARK
            );
            break;
        case COLON:
            this.addToken(BaseTokenType.COLON);
            break;
        case BANG:
            this.addToken(this.match(DefaultTokenCharacter.EQUAL)
                    ? ConditionTokenType.BANG_EQUAL
                    : BaseTokenType.BANG
            );
            break;
        case EQUAL:
            this.addToken(this.match(DefaultTokenCharacter.EQUAL)
                    ? ConditionTokenType.EQUAL_EQUAL
                    : BaseTokenType.EQUAL
            );
            break;
        case LESS:
            if(this.match(DefaultTokenCharacter.EQUAL)) {
                this.addToken(ConditionTokenType.LESS_EQUAL);
            }
            else {
                if(this.match(DefaultTokenCharacter.LESS)) {
                    if(this.match(DefaultTokenCharacter.EQUAL)) {
                        this.addToken(ConditionTokenType.SHIFT_LEFT_EQUAL);
                    }
                    else {
                        this.addToken(BitwiseTokenType.SHIFT_LEFT);
                    }
                }
                else {
                    this.addToken(ConditionTokenType.LESS);
                }
            }
            break;
        case GREATER:
            if(this.match(DefaultTokenCharacter.EQUAL)) {
                this.addToken(ConditionTokenType.GREATER_EQUAL);
            }
            else {
                if(this.match(DefaultTokenCharacter.GREATER)) {
                    if(this.match(DefaultTokenCharacter.GREATER)) {
                        this.addToken(BitwiseTokenType.LOGICAL_SHIFT_RIGHT);
                    }
                    else if(this.match(DefaultTokenCharacter.EQUAL)) {
                        this.addToken(ConditionTokenType.SHIFT_RIGHT_EQUAL);
                    }
                    else {
                        this.addToken(BitwiseTokenType.SHIFT_RIGHT);
                    }
                }
                else {
                    this.addToken(ConditionTokenType.GREATER);
                }
            }
            break;
        case SLASH:
            if(this.match(DefaultTokenCharacter.SLASH)) {
                this.scanComment();
            }
            else if(this.match(DefaultTokenCharacter.STAR)) {
                this.scanMultilineComment();
            }
            else {
                this.addToken(ArithmeticTokenType.SLASH);
            }
            break;
        case AMPERSAND:
            if(this.match(DefaultTokenCharacter.AMPERSAND)) {
                this.addToken(ConditionTokenType.AND);
            }
            else if(this.match(DefaultTokenCharacter.EQUAL)) {
                this.addToken(ConditionTokenType.BITWISE_AND_EQUAL);
            }
            else {
                this.addToken(BitwiseTokenType.BITWISE_AND);
            }
            break;
        case PIPE:
            if(this.match(DefaultTokenCharacter.PIPE)) {
                this.addToken(ConditionTokenType.OR);
            }
            else if(this.match(DefaultTokenCharacter.EQUAL)) {
                this.addToken(ConditionTokenType.BITWISE_OR_EQUAL);
            }
            else {
                this.addToken(BitwiseTokenType.BITWISE_OR);
            }
            break;
        case CARET:
            this.addToken(this.match(DefaultTokenCharacter.EQUAL)
                    ? ConditionTokenType.XOR_EQUAL
                    : BitwiseTokenType.XOR);
            break;
        case TILDE:
            this.addToken(this.match(DefaultTokenCharacter.EQUAL)
                    ? ConditionTokenType.COMPLEMENT_EQUAL
                    : BitwiseTokenType.COMPLEMENT);
            break;
        case HASH:
            this.scanComment();
            break;
        case SPACE:
        case CARRIAGE_RETURN:
        case TAB:
            // Ignore whitespace.
            break;
        case NEWLINE:
            this.nextLine();
            break;
        case QUOTE:
            this.scanString();
            break;
        case SINGLE_QUOTE:
            this.scanChar();
            break;
        case UNDERSCORE:
            throw new ScriptEvaluationError("Unexpected dangling number separator", Phase.TOKENIZING, this.line(), this.column());
        case NULL:
            throw new ScriptEvaluationError("Unexpected null character", Phase.TOKENIZING, this.line(), this.column());
        default:
            throw new ScriptEvaluationError("Unexpected token character, this is a bug in the lexer", Phase.TOKENIZING, this.line(), this.column());
        }
    }

    private void scanOtherToken(TokenCharacter character) {
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

    private void scanMultilineComment() {
        final StringBuilder text = new StringBuilder();
        final int line = this.line();
        while (!this.isAtEnd()) {
            if (this.currentChar() == DefaultTokenCharacter.STAR && this.nextChar() == DefaultTokenCharacter.SLASH) {
                this.incrementCurrent(2);
                break;
            }
            if (tokenSet().isLineSeparator(this.currentChar())) {
                this.nextLine();
            }
            text.append(this.pointToNextChar().character());
        }
        this.comments().add(new Comment(line, text.toString()));
    }

    protected void scanString() {
        while (this.currentChar() != DefaultTokenCharacter.QUOTE && !this.isAtEnd()) {
            if (tokenSet().isLineSeparator(this.currentChar())) {
                this.nextLine();
            }
            this.pointToNextChar();
        }

        // Unterminated scanString.
        if (this.isAtEnd()) {
            throw new ScriptEvaluationError("Unterminated string", Phase.TOKENIZING, this.line(), this.column());
        }

        // The closing ".
        this.pointToNextChar();

        // Trim the surrounding quotes.
        final String value = this.source().substring(this.start() + 1, this.current() - 1);
        this.addToken(LiteralTokenType.STRING, value);
    }

    private void scanChar() {
        final String value = this.source().substring(this.start() + 1, this.start() + 2);
        this.pointToNextChar();
        if (this.currentChar() != DefaultTokenCharacter.SINGLE_QUOTE) {
            throw new ScriptEvaluationError("Unterminated char variable", Phase.TOKENIZING, this.line(), this.column());
        }
        this.pointToNextChar();
        this.addToken(LiteralTokenType.CHAR, value.charAt(0));
    }
}
