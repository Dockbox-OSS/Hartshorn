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

package org.dockbox.hartshorn.inject.annotations.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.dockbox.hartshorn.inject.binding.AliasBindingFunction;
import org.dockbox.hartshorn.inject.binding.AliasCapableBinder;
import org.dockbox.hartshorn.inject.binding.AliasableBindingHierarchy;
import org.dockbox.hartshorn.inject.provider.AliasCapableComponentProviderOrchestrator;

/**
 * An annotation that can be used to define aliases for a binding. This allows multiple keys to be
 * used to resolve the same provider. This can be useful when a provider can be reused for multiple
 * logical keys that are not inherently related.
 *
 * <p>For example, consider a provider that returns a {@link String}. This provider could be aliased
 * to also provide
 * {@link CharSequence}.
 *
 * <p><pre>{@code
 * @Singleton
 * @BindingAlias(CharSequence.class)
 * public String messageToTheWorld() {
 *     return "Hello, World!";
 * }
 * }</pre>
 *
 * <p>When resolving a provider, the primary key will always take precedence over the aliases. If an
 * alias overlaps
 * with a primary key, the primary key will be used. Aliases are not allowed to overlap with each
 * other, unless they have different priorities.
 *
 * <p>Note that this annotation is only useful when used in combination with an application that is
 * configured to
 * support aliases. Typically, this means that the application is using an
 * {@link AliasCapableBinder} and an {@link AliasCapableComponentProviderOrchestrator}.
 *
 * @author Guus Lieben
 * @see AliasableBindingHierarchy
 * @see AliasCapableBinder
 * @see AliasBindingFunction#alias(Class)
 * @since 0.7.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface BindingAlias {

    /**
     * The types that should be aliased to the annotated provider.
     *
     * @return the types that should be aliased to the annotated provider
     */
    Class<?>[] value();
}
