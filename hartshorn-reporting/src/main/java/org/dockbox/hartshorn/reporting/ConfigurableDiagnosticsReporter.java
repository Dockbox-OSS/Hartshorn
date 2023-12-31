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

package org.dockbox.hartshorn.reporting;

/**
 * A reporter that can be configured using a configuration object. This configuration may be
 * mutable or immutable, depending on the implementation.
 *
 * @param <C> the type of the configuration object
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ConfigurableDiagnosticsReporter<C> extends Reportable {

    /**
     * Returns the configuration object of this reporter.
     *
     * @return the configuration object of this reporter
     */
    C configuration();

}
