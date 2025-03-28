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

package org.dockbox.hartshorn.reporting;

/**
 * A type that can be reported on. This is used to provide diagnostics information about a type.
 *
 * <p>Implementations are expected to provide information about the type itself, as well as any
 * information about the type's dependencies. Note that the information provided by this interface
 * is not expected to be exhaustive, but rather to provide a high-level overview of the type.
 *
 * <p>It is not required that implementations of this interface be thread-safe, nor is it required
 * that they be immutable. However, implementations are expected to be side-effect free.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface Reportable {

    /**
     * Reports information about this type to the given {@link DiagnosticsPropertyCollector}.
     *
     * @param collector the collector to report to
     */
    void report(DiagnosticsPropertyCollector collector);
}
