/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.view;

import java.util.Set;

import org.dockbox.hartshorn.util.introspect.view.wildcard.WildcardTypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents a view of a type parameter, whether it is a {@link #isWildcard() wildcard}, {@link #isClass()
 * concrete type}, or {@link #isVariable() type variable}. This view can be used to introspect various
 * properties of the type parameter, such as its {@link #name() name}, {@link #upperBounds() bounds}, and
 * whether it represents a {@link #isClass() class} or {@link #isInterface() interface}.
 *
 * <p>Type parameters are divided into two groups: {@link #isInputParameter() input parameters} and {@link
 * #isOutputParameter() output parameters}. Input parameters are those that are {@link #declaredBy() declared
 * on-} and {@link #consumedBy() consumed by} the type itself, while output parameters are those that are
 * declared on the type, but consumed by its parents.
 *
 * <p>For example, given the following type declaration:
 *
 * <pre><code>List&lt;E<sub>1</sub>&gt; extends Collection&lt;E<sub>2</sub>&gt;</code></pre>
 *
 * <p>The type parameter <code>E<sub>1</sub></code> is an input parameter, while <code>E<sub>2</sub></code>
 * is an output parameter. The type parameter <code>E<sub>1</sub></code> is declared on the type <code>List</code>,
 * and consumed by the type <code>List</code>. The type parameter <code>E<sub>2</sub></code> is declared on the
 * type <code>List</code>, but is consumed by the type <code>Collection</code>.
 *
 * <p>For all input type parameters that are {@link #consumedBy() consumed}, the {@link #represents()} method will
 * return a set of output type parameters which represent the counterparts of the input type parameters. For example,
 * given the example above, the {@link #represents()} method of <code>E<sub>1</sub></code> will return a set containing
 * <code>E<sub>2</sub></code>.
 *
 * <p>For all output parameters which do not have a {@link #resolvedType() resolved type}, the {@link #definition()}
 * method will return the input type parameter which defines the output type parameter. For example, given the
 * example above, the {@link #definition()} method of <code>E<sub>2</sub></code> will return a type parameter
 * representing <code>E<sub>1</sub></code>.
 *
 * <p>Type parameters may be bounded, unbounded, or concrete. A type parameter is bounded if it has an upper bound
 * that is not {@link Object}. An example of a bounded type parameter is {@code List<T extends CharSequence>}. Here
 * the type parameter {@code T} is bounded, as it has an upper bound of {@link CharSequence}. A type parameter is
 * unbounded if it has no upper bound. An example of this is {@code List<T>}. Here the type parameter {@code T} is
 * unbounded, as it has no upper bound. An example of a concrete type parameter is {@code List<String>}. Here the
 * type parameter {@code String} is concrete, as it represents the type {@link String}.
 *
 * <p>Note that a bounded type parameter may be either a {@link #isVariable() type variable} or a {@link #isWildcard()}
 * wildcard}, for example {@code List<? extends CharSequence>} and {@code List<T extends CharSequence>}. In both cases,
 * the type parameter is bounded, as it has an upper bound that is not {@link Object}. However, in the first case, the
 * type parameter is a wildcard, while in the second case, the type parameter is a variable.
 *
 * <p>Annotations on type parameters are accessible, as long as they are directly on the parameter itself, and not on
 * upperbounds. For example, given the following type declaration: {@code List<@NotNull T extends CharSequence>}, the
 * type parameter <code>T</code> has an annotation of <code>@NotNull</code>, which can be accessed through the
 * {@link #annotations()} method. Given the following type declaration however: {@code List<T extends @NotNull CharSequence>},
 * the type parameter <code>T</code> has no annotations, as the annotation is declared on the upper bound, and not on
 * the type parameter itself.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface TypeParameterView extends AnnotatedElementView {

    /**
     * Returns the index of the type parameter. This is the index of the type parameter in the type's
     * type parameter list. For example, given the following type declaration:
     *
     * <pre><code>List&lt;T, U&gt;</code></pre>
     *
     * <p>The type parameter <code>T</code> has an index of 0, while the type parameter <code>U</code>
     * has an index of 1.
     *
     * @return the index of the type parameter
     */
    int index();

    /**
     * Returns {@code true} if this type parameter is an input parameter, or {@code false} otherwise.
     * An input parameter is a type parameter that is declared on- and consumed by the type itself.
     * For example, given the following type declaration:
     *
     * <pre><code>List&lt;E<sub>1</sub>&gt; extends Collection&lt;E<sub>2</sub>&gt;</code></pre>
     *
     * <p>The type parameter <code>E<sub>1</sub></code> is an input parameter, as it is declared on the
     * type <code>List</code>, and consumed by the type <code>List</code>.
     *
     * @return {@code true} if this type parameter is an input parameter, or {@code false} otherwise
     */
    boolean isInputParameter();

    /**
     * Returns {@code true} if this type parameter is an output parameter, or {@code false} otherwise.
     * An output parameter is a type parameter that is declared on the type, but consumed by its parents.
     * For example, given the following type declaration:
     *
     * <pre><code>List&lt;E<sub>1</sub>&gt; extends Collection&lt;E<sub>2</sub>&gt;</code></pre>
     *
     * <p>The type parameter <code>E<sub>2</sub></code> is an output parameter, as it is declared on the
     * type <code>List</code>, but consumed by the type <code>Collection</code>.
     *
     * @return {@code true} if this type parameter is an output parameter, or {@code false} otherwise
     */
    boolean isOutputParameter();

    /**
     * Returns the type that declares this type parameter. This is the type that declares the type
     * parameter. It is not required nor guaranteed that the declaring type is also the consuming
     * type. For example, given the following type declaration:
     *
     * <pre><code>List&lt;E&gt; extends Collection&lt;E&gt;</code></pre>
     *
     * <p>The type parameter <code>E</code> is declared by the type <code>List</code>.
     *
     * @return the type that declares this type parameter
     */
    TypeView<?> declaredBy();

    /**
     * Returns the type that consumes this type parameter. This is the type that consumes the type
     * parameter. It is not required nor guaranteed that the consuming type is also the declaring
     * type. For example, given the following type declaration:
     *
     * <pre><code>List&lt;E&gt; extends Collection&lt;E&gt;</code></pre>
     *
     * <p>The type parameter <code>E</code> is consumed by the type <code>Collection</code>.
     *
     * @return the type that consumes this type parameter
     */
    TypeView<?> consumedBy();

    /**
     * Returns the input type parameter that defines this output type parameter. This is the type parameter
     * that is declared on the type. For example, given the following type declaration:
     *
     * <pre><code>List&lt;E<sub>1</sub>&gt; extends Collection&lt;E<sub>2</sub>&gt;</code></pre>
     *
     * <p>Given that the current type parameter is <code>E<sub>2</sub></code>, the definition of this type
     * parameter is <code>E<sub>1</sub></code>.
     *
     * <p>If this type parameter is an input type parameter, this method will return an empty option.
     *
     * @return the input type parameter that defines this output type parameter
     */
    Option<TypeParameterView> definition();

    /**
     * Returns the set of output type parameters that this input type parameter represents. This is the set
     * of type parameters that are declared on the type, but consumed by its parents. For example, given the
     * following type declaration:
     *
     * <pre><code>List&lt;E<sub>1</sub>&gt; extends Collection&lt;E<sub>2</sub>&gt;, Iterable&lt;E<sub>3</sub>&gt;</code></pre>
     *
     * <p>Given that the current type parameter is <code>E<sub>1</sub></code>, the set of type parameters that
     * this input type parameter represents is <code>[E<sub>2</sub>, E<sub>3</sub>]</code>.
     *
     * <p>If this type parameter is an output type parameter, this method will return an empty set.
     *
     * @return the set of output type parameters that this input type parameter represents
     */
    Set<TypeParameterView> represents();

    /**
     * Returns the set of upper bounds of this type parameter. This is the set of types that this type parameter
     * extends. For example, given the following type declaration:
     *
     * <pre><code>List&lt;? extends CharSequence &amp; Iterable&lt;?&gt;&gt;</code></pre>
     *
     * <p>The set of upper bounds of the type parameter {@code ?} is <code>[CharSequence, Iterable&lt;?&gt;]</code>
     *
     * <p>If this type parameter is unbounded, this method will return an empty set.
     *
     * @return the set of upper bounds of this type parameter
     */
    Set<TypeView<?>> upperBounds();

    /**
     * Returns the concrete type that this type parameter represents. This may be a raw-, parameterized- or
     * wildcard type. For example, given the following type declaration:
     *
     * <pre><code>List&lt;String&gt;</code></pre>
     *
     * <p>The type parameter {@code String} is a concrete type, as it represents the type {@link String}. Note
     * that if a type parameter is a wildcard with upper bounds, it is returned as a non-parameterized wildcard
     * type. In such cases {@link #upperBounds()} should be used to introspect the upper bounds of the wildcard.
     * For example, given the following type declaration:
     *
     * <pre><code>List&lt;? extends CharSequence&gt;</code></pre>
     *
     * <p>The type parameter {@code ?} is a wildcard type, and will only return a 'raw' {@link WildcardTypeView}.
     *
     * @return the concrete type that this type parameter represents
     */
    Option<TypeView<?>> resolvedType();

    /**
     * Returns {@code true} if this type parameter is bounded, or {@code false} otherwise. A type parameter
     * is bounded if it has an upper bound that is not {@link Object}. This is only applicable to type variables
     * and wildcards. For example, {@code T extends CharSequence} is bounded, but {@code T} is not.
     *
     * @return {@code true} if this type parameter is bounded, or {@code false} otherwise
     */
    boolean isBounded();

    /**
     * Returns {@code true} if this type parameter is unbounded, or {@code false} otherwise. A type parameter
     * is unbounded if it has no upper bound. This is only applicable to type variables and wildcards. Any other
     * type will default to {@code true}. For example, {@code T} is unbounded, but {@code T extends CharSequence}
     * is not.
     *
     * @return {@code true} if this type parameter is unbounded, or {@code false} otherwise
     */
    boolean isUnbounded();

    /**
     * Returns {@code true} if this type parameter is a concrete type that is not a wildcard, or {@code false}
     * otherwise. A type is considered concrete it {@link #resolvedType()} returns a non-empty non-wildcard option.
     *
     * @return {@code true} if this type parameter is a concrete type that is not a wildcard, or {@code false}
     */
    boolean isClass();

    /**
     * Returns {@code true} if this type parameter is a concrete type that is an interface, or {@code false}
     * otherwise. A type is considered concrete it {@link #resolvedType()} returns a non-empty non-wildcard option.
     *
     * @return {@code true} if this type parameter is a concrete type that is an interface, or {@code false}
     */
    boolean isInterface();

    /**
     * Returns {@code true} if this type parameter is a concrete type that is an enum, or {@code false}
     * otherwise. A type is considered concrete it {@link #resolvedType()} returns a non-empty non-wildcard option.
     *
     * @return {@code true} if this type parameter is a concrete type that is an enum, or {@code false}
     */
    boolean isEnum();

    /**
     * Returns {@code true} if this type parameter is a concrete type that is an annotation, or {@code false}
     * otherwise. A type is considered concrete it {@link #resolvedType()} returns a non-empty non-wildcard option.
     *
     * @return {@code true} if this type parameter is a concrete type that is an annotation, or {@code false}
     */
    boolean isAnnotation();

    /**
     * Returns {@code true} if this type parameter is a concrete type that is a record, or {@code false}
     * otherwise. A type is considered concrete it {@link #resolvedType()} returns a non-empty non-wildcard option.
     *
     * @return {@code true} if this type parameter is a concrete type that is a record, or {@code false}
     */
    boolean isRecord();

    /**
     * Returns {@code true} if this type parameter is type variable, or {@code false} otherwise.
     *
     * @return {@code true} if this type parameter is type variable, or {@code false} otherwise
     */
    boolean isVariable();

    /**
     * Returns {@code true} if this type parameter is a wildcard, or {@code false} otherwise.
     *
     * @return {@code true} if this type parameter is a wildcard, or {@code false} otherwise
     */
    boolean isWildcard();

    /**
     * Resolves the current output type parameter to an input type parameter on its consumer. Unlike {@link #definition()},
     * which resolves the input parameter on the declaring type, this method resolves the input parameter on the
     * consuming type. For example, given the following type declaration:
     *
     * <pre><code>List&lt;E<sub>1</sub>&gt; extends Collection&lt;E<sub>2</sub>&gt;</code></pre>
     *
     * <p>Given that the current type parameter is <code>E<sub>2</sub></code>, the resolved input type parameter
     * is {@code E} on {@code Collection}, and not <code>E<sub>1</sub></code> on {@code List}.
     *
     * <p>If this type parameter is an input type parameter, this method will return the current type parameter.
     *
     * @return the resolved input type parameter
     */
    TypeParameterView asInputParameter();
}
