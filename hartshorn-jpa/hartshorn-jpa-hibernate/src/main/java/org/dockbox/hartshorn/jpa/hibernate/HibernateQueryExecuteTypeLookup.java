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
