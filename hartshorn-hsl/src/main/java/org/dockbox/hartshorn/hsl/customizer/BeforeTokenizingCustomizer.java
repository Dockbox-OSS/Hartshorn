package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.lexer.HslLexer;

public interface BeforeTokenizingCustomizer extends HslCustomizer {
    String customize(String source, HslLexer lexer);
}
