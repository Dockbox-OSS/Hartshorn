package com.darwinreforged.server.mcp.entities;

import com.darwinreforged.server.mcp.reference.ForgeWorld;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Entities {

    public static abstract class AbstractEntity<T extends Entity> {
        protected T t;
        public T get() {
            return t;
        }
    }

    public static class LightningBolt extends AbstractEntity<EntityLightningBolt> {

        public LightningBolt(ForgeWorld forgeWorld, double x, double y, double z, boolean effectOnlyIn) {
            this.t = new EntityLightningBolt(forgeWorld.getWorld(), x, y, z, effectOnlyIn);
        }

    }

    public static class Player {

        public static List<UUID> getPlayerUUIDList() {
            return getPlayerList().map(playerList -> Arrays.stream(playerList.getOnlinePlayerProfiles())
                    .map(GameProfile::getId)
                    .collect(Collectors.toList())).orElseGet(ArrayList::new);
        }

        public static Optional<PlayerList> getPlayerList() {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server == null) return Optional.empty();
            else return Optional.of(server.getPlayerList());
        }

        public static EntityPlayerMP getPlayer(UUID uuid) {
            return getPlayerList().map(list -> list.getPlayerByUUID(uuid)).orElse(null);
        }

        @SuppressWarnings("unchecked")
        public static Map<String, String> getModList(UUID uuid) {
            try {
                EntityPlayerMP pmp = getPlayer(uuid);
                NetHandlerPlayServer connection = (NetHandlerPlayServer) pmp.getClass().getField("field_71135_a").get(pmp);
                NetworkManager nm = (NetworkManager) connection.getClass().getField("field_147371_a").get(connection);
                NetworkDispatcher np = NetworkDispatcher.get(nm);

                // In old versions of Forge modList by default its null, then when you use the normal np.getModList() it try to translate a normal Map into a unmodifiableMap
                // It obviously throws a null pointer exception, and you can't catch it
                // Then we check the native Map to see it that player is playing in vanilla
                // You can see that in newest version of Forge they changed that to a empty collection
                // Check this: https://github.com/MinecraftForge/MinecraftForge/blob/1.12.x/src/main/java/net/minecraftforge/fml/common/network/handshake/NetworkDispatcher.java#L113
                // This cause a ton of problems to ModBanner... and a lot of time to found the problem
                Class<?> npClass = np.getClass();

                // Now to avoid problems we use the normal method
                java.lang.reflect.Field modListF = npClass.getField("modList");
                modListF.setAccessible(true);
                return (Map<String, String>) modListF.get(np);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
                return new HashMap<>();
            }
        }

    }
}
