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

package org.dockbox.hartshorn.sponge.util;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.Action;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.Subject;
import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.RunCommandAction;
import org.dockbox.hartshorn.di.TypeConversionException;
import org.dockbox.hartshorn.i18n.entry.DefaultResources;
import org.dockbox.hartshorn.i18n.entry.Resource;
import org.dockbox.hartshorn.i18n.permissions.PermissionContext;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.i18n.text.actions.HoverAction;
import org.dockbox.hartshorn.i18n.text.actions.ShiftClickAction;
import org.dockbox.hartshorn.i18n.text.pagination.Pagination;
import org.dockbox.hartshorn.server.minecraft.bossbar.BossbarColor;
import org.dockbox.hartshorn.server.minecraft.bossbar.BossbarStyle;
import org.dockbox.hartshorn.server.minecraft.dimension.Block;
import org.dockbox.hartshorn.server.minecraft.dimension.position.BlockFace;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.dimension.world.generation.GeneratorType;
import org.dockbox.hartshorn.server.minecraft.entities.ItemFrame;
import org.dockbox.hartshorn.server.minecraft.entities.ItemFrame.Rotation;
import org.dockbox.hartshorn.server.minecraft.events.entity.SpawnSource;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;
import org.dockbox.hartshorn.server.minecraft.inventory.Slot;
import org.dockbox.hartshorn.server.minecraft.item.Enchant;
import org.dockbox.hartshorn.server.minecraft.item.EnchantImpl;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ReferencedItem;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.server.minecraft.players.Hand;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Sounds;
import org.dockbox.hartshorn.sponge.Sponge8Application;
import org.dockbox.hartshorn.sponge.dim.SpongeBlock;
import org.dockbox.hartshorn.sponge.dim.SpongeLocation;
import org.dockbox.hartshorn.sponge.dim.SpongeWorld;
import org.dockbox.hartshorn.sponge.game.SpongePlayer;
import org.dockbox.hartshorn.sponge.game.entity.SpongeArmorStand;
import org.dockbox.hartshorn.sponge.game.entity.SpongeGenericEntity;
import org.dockbox.hartshorn.sponge.game.entity.SpongeItemFrame;
import org.dockbox.hartshorn.sponge.inventory.SpongeItem;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.cause.entity.SpawnType;
import org.spongepowered.api.event.cause.entity.SpawnTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.registry.DefaultedRegistryReference;
import org.spongepowered.api.registry.RegistryReference;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.orientation.Orientation;
import org.spongepowered.api.util.orientation.Orientations;
import org.spongepowered.api.world.WorldType;
import org.spongepowered.api.world.WorldTypes;
import org.spongepowered.api.world.biome.Biome;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.portal.PortalType;
import org.spongepowered.api.world.portal.PortalTypes;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.storage.ServerWorldProperties;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

public final class SpongeAdapter {

    private static final Map<TextColor, Character> textColors =
            HartshornUtils.ofEntries(
                    HartshornUtils.entry(NamedTextColor.BLACK, '0'),
                    HartshornUtils.entry(NamedTextColor.DARK_BLUE, '1'),
                    HartshornUtils.entry(NamedTextColor.DARK_GREEN, '2'),
                    HartshornUtils.entry(NamedTextColor.DARK_AQUA, '3'),
                    HartshornUtils.entry(NamedTextColor.DARK_RED, '4'),
                    HartshornUtils.entry(NamedTextColor.DARK_PURPLE, '5'),
                    HartshornUtils.entry(NamedTextColor.GOLD, '6'),
                    HartshornUtils.entry(NamedTextColor.GRAY, '7'),
                    HartshornUtils.entry(NamedTextColor.DARK_GRAY, '8'),
                    HartshornUtils.entry(NamedTextColor.BLUE, '9'),
                    HartshornUtils.entry(NamedTextColor.GREEN, 'a'),
                    HartshornUtils.entry(NamedTextColor.AQUA, 'b'),
                    HartshornUtils.entry(NamedTextColor.RED, 'c'),
                    HartshornUtils.entry(NamedTextColor.LIGHT_PURPLE, 'd'),
                    HartshornUtils.entry(NamedTextColor.YELLOW, 'e'),
                    HartshornUtils.entry(NamedTextColor.WHITE, 'f'));

