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

import com.flowpowered.math.vector.Vector3d;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.events.EventBus;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.DefaultResources;
import org.dockbox.selene.api.i18n.entry.Resource;
import org.dockbox.selene.api.i18n.permissions.Permission;
import org.dockbox.selene.api.i18n.permissions.PermissionContext;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.api.i18n.text.pagination.Pagination;
import org.dockbox.selene.api.keys.PersistentDataKey;
import org.dockbox.selene.api.keys.TransactionResult;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.nms.packets.NMSPacket;
import org.dockbox.selene.server.minecraft.dimension.position.Location;
import org.dockbox.selene.server.minecraft.events.chat.SendMessageEvent;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.selene.server.minecraft.packets.Packet;
import org.dockbox.selene.server.minecraft.players.GameSettings;
import org.dockbox.selene.server.minecraft.players.Gamemode;
import org.dockbox.selene.server.minecraft.players.Hand;
import org.dockbox.selene.server.minecraft.players.Player;
import org.dockbox.selene.server.minecraft.players.Profile;
import org.dockbox.selene.server.minecraft.players.SimpleGameSettings;
import org.dockbox.selene.server.minecraft.players.Sounds;
import org.dockbox.selene.server.minecraft.players.inventory.PlayerInventory;
import org.dockbox.selene.sponge.objects.SpongeProfile;
import org.dockbox.selene.sponge.objects.composite.SpongeComposite;
import org.dockbox.selene.sponge.objects.inventory.SpongePlayerInventory;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.dockbox.selene.util.Wrapper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.blockray.BlockRay;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;

@SuppressWarnings({ "ClassWithTooManyMethods", "CodeBlock2Expr" })
public class SpongePlayer extends Player implements SpongeComposite, Wrapper<org.spongepowered.api.entity.living.player.Player> {

    private final ApplicationContext context = Selene.context();
    
    private static final double BLOCKRAY_LIMIT = 50d;
    private WeakReference<org.spongepowered.api.entity.living.player.Player> reference = new WeakReference<>(null);

    public SpongePlayer(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
    }

    @Override
    public boolean isOnline() {
        return this.referenceExists() && this.getReference().get().isOnline();
    }

    @Override
    public void kick(@NotNull Text message) {
        if (this.referenceExists()) this.getReference().get().kick();
    }

    @NotNull
    @Override
    public Gamemode getGamemode() {
        if (this.referenceExists()) {
            GameMode mode = this.getReference().get().get(Keys.GAME_MODE).orElse(GameModes.NOT_SET);
            return SpongeConversionUtil.fromSponge(mode);
        }
        else return Gamemode.OTHER;
    }

    @Override
    public void setGamemode(@NotNull Gamemode gamemode) {
        if (this.referenceExists())
            this.getReference().get().offer(Keys.GAME_MODE, SpongeConversionUtil.toSponge(gamemode));
    }

    @Override
    public Item getItemInHand(Hand hand) {
        switch (hand) {
            case MAIN_HAND:
            case OFF_HAND:
                return this.getReference().map(p -> {
                    ItemStack stack = p.getItemInHand(SpongeConversionUtil.toSponge(hand))
                            .orElse(ItemStack.of(ItemTypes.AIR));
                    return SpongeConversionUtil.fromSponge(stack);
                }).map(Item.class::cast).or(MinecraftItems.getInstance().getAir());
            default:
                throw new IllegalArgumentException("Unsupported type: " + hand);
        }
    }

    @Override
    public void setItemInHand(Hand hand, Item item) {
        this.getReference().present(player -> {
            player.setItemInHand(SpongeConversionUtil.toSponge(hand), SpongeConversionUtil.toSponge(item));
        });
    }

    @Override
    public void play(Sounds sound) {
        this.getReference().present(player -> {
            SpongeConversionUtil.toSponge(sound).present(soundType -> {
                player.playSound(soundType, Vector3d.ZERO, 1);
            });
        });
    }

    @Override
    public boolean isSneaking() {
        return this.getReference().map(p -> p.get(Keys.IS_SNEAKING).orElse(false)).or(false);
    }

    @Override
    public Profile getProfile() {
        return this.getReference()
                .map(p -> new SpongeProfile(p.getProfile()))
                .get(() -> new SpongeProfile(this.getUniqueId()));
    }

