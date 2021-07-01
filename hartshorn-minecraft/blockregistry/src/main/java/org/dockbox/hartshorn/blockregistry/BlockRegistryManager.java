package org.dockbox.hartshorn.blockregistry;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.persistence.registry.Registry;
import org.dockbox.hartshorn.persistence.registry.RegistryColumn;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NonNls;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockRegistryManager
{
    private final Map<String, String> aliasToRootMappings  = HartshornUtils.emptyMap();
    private final Map<String, String> rootToFamilyMappings = HartshornUtils.emptyMap();

    private final Registry<Registry<String>> blockRegistry;

    public BlockRegistryManager() {
        this.blockRegistry = new Registry<>();
    }

    public Exceptional<String> getRootId(String id) {
        if (this.isRootId(id))
            return Exceptional.of(id);

        if (this.aliasToRootMappings.containsKey(id))
            return Exceptional.of(this.aliasToRootMappings.get(id));

        return Exceptional.empty();
    }

    public Exceptional<String> getFamilyId(String id) {
        if (this.isFamilyId(id))
            return Exceptional.of(id);

        return this.getRootId(id)
            .map(rootID -> this.isFamilyId(rootID) ? rootID : this.rootToFamilyMappings.get(rootID));
    }

    /**
     * If the specified id is a root id (The id that is assigned by CR, rather than an alias)
     *
     * @param id
     *      The id to check
     *
     * @return If the specified id is a root id
     */
    public boolean isRootId(String id) {
        return this.rootToFamilyMappings.containsKey(id);
    }

    /**
     * If the specified id is a family id (The fullblock for a specific texture).
     *
     * @param id
     *      The id to check
     *
     * @return If the specified id is a family id
     */
    public boolean isFamilyId(String id) {
        return this.blockRegistry.containsKey(id);
    }

    /**
     * Retrieves the registered aliases for the specified id (including the root id). If the id is an alias, then it
     * will retrieve the root id first and then return the registered aliases for that.
     *
     * @param id
     *      The id to find the aliases for
     *
     * @return A {@link Set} containing the registered aliases
     */
    public Set<String> getAliases(String id) {
        return this.getRootId(id).map((@NonNls String rootId) -> {
            Set<String> aliases = this.aliasToRootMappings.keySet()
                .stream()
                .filter(alias -> this.aliasToRootMappings.get(alias).equals(rootId))
                .collect(Collectors.toSet());

            aliases.add(rootId);
            return aliases;
        }).or(HartshornUtils.emptySet());


    }

    public Registry<String> getVariants(String id) {
        return this.getFamilyId(id)
            .flatMap(familyId ->
                this.blockRegistry.getOrDefault(familyId, new RegistryColumn<>())
                    .first())
            .or(new Registry<>());

    }
}