    public static org.dockbox.hartshorn.server.minecraft.entities.Entity fromSponge(final Entity entity) {
        final EntityType<?> type = entity.type();
        // TODO: Better switching
        if (type == EntityTypes.PLAYER.get()) {
            return new SpongePlayer(entity.uniqueId(), ((ServerPlayer) entity).name());
        }
        else if (type == EntityTypes.ARMOR_STAND.get()) {
            return new SpongeArmorStand((ArmorStand) entity);
        }
        else if (type == EntityTypes.ITEM_FRAME.get()) {
            return new SpongeItemFrame((org.spongepowered.api.entity.hanging.ItemFrame) entity);
        }
        else {
            return new SpongeGenericEntity(new WeakReference<>(entity));
        }
    }

    @NotNull
    public static Exceptional<CommandSource> fromSponge(final org.spongepowered.api.service.permission.Subject subject) {
        if (subject instanceof SystemSubject) {
            return Exceptional.of(Sponge8Application.context().get(org.dockbox.hartshorn.commands.SystemSubject.class));
        }
        else if (subject instanceof ServerPlayer) {
            return Exceptional.of(fromSponge((ServerPlayer) subject));
        }
        // TODO: Reimplement once Discord bridge is added
//        else if (subject instanceof BridgeCommandSource) {
//            return Exceptional.of(new MagiBridgeCommandSource((BridgeCommandSource) subject));
//        }
        return Exceptional.of(new TypeConversionException(CommandSource.class, subject.getClass().getSimpleName()));
    }

    @NotNull
    public static Location fromSponge(final ServerLocation location) {
        final SpongeWorld world = fromSponge(location.world());
        final Vector3N vector3N = Vector3N.of(location.x(), location.y(), location.z());
        return new SpongeLocation(vector3N, world);
    }

    @NotNull
    public static SpongeWorld fromSponge(final ServerWorld world) {
        return new SpongeWorld(world.key());
    }

    @NotNull
    public static Gamemode fromSponge(final GameMode gamemode) {
        try {
            return Enum.valueOf(Gamemode.class, gamemode.toString());
        }
        catch (final IllegalArgumentException | NullPointerException e) {
            return Gamemode.OTHER;
        }
    }

    @NotNull
    public static ReferencedItem<ItemStack> fromSponge(final ItemStack item) {
        // Create a copy of the ItemStack so Sponge doesn't modify the Item reference
        return new SpongeItem(item.copy());
    }

    @NotNull
    public static Exceptional<Enchant> fromSponge(final Enchantment enchantment) {
        try {
            final String id = enchantment.type().key(RegistryTypes.ENCHANTMENT_TYPE).value();
            final int level = enchantment.level();
            final Enchant enchant = new EnchantImpl(org.dockbox.hartshorn.server.minecraft.item.Enchantment.valueOf(id.toUpperCase()), level);
            return Exceptional.of(enchant);
        }
        catch (final IllegalArgumentException | NullPointerException e) {
            return Exceptional.of(e);
        }
    }

    @NotNull
    public static Player fromSponge(final ServerPlayer player) {
        return fromSponge(player.user());
    }

    @NotNull
    public static Player fromSponge(final org.spongepowered.api.entity.living.player.User player) {
        return new SpongePlayer(player.uniqueId(), player.name());
    }

    @NotNull
    public static Exceptional<Enchantment> toSponge(final Enchant enchantment) {
        return SpongeUtil.fromMCRegistry(
                RegistryTypes.ENCHANTMENT_TYPE,
                enchantment.enchantment().name().toLowerCase(Locale.ROOT)
        ).map(type -> Enchantment.builder().type(type).level(enchantment.level()).build());
    }

