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

package org.dockbox.hartshorn.reporting.system;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.DiagnosticsReporter;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class MemoryUsageDiagnosticsReporter implements DiagnosticsReporter {

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        final Runtime runtime = Runtime.getRuntime();
        final long total = runtime.totalMemory();
        final long free = runtime.freeMemory();
        final long max = runtime.maxMemory();
        final long used = total - free;

        collector.property("total").write(humanReadableByteCountSI(total));
        collector.property("free").write(humanReadableByteCountSI(free));
        collector.property("max").write(humanReadableByteCountSI(max));
        collector.property("used").write(humanReadableByteCountSI(used));
    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        final CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}
