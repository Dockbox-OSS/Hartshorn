/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.config.annotations;

import org.dockbox.hartshorn.config.ConfigurationServicePreProcessor;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.data.FileFormats;
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
 * {@link #filetype()}. The {@link FileFormats} is also used to configure the underlying {@link org.dockbox.hartshorn.data.mapping.ObjectMapper}
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
 * @see org.dockbox.hartshorn.data.mapping.ObjectMapper
 * @see org.dockbox.hartshorn.config.ResourceLookupStrategy
 * @see ConfigurationServicePreProcessor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Service.class)
public @interface Configuration {
    String source();

    Class<?> owner() default Hartshorn.class;

    FileFormats filetype() default FileFormats.YAML;

    /**
     * @see Service#lazy()
     */
    boolean lazy() default false;
}
