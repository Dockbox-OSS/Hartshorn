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

import org.dockbox.hartshorn.util.describe.ObjectDescriber;

import java.util.Objects;

public final class PathSpecPart {

    private final PathSpecPartType type;
    private final String stringValue;

    private PathSpecPart(
            PathSpecPartType type,
            String stringValue
    ) {
        this.type = type;
        this.stringValue = stringValue;
    }

    public static PathSpecPart ofStatic(String part) {
        return new PathSpecPart(PathSpecPartType.STATIC, part);
    }

    public static PathSpecPart ofWildcard() {
        return new PathSpecPart(PathSpecPartType.WILDCARD, "*");
    }

    public static PathSpecPart ofParameter(String parameterName) {
        return new PathSpecPart(PathSpecPartType.PARAMETER, parameterName);
    }

    public PathSpecPartType type() {
        return this.type;
    }

    public String stringValue() {
        return this.stringValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PathSpecPart) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.stringValue, that.stringValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, stringValue);
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("stringValue", this.stringValue)
                .field("type", this.type)
                .describe();
    }
}