    public static BossBar.Color toSponge(final BossbarColor bossbarColor) {
        return Color.valueOf(bossbarColor.name());
    }

    public static BossBar.Overlay toSponge(final BossbarStyle style) {
        return Overlay.valueOf(style.name());
    }

    @NotNull
    public static Exceptional<SoundType> toSponge(final Sounds sound) {
        return SpongeUtil.fromMCRegistry(RegistryTypes.SOUND_TYPE, sound.name().toLowerCase(Locale.ROOT));
    }

    @NotNull
    public static PaginationList toSponge(final Pagination pagination) {
        final PaginationList.Builder builder = PaginationList.builder();

        if (null != pagination.title()) builder.title(toSponge(pagination.title()));
        if (null != pagination.header()) builder.header(toSponge(pagination.header()));
        if (null != pagination.footer()) builder.footer(toSponge(pagination.footer()));

        builder.padding(toSponge(pagination.padding()));
        if (0 < pagination.linesPerPage()) builder.linesPerPage(pagination.linesPerPage());
        final List<Component> convertedContent = pagination.content().stream()
                .map(SpongeAdapter::toSponge)
                .map(Component::asComponent)
                .toList();

        builder.contents(convertedContent);
        return builder.build();
    }

    @NotNull
    public static TextComponent toSponge(final Text message) {
        final Iterable<Text> parts = message.parts();
        final TextComponent.Builder b = TextComponent.ofChildren().toBuilder();
        parts.forEach(part -> {
            final TextComponent.Builder pb = TextComponent.ofChildren().toBuilder();

            // Wrapping in parseColors first so internal color codes are parsed as well. Technically
            // the FormattingCode from TextSerializers won't be needed, but to ensure no trailing codes
            // are left we use it here anyway.
            final TextComponent component = LegacyComponentSerializer.legacyAmpersand().deserialize(Resource.parseColors(part.toLegacy()));
            pb.append(component);

            final Exceptional<ClickEvent> clickAction = toSponge(part.onClick());
            clickAction.present(pb::clickEvent);

            final Exceptional<HoverEvent<?>> hoverAction = toSponge(part.onHover());
            hoverAction.present(pb::hoverEvent);

            if (message.onShiftClick() instanceof ShiftClickAction.InsertText insertText) {
                pb.insertion(insertText.result().toLegacy());
            }

            b.append(pb.build());
        });

        return b.build();
    }

    @NotNull
    private static Exceptional<HoverEvent<?>> toSponge(final HoverAction<?> action) {
        if (null == action) return Exceptional.empty();
        final Object result = action.result();
        if (action instanceof HoverAction.ShowText) {
            return Exceptional.of(HoverEvent.showText(toSponge(((Text) result))));
        }
        return Exceptional.empty();
    }

    @NotNull
    private static Exceptional<ClickEvent> toSponge(final ClickAction<?> action) {
        if (null == action) return Exceptional.empty();
        final Object result = action.result();
        if (action instanceof ClickAction.OpenUrl) {
            return Exceptional.of(ClickEvent.openUrl((URL) result));
        }
        else if (action instanceof RunCommandAction.RunCommand) {
            return Exceptional.of(ClickEvent.runCommand((String) result));
        }
        else if (action instanceof ClickAction.ChangePage) {
            return Exceptional.of(ClickEvent.changePage((int) result));
        }
        else if (action instanceof ClickAction.SuggestCommand) {
            return Exceptional.of(ClickEvent.suggestCommand((String) result));
        }
        else if (action instanceof ClickAction.ExecuteCallback) {
            SpongeComponents.executeCallback(source -> {
                final Consumer<Subject> consumer = ((ClickAction.ExecuteCallback) action).result();
                try {
                    SpongeAdapter.fromSponge(source).present(consumer).rethrow();
                }
                catch (final Throwable throwable) {
                    source.sendMessage(Identity.nil(), toSponge(DefaultResources.instance(Sponge8Application.context()).unknownError(throwable.getMessage()).asText()));
                }
            });
            return Exceptional.empty();
        }
        return Exceptional.empty();
    }

