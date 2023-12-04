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
 * A writer for {@link DiagnosticsPropertyCollector} instances. Each writer is responsible for writing a specific
 * property, configured by {@link DiagnosticsPropertyCollector#property(String)}.
 *
 * @see DiagnosticsPropertyCollector
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface DiagnosticsPropertyWriter {

    /**
     * Writes the given value to the collector.
     *
     * @param value the value to write
     * @return the collector that was used to collect the value
     */
    DiagnosticsReportCollector write(String value);

    /**
     * Writes the given value to the collector.
     *
     * @param value the value to write
     * @return the collector that was used to collect the value
     */
    DiagnosticsReportCollector write(int value);

    /**
     * Writes the given value to the collector.
     *
     * @param value the value to write
     * @return the collector that was used to collect the value
     */
    DiagnosticsReportCollector write(long value);

    /**
     * Writes the given value to the collector.
     *
     * @param value the value to write
     * @return the collector that was used to collect the value
     */
    DiagnosticsReportCollector write(float value);

    /**
     * Writes the given value to the collector.
     *
     * @param value the value to write
     * @return the collector that was used to collect the value
     */
    DiagnosticsReportCollector write(double value);

    /**
     * Writes the given value to the collector.
     *
     * @param value the value to write
     * @return the collector that was used to collect the value
     */
    DiagnosticsReportCollector write(boolean value);

    /**
     * Creates a new group node in the collector, and delegates the writing of
     * properties to the given {@link Reportable}.
     *
     * @param reportable the reportable to write
     * @return the collector that was used to collect the value
     */
    DiagnosticsReportCollector write(Reportable reportable);

    /**
     * Writes the given values to the collector.
     *
     * @param values the values to write
     * @return the collector that was used to collect the values
     */
    DiagnosticsReportCollector write(String... values);

    /**
     * Writes the given values to the collector.
     *
     * @param values the values to write
     * @return the collector that was used to collect the values
     */
    DiagnosticsReportCollector write(int... values);

    /**
     * Writes the given values to the collector.
     *
     * @param values the values to write
     * @return the collector that was used to collect the values
     */
    DiagnosticsReportCollector write(long... values);

    /**
     * Writes the given values to the collector.
     *
     * @param values the values to write
     * @return the collector that was used to collect the values
     */
    DiagnosticsReportCollector write(float... values);

    /**
     * Writes the given values to the collector.
     *
     * @param values the values to write
     * @return the collector that was used to collect the values
     */
    DiagnosticsReportCollector write(double... values);

    /**
     * Writes the given values to the collector.
     *
     * @param values the values to write
     * @return the collector that was used to collect the values
     */
    DiagnosticsReportCollector write(boolean... values);

    /**
     * Creates a new array node in the collector, and delegates the writing of
     * properties to the given {@link Reportable}s.
     *
     * @param reportables the reportables to write
     * @return the collector that was used to collect the values
     */
    DiagnosticsReportCollector write(Reportable... reportables);

}
