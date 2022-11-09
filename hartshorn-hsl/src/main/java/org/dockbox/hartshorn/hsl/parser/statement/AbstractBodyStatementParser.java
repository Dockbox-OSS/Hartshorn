package org.dockbox.hartshorn.hsl.parser.statement;

import java.util.Set;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.util.option.Option;

public abstract class AbstractBodyStatementParser<T extends ASTNode> implements ASTNodeParser<T> {

    protected BlockStatement blockStatement(final String afterStatement, final ASTNode at, final TokenParser parser, final TokenStepValidator validator) {
        final Set<ASTNodeParser<BlockStatement>> parsers = parser.compatibleParsers(BlockStatement.class);
        if (parsers.isEmpty()) throw new ScriptEvaluationError("No BlockStatement parsers found", Phase.PARSING, at);

        for (final ASTNodeParser<BlockStatement> nodeParser : parsers) {
            final Option<BlockStatement> statement = nodeParser.parse(parser, validator);
            if (statement.present()) return statement.get();
        }

        throw new ScriptEvaluationError("Expected block after " + afterStatement, Phase.PARSING, parser.peek());
    }
}