    @NotNull
    public static Exceptional<ServerLocation> toSponge(final org.dockbox.hartshorn.server.minecraft.dimension.position.Location location) {
        final Exceptional<ServerWorld> world = toSponge(location.world());
        if (world.caught()) return Exceptional.of(world.error());
        if (!world.present()) return Exceptional.empty();
        final Vector3d vector3d = new Vector3d(
                location.vector().xD(),
                location.vector().yD(),
                location.vector().zD()
        );
        return Exceptional.of(ServerLocation.of(world.get(), vector3d));
    }

    @NotNull
    public static Exceptional<ServerWorld> toSponge(final org.dockbox.hartshorn.server.minecraft.dimension.world.World world) {
        if (world instanceof SpongeWorld) {
            final ResourceKey key = ((SpongeWorld) world).key();
            return Exceptional.of(Sponge.server().worldManager().world(key));
        }
        return Exceptional.of(new IllegalArgumentException("Cannot convert non-Sponge world to Sponge reference"));
    }

    @NotNull
    public static DefaultedRegistryReference<GameMode> toSponge(final Gamemode gamemode) {
        return switch (gamemode) {
            case SURVIVAL -> GameModes.SURVIVAL;
            case CREATIVE -> GameModes.CREATIVE;
            case ADVENTURE -> GameModes.ADVENTURE;
            case SPECTATOR -> GameModes.SPECTATOR;
            default -> GameModes.NOT_SET;
        };
    }

    public static HandType toSponge(final Hand hand) {
        if (Hand.MAIN_HAND == hand) return HandTypes.MAIN_HAND.get();
        if (Hand.OFF_HAND == hand) return HandTypes.OFF_HAND.get();
        throw new RuntimeException("Invalid value in context '" + hand + "'");
    }

    public static Text fromSponge(final Component component) {
        if (component instanceof TextComponent textComponent) {
            return fromSponge(textComponent);
        }
        else return Text.of();
    }

    @NotNull
    public static Text fromSponge(final TextComponent text) {
        final String style = fromSponge(text.style());
        final String color = fromSponge(text.color());
        final String value = text.content();

        final Text t = Text.of(color + style + value);

        Exceptional.of(text.clickEvent())
                .map(SpongeAdapter::fromSponge)
                .present(action -> action.present(t::onClick));
        Exceptional.of(text.hoverEvent())
                .map(SpongeAdapter::fromSponge)
                .present(action -> action.present(t::onHover));
        Exceptional.of(text.insertion())
                .present(insertion -> t.onShiftClick(ShiftClickAction.insertText(Text.of(insertion))));

        text.children().stream().map(SpongeAdapter::fromSponge).forEach(t::append);
        return t;
    }

    private static Exceptional<HoverAction<?>> fromSponge(final HoverEvent<?> hoverAction) {
        if (hoverAction.action() == Action.SHOW_TEXT) {
            //noinspection unchecked
            return Exceptional.of(HoverAction.showText(fromSponge(((HoverEvent<Component>) hoverAction).value())));
        }
        else return Exceptional.empty();
    }

    private static Exceptional<ClickAction<?>> fromSponge(final ClickEvent clickAction) {
        return switch (clickAction.action()) {
            case OPEN_URL -> Exceptional.of(ClickAction.openUrl(clickAction.value()));
            case RUN_COMMAND -> Exceptional.of(RunCommandAction.runCommand(clickAction.value()));
            case SUGGEST_COMMAND -> Exceptional.of(ClickAction.suggestCommand(clickAction.value()));
            case CHANGE_PAGE -> Exceptional.of(ClickAction.changePage(Integer.parseInt(clickAction.value())));
            case OPEN_FILE, COPY_TO_CLIPBOARD -> Exceptional.empty();
        };
    }

    private static String fromSponge(final TextColor color) {
        return Text.sectionSymbol + (textColors.getOrDefault(color, 'f') + "");
    }

