package test.org.dockbox.hartshorn.hsl.support;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.extension.CustomExpression;
import org.dockbox.hartshorn.hsl.extension.ExpressionModule;
import org.dockbox.hartshorn.hsl.extension.ResolverExtension;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.parser.expression.ExpressionParser;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.type.SimpleTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

import java.util.HashMap;
import java.util.Map;

public class CheckpointModule implements ExpressionModule<CheckpointModule.CheckpointExpression> {

    public static final TokenType CHECKPOINT = SimpleTokenType.builder()
            .representation("checkpoint")
            .tokenName("checkpoint")
            .keyword(true)
            .build();

    private final Map<String, Integer> checkpoints = new HashMap<>();

    public Map<String, Integer> checkpoints() {
        return this.checkpoints;
    }

    public boolean checkpointAccessed(String descriptor) {
        return this.checkpoints.containsKey(descriptor);
    }

    public int checkpointAccessCount(String descriptor) {
        return this.checkpoints.getOrDefault(descriptor, 0);
    }

    @Override
    public ExpressionParser parser() {
        return (parser, validator, chain) -> {
            if (parser.match(CHECKPOINT)) {
                TokenTypePair parameters = parser.tokenRegistry().tokenPairs().parameters();
                validator.expectAfter(parameters.open(), "checkpoint");
                Expression expression = parser.expression();
                validator.expectAfter(parameters.close(), "checkpoint descriptor");
                return new CheckpointExpression(
                        parser.previous(),
                        CheckpointModule.this,
                        expression
                );
            }
            return chain.next(parser, validator);
        };
    }

    @Override
    public <U> U accept(ExpressionVisitor<U> visitor) {
        return null;
    }

    @Override
    public TokenType tokenType() {
        return CHECKPOINT;
    }

    @Override
    public ASTNodeInterpreter<Object, CheckpointModule.CheckpointExpression> interpreter() {
        return (node, interpreter) -> {
            Object descriptor = interpreter.evaluate(node.descriptor());
            if (descriptor instanceof String string) {
                Integer count = checkpoints.getOrDefault(string, 0) + 1;
                checkpoints.put(string, count);
                return count;
            } else {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                        .at(node.descriptor())
                        .message("Checkpoint descriptor must be a string")
                        .build();
            }
        };
    }

    @Override
    public ResolverExtension<CheckpointExpression> resolver() {
        return (node, resolver) -> {
            resolver.resolve(node.descriptor());
        };
    }

    public static class CheckpointExpression extends CustomExpression<CheckpointExpression> {

        private final Expression descriptor;

        protected CheckpointExpression(ASTNode at, CheckpointModule module, Expression descriptor) {
            super(at, module);
            this.descriptor = descriptor;
        }

        public Expression descriptor() {
            return descriptor;
        }
    }
}
