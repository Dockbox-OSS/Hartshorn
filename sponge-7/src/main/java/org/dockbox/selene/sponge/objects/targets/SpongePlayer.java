/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.objects.targets;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.FawePlayer;
import com.flowpowered.math.vector.Vector3d;

import org.dockbox.selene.core.PlayerStorageService;
import org.dockbox.selene.core.events.EventBus;
import org.dockbox.selene.core.events.chat.SendMessageEvent;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.FieldReferenceHolder;
import org.dockbox.selene.core.objects.inventory.PlayerInventory;
import org.dockbox.selene.core.objects.inventory.Slot;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.keys.PersistentDataKey;
import org.dockbox.selene.core.objects.keys.TransactionResult;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.player.Gamemode;
import org.dockbox.selene.core.objects.player.Hand;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.profile.Profile;
import org.dockbox.selene.core.objects.special.Sounds;
import org.dockbox.selene.core.packets.Packet;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.SeleneInformation;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.pagination.Pagination;
import org.dockbox.selene.nms.packets.NMSPacket;
import org.dockbox.selene.sponge.objects.composite.SpongeComposite;
import org.dockbox.selene.sponge.objects.SpongeProfile;
import org.dockbox.selene.sponge.objects.inventory.SpongePlayerInventory;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.blockray.BlockRay;

import java.util.UUID;

import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;

@SuppressWarnings("ClassWithTooManyMethods")
public class SpongePlayer extends Player implements SpongeComposite {

    private static final double BLOCKRAY_LIMIT = 50d;

    private final FieldReferenceHolder<org.spongepowered.api.entity.living.player.Player> spongePlayer =
            new FieldReferenceHolder<>(Exceptional.of(Sponge.getServer().getPlayer(this.getUniqueId())), player -> {
                if (null == player) return Exceptional.of(Sponge.getServer().getPlayer(this.getUniqueId()));
                else return Exceptional.empty();
            }, org.spongepowered.api.entity.living.player.Player.class);

    public SpongePlayer(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
    }

