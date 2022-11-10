package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassStatementParser implements ASTNodeParser<ClassStatement> {

    @Override
    public Option<ClassStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.CLASS)) {
            final Token name = validator.expect(TokenType.IDENTIFIER, "class name");

            final boolean isDynamic = parser.match(TokenType.QUESTION_MARK);

            VariableExpression superClass = null;
            if (parser.match(TokenType.EXTENDS)) {
                validator.expect(TokenType.IDENTIFIER, "super class name");
                superClass = new VariableExpression(parser.previous());
            }

            validator.expectBefore(TokenType.LEFT_BRACE, "class body");

            final List<FunctionStatement> methods = new ArrayList<>();
            final List<FieldStatement> fields = new ArrayList<>();
            ConstructorStatement constructor = null;
            while (!parser.check(TokenType.RIGHT_BRACE) && !parser.isAtEnd()) {
                final Statement declaration = this.classBodyStatement(parser, validator);
                if (declaration instanceof ConstructorStatement constructorStatement) {
                    constructor = constructorStatement;
                }
                else if (declaration instanceof FunctionStatement function) {
                    methods.add(function);
                }
                else if (declaration instanceof FieldStatement field) {
                    fields.add(field);
                }
                else {
                    throw new ScriptEvaluationError("Unsupported class body statement type: " + declaration.getClass()
                            .getSimpleName(), Phase.PARSING, parser.peek());
                }
            }

            validator.expectAfter(TokenType.RIGHT_BRACE, "class body");

            return Option.of(new ClassStatement(name, superClass, constructor, methods, fields, isDynamic));
        }
        return Option.empty();
    }

    private Statement classBodyStatement(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.check(TokenType.CONSTRUCTOR)) {
            return this.handleDelegate(parser, validator, parser.firstCompatibleParser(ConstructorStatement.class));
        }
        else if (parser.check(TokenType.FUN)) {
            return this.handleDelegate(parser, validator, parser.firstCompatibleParser(FunctionStatement.class));
        }
        else {
            return this.handleDelegate(parser, validator, parser.firstCompatibleParser(FieldStatement.class));
        }
    }

    private <T extends Statement> T handleDelegate(final TokenParser parser, final TokenStepValidator validator,
                                                   final Option<ASTNodeParser<T>> statement) {
        return statement
                .flatMap(p -> p.parse(parser, validator))
                .attempt(ScriptEvaluationError.class)
                .rethrow()
                .orNull();
    }

    @Override
    public Set<Class<? extends ClassStatement>> types() {
        return Set.of(ClassStatement.class);
    }
}
