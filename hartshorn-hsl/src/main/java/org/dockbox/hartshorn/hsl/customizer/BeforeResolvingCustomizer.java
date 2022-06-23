package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.callable.NativeModule;
import org.dockbox.hartshorn.hsl.semantic.Resolver;

import java.util.List;
import java.util.Map;

public interface BeforeResolvingCustomizer extends HslCustomizer {
    List<Statement> customize(List<Statement> statements, Resolver resolver, Map<String, NativeModule> modules);
}
