/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.persistence.hibernate.dialects;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.AbstractAnsiTrimEmulationFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

public class SQLiteDialect extends Dialect {
    public SQLiteDialect() {
        this.registerColumnType(Types.BIT, "integer");
        this.registerColumnType(Types.TINYINT, "tinyint");
        this.registerColumnType(Types.SMALLINT, "smallint");
        this.registerColumnType(Types.INTEGER, "integer");
        this.registerColumnType(Types.BIGINT, "bigint");
        this.registerColumnType(Types.FLOAT, "float");
        this.registerColumnType(Types.REAL, "real");
        this.registerColumnType(Types.DOUBLE, "double");
        this.registerColumnType(Types.NUMERIC, "numeric");
        this.registerColumnType(Types.DECIMAL, "decimal");
        this.registerColumnType(Types.CHAR, "char");
        this.registerColumnType(Types.VARCHAR, "varchar");
        this.registerColumnType(Types.LONGVARCHAR, "longvarchar");
        this.registerColumnType(Types.DATE, "date");
        this.registerColumnType(Types.TIME, "time");
        this.registerColumnType(Types.TIMESTAMP, "timestamp");
        this.registerColumnType(Types.BINARY, "blob");
        this.registerColumnType(Types.VARBINARY, "blob");
        this.registerColumnType(Types.LONGVARBINARY, "blob");
        this.registerColumnType(Types.NULL, "null");
        this.registerColumnType(Types.BLOB, "blob");
        this.registerColumnType(Types.CLOB, "clob");
        this.registerColumnType(Types.BOOLEAN, "boolean");

        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
        this.registerFunction("mod", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "?1 % ?2"));
        this.registerFunction("quote", new StandardSQLFunction("quote", StandardBasicTypes.STRING));
        this.registerFunction("random", new NoArgSQLFunction("random", StandardBasicTypes.INTEGER));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("trim", new AbstractAnsiTrimEmulationFunction() {
            protected SQLFunction resolveBothSpaceTrimFunction() {
                return new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1)");
            }

            protected SQLFunction resolveBothSpaceTrimFromFunction() {
                return new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?2)");
            }

            protected SQLFunction resolveLeadingSpaceTrimFunction() {
                return new SQLFunctionTemplate(StandardBasicTypes.STRING, "ltrim(?1)");
            }

            protected SQLFunction resolveTrailingSpaceTrimFunction() {
                return new SQLFunctionTemplate(StandardBasicTypes.STRING, "rtrim(?1)");
            }

            protected SQLFunction resolveBothTrimFunction() {
                return new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1, ?2)");
            }

            protected SQLFunction resolveLeadingTrimFunction() {
                return new SQLFunctionTemplate(StandardBasicTypes.STRING, "ltrim(?1, ?2)");
            }

            protected SQLFunction resolveTrailingTrimFunction() {
                return new SQLFunctionTemplate(StandardBasicTypes.STRING, "rtrim(?1, ?2)");
            }
        });
    }

    public boolean supportsIdentityColumns() {
        return true;
    }


    public boolean supportsInsertSelectIdentity() {
        return true; // As specify in NHibernate dialect
    }


    public boolean hasDataTypeInIdentityColumn() {
        return false; // As specify in NHibernate dialect
    }


    public String appendIdentitySelectToInsert(String insertString) {
        return insertString + "; " + this.getIdentitySelectString();
    }

    public String getIdentitySelectString() {
        return "select last_insert_rowid()";
    }

    public String getIdentityColumnString() {
        // return "integer primary key autoincrement";
        return "integer";
    }

    public boolean supportsLimit() {
        return true;
    }

    protected String getLimitString(String query, boolean hasOffset) {
        return query + (hasOffset ? " limit ? offset ?" : " limit ?");
    }

    public boolean supportsTemporaryTables() {
        return true;
    }

    public String getCreateTemporaryTableString() {
        return "create temporary table if not exists";
    }
}
