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

package org.dockbox.hartshorn.core.annotations.inject;

import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that a method will act as a binding provider. The return type of the
 * method, combined with the {@link #value()} form the {@link org.dockbox.hartshorn.core.Key} of
 * the binding.
 *
 * <p>The use of provider methods require the presence of {@link UseServiceProvision} on the activator
 * class.
 *
 * <p>Provider methods can have parameters, which will be injected through the active
 * {@link ApplicationContext}. This includes support for {@link javax.inject.Named} parameters.
 *
 * <p>If {@link javax.inject.Singleton} is used on the provider method, the result of the provider
 * method will be cached immediately, and the same instance will be returned on subsequent calls.
 *
 * @author Guus Lieben
 * @since 21.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Provider {
    String value() default "";
    int priority() default -1;
}
