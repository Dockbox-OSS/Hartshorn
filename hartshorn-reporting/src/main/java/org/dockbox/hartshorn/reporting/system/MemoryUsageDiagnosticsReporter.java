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

package org.dockbox.hartshorn.reporting.system;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.function.Function;

public class MemoryUsageDiagnosticsReporter implements Reportable {

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long max = runtime.maxMemory();
        long used = total - free;

        Function<Long, Reportable> byteReporter = bytes -> totalCollector -> {
            totalCollector.property("bytes").write(bytes);
            totalCollector.property("si").write(humanReadableByteCountSI(bytes));
            totalCollector.property("iec").write(humanReadableByteCountIEC(bytes));
        };

        collector.property("total").write(byteReporter.apply(total));
        collector.property("free").write(byteReporter.apply(free));
        collector.property("max").write(byteReporter.apply(max));
        collector.property("used").write(byteReporter.apply(used));
    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator iterator = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            iterator.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, iterator.current());
    }

    public static String humanReadableByteCountIEC(long bytes) {
        long absoluteBytes = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absoluteBytes < 1024) {
            return bytes + " B";
        }
        long value = absoluteBytes;
        CharacterIterator iterator = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absoluteBytes > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            iterator.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, iterator.current());
    }
}
