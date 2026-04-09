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
