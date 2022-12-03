package org.dockbox.hartshorn.jpa.hibernate;

import org.antlr.v4.runtime.tree.ParseTree;
import org.dockbox.hartshorn.jpa.query.QueryExecuteType;
import org.dockbox.hartshorn.jpa.query.QueryExecuteTypeLookup;
import org.hibernate.grammars.hql.HqlParser;
import org.hibernate.grammars.hql.HqlParser.StatementContext;
import org.hibernate.query.hql.internal.HqlParseTreeBuilder;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.query.sqm.tree.SqmStatement;
import org.hibernate.query.sqm.tree.delete.SqmDeleteStatement;
import org.hibernate.query.sqm.tree.insert.SqmInsertStatement;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.query.sqm.tree.update.SqmUpdateStatement;

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
        if (query instanceof QuerySqmImpl<?> sqm) {
            final SqmStatement<?> sqmStatement = sqm.getSqmStatement();
            if (sqmStatement instanceof SqmSelectStatement<?>) {
                return QueryExecuteType.SELECT;
            }
            else if (sqmStatement instanceof SqmUpdateStatement<?>){
                return QueryExecuteType.UPDATE;
            }
            else if (sqmStatement instanceof SqmDeleteStatement<?>) {
                return QueryExecuteType.DELETE;
            }
            else if (sqmStatement instanceof SqmInsertStatement<?>) {
                return QueryExecuteType.INSERT;
            }
            else throw new IllegalArgumentException("Unexpected SQM statement type: " + sqmStatement.getClass().getName());
        }
        else if (query instanceof NativeQueryImpl<?>) {
            return QueryExecuteType.NATIVE;
        }
        else throw new IllegalArgumentException("Unexpected query type: " + query.getClass().getName());
    }
}
