package test.org.dockbox.hartshorn.hsl.support;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.extension.CustomStatement;
import org.dockbox.hartshorn.hsl.extension.ResolverExtension;
import org.dockbox.hartshorn.hsl.extension.StatementModule;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.parser.statement.StatementParser;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.SimpleTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class CaptureModule implements StatementModule<CaptureModule.CaptureStatement> {

    public static final TokenType CAPTURE = SimpleTokenType.builder()
            .keyword(true)
            .standaloneStatement(true)
            .tokenName("capture")
            .build();

    private Object capturedValue;

    @Override
    public StatementParser<CaptureStatement> parser() {
        return new StatementParser<>() {
            @Override
            public Option<? extends CaptureStatement> parse(TokenParser parser, TokenStepValidator validator) throws ScriptEvaluationError {
                if (parser.match(CAPTURE)) {
                    Token capture = parser.previous();
                    TokenTypePair parameters = parser.tokenRegistry().tokenPairs().parameters();
                    validator.expectAfter(parameters.open(), "capture");
                    Expression expression = parser.expression();
                    validator.expectAfter(parameters.close(), "capture");
                    return Option.of(new CaptureStatement(capture, CaptureModule.this, expression));
                }
                return Option.empty();
            }

            @Override
            public Set<Class<? extends CaptureStatement>> types() {
                return Set.of(CaptureStatement.class);
            }
        };
    }

    @Override
    public ResolverExtension<CaptureStatement> resolver() {
        return (node, resolver) -> {
            resolver.beginScope();
            resolver.resolve(node.expression());
            resolver.endScope();
        };
    }

    @Override
    public <U> U accept(StatementVisitor<U> visitor) {
        return null;
    }

    @Override
    public TokenType tokenType() {
        return CAPTURE;
    }

    @Override
    public ASTNodeInterpreter<Void, CaptureStatement> interpreter() {
        return (node, interpreter) -> {
            CaptureModule.this.capturedValue = interpreter.evaluate(node.expression());
            return null;
        };
    }

    public Object capturedValue() {
        return this.capturedValue;
    }


    public static class CaptureStatement extends CustomStatement<CaptureStatement> {

        private final Expression expression;

        public CaptureStatement(
                ASTNode at,
                StatementModule<CaptureStatement> module,
                Expression expression
        ) {
            super(at, module);
            this.expression = expression;
        }

        public Expression expression() {
            return expression;
        }
    }
}
