package org.dockbox.hartshorn.hsl.interpreter;

import java.util.Map;
import java.util.Set;
import org.dockbox.hartshorn.hsl.objects.external.ExternalClass;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public interface ExternalClassRegistry {

    Set<ExternalClass<?>> defineClasses(Map<String, TypeView<?>> imports);

    <T> ExternalClass<T> defineClass(TypeView<T> typeView);

    <T> ExternalClass<T> defineClass(TypeView<T> typeView, String as);

    boolean removeImported(String name);

    boolean removeImported(Class<?> type);

    boolean containsClassName(String name);

    Option<ExternalClass<?>> getByClassNameOrAlias(String name);

    Set<String> getNamesForClass(Class<?> type);

    Map<String, ExternalClass<?>> importedClasses();
}
