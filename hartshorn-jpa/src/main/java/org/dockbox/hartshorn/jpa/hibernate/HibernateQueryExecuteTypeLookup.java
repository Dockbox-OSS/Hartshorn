package org.dockbox.hartshorn.jpa.hibernate;

import org.antlr.v4.runtime.tree.ParseTree;
import org.dockbox.hartshorn.jpa.query.QueryExecuteType;
import org.dockbox.hartshorn.jpa.query.QueryExecuteTypeLookup;
import org.hibernate.grammars.hql.HqlParser;
import org.hibernate.grammars.hql.HqlParser.StatementContext;
import org.hibernate.query.hql.internal.HqlParseTreeBuilder;

import jakarta.persistence.Query;

public class HibernateQueryExecuteTypeLookup implements QueryExecuteTypeLookup {

    @Override
    public QueryExecuteType lookup(final String jpqlQuery) {
        final HqlParser hqlParser = new HqlParseTreeBuilder().buildHqlParser(jpqlQuery);
        final StatementContext statement = hqlParser.statement();
        final ParseTree parseTree = statement.getChild(0);

        if (parseTree instanceof HqlParser.SelectStatementContext) {
            return QueryExecuteType.SELECT;
        }
        else if (parseTree instanceof HqlParser.InsertStatementContext) {
            return QueryExecuteType.INSERT;
        }
        else if (parseTree instanceof HqlParser.UpdateStatementContext) {
            return QueryExecuteType.UPDATE;
        }
        else if (parseTree instanceof HqlParser.DeleteStatementContext) {
            return QueryExecuteType.DELETE;
        }
        else throw new IllegalArgumentException("Unexpected parse tree type: " + parseTree.getClass().getName() + " for JPQL query: " + jpqlQuery);
    }

    @Override
    public QueryExecuteType lookup(final Query query) {
        // TODO: Implement
        return null;
    }
}
