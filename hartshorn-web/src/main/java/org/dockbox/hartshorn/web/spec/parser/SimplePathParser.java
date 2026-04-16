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
