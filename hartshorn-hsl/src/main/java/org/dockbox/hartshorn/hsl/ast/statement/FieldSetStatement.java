/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

/**
 * Field member statement representing a setter method for a field.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class FieldSetStatement extends FieldMemberStatement {

    private final Parameter parameter;

    public FieldSetStatement(
        Token modifier,
        Token set,
        FieldStatement fieldStatement,
        BlockStatement body,
        Parameter parameter
    ) {
        super(modifier,
            set,
            fieldStatement,
            parameter == null ? List.of() : List.of(parameter),
            body);
        this.parameter = parameter;
    }

    /**
     * The parameter of the setter method.
     *
     * @return the parameter
     */
    public Parameter parameter() {
        return this.parameter;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
