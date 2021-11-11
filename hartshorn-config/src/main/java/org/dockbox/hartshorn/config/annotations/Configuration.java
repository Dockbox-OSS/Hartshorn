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

package org.dockbox.hartshorn.config.annotations;

import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.core.annotations.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Component type to specify a source for a configuration file. This supports files with any registered
 * {@link org.dockbox.hartshorn.config.ResourceLookupStrategy}. For example a {@link org.dockbox.hartshorn.config.ClassPathResourceLookupStrategy}
 * will accept a source formatted as {@code classpath:filename}.
 *
 * <p>The {@link #source()} should not contain the file extension, this is automatically formatted based on the
 * {@link #filetype()}. The {@link FileType} is also used to configure the underlying {@link org.dockbox.hartshorn.persistence.mapping.ObjectMapper}
 * used to read the configuration file.
 *
 * <p>The example below will target demo.yml as a classpath resource.
 * <pre>{@code
 * @Configuration(source = "classpath:demo")
 * public class SampleClassPathConfiguration {
 *    @Value("sample.value")
 *    private final String value = "default value if key does not exist";
 * }
 * }</pre>
 *
 * @see Value
 * @see org.dockbox.hartshorn.persistence.mapping.ObjectMapper
 * @see org.dockbox.hartshorn.config.ResourceLookupStrategy
 * @see org.dockbox.hartshorn.config.ConfigurationServiceProcessor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Service.class)
public @interface Configuration {
    String source();
    Class<?> owner() default Hartshorn.class;
    FileType filetype() default FileType.YAML;
}