    private static String fromSponge(final Style style) {
        final char styleChar = Text.sectionSymbol;
        final StringBuilder styleString = new StringBuilder(styleChar + "r");
        for (final Entry<TextDecoration, State> decoration : style.decorations().entrySet()) {
            if (decoration.getValue() == State.TRUE) {
                final char c = switch (decoration.getKey()) {
                    case OBFUSCATED -> 'k';
                    case BOLD -> 'l';
                    case STRIKETHROUGH -> 'm';
                    case UNDERLINED -> 'n';
                    case ITALIC -> 'o';
                    default -> throw new IllegalStateException("Unexpected value: " + decoration.getKey());
                };
                styleString.append(styleChar).append(c);
            }
        }
        return styleString.toString();
    }

    public static Hand fromSponge(final HandType handType) {
        if (handType == HandTypes.MAIN_HAND.get()) return Hand.MAIN_HAND;
        else if (handType == HandTypes.OFF_HAND.get()) return Hand.OFF_HAND;
        throw new RuntimeException("Invalid value in context '" + handType + "'");
    }

    @NotNull
    public static ItemStack toSponge(final Item item) {
        if (item instanceof SpongeItem)
            // Create a copy of the ItemStack so Sponge doesn't modify the Item reference
            return ((SpongeItem) item).reference().or(ItemStack.empty()).copy();
        return ItemStack.empty();
    }

    public static Exceptional<ServerPlayer> toSponge(final Player player) {
        if (player instanceof SpongePlayer) {
            return ((SpongePlayer) player).player();
        }
        return Exceptional.empty();
    }

    public static EquipmentType toSponge(final Slot slot) {
        return switch (slot) {
            case HELMET -> EquipmentTypes.HEAD.get();
            case CHESTPLATE -> EquipmentTypes.CHEST.get();
            case LEGGINGS -> EquipmentTypes.LEGS.get();
            case BOOTS -> EquipmentTypes.FEET.get();
            case MAIN_HAND -> EquipmentTypes.MAIN_HAND.get();
            case OFF_HAND -> EquipmentTypes.OFF_HAND.get();
        };
    }

    public static Vector3N fromSponge(final Vector3i v3d) {
        return Vector3N.of(v3d.x(), v3d.y(), v3d.z());
    }

    public static Vector3N fromSponge(final Vector3d v3d) {
        return Vector3N.of(v3d.x(), v3d.y(), v3d.z());
    }

    public static Vector3i toSponge(final Vector3N v3n) {
        return new Vector3i(v3n.xI(), v3n.yI(), v3n.zI());
    }

    public static Vector3d toSpongeDouble(final Vector3N v3n) {
        return new Vector3d(v3n.xD(), v3n.yD(), v3n.zD());
    }

    public static ItemFrame.Rotation fromSponge(final Orientation rotation) {
        if (rotation == Orientations.BOTTOM.get()) return Rotation.BOTTOM;
        else if (rotation == Orientations.BOTTOM_LEFT.get()) return Rotation.BOTTOM_LEFT;
        else if (rotation == Orientations.BOTTOM_RIGHT.get()) return Rotation.BOTTOM_RIGHT;
        else if (rotation == Orientations.LEFT.get()) return Rotation.LEFT;
        else if (rotation == Orientations.RIGHT.get()) return Rotation.RIGHT;
        else if (rotation == Orientations.TOP.get()) return Rotation.TOP;
        else if (rotation == Orientations.TOP_LEFT.get()) return Rotation.TOP_LEFT;
        else if (rotation == Orientations.TOP_RIGHT.get()) return Rotation.TOP_RIGHT;
        else return Rotation.TOP;
    }

    public static Orientation toSponge(final ItemFrame.Rotation rotation) {
        return SpongeUtil.fromSpongeRegistry(RegistryTypes.ORIENTATION, rotation.name().toLowerCase(Locale.ROOT))
                .or(Orientations.TOP.get());
    }