    @Override
    public Exceptional<Location> getLookingAtBlockPos() {
        return this.getReference().map(p -> {
            BlockRay<org.spongepowered.api.world.World> ray =
                    BlockRay.from(p)
                            .select(BlockRay.notAirFilter())
                            .whilst(BlockRay.allFilter())
                            .distanceLimit(BLOCKRAY_LIMIT)
                            .build();
            if (ray.hasNext()) {
                return SpongeConversionUtil.fromSponge(ray.next().getLocation());
            }
            else //noinspection ReturnOfNull
                return null;
        });
    }

    @NotNull
    @Override
    public PlayerInventory getInventory() {
        return new SpongePlayerInventory(this);
    }

    @Override
    public GameSettings getGameSettings() {
        return this.getSpongePlayer().map(player -> {
            final Locale locale = player.getLocale();
            final Language language = Language.of(locale);
            return new SimpleGameSettings(language);
        }).then(() -> new SimpleGameSettings(Language.EN_US)).get();
    }

    @Override
    public Exceptional<org.spongepowered.api.entity.living.player.Player> getReference() {
        if (null == this.reference.get()) {
            this.setReference(Exceptional.of(Sponge.getServer().getPlayer(this.getUniqueId())));
        }
        return Exceptional.of(this.reference.get());
    }

    @Override
    public void setReference(@NotNull Exceptional<org.spongepowered.api.entity.living.player.Player> reference) {
        reference.present(player -> this.reference = new WeakReference<>(player));
    }

    @Override
    public Exceptional<org.spongepowered.api.entity.living.player.Player> constructInitialReference() {
        return Exceptional.of(Sponge.getServer().getPlayer(this.getUniqueId()));
    }

    @Override
    public void execute(@NotNull String command) {
        if (this.referenceExists())
            Sponge.getCommandManager().process(this.getReference().get(), command);
    }

    @Override
    public void send(@NotNull ResourceEntry text) {
        String formattedValue = Resource.parseColors(text.translate(this.getLanguage()).asString());
        this.send(Text.of(formattedValue));
    }

    @Override
    public void send(@NotNull Text text) {
        if (this.referenceExists()) {
            this.postEventPre(text).present(msg -> {
                this.getReference().get().sendMessage(SpongeConversionUtil.toSponge(msg));
            });
        }
    }

    @Override
    public void sendWithPrefix(@NotNull ResourceEntry text) {
        String formattedValue = Resource.parseColors(text.translate(this.getLanguage()).asString());
        this.sendWithPrefix(Text.of(formattedValue));
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        if (this.referenceExists()) {
            this.postEventPre(text).present(msg -> {
                this.getReference().get().sendMessage(org.spongepowered.api.text.Text.of(
                        SpongeConversionUtil.toSponge(DefaultResources.instance().getPrefix().asText()),
                        SpongeConversionUtil.toSponge(msg))
                );
            });
        }
    }

    @Override
    public void send(@NotNull Pagination pagination) {
        if (this.referenceExists()) {
            SpongeConversionUtil.toSponge(pagination).sendTo(this.getReference().get());
        }
    }

    private Exceptional<Text> postEventPre(Text text) {
        SendMessageEvent event = new SendMessageEvent(this, text);
        this.context.get(EventBus.class).post(event);
        text = event.getMessage();
        if (event.isCancelled()) return Exceptional.none();
        else return Exceptional.of(text);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        if (SeleneInformation.GLOBALLY_PERMITTED.contains(this.getUniqueId())) return true;
        return this.hasPermission(permission, SubjectData.GLOBAL_CONTEXT);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        if (permission.getContext().absent()) {
            return this.hasPermission(permission.get());
        }
        else {
            PermissionContext context = permission.getContext().get();
            if (SeleneInformation.GLOBALLY_PERMITTED.contains(this.getUniqueId())) return true;

            Set<Context> contexts = SpongeConversionUtil.toSponge(context);
            return this.hasPermission(permission.get(), contexts);
        }
    }

    private boolean hasPermission(String permission, Set<Context> contexts) {
        if (this.referenceExists()) return this.getReference().get().hasPermission(contexts, permission);
        else return Sponge.getServiceManager()
                .provide(UserStorageService.class)
                .map(uss -> uss.get(this.getUniqueId())
                        .map(user -> user.hasPermission(contexts, permission))
                        .orElse(false))
                .orElse(false);
    }

