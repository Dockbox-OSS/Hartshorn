package org.dockbox.hartshorn.web.spec;

import java.util.List;
import java.util.Objects;
import java.util.SequencedCollection;
import java.util.stream.Collectors;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;

public final class PathSpec {

    private final SequencedCollection<PathPartSpec> parts;
    private final String pattern;

    public PathSpec(
            SequencedCollection<PathPartSpec> parts
    ) {
        this.parts = parts;
        this.pattern = parts.stream()
                .map(PathPartSpec::stringValue)
                .collect(Collectors.joining("/"));
    }

    public String pattern() {
        return this.pattern;
    }

    public SequencedCollection<PathPartSpec> parts() {
        return this.parts;
    }

    public PathSpec merge(PathSpec other) {
        List<PathPartSpec> parts = CollectionUtilities.mergeList(
                this.parts(),
                other.parts()
        );
        return new PathSpec(parts);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PathSpec) obj;
        return Objects.equals(this.parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parts);
    }

    @Override
    public String toString() {
        return "PathSpec[" +
                "parts=" + parts + ']';
    }

}
