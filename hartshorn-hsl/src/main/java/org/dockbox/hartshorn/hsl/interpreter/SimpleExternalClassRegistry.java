package org.dockbox.hartshorn.hsl.interpreter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.dockbox.hartshorn.hsl.objects.external.ExternalClass;
import org.dockbox.hartshorn.util.collections.ArrayListHashBiMultiMap;
import org.dockbox.hartshorn.util.collections.BiMultiMap;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.stream.EntryStream;

public class SimpleExternalClassRegistry implements ExternalClassRegistry {

    private final BiMultiMap<ExternalClass<?>, String> imports = new ArrayListHashBiMultiMap<>();

    @Override
    public Set<ExternalClass<?>> defineClasses(Map<String, TypeView<?>> imports) {
        return imports.entrySet().stream()
            .map(entry -> this.defineClass(entry.getValue(), entry.getKey()))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public <T> ExternalClass<T> defineClass(TypeView<T> typeView) {
        return this.defineClass(typeView, typeView.name());
    }

    @Override
    public <T> ExternalClass<T> defineClass(TypeView<T> typeView, String as) {
        ExternalClass<T> externalClass = new ExternalClass<>(this, typeView, as);
        if (this.imports.containsValue(as)) {
            // TODO: Use a proper exception type
            throw new IllegalStateException("An external class is already imported as '" + as + "'");
        }
        this.imports.put(externalClass, as);
        return externalClass;
    }

    @Override
    public boolean removeImported(String name) {
        return this.imports.removeValue(name) > 0;
    }

    @Override
    public boolean removeImported(Class<?> type) {
        return this.imports.removeIf((externalClass, alias) -> {
            TypeView<?> typeView = externalClass.type();
            return typeView.is(type);
        }) > 0;
    }

    @Override
    public boolean containsClassName(String name) {
        return this.imports.containsValue(name);
    }

    @Override
    public Option<ExternalClass<?>> getByClassNameOrAlias(String name) {
        ExternalClass<?> externalClass = CollectionUtilities.first(this.imports.inverse().get(name));
        return Option.of(externalClass);
    }

    @Override
    public Set<String> getNamesForClass(Class<?> type) {
        return this.imports.stream()
            .filterKeys(externalClass -> externalClass.type().is(type))
            .values()
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public Map<String, ExternalClass<?>> importedClasses() {
        return EntryStream.of(this.imports.inverse())
            .mapValues(CollectionUtilities::first)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