    @Override
    public void setPermission(String permission, org.dockbox.selene.api.domain.tuple.Tristate state) {
        this.setPermission(permission, SubjectData.GLOBAL_CONTEXT, state);
    }

    @Override
    public void setPermission(Permission permission, org.dockbox.selene.api.domain.tuple.Tristate state) {
        if (permission.getContext().absent()) {
            this.setPermission(permission.get(), state);
        } else {
            Set<Context> contexts = SpongeConversionUtil.toSponge(permission.getContext().get());
            this.setPermission(permission.get(), contexts, state);
        }
    }

    private void setPermission(String permission, Set<Context> contexts, org.dockbox.selene.api.domain.tuple.Tristate state) {
        Tristate tristate = SpongeConversionUtil.toSponge(state);
        if (this.referenceExists())
            this.getReference()
                    .get()
                    .getSubjectData()
                    .setPermission(contexts, permission, tristate);
        else
            Sponge.getServiceManager()
                    .provide(UserStorageService.class)
                    .flatMap(uss -> uss.get(this.getUniqueId()))
                    .ifPresent(user -> {
                        user.getSubjectData()
                                .setPermission(contexts, permission, tristate);
                    });
    }

    @Override
    public void send(Packet packet) {
        if (packet instanceof NMSPacket) {
            Sponge.getServiceManager().provide(PacketGate.class)
                    .ifPresent(packetGate -> {
                        // connectionByPlayer only calls getUniqueId on the Sponge Player object. Avoid
                        // constant rewrapping of types.
                        Exceptional<PacketConnection> connection = Exceptional.of(packetGate.connectionByUniqueId(this.getUniqueId()));
                        connection.present(packetConnection -> {
                            ((NMSPacket<?>) packet).write(packetConnection.getChannel());
                        }).absent(() -> {
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

    public Exceptional<org.spongepowered.api.entity.living.player.Player> getSpongePlayer() {
        return this.getReference();
    }

    @Override
    public Text getDisplayName() {
        if (this.referenceExists()) {
            return SpongeConversionUtil.fromSponge(this.getSpongePlayer().get().getOrElse(Keys.DISPLAY_NAME, org.spongepowered.api.text.Text.EMPTY));
        }
        return Text.of();
    }

    @NotNull
    @Override
    public Location getLocation() {
        if (this.referenceExists())
            return SpongeConversionUtil.fromSponge(this.getReference().get().getLocation());
        else return Location.empty();
    }

    @Override
    public void setDisplayName(Text displayName) {
        if (this.referenceExists()) this.getSpongePlayer().get().offer(Keys.DISPLAY_NAME, SpongeConversionUtil.toSponge(displayName));
    }

    @Override
    public void setLocation(@NotNull Location location) {
        if (this.referenceExists()) {
            SpongeConversionUtil.toSponge(location)
                    .present(loc -> this.getReference().get().setLocation(loc));
        }
    }

    @Override
    public double getHealth() {
        return this.getSpongePlayer().map(player -> player.get(Keys.HEALTH).orElse(0D)).or(0D);
    }

    @Override
    public void setHealth(double health) {
        this.getSpongePlayer().present(player -> player.offer(Keys.HEALTH, health));
    }

    @Override
    public boolean isInvisible() {
        return this.getSpongePlayer().map(player -> player.get(Keys.VANISH).orElse(false)).or(false);
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.getSpongePlayer().present(player -> player.offer(Keys.VANISH, invisible));
    }

    @Override
    public boolean isInvulnerable() {
        return this.getSpongePlayer().map(player -> player.get(Keys.INVULNERABLE).orElse(false)).or(false);
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
        this.getSpongePlayer().present(player -> player.offer(Keys.INVULNERABLE, invulnerable));
    }

    @Override
    public boolean hasGravity() {
        return this.getSpongePlayer().map(player -> player.get(Keys.HAS_GRAVITY).orElse(false)).or(false);
    }

    @Override
    public void setGravity(boolean gravity) {
        this.getSpongePlayer().present(player -> player.offer(Keys.HAS_GRAVITY, gravity));
    }

}
