package org.dockbox.selene.palswap.fileparsers;

import org.dockbox.selene.api.util.SeleneUtils;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Map;

@ConfigSerializable
public class ItemData {

    @Setting
    private Map<String, String> itemRegistry = SeleneUtils.emptyConcurrentMap();

    @Setting
    private Map<String, String> blockIdentifierIDs = SeleneUtils.emptyConcurrentMap();

    public static ItemData of(Map<String, String> itemRegistry, Map<String, String> blockIdentifierIDs) {
        ItemData instance = new ItemData();
        instance.itemRegistry = itemRegistry;
        instance.blockIdentifierIDs = blockIdentifierIDs;
        return instance;
    }

    public Map<String, String> getItemRegistry() {
        return this.itemRegistry;
    }

    public Map<String, String> getBlockIdentifierIDs() {
        return this.blockIdentifierIDs;
    }
}
