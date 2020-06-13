package org.dockbox.darwin.core.util.module;

import org.dockbox.darwin.core.annotations.Module;
import org.dockbox.darwin.core.exceptions.NoModulePresentException;
import org.dockbox.darwin.core.objects.module.ModuleInformation;
import org.dockbox.darwin.core.objects.module.ModuleRegistration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleModuleLoader implements ModuleLoader {

    private final List<ModuleRegistration> registrations = new CopyOnWriteArrayList<>();

    @NotNull
    @Override
    public <I> I getModuleInstance(@NotNull Class<?> module) {
        Object potentialInstance = getRegistration(module).getInstance();
        if (potentialInstance.getClass().equals(module)) //noinspection unchecked
            return (I) potentialInstance;
        throw new NoModulePresentException(module.getCanonicalName());
    }

    @Override
    public void loadModule(@NotNull Class<?> module) {
        // TODO
    }

    @NotNull
    @Override
    public ModuleInformation getModuleInformation(@NotNull Class<?> module) {
        return getRegistration(module).getInformation();
    }

    @NotNull
    @Override
    public ModuleInformation getModuleInformation(@NotNull String module) {
        return getRegistration(module).getInformation();
    }

    @NotNull
    @Override
    public String getModuleSource(@NotNull Class<?> module) {
        return getRegistration(module).getInformation().getSource();
    }

    @NotNull
    @Override
    public String getModuleSource(@NotNull String module) {
        return getRegistration(module).getInformation().getSource();
    }

    @NotNull
    @Override
    public Module getModule(@NotNull Class<?> module) {
        return getRegistration(module).getInformation().getModule();
    }

    @NotNull
    @Override
    public Module getModule(@NotNull String module) {
        return getRegistration(module).getInformation().getModule();
    }

    @NotNull
    @Override
    public Iterable<ModuleRegistration> getAllRegistrations() {
        return registrations;
    }

    @NotNull
    @Override
    public ModuleRegistration getRegistration(@NotNull Class<?> module) {
        return registrations.stream().filter(registration -> registration.getInstance().getClass().equals(module))
                .findFirst().orElseThrow(() -> new NoModulePresentException(module.getCanonicalName()));
    }

    @NotNull
    @Override
    public ModuleRegistration getRegistration(@NotNull String module) {
        return registrations.stream().filter(registration -> registration.getInformation().getModule().id().equalsIgnoreCase(module))
                .findFirst().orElseThrow(() -> new NoModulePresentException(module));
    }
}
