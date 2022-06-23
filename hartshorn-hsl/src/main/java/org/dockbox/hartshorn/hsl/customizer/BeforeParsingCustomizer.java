package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.parser.Parser;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;

public interface BeforeParsingCustomizer extends HslCustomizer {
    List<Token> customize(List<Token> source, Parser parser);
}
