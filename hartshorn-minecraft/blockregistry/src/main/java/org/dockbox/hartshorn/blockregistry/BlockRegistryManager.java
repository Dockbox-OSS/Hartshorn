package org.dockbox.hartshorn.blockregistry;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.Bean;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.persistence.registry.Registry;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NonNls;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlockRegistryManager
{
    private final Map<String, String> aliasToRootMappings  = HartshornUtils.emptyMap();
    private final Map<String, String> rootToFamilyMappings = HartshornUtils.emptyMap();

    private final Registry<Registry<String>> blockRegistry;

    protected BlockRegistryManager() {
        this.blockRegistry = new Registry<>();
    }

    /**
     * @return A singleton instance of the {@link BlockRegistryManager}
     */
    @Bean("singleton")
    private BlockRegistryManager getSingleton() {
        return new BlockRegistryManager();
    }

    /**
     * Determines if the specified id is an alias.
     *
     * @param alias
     *      The id to check
     *
     * @return If the specific is an alias
     */
    public boolean isAlias(String alias) {
        return this.aliasToRootMappings.containsKey(alias);
    }

    /**
     * Determines if the specified id is a root id (The id that is assigned by CR, rather than an alias)
     *
     * @param rootId
     *      The id to check
     *
     * @return If the specified id is a root id
     */
    public boolean isRootId(String rootId) {
        return this.rootToFamilyMappings.containsKey(rootId);
    }

    /**
     * Determines if the specified id is a family id (The fullblock variant for a block type).
     *
     * @param familyId
     *      The id to check
     *
     * @return If the specified id is a family id
     */
    public boolean isFamilyId(String familyId) {
        return this.blockRegistry.containsKey(familyId);
    }

    /**
     * Retrieves the root id of the specified id, if it exists.
     *
     * @param id
     *      The id to get the root id for
     *
     * @return An {@link Exceptional} containing the root id
     */
    public Exceptional<String> getRootId(String id) {
        if (this.isRootId(id))
            return Exceptional.of(id);

        if (this.aliasToRootMappings.containsKey(id))
            return Exceptional.of(this.aliasToRootMappings.get(id));

        return Exceptional.empty();
    }

    /**
     * Retrieves the family id of the specified id, if it exists.
     *
     * @param id
     *      The id to get the family id for
     *
     * @return An {@link Exceptional} containing the family id
     */
    public Exceptional<String> getFamilyId(String id) {
        if (this.isFamilyId(id))
            return Exceptional.of(id);

        return this.getRootId(id)
            .map(rootID -> this.isFamilyId(rootID) ? rootID : this.rootToFamilyMappings.get(rootID));
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

    /**
     * Retrieves a {@link Registry} containing the root ids of the variants of the block family specified by the
     * passed in id mapped to {@link VariantIdentifier variant identifiers}.
     *
     * @param id
     *      The id of any block in the family you want to retrieve the variants for
     *
     * @return A {@link Registry} mapping the root ids of the variants to {@link VariantIdentifier variant identifiers}
     */
    public Registry<String> getVariants(String id) {
        return this.getFamilyId(id)
            .flatMap(familyId ->
                this.blockRegistry.getOrEmpty(familyId)
                    .first())
            .or(new Registry<>());
    }

    /**
     * Maps the alias to the specified root id and automatically registers it within Hartshorn. If the alias already
     * exists and is not mapped to the specified root id then an {@link IllegalArgumentException} will be thrown.
     *
     * @param alias
     *      The alias to add
     * @param rootId
     *      The root id to map the alias to
     */
    public void addAlias(String alias, @NonNls String rootId) {
        String currentRoot = this.aliasToRootMappings.putIfAbsent(alias, rootId);

        if (null != currentRoot && !currentRoot.equals(rootId))
            throw new IllegalArgumentException(
                String.format("The alias %s has already been used for the root %s", alias, currentRoot));

        MinecraftItems.getInstance().registerCustom(alias, () -> Item.of(rootId));
    }

    /**
     * Maps the root id to the specified family id. If the root id already exists and is not mapped to the specified
     * family id then an {@link IllegalArgumentException} will be thrown.
     *
     * @param rootId
     *      The root id to add
     * @param familyId
     *      The family id to map the root id to
     */
    public void addRoot(@NonNls String rootId, @NonNls String familyId) {
        String currentFamily = this.rootToFamilyMappings.putIfAbsent(rootId, familyId);

        if (null != currentFamily && !currentFamily.equals(familyId))
            throw new IllegalArgumentException(
                String.format("The root %s has already been mapped to the family %s", rootId, currentFamily));
    }

    /**
     * Adds the variant to the block registry.
     *
     * @param familyId
     *      The family id of the variant
     * @param variant
     *      The {@link VariantIdentifier} for the variant
     * @param rootId
     *      The root id of the variant
     */
    public void addVariant(String familyId, VariantIdentifier variant, String rootId) {
        this.blockRegistry.getColumnOrCreate(familyId)
            .first()
            .present(r -> r.addData(variant, rootId));
    }
}