    @Override
    public boolean isOnline() {
        return this.spongePlayer.referenceExists() && this.spongePlayer.getReference().get().isOnline();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Exceptional<FawePlayer<?>> getFawePlayer() {
        if (this.spongePlayer.referenceExists())
            return Exceptional.of(FaweAPI.wrapPlayer(this.spongePlayer.getReference().get()));
        else return Exceptional.empty();
    }

    @Override
    public void kick(@NotNull Text message) {
        if (this.spongePlayer.referenceExists()) this.spongePlayer.getReference().get().kick();
    }

    @NotNull
    @Override
    public Gamemode getGamemode() {
        if (this.spongePlayer.referenceExists()) {
            GameMode mode = this.spongePlayer.getReference().get().get(Keys.GAME_MODE).orElse(GameModes.NOT_SET);
            return SpongeConversionUtil.fromSponge(mode);
        } else return Gamemode.OTHER;
    }

    @Override
    public void setGamemode(@NotNull Gamemode gamemode) {
        if (this.spongePlayer.referenceExists())
            this.spongePlayer.getReference().get().offer(Keys.GAME_MODE, SpongeConversionUtil.toSponge(gamemode));
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return Selene.provide(PlayerStorageService.class).getLanguagePreference(this.getUniqueId());
    }

    @Override
    public void setLanguage(@NotNull Language lang) {
        Selene.provide(PlayerStorageService.class).setLanguagePreference(this.getUniqueId(), lang);
    }

    @Override
    public Item getItemInHand(Hand hand) {
        switch (hand) {
            case MAIN_HAND:
                return this.getInventory().getSlot(Slot.MAIN_HAND);
            case OFF_HAND:
                return this.getInventory().getSlot(Slot.OFF_HAND);
            default:
                throw new IllegalArgumentException("Unsupported type: " + hand);
        }
    }

    @Override
    public void setItemInHand(Hand hand, Item item) {
        this.spongePlayer.getReference().ifPresent(player -> {
            player.setItemInHand(SpongeConversionUtil.toSponge(hand), SpongeConversionUtil.toSponge(item));
        });
    }

    @Override
    public void play(Sounds sound) {
        this.spongePlayer.getReference().ifPresent(player -> {
            SpongeConversionUtil.toSponge(sound).ifPresent(soundType -> {
                player.playSound(soundType, Vector3d.ZERO, 1);
            });
        });
    }

    @Override
    public boolean isSneaking() {
        return this.spongePlayer.getReference().map(p -> p.get(Keys.IS_SNEAKING).orElse(false))
                .orElse(false);
    }

    @Override
    public Profile getProfile() {
        return this.spongePlayer.getReference()
                .map(p -> new SpongeProfile(p.getProfile()))
                .orElseGet(() -> new SpongeProfile(this.getUniqueId()));
    }

    @Override
    public Exceptional<Location> getLookingAtBlockPos() {
        return this.spongePlayer.getReference().map(p -> {
            BlockRay<org.spongepowered.api.world.World> ray = BlockRay.from(p)
                .select(BlockRay.notAirFilter())
                .whilst(BlockRay.allFilter())
                .distanceLimit(BLOCKRAY_LIMIT)
                .build();
            if (ray.hasNext()) {
                return SpongeConversionUtil.fromSponge(ray.next().getLocation());
            } else //noinspection ReturnOfNull
                return null;
        });
    }

    @Override
    public void execute(@NotNull String command) {
        if (this.spongePlayer.referenceExists())
            Sponge.getCommandManager().process(this.spongePlayer.getReference().get(), command);
    }

    @NotNull
    @Override
    public PlayerInventory getInventory() {
        return new SpongePlayerInventory(this);
    }

    @NotNull
    @Override
    public Location getLocation() {
        if (this.spongePlayer.referenceExists())
            return SpongeConversionUtil.fromSponge(this.spongePlayer.getReference().get().getLocation());
        else return Location.empty();
    }

    @Override
    public void setLocation(@NotNull Location location) {
        if (this.spongePlayer.referenceExists()) {
            SpongeConversionUtil.toSponge(location).ifPresent(loc -> this.spongePlayer.getReference().get().setLocation(loc));
        }
    }

    @NotNull
    @Override
    public World getWorld() {
        // No reference refresh required as this is done by getLocation. Should never throw NPE as Location is either
        // valid or EMPTY (World instance follows this same guideline).
        return this.getLocation().getWorld();
    }

    @Override
    public void send(@NotNull ResourceEntry text) {
        String formattedValue = IntegratedResource.NONE.parseColors(text.translate(this.getLanguage()).asString());
        this.send(Text.of(formattedValue));
    }

    @Override
    public void send(@NotNull Text text) {
        if (this.spongePlayer.referenceExists()) {
            this.postEventPre(text).ifPresent(msg -> {
                this.spongePlayer.getReference().get().sendMessage(SpongeConversionUtil.toSponge(msg));
            });
        }
    }

    @Override
    public void sendWithPrefix(@NotNull ResourceEntry text) {
        String formattedValue = IntegratedResource.NONE.parseColors(text.translate(this.getLanguage()).asString());
        this.sendWithPrefix(Text.of(formattedValue));
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        if (this.spongePlayer.referenceExists()) {
            this.postEventPre(text).ifPresent(msg -> {
                this.spongePlayer.getReference().get().sendMessage(org.spongepowered.api.text.Text.of(
                        SpongeConversionUtil.toSponge(IntegratedResource.PREFIX.asText()),
                        SpongeConversionUtil.toSponge(msg))
                );
            });
        }
    }

    @Override
    public void sendPagination(@NotNull Pagination pagination) {
        if (this.spongePlayer.referenceExists()) {
            SpongeConversionUtil.toSponge(pagination).sendTo(this.spongePlayer.getReference().get());
        }
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        if (SeleneInformation.GLOBALLY_PERMITTED.contains(this.getUniqueId())) return true;
        if (this.spongePlayer.referenceExists())
            return this.spongePlayer.getReference().get().hasPermission(permission);
        else return Sponge.getServiceManager().provide(UserStorageService.class)
                .map(uss -> uss.get(this.getUniqueId())
                        .map(user -> user.hasPermission(permission))
                        .orElse(false)
                ).orElse(false);
    }

    @Override
    public void setPermission(String permission, boolean value) {
        if (this.spongePlayer.referenceExists())
            this.spongePlayer.getReference().get().getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, permission, Tristate.fromBoolean(value));
        else Sponge.getServiceManager().provide(UserStorageService.class)
                .flatMap(uss -> uss.get(this.getUniqueId()))
                .ifPresent(user -> {
                    user.getSubjectData()
                            .setPermission(SubjectData.GLOBAL_CONTEXT, permission, Tristate.fromBoolean(value));
                });
    }

    private Exceptional<Text> postEventPre(Text text) {
        SendMessageEvent event = new SendMessageEvent(this, text);
        Selene.provide(EventBus.class).post(event);
        text = event.getMessage();
        if (event.isCancelled()) return Exceptional.empty();
        else return Exceptional.ofNullable(text);
    }

    public Exceptional<org.spongepowered.api.entity.living.player.Player> getSpongePlayer() {
        return this.spongePlayer.getReference();
    }

    @Override
    public void send(Packet packet) {
        if (packet instanceof NMSPacket) {
            Sponge.getServiceManager().provide(PacketGate.class).ifPresent(packetGate -> {
                // connectionByPlayer only calls getUniqueId on the Sponge Player object. Avoid constant rewrapping of types.
                Exceptional<PacketConnection> connection = Exceptional.of(packetGate.connectionByUniqueId(this.getUniqueId()));
                connection.ifPresent(packetConnection -> {
                    ((NMSPacket<?>) packet).write(packetConnection.getChannel());
                }).ifAbsent(() -> {
                    Selene.log().warn("Could not create packet connection for player '" + this.getName() + "'");
                });
            });
        }
    }

    @Override
    public <T> Exceptional<T> get(PersistentDataKey<T> dataKey) {
        return SpongeComposite.super.get(dataKey);
    }

    @Override
    public <T> TransactionResult set(PersistentDataKey<T> dataKey, T value) {
        return SpongeComposite.super.set(dataKey, value);
    }

    @Override
    public <T> void remove(PersistentDataKey<T> dataKey) {
        SpongeComposite.super.remove(dataKey);
    }

    @Override
    public Exceptional<? extends DataHolder> getDataHolder() {
        return this.getSpongePlayer();
    }
}
