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

/**
 * Specification for a path pattern, consisting of multiple {@link PathPartSpec} instances. This
 * represents a parsed path pattern, and can be used to match against actual paths and extract path
 * parameters from incoming requests.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
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

    /**
     * Returns an empty {@link PathSpec} with no parts and a default path separator of
     * <code>'/'</code>.
     *
     * @return an empty {@link PathSpec}
     */
    public static PathSpec empty() {
        return empty('/');
    }

    /**
     * Returns an empty {@link PathSpec} with no parts and the specified path separator.
     *
     * @param pathSeparator the character to use as a path separator
     * @return an empty {@link PathSpec} with the specified path separator
     */
    public static PathSpec empty(char pathSeparator) {
        return new PathSpec(List.of(), pathSeparator);
    }

    /**
     * Returns the original path pattern string that this {@link PathSpec} represents. This is
     * constructed from the individual {@link PathPartSpec} instances, and should ideally be
     * equivalent to the original pattern string that was parsed to create this {@link PathSpec}.
     *
     * @return the original path pattern string that this {@link PathSpec} represents
     */
    public String pattern() {
        return this.pattern;
    }

    /**
     * Returns the list of {@link PathPartSpec} instances that make up this {@link PathSpec}. Each
     * {@link PathPartSpec} represents a single part of the path pattern, and can be used to match
     * against individual parts of a request path and extract path parameters.
     *
     * @return the list of {@link PathPartSpec} instances that make up this {@link PathSpec}
     */
    public List<PathPartSpec> parts() {
        return this.parts;
    }

    /**
     * Returns the character used as a separator between parts of the path pattern. This is used to
     * split incoming request paths into parts for matching against the individual
     * {@link PathPartSpec} instances.
     *
     * @return the character used as a separator between parts of the path pattern
     */
    public char pathSeparator() {
        return this.pathSeparator;
    }

    /**
     * Determines whether the given path matches this {@link PathSpec}. This is done by splitting
     * the given path into parts using the path separator, and then matching each part against the
     * corresponding {@link PathPartSpec} instance in this {@link PathSpec}. If all parts match, any
     * path parameters extracted from the path are added to the provided map of path parameters.
     *
     * @param path the path to match against this {@link PathSpec}
     * @param pathParameters a map to which any path parameters extracted from the path should be
     * added if the match is successful
     *
     * @return {@code true} if the given path matches this {@link PathSpec}, or {@code false}
     * otherwise
     */
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

    /**
     * Combines this {@link PathSpec} with another {@link PathSpec} to create a new {@link PathSpec}
     * that represents the combination of both specifications. The given {@link PathSpec} is
     * appended to the current instance.
     *
     * <p>For example, if the current instance represents the pattern <code>"/api"</code> and the
     * given {@link PathSpec} represents the pattern <code>"/users/{id}"</code>, the resulting
     * {@link PathSpec} would represent the combined pattern <code>"/api/users/{id}"</code>.
     *
     * @param other the {@link PathSpec} to combine with this instance
     *
     * @return a new {@link PathSpec} that represents the combination of this instance and the given
     * {@link PathSpec}
     */
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

    /**
     * Combines multiple {@link PathSpec} instances into a single {@link PathSpec} that represents
     * the combination of all the given specifications.
     *
     * @param pathSpecs the {@link PathSpec} instances to combine
     * @return a new {@link PathSpec} that represents the combination of all the given
     * specifications
     *
     * @see #combineWith(PathSpec)
     */
    public static PathSpec combineAll(PathSpec... pathSpecs) {
        if (pathSpecs.length == 0) {
            return PathSpec.empty();
        }
        PathSpec combinedSpec = pathSpecs[0];
        for (int i = 1; i < pathSpecs.length; i++) {
            PathSpec next = pathSpecs[i];
            combinedSpec = combinedSpec.combineWith(next);
        }
        return combinedSpec;
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
