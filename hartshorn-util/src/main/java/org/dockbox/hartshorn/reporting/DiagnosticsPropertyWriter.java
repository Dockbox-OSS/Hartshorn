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

public interface DiagnosticsPropertyWriter {

    DiagnosticsReportCollector write(String value);

    DiagnosticsReportCollector write(int value);

    DiagnosticsReportCollector write(long value);

    DiagnosticsReportCollector write(float value);

    DiagnosticsReportCollector write(double value);

    DiagnosticsReportCollector write(boolean value);

    DiagnosticsReportCollector write(Reportable reportable);

    DiagnosticsReportCollector write(String... values);

    DiagnosticsReportCollector write(int... values);

    DiagnosticsReportCollector write(long... values);

    DiagnosticsReportCollector write(float... values);

    DiagnosticsReportCollector write(double... values);

    DiagnosticsReportCollector write(boolean... values);

    DiagnosticsReportCollector write(Reportable... reportables);

}
