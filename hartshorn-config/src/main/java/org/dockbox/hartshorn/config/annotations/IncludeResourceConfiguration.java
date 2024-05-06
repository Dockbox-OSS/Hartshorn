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

package org.dockbox.hartshorn.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.config.ConfigurationServicePreProcessor;
import org.dockbox.hartshorn.config.FileFormats;
import org.dockbox.hartshorn.config.ObjectMapper;
import org.dockbox.hartshorn.util.resources.ClassPathResourceLookupStrategy;
import org.dockbox.hartshorn.util.resources.ResourceLookupStrategy;

/**
 * Declaration for a configuration file that should be included in the resource configuration, if it
 * exists. This supports files with any registered {@link ResourceLookupStrategy}. For example a
 * {@link ClassPathResourceLookupStrategy} will accept a source formatted as {@code classpath:filename}.
 *
 * <p>The {@link #value()} does not have to include the file extension, the file format is automatically
 * adjusted based on available files if no explicit file extension is provided.
 *
 * <p>Assuming there is a file called {@code demo.yml} on the classpath, the following will adapt to
 * {@link FileFormats#YAML}.
 * <pre>{@code
 * @Component
 * @Configuration("classpath:demo")
 * public class SampleClassPathConfiguration {
 *    @Value("sample.value")
 *    private final String value = "default value if key does not exist";
 * }
 * }</pre>
 *
 * @see Value
 * @see ObjectMapper
 * @see ResourceLookupStrategy
 * @see ConfigurationServicePreProcessor
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IncludeResourceConfiguration {
    String[] value();
    boolean failOnMissing() default false;
}
