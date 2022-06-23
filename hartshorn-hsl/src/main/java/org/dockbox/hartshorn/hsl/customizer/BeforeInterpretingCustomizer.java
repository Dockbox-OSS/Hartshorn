package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;

import java.util.List;

public interface BeforeInterpretingCustomizer extends HslCustomizer {
    List<Statement> customize(List<Statement> statements, Interpreter interpreter);
}
