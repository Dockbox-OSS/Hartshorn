/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web.spec.parser;

import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.spec.PathPartSpec;
import org.dockbox.hartshorn.web.spec.PathSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SimplePathParser implements PathParser {

    private final List<PathPartSpecParser> partParsers = new ArrayList<>();
    private final char pathSeparator;
    private final String pathSeparatorPattern;

    public SimplePathParser(char pathSeparator, List<PathPartSpecParser> parsers) {
        this.partParsers.addAll(parsers);
        this.pathSeparator = pathSeparator;
        this.pathSeparatorPattern = Pattern.quote(String.valueOf(this.pathSeparator));
    }

    @Override
    public PathSpec parse(String pattern) {
        String[] parts = StringUtilities.trimWith(
                this.pathSeparator,
                pattern
        ).split(pathSeparatorPattern);
        List<PathPartSpec> specs = new ArrayList<>();
        for (String part : parts) {
            Option<? extends PathPartSpec> spec = this.parsePart(part);
            if (spec.absent()) {
                throw new IllegalArgumentException("Invalid path part: " + part);
            }
            specs.add(spec.get());
        }
        return new PathSpec(specs, pathSeparator);
    }

    protected Option<? extends PathPartSpec> parsePart(String part) {
        for (PathPartSpecParser parser : partParsers) {
            Option<? extends PathPartSpec> parsedPart = parser.parse(part);
            if (parsedPart.present()) {
                return parsedPart;
            }
        }
        return Option.empty();
    }
}