    public static Direction toSponge(final BlockFace blockFace) {
        try {
            return Direction.valueOf(blockFace.name());
        }
        catch (final IllegalArgumentException | NullPointerException e) {
            return Direction.NONE;
        }
    }

    public static BlockFace fromSponge(final Direction direction) {
        try {
            return BlockFace.valueOf(direction.name());
        }
        catch (final IllegalArgumentException | NullPointerException e) {
            return BlockFace.NONE;
        }
    }

    public static Set<Context> toSponge(final PermissionContext context) {
        final Set<Context> contexts = HartshornUtils.emptySet();

        if (!HartshornUtils.empty(context.dimension()))
            contexts.add(new Context(Context.DIMENSION_KEY, context.dimension()));

        if (!HartshornUtils.empty(context.localHost()))
            contexts.add(new Context(Context.LOCAL_HOST_KEY, context.localHost()));

        if (!HartshornUtils.empty(context.localIp()))
            contexts.add(new Context(Context.LOCAL_IP_KEY, context.localIp()));

        if (!HartshornUtils.empty(context.remoteIp()))
            contexts.add(new Context(Context.REMOTE_IP_KEY, context.remoteIp()));

        if (!HartshornUtils.empty(context.user()))
            contexts.add(new Context(Context.USER_KEY, context.user()));

        if (!HartshornUtils.empty(context.world()))
            contexts.add(new Context(Context.WORLD_KEY, context.world()));

        return contexts;
    }

    public static org.spongepowered.api.util.Tristate toSponge(final Tristate state) {
        return switch (state) {
            case TRUE -> org.spongepowered.api.util.Tristate.TRUE;
            case FALSE -> org.spongepowered.api.util.Tristate.FALSE;
            default -> org.spongepowered.api.util.Tristate.UNDEFINED;
        };
    }

    public static SpawnSource fromSponge(final SpawnType spawnType) {
        if (spawnType == SpawnTypes.BLOCK_SPAWNING.get()) return SpawnSource.BLOCK;
        else if (spawnType == SpawnTypes.BREEDING.get()) return SpawnSource.BREEDING;
        else if (spawnType == SpawnTypes.CHUNK_LOAD.get()) return SpawnSource.CHUNK;
        else if (spawnType == SpawnTypes.CUSTOM.get()) return SpawnSource.PLACEMENT;
        else if (spawnType == SpawnTypes.DISPENSE.get()) return SpawnSource.DISPENSE;
        else if (spawnType == SpawnTypes.DROPPED_ITEM.get()) return SpawnSource.DROP;
        else if (spawnType == SpawnTypes.EXPERIENCE.get()) return SpawnSource.EXPERIENCE;
        else if (spawnType == SpawnTypes.FALLING_BLOCK.get()) return SpawnSource.FALLING_BLOCK;
        else if (spawnType == SpawnTypes.MOB_SPAWNER.get()) return SpawnSource.SPAWNER;
        else if (spawnType == SpawnTypes.PASSIVE.get()) return SpawnSource.PLACEMENT;
        else if (spawnType == SpawnTypes.PLACEMENT.get()) return SpawnSource.PLACEMENT;
        else if (spawnType == SpawnTypes.PLUGIN.get()) return SpawnSource.PLACEMENT;
        else if (spawnType == SpawnTypes.PROJECTILE.get()) return SpawnSource.PROJECTILE;
        else if (spawnType == SpawnTypes.SPAWN_EGG.get()) return SpawnSource.SPAWN_EGG;
        else if (spawnType == SpawnTypes.STRUCTURE.get()) return SpawnSource.STRUCTURE;
        else if (spawnType == SpawnTypes.TNT_IGNITE.get()) return SpawnSource.TNT;
        else if (spawnType == SpawnTypes.WEATHER.get()) return SpawnSource.WEATHER;
        else if (spawnType == SpawnTypes.WORLD_SPAWNER.get()) return SpawnSource.WORLD;
        else return SpawnSource.PLACEMENT;
    }

