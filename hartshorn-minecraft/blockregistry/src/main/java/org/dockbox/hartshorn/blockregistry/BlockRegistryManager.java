package org.dockbox.hartshorn.blockregistry;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.blockregistry.models.FamilyModel;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.persistence.mapping.GenericType;
import org.dockbox.hartshorn.persistence.mapping.JacksonObjectMapper;
import org.dockbox.hartshorn.persistence.registry.Registry;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BlockRegistryManager
{

    private final Map<String, Set<String>> rootAliases     = HartshornUtils.emptyMap();
    private final Map<String, String> rootToFamilyMappings = HartshornUtils.emptyMap();

    private final Registry<Registry<String>> blockRegistry;

    public BlockRegistryManager() {
        this.blockRegistry = this.loadBlockRegistry();
    }

    /**
     * Determines if the specified id is a root id (The id that is assigned by CR, rather than an alias)
     *
     * @param rootId
     *      The id to check
     *
     * @return If the specified id is a root id
     */
    public boolean isRootId(@NotNull String rootId) {
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
    public boolean isFamilyId(@NotNull String familyId) {
        return this.blockRegistry.containsKey(familyId);
    }

    /**
     * Retrieves the family id of the specified root id, if it exists.
     *
     * @param rootId
     *      The root id to get the family id for
     *
     * @return An {@link Exceptional} containing the family id
     */
    public Exceptional<String> getFamilyId(@NotNull String rootId) {
        if (this.isFamilyId(rootId))
            return Exceptional.of(rootId);

        return this.isRootId(rootId)
            ? Exceptional.of(this.rootToFamilyMappings.get(rootId))
            : Exceptional.empty();
    }

    /**
     * Retrieves the registered aliases for the specified root id (excluding the root id). If there are no aliases
     * for that root id then an empty set will be returned.
     *
     * @param rootId
     *      The root id to find the aliases for
     *
     * @return A {@link Set} containing the registered aliases
     */
    public Set<String> getAliases(@NotNull String rootId) {
        return this.rootAliases.getOrDefault(rootId, HartshornUtils.emptySet());
    }

    /**
     * Retrieves a {@link Registry} containing the root ids of the variants of the block family specified by the {@link Item}.
     *
     * @param item
     *      The {@link Item} of any block in the family you want to retrieve the variants for
     *
     * @return A {@link Registry} mapping the root ids of the variants to {@link VariantIdentifier variant identifiers}
     */
    public Registry<String> getVariants(@NotNull Item item) {
        return this.getVariants(item.getId());
    }

    /**
     * Retrieves a {@link Registry} containing the root ids of the variants of the block family specified by the id
     *
     * @param rootId
     *      The root id of any block in the family you want to retrieve the variants for
     *
     * @return A {@link Registry} mapping the root ids of the variants to {@link VariantIdentifier variant identifiers}
     */
    public Registry<String> getVariants(@NotNull String rootId) {
        return this.getFamilyId(rootId)
            .flatMap(familyId ->
                this.blockRegistry.getColumnOrCreate(familyId)
                    .first())
            .or(new Registry<>());
    }

    /**
     * Determines if the specified alias has been registered already.
     *
     * @param alias
     *      The alias to check if registered
     *
     * @return If the alias is already registered
     */
    public boolean hasAliasRegistered(@NotNull String alias) {
        return this.rootAliases.values()
            .stream()
            .anyMatch(a -> a.contains(alias));
    }

    /**
     * Maps the alias to the specified root id and automatically registers it within Hartshorn. If the alias already
     * exists then an {@link IllegalArgumentException} will be thrown.
     *
     * @param rootId
     *      The root id to map the alias to
     * @param alias
     *      The alias to add
     */
    public void addAlias(@NotNull @NonNls String rootId, @NotNull String alias) {
        if (this.hasAliasRegistered(alias))
            throw new IllegalArgumentException(
                String.format("The alias %s has already been registered", alias));

        if (this.rootAliases.containsKey(rootId))
            this.rootAliases.get(rootId).add(alias);
        else this.rootAliases.put(rootId, HartshornUtils.asSet(alias));

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
    public void addRoot(@NotNull String rootId, @NotNull @NonNls String familyId) {
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
    public void addVariant(@NotNull String familyId, @NotNull VariantIdentifier variant, @NotNull String rootId) {
        this.blockRegistry.getColumnOrCreate(familyId, new Registry<>())
            .first()
            .present(r -> r.addData(variant, rootId));
    }

    /**
     * Loads the serialised block registry from blockregistry.json. If that file doesn't exist or there was an issue
     * loading the block registry, then an empty {@link Registry} is returned instead.
     *
     * @return The loaded {@link Registry block registry}
     */
    private Registry<Registry<String>> loadBlockRegistry() {
        //Hartshorn.context()
        //            .get(ObjectMapper.class)

        return new JacksonObjectMapper()
            .read("blockregistry.json", new GenericType<Registry<Registry<String>>>() {})
            .or(new Registry<>());
    }

    public void saveBlockRegistry() {
//        Hartshorn.context()
//            .get(ObjectMapper.class)
        new JacksonObjectMapper()
            .write(Path.of("blockregistry.json"), this.blockRegistry);
    }

    private List<FamilyModel> generatePersistentModels() {
        return HartshornUtils.emptyList();
    }

    @Override
    public String toString() {
        return this.blockRegistry.toString();
    }
}
