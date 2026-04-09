package org.dockbox.hartshorn.web.spec.parser;

import java.util.ArrayList;
import java.util.List;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.spec.PathPartSpec;
import org.dockbox.hartshorn.web.spec.PathSpec;

public class SimplePathParser implements PathParser {

    private final List<PathPartSpecParser> partParsers = new ArrayList<>();
    private final char pathSeparator;

    protected SimplePathParser(char pathSeparator, List<PathPartSpecParser> parsers) {
        this.partParsers.addAll(parsers);
        this.pathSeparator = pathSeparator;
    }

    @Override
    public PathSpec parse(String pattern) {
        String[] parts = StringUtilities.trimWith(this.pathSeparator, pattern).split("/");
        List<PathPartSpec> specs = new ArrayList<>();
        for (String part : parts) {
            Option<PathPartSpec> spec = this.parsePart(part);
            if (spec.absent()) {
                throw new IllegalArgumentException("Invalid path part: " + part);
            }
            specs.add(spec.get());
        }
        return new PathSpec(specs);
    }

    protected Option<PathPartSpec> parsePart(String part) {
        
    }
}