    public static BlockSnapshot toSnapshot(final BlockState blockState) {
        return BlockSnapshot.builder()
                .blockState(blockState)
                .world(Sponge.server().worldManager().defaultWorld().properties())
                .position(Vector3i.ZERO)
                .build();
    }

    public static Exceptional<BlockState> toSponge(final Block block) {
        if (block instanceof SpongeBlock spongeBlock) return spongeBlock.state();
        else return Exceptional.empty();
    }

    public static Block fromSponge(final BlockSnapshot block) {
        return new SpongeBlock(block);
    }

    public static World fromSponge(final ServerWorldProperties world) {
        return new SpongeWorld(world.key());
    }

    public static Player toSponge(final User user) {
        return new SpongePlayer(user.uniqueId(), user.name());
    }

    public static org.dockbox.hartshorn.server.minecraft.enums.PortalType fromSponge(final PortalType type) {
        if (type == PortalTypes.END.get())
            return org.dockbox.hartshorn.server.minecraft.enums.PortalType.END;
        else if (type == PortalTypes.NETHER.get())
            return org.dockbox.hartshorn.server.minecraft.enums.PortalType.NETHER;
        return org.dockbox.hartshorn.server.minecraft.enums.PortalType.UNKOWN;
    }

    public static RegistryReference<Biome> toSponge(final org.dockbox.hartshorn.server.minecraft.dimension.world.generation.Biome biome) {
        return RegistryTypes.BIOME.referenced(ResourceKey.minecraft(biome.id().toLowerCase(Locale.ROOT)));
    }

    public static RegistryReference<WorldType> toSponge(final GeneratorType type) {
        return switch (type) {
            case OVERWORLD, FLAT -> WorldTypes.OVERWORLD;
            case END -> WorldTypes.THE_END;
            case NETHER -> WorldTypes.THE_NETHER;
        };
    }

    public static RegistryReference<Difficulty> toSponge(final org.dockbox.hartshorn.server.minecraft.dimension.world.generation.Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> Difficulties.PEACEFUL;
            case EASY -> Difficulties.EASY;
            case NORMAL -> Difficulties.NORMAL;
            case HARD -> Difficulties.HARD;
        };
    }

    public static ContainerType toSponge(final InventoryType inventoryType) {
        return (switch (inventoryType) {
            case CHEST -> ContainerTypes.GENERIC_9X3;
            case DOUBLE_CHEST -> ContainerTypes.GENERIC_9X6;
            case HOPPER -> ContainerTypes.HOPPER;
            case SHULKER_BOX -> ContainerTypes.SHULKER_BOX;
            case GENERIC_5_ROWS -> ContainerTypes.GENERIC_9X5;
            case GENERIC_4_ROWS -> ContainerTypes.GENERIC_9X4;
            case DISPENSER -> ContainerTypes.GENERIC_3X3;
            case SMOKER -> ContainerTypes.SMOKER;
            case DROPPER -> ContainerTypes.GENERIC_9X1;
            case LECTERN -> ContainerTypes.LECTERN;
            case GENERIC_2_ROWS -> ContainerTypes.GENERIC_9X2;
            case CRAFTING_BENCH -> ContainerTypes.CRAFTING;
            case BREWING_STAND -> ContainerTypes.BREWING_STAND;
            case BLAST_FURNACE -> ContainerTypes.BLAST_FURNACE;
            case FURNACE -> ContainerTypes.FURNACE;
            case ANVIL -> ContainerTypes.ANVIL;
            case ENCHANTMENT_TABLE -> ContainerTypes.ENCHANTMENT;
            case STONE_CUTTER -> ContainerTypes.STONECUTTER;
            case GRINDSTONE -> ContainerTypes.GRINDSTONE;
            case MERCHANT -> ContainerTypes.MERCHANT;
            case LOOM -> ContainerTypes.LOOM;
            case CARTOGRAPHY_TABLE -> ContainerTypes.CARTOGRAPHY_TABLE;
            case BEACON -> ContainerTypes.BEACON;
        }).get();
    }
}
