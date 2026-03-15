/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

/**
 * A class statement, which represents the declaration of a class. A class may have an optional
 * superclass, a constructor, methods, and fields. Classes can also be marked as dynamic, allowing
 * additional properties to be added at runtime.
 *
 * <p>For example, the following class declaration includes the full structure of a class:
 * <pre>{@code
 * class Point {}
 * class Vector? extends Point {
 *   private x;
 *   private y;
 *   constructor(x, y) {
 *     this.x = x;
 *     this.y = y;
 *   }
 *   function magnitude() {
 *     return Math.sqrt(this.x * this.x + this.y * this.y);
 *   }
 * }
 * // Initialize a new Vector instance
 * var vector = Vector(3, 4);
 * vector.z = 5; // Adding a new property dynamically
 * }</pre>
 *
 * <p>In this example, {@code Vector} is the name of the class, which extends the superclass
 * {@code Point}. The class has two private fields, {@code x} and {@code y}, a
 * constructor
 * that initializes these fields, and a public method {@code magnitude} that calculates the
 * magnitude of the vector. As the class name is followed by a question mark, it is marked as
 * dynamic, allowing additional properties to be added at runtime.
 *
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
public class ClassStatement extends FinalizableStatement implements NamedNode {

    private final Token name;
    private final VariableExpression superClass;
    private final ConstructorStatement constructor;
    private final List<FunctionStatement> methods;
    private final List<FieldStatement> fields;
    private final boolean isDynamic;

    public ClassStatement(
        Token name,
        VariableExpression superClass, ConstructorStatement constructor,
        List<FunctionStatement> methods, List<FieldStatement> fields,
        boolean isDynamic
    ) {
        this(name, false, name, superClass, constructor, methods, fields, isDynamic);
    }

    public ClassStatement(
        ASTNode at, boolean finalized, Token name,
        VariableExpression superClass, ConstructorStatement constructor,
        List<FunctionStatement> methods, List<FieldStatement> fields,
        boolean isDynamic
    ) {
        super(at, finalized);
        this.name = name;
        this.superClass = superClass;
        this.constructor = constructor;
        this.methods = methods;
        this.fields = fields;
        this.isDynamic = isDynamic;
    }

    @Override
    public Token name() {
        return this.name;
    }

    /**
     * Returns the superclass of the class, if any.
     *
     * @return the superclass variable expression, or null if there is no superclass
     */
    public VariableExpression superClass() {
        return this.superClass;
    }

    /**
     * Returns the constructor of the class, if any.
     *
     * @return the constructor statement, or null if there is no explicit constructor
     */
    public ConstructorStatement constructor() {
        return this.constructor;
    }

    /**
     * Returns the list of methods defined in the class.
     *
     * @return the list of function statements representing the methods
     */
    public List<FunctionStatement> methods() {
        return this.methods;
    }

    /**
     * Returns the list of fields defined in the class.
     *
     * @return the list of field statements representing the fields
     */
    public List<FieldStatement> fields() {
        return this.fields;
    }

    /**
     * Indicates whether the class is dynamic, allowing additional properties to be added at
     * runtime.
     *
     * @return true if the class is dynamic, false otherwise
     */
    public boolean isDynamic() {
        return this.isDynamic;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
