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

package org.dockbox.hartshorn.web.spec;

import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class PathSpec {

    private final List<PathPartSpec> parts;
    private final String pattern;
    private final char pathSeparator;
    private final String pathSeparatorPattern;

    public PathSpec(
            List<PathPartSpec> parts,
            char pathSeparator
    ) {
        this.parts = parts;
        this.pattern = parts.stream()
                .map(PathPartSpec::stringValue)
                .collect(Collectors.joining(String.valueOf(pathSeparator)));
        this.pathSeparator = pathSeparator;
        this.pathSeparatorPattern = Pattern.quote(String.valueOf(this.pathSeparator));
    }

    public String pattern() {
        return this.pattern;
    }

    public List<PathPartSpec> parts() {
        return this.parts;
    }

    public char pathSeparator() {
        return pathSeparator;
    }

    public boolean matches(String path, Map<String, String> pathParameters) {
        String[] parts = StringUtilities.trimWith(
                this.pathSeparator,
                path
        ).split(this.pathSeparatorPattern);
        if (parts.length != this.parts.size()) {
            return false;
        }
        for (int i = 0; i < parts.length; i++) {
            PathPartSpec partSpec = this.parts().get(i);
            if (!partSpec.matches(parts[i], pathParameters)) {
                return false;
            }
        }
        return true;
    }

    public PathSpec combineWith(PathSpec other) {
        if (other.pathSeparator != this.pathSeparator) {
            throw new IllegalArgumentException(
                    "Cannot merge PathSpecs with different path separators: %s and %s"
                            .formatted(this.pathSeparator, other.pathSeparator)
            );
        }
        List<PathPartSpec> parts = CollectionUtilities.mergeList(
                this.parts(),
                other.parts()
        );
        return new PathSpec(parts, this.pathSeparator);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PathSpec) obj;
        return Objects.equals(this.pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern);
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("pattern", this.pattern)
                .field("parts", this.parts)
                .describe();
    }

}
