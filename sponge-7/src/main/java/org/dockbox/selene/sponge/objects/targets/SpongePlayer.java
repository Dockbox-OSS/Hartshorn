/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.objects.targets;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.FawePlayer;
import com.flowpowered.math.vector.Vector3d;

import org.dockbox.selene.core.PlayerStorageService;
import org.dockbox.selene.core.events.EventBus;
import org.dockbox.selene.core.events.chat.SendMessageEvent;
import org.dockbox.selene.core.exceptions.global.UncheckedSeleneException;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.FieldReferenceHolder;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.player.Gamemode;
import org.dockbox.selene.core.objects.player.Hand;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.special.Sounds;
import org.dockbox.selene.core.server.SeleneInformation;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.pagination.Pagination;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.sponge.objects.item.SpongeItem;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"ClassWithTooManyMethods", "rawtypes"})
public class SpongePlayer extends Player {

    private final FieldReferenceHolder<org.spongepowered.api.entity.living.player.Player> spongePlayer =
            new FieldReferenceHolder<>(Exceptional.of(Sponge.getServer().getPlayer(this.getUniqueId())), player -> {
                if (null == player) return Exceptional.of(Sponge.getServer().getPlayer(this.getUniqueId()));
                else return Exceptional.empty();
            }, org.spongepowered.api.entity.living.player.Player.class);

    public SpongePlayer(@NotNull UUID uniqueId, @NotNull String name) {
        super(uniqueId, name);
    }

    public org.spongepowered.api.entity.living.player.Player getSpongePlayer() {
        return this.spongePlayer.getReference().orElse(null);
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
        return SeleneUtils.INJECT.getInstance(PlayerStorageService.class).getLanguagePreference(this.getUniqueId());
    }

    @Override
    public void setLanguage(@NotNull Language lang) {
        SeleneUtils.INJECT.getInstance(PlayerStorageService.class).setLanguagePreference(this.getUniqueId(), lang);
    }

    @Override
    public Item getItemInHand(Hand hand) {
        return this.spongePlayer.getReference().map(player -> {
            HandType handType;
            switch (hand) {
                case MAIN_HAND:
                    handType = HandTypes.MAIN_HAND;
                    break;
                case OFF_HAND:
                    handType = HandTypes.OFF_HAND;
                    break;
                default:
                    throw new UncheckedSeleneException("Unsupported value in context '" + hand + "'");
            }
            Optional<ItemStack> itemStack = player.getItemInHand(handType);
            if (itemStack.isPresent()) {
                return SpongeConversionUtil.fromSponge(itemStack.get());
            }
            return Item.AIR;
        }).orElse(Item.AIR);
    }

    @SuppressWarnings("OverlyStrongTypeCast")
    @Override
    public void setItemInHand(Hand hand, Item item) {
        this.spongePlayer.getReference().ifPresent(player -> {
            player.setItemInHand(SpongeConversionUtil.toSponge(hand), SpongeConversionUtil.toSponge((SpongeItem) item));
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
    public void execute(@NotNull String command) {
        if (this.spongePlayer.referenceExists())
            Sponge.getCommandManager().process(this.spongePlayer.getReference().get(), command);
    }


    @Override
    public void giveItem(@NotNull Item item) {
        if (!(item instanceof SpongeItem)) {
            return;
        }
        if (this.spongePlayer.referenceExists()) {
            this.spongePlayer.getReference().ifPresent(player -> {
                player.getInventory().offer(SpongeConversionUtil.toSponge((SpongeItem) item));
            });
        }
    }

    @Override
    public void giveItem(@NotNull Item item, int row, int column) {
        if (!(item instanceof SpongeItem)) {
            return;
        }
        if (this.spongePlayer.referenceExists()) {
            this.spongePlayer.getReference().ifPresent(player -> {
                MainPlayerInventory main = player.getInventory().query(MainPlayerInventory.class);
                Optional<Slot> slotOptional = main.getSlot(SlotPos.of(column, row));
                slotOptional.ifPresent(slot ->
                        slot.offer(SpongeConversionUtil.toSponge((SpongeItem) item))
                );
                if (!slotOptional.isPresent()) {
                    // TODO: Handle unavailable slot
                }
            });
        }
    }

    @NotNull
    @Override
    public Exceptional<Item> getItemAt(int row, int column) {
        if (this.spongePlayer.referenceExists()) {
            return this.spongePlayer.getReference().map(player -> {
                MainPlayerInventory main = player.getInventory().query(MainPlayerInventory.class);
                Exceptional<Slot> optionalSlot = Exceptional.of(main.getSlot(SlotPos.of(column, row)));
                return optionalSlot.map(slot ->
                        slot.peek().map(SpongeConversionUtil::fromSponge)
                                .map(item -> (Item) item)
                                .orElseGet(() -> Item.of("0"))).orNull();
            });
        }
        return Exceptional.of(new IllegalStateException("Player reference lost"));
    }

    @NotNull
    @Override
    public Item[][] getInventory() {
        // TODO: Implementation pending
        return new Item[0][];
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
        String formattedValue = IntegratedResource.NONE.parseColors(text.getValue(this.getLanguage()));
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
    public void send(@NotNull CharSequence text) {
        if (this.spongePlayer.referenceExists()) {
            Text message = Text.of(text);
            this.postEventPre(message).ifPresent(msg -> {
                this.spongePlayer.getReference().get().sendMessage(SpongeConversionUtil.toSponge(msg));
            });
        }
    }

    @Override
    public void sendWithPrefix(@NotNull ResourceEntry text) {
        String formattedValue = IntegratedResource.NONE.parseColors(text.getValue(this.getLanguage()));
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
    public void sendWithPrefix(@NotNull CharSequence text) {
        if (this.spongePlayer.referenceExists()) {
            Text message = Text.of(text);
            this.postEventPre(message).ifPresent(msg -> {
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
        SeleneUtils.INJECT.getInstance(EventBus.class).post(event);
        text = event.getMessage();
        if (event.isCancelled()) return Exceptional.empty();
        else return Exceptional.ofNullable(text);
    }
}
