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
import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.api.domain.Target;
import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.api.i18n.entry.DefaultResources;
import org.dockbox.hartshorn.api.i18n.entry.Resource;
import org.dockbox.hartshorn.api.i18n.permissions.PermissionContext;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.api.i18n.text.actions.HoverAction;
import org.dockbox.hartshorn.api.i18n.text.actions.ShiftClickAction;
import org.dockbox.hartshorn.api.i18n.text.pagination.Pagination;
import org.dockbox.hartshorn.commands.RunCommandAction;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.server.minecraft.bossbar.BossbarColor;
import org.dockbox.hartshorn.server.minecraft.bossbar.BossbarStyle;
import org.dockbox.hartshorn.server.minecraft.dimension.Block;
import org.dockbox.hartshorn.server.minecraft.dimension.position.BlockFace;
import org.dockbox.hartshorn.server.minecraft.entities.ItemFrame;
import org.dockbox.hartshorn.server.minecraft.entities.ItemFrame.Rotation;
import org.dockbox.hartshorn.server.minecraft.events.entity.SpawnSource;
import org.dockbox.hartshorn.server.minecraft.inventory.Slot;
import org.dockbox.hartshorn.server.minecraft.item.Enchant;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ReferencedItem;
import org.dockbox.hartshorn.server.minecraft.item.SimpleEnchant;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.server.minecraft.players.Hand;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Sounds;
import org.dockbox.hartshorn.sponge.dim.SpongeBlock;
import org.dockbox.hartshorn.sponge.dim.SpongeWorld;
import org.dockbox.hartshorn.sponge.game.SpongeConsole;
import org.dockbox.hartshorn.sponge.game.SpongePlayer;
import org.dockbox.hartshorn.sponge.game.entity.SpongeArmorStand;
import org.dockbox.hartshorn.sponge.game.entity.SpongeGenericEntity;
import org.dockbox.hartshorn.sponge.game.entity.SpongeItemFrame;
import org.dockbox.hartshorn.sponge.inventory.SpongeItem;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.exceptions.TypeConversionException;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Tamer;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.cause.entity.SpawnType;
import org.spongepowered.api.event.cause.entity.SpawnTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.orientation.Orientation;
import org.spongepowered.api.util.orientation.Orientations;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
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

@SuppressWarnings({
                          "ClassWithTooManyMethods",
                          "OverlyComplexClass",
                          "unchecked",
                          "OverlyStrongTypeCast"
                  })
public enum SpongeConvert {
    ;

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

    @NotNull
    public static <T> Exceptional<?> autoDetectFromSponge(T object) {
        if (object instanceof org.spongepowered.api.entity.living.player.Player) {
            return Exceptional.of(fromSponge((org.spongepowered.api.entity.living.player.Player) object));
        }
        else if (object instanceof CommandCause) {
            return fromSponge((CommandCause) object);
        }
        else if (object instanceof ServerLocation) {
            return Exceptional.of(fromSponge((ServerLocation) object));
        }
        else if (object instanceof ServerWorld) {
            return Exceptional.of(fromSponge((ServerWorld) object));
        }
        else if (object instanceof GameMode) {
            return Exceptional.of(fromSponge((GameMode) object));
        }
        else if (object instanceof User) {
            return Exceptional.of(
                    new SpongePlayer(((Identifiable) object).getUniqueId(), ((Tamer) object).name()));
        }
        else if (object instanceof ItemStack) {
            return Exceptional.of(fromSponge((ItemStack) object));
        }
        else if (object instanceof Enchantment) {
            return fromSponge((Enchantment) object);
        }
        return Exceptional.empty();
    }

    @NotNull
    public static Exceptional<Enchantment> toSponge(Enchant enchantment) {
        return SpongeUtil.fromMCRegistry(
                RegistryTypes.ENCHANTMENT_TYPE,
                enchantment.getEnchantment().name().toLowerCase(Locale.ROOT)
        ).map(type -> Enchantment.builder().type(type).level(enchantment.getLevel()).build());
    }

    public static BossBar.Color toSponge(BossbarColor bossbarColor) {
        return Color.valueOf(bossbarColor.name());
    }

    public static BossBar.Overlay toSponge(BossbarStyle style) {
        return Overlay.valueOf(style.name());
    }

    @NotNull
    public static Exceptional<SoundType> toSponge(Sounds sound) {
        return SpongeUtil.fromMCRegistry(RegistryTypes.SOUND_TYPE, sound.name().toLowerCase(Locale.ROOT));
    }

    @NotNull
    public static PaginationList toSponge(Pagination pagination) {
        PaginationList.Builder builder = PaginationList.builder();

        if (null != pagination.getTitle()) builder.title(toSponge(pagination.getTitle()));
        if (null != pagination.getHeader()) builder.header(toSponge(pagination.getHeader()));
        if (null != pagination.getFooter()) builder.footer(toSponge(pagination.getFooter()));

        builder.padding(toSponge(pagination.getPadding()));
        if (0 < pagination.getLinesPerPage()) builder.linesPerPage(pagination.getLinesPerPage());
        List<Component> convertedContent = pagination.getContent().stream()
                .map(SpongeConvert::toSponge)
                .map(Component::asComponent)
                .toList();

        builder.contents(convertedContent);
        return builder.build();
    }

    @NotNull
    public static TextComponent toSponge(Text message) {
        Iterable<Text> parts = message.getParts();
        TextComponent.Builder b = TextComponent.ofChildren().toBuilder();
        parts.forEach(part -> {
            TextComponent.Builder pb = TextComponent.ofChildren().toBuilder();

            // Wrapping in parseColors first so internal color codes are parsed as well. Technically
            // the FormattingCode from TextSerializers won't be needed, but to ensure no trailing codes
            // are left we use it here anyway.
            final TextComponent component = LegacyComponentSerializer.legacyAmpersand().deserialize(Resource.parseColors(part.toLegacy()));
            pb.append(component);

            Exceptional<ClickEvent> clickAction = toSponge(part.getClickAction());
            clickAction.present(pb::clickEvent);

            Exceptional<HoverEvent<?>> hoverAction = toSponge(part.getHoverAction());
            hoverAction.present(pb::hoverEvent);

            if (message.getShiftClickAction() instanceof ShiftClickAction.InsertText insertText) {
                pb.insertion(insertText.getResult().toLegacy());
            }

            b.append(pb.build());
        });

        return b.build();
    }

    @NotNull
    private static Exceptional<HoverEvent<?>> toSponge(HoverAction<?> action) {
        if (null == action) return Exceptional.empty();
        Object result = action.getResult();
        if (action instanceof HoverAction.ShowText) {
            return Exceptional.of(HoverEvent.showText(toSponge(((Text) result))));
        }
        return Exceptional.empty();
    }

    @NotNull
    private static Exceptional<ClickEvent> toSponge(ClickAction<?> action) {
        if (null == action) return Exceptional.empty();
        Object result = action.getResult();
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
                Consumer<Target> consumer = ((ClickAction.ExecuteCallback) action).getResult();
                try {
                    SpongeConvert.fromSponge(source).present(consumer).rethrow();
                }
                catch (Throwable throwable) {
                    source.sendMessage(Identity.nil(), toSponge(DefaultResources.instance().getUnknownError(throwable.getMessage()).asText()));
                }
            });
            return Exceptional.empty();
        }
        return Exceptional.empty();
    }

    @NotNull
    public static Exceptional<ServerLocation> toSponge(org.dockbox.hartshorn.server.minecraft.dimension.position.Location location) {
        Exceptional<ServerWorld> world = toSponge(location.getWorld());
        if (world.caught()) return Exceptional.of(world.error());
        if (!world.present()) return Exceptional.empty();
        Vector3d vector3d = new Vector3d(
                location.getVectorLoc().getXd(),
                location.getVectorLoc().getYd(),
                location.getVectorLoc().getZd()
        );
        return Exceptional.of(ServerLocation.of(world.get(), vector3d));
    }

    @NotNull
    public static Exceptional<ServerWorld> toSponge(org.dockbox.hartshorn.server.minecraft.dimension.world.World world) {
        if (world instanceof SpongeWorld) {
            ResourceKey key = ((SpongeWorld) world).getKey();
            return Exceptional.of(Sponge.server().worldManager().world(key));
        }
        return Exceptional.of(new IllegalArgumentException("Cannot convert non-Sponge world to Sponge reference"));
    }

    @NotNull
    public static Exceptional<Enchant> fromSponge(Enchantment enchantment) {
        try {
            String id = enchantment.type().key(RegistryTypes.ENCHANTMENT_TYPE).value();
            int level = enchantment.level();
            Enchant enchant = new SimpleEnchant(org.dockbox.hartshorn.server.minecraft.item.Enchantment.valueOf(id.toUpperCase()), level);
            return Exceptional.of(enchant);
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return Exceptional.of(e);
        }
    }

    @NotNull
    public static GameMode toSponge(Gamemode gamemode) {
        return switch (gamemode) {
            case SURVIVAL -> GameModes.SURVIVAL.get();
            case CREATIVE -> GameModes.CREATIVE.get();
            case ADVENTURE -> GameModes.ADVENTURE.get();
            case SPECTATOR -> GameModes.SPECTATOR.get();
            default -> GameModes.NOT_SET.get();
        };
    }

    public static HandType toSponge(Hand hand) {
        if (Hand.MAIN_HAND == hand) return HandTypes.MAIN_HAND.get();
        if (Hand.OFF_HAND == hand) return HandTypes.OFF_HAND.get();
        throw new RuntimeException("Invalid value in context '" + hand + "'");
    }

    public static Text fromSponge(Component component) {
        if (component instanceof TextComponent textComponent) {
            return fromSponge(textComponent);
        } else return Text.of();
    }

    @NotNull
    public static Text fromSponge(TextComponent text) {
        String style = fromSponge(text.style());
        String color = fromSponge(text.color());
        String value = text.content();

        Text t = Text.of(color + style + value);

        Exceptional.of(text.clickEvent())
                .map(SpongeConvert::fromSponge)
                .present(action -> action.present(t::onClick));
        Exceptional.of(text.hoverEvent())
                .map(SpongeConvert::fromSponge)
                .present(action -> action.present(t::onHover));
        Exceptional.of(text.insertion())
                .present(insertion -> t.onShiftClick(ShiftClickAction.insertText(Text.of(insertion))));

        text.children().stream().map(SpongeConvert::fromSponge).forEach(t::append);
        return t;
    }

    private static Exceptional<HoverAction<?>> fromSponge(HoverEvent<?> hoverAction) {
        if (hoverAction.action() == Action.SHOW_TEXT) {
            return Exceptional.of(HoverAction.showText(fromSponge(((HoverEvent<Component>) hoverAction).value())));
        }
        else return Exceptional.empty();
    }

    private static Exceptional<ClickAction<?>> fromSponge(ClickEvent clickAction) {
        return switch (clickAction.action()) {
            case OPEN_URL -> Exceptional.of(ClickAction.openUrl(clickAction.value()));
            case RUN_COMMAND -> Exceptional.of(RunCommandAction.runCommand(clickAction.value()));
            case SUGGEST_COMMAND -> Exceptional.of(ClickAction.suggestCommand(clickAction.value()));
            case CHANGE_PAGE -> Exceptional.of(ClickAction.changePage(Integer.parseInt(clickAction.value())));
            case OPEN_FILE, COPY_TO_CLIPBOARD -> Exceptional.empty();
        };
    }

    private static String fromSponge(TextColor color) {
        return Text.sectionSymbol + (textColors.getOrDefault(color, 'f') + "");
    }

    private static String fromSponge(Style style) {
        final char styleChar = Text.sectionSymbol;
        StringBuilder styleString = new StringBuilder(styleChar + "r");
        for (Entry<TextDecoration, State> decoration : style.decorations().entrySet()) {
            if (decoration.getValue() == State.TRUE) {
                char c = switch (decoration.getKey()) {
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

    @NotNull
    public static Exceptional<CommandSource> fromSponge(Subject subject) {
        if (subject instanceof SystemSubject) {
            return Exceptional.of(SpongeConsole.getInstance());
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
    public static Gamemode fromSponge(GameMode gamemode) {
        try {
            return Enum.valueOf(Gamemode.class, gamemode.toString());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return Gamemode.OTHER;
        }
    }

    @NotNull
    public static org.dockbox.hartshorn.server.minecraft.dimension.position.Location fromSponge(ServerLocation location) {
        org.dockbox.hartshorn.server.minecraft.dimension.world.World world = fromSponge(location.world());
        Vector3N vector3N = Vector3N.of(location.x(), location.y(), location.z());
        return new org.dockbox.hartshorn.server.minecraft.dimension.position.Location(vector3N, world);
    }

    @NotNull
    public static org.dockbox.hartshorn.server.minecraft.dimension.world.World fromSponge(ServerWorld world) {
        return new SpongeWorld(world.key());
    }

    public static Hand fromSponge(HandType handType) {
        if (handType == HandTypes.MAIN_HAND.get()) return Hand.MAIN_HAND;
        else if (handType == HandTypes.OFF_HAND.get()) return Hand.OFF_HAND;
        throw new RuntimeException("Invalid value in context '" + handType + "'");
    }

    @NotNull
    public static ItemStack toSponge(Item item) {
        if (item instanceof SpongeItem)
            // Create a copy of the ItemStack so Sponge doesn't modify the Item reference
            return ((SpongeItem) item).getReference().or(ItemStack.empty()).copy();
        return ItemStack.empty();
    }

    @NotNull
    public static Player fromSponge(ServerPlayer player) {
        return fromSponge(player.user());
    }

    @NotNull
    public static Player fromSponge(org.spongepowered.api.entity.living.player.User player) {
        return new SpongePlayer(player.uniqueId(), player.name());
    }

    @NotNull
    public static ReferencedItem<ItemStack> fromSponge(ItemStack item) {
        // Create a copy of the ItemStack so Sponge doesn't modify the Item reference
        return new SpongeItem(item.copy());
    }

    public static Exceptional<ServerPlayer> toSponge(Player player) {
        if (player instanceof SpongePlayer) {
            return ((SpongePlayer) player).player();
        }
        return Exceptional.empty();
    }

    public static EquipmentType toSponge(Slot slot) {
        return switch (slot) {
            case HELMET -> EquipmentTypes.HEAD.get();
            case CHESTPLATE -> EquipmentTypes.CHEST.get();
            case LEGGINGS -> EquipmentTypes.LEGS.get();
            case BOOTS -> EquipmentTypes.FEET.get();
            case MAIN_HAND -> EquipmentTypes.MAIN_HAND.get();
            case OFF_HAND -> EquipmentTypes.OFF_HAND.get();
        };
    }

    public static Vector3N fromSponge(Vector3i v3d) {
        return Vector3N.of(v3d.x(), v3d.y(), v3d.z());
    }

    public static Vector3N fromSponge(Vector3d v3d) {
        return Vector3N.of(v3d.x(), v3d.y(), v3d.z());
    }

    public static Vector3i toSponge(Vector3N v3n) {
        return new Vector3i(v3n.getXi(), v3n.getYi(), v3n.getZi());
    }

    public static Vector3d toSpongeDouble(Vector3N v3n) {
        return new Vector3d(v3n.getXd(), v3n.getYd(), v3n.getZd());
    }

    public static ItemFrame.Rotation fromSponge(Orientation rotation) {
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

    public static Orientation toSponge(ItemFrame.Rotation rotation) {
        return SpongeUtil.fromSpongeRegistry(RegistryTypes.ORIENTATION, rotation.name().toLowerCase(Locale.ROOT))
                .or(Orientations.TOP.get());
    }

    public static Direction toSponge(BlockFace blockFace) {
        try {
            return Direction.valueOf(blockFace.name());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return Direction.NONE;
        }
    }

    public static BlockFace fromSponge(Direction direction) {
        try {
            return BlockFace.valueOf(direction.name());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return BlockFace.NONE;
        }
    }

    public static org.dockbox.hartshorn.server.minecraft.entities.Entity fromSponge(Entity entity) {
        EntityType<?> type = entity.type();
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

    public static Set<Context> toSponge(PermissionContext context) {
        Set<Context> contexts = HartshornUtils.emptySet();

        if (!HartshornUtils.isEmpty(context.getDimension()))
            contexts.add(new Context(Context.DIMENSION_KEY, context.getDimension()));

        if (!HartshornUtils.isEmpty(context.getLocalHost()))
            contexts.add(new Context(Context.LOCAL_HOST_KEY, context.getLocalHost()));

        if (!HartshornUtils.isEmpty(context.getLocalIp()))
            contexts.add(new Context(Context.LOCAL_IP_KEY, context.getLocalIp()));

        if (!HartshornUtils.isEmpty(context.getRemoteIp()))
            contexts.add(new Context(Context.REMOTE_IP_KEY, context.getRemoteIp()));

        if (!HartshornUtils.isEmpty(context.getUser()))
            contexts.add(new Context(Context.USER_KEY, context.getUser()));

        if (!HartshornUtils.isEmpty(context.getWorld()))
            contexts.add(new Context(Context.WORLD_KEY, context.getWorld()));

        return contexts;
    }

    public static org.spongepowered.api.util.Tristate toSponge(Tristate state) {
        return switch (state) {
            case TRUE -> org.spongepowered.api.util.Tristate.TRUE;
            case FALSE -> org.spongepowered.api.util.Tristate.FALSE;
            default -> org.spongepowered.api.util.Tristate.UNDEFINED;
        };
    }

    public static SpawnSource fromSponge(SpawnType spawnType) {
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

    public static BlockSnapshot toSnapshot(BlockState blockState) {
        return BlockSnapshot.builder()
                .blockState(blockState)
                .world(Sponge.server().worldManager().defaultWorld().properties())
                .position(Vector3i.ZERO)
                .build();
    }

    public static Exceptional<BlockState> toSponge(Block block) {
        if (block instanceof SpongeBlock spongeBlock) return spongeBlock.state();
        else return Exceptional.empty();
    }

//    public static Element toSponge(org.dockbox.hartshorn.server.minecraft.inventory.Element element) {
//        if (element instanceof SimpleElement) {
//            return Element.of(toSponge(element.getItem()),
//                    a -> ((SimpleElement) element).perform(fromSponge(a.getPlayer()))
//            );
//        }
//        return Element.EMPTY;
//    }

//    public static com.intellectualcrafters.plot.object.Location toPlotSquared(org.dockbox.hartshorn.server.minecraft.dimension.position.Location location) {
//        return new com.intellectualcrafters.plot.object.Location(
//                location.getWorld().getName(),
//                (int) location.getX(),
//                (int) location.getY(),
//                (int) location.getZ(),
//                0, 0
//        );
//    }

//    public static org.dockbox.hartshorn.server.minecraft.dimension.position.Location fromPlotSquared(com.intellectualcrafters.plot.object.Location location) {
//        org.dockbox.hartshorn.server.minecraft.dimension.world.World world = Hartshorn.context().get(Worlds.class).getWorld(location.getWorld()).orNull();
//        return new org.dockbox.hartshorn.server.minecraft.dimension.position.Location(
//                location.getX(), location.getY(), location.getZ(), world
//        );
//    }

//    public static Player fromPlotSquared(PlotPlayer player) {
//        return new SpongePlayer(player.getUUID(), player.getName());
//    }

//    public static PlotPlayer toPlotSquared(Player player) {
//        if (player instanceof SpongePlayer) {
//            return PlotPlayer.wrap(((SpongePlayer) player).getSpongePlayer().orNull());
//        }
//        return PlotPlayer.get(player.getName());
//    }

//    public static Exceptional<PlotBlock> toPlotSquared(Item item) {
//        if (!item.isBlock()) return Exceptional.empty();
//        int id = item.getIdNumeric();
//        int meta = item.getMeta();
//        // Casting is safe in this use-case, as this is the same approach used by PlotSquared (legacy) in PlotBlock itself
//        return Exceptional.of(new PlotBlock((short) id, (byte) meta));
//    }

//    public static Mask toWorldEdit(org.dockbox.hartshorn.worldedit.region.Mask mask) {
//        if (mask instanceof WrappedMask) {
//            return ((WrappedMask) mask).getReference().orNull();
//        }
//        throw new IllegalStateException("Unknown implementation for Mask: [" + mask.getClass() + "]");
//    }

//    public static Pattern toWorldEdit(org.dockbox.hartshorn.worldedit.region.Pattern pattern) {
//        if (pattern instanceof WrappedPattern) {
//            return ((WrappedPattern) pattern).getReference().orNull();
//        }
//        throw new IllegalStateException("Unknown implementation for Pattern: [" + pattern.getClass() + "]");
//    }

//    public static Vector3N fromWorldEdit(Vector vector) {
//        return Vector3N.of(vector.getX(), vector.getY(), vector.getZ());
//    }

//    public static org.dockbox.hartshorn.server.minecraft.dimension.world.World fromWorldEdit(com.sk89q.worldedit.world.World world) {
//        return Hartshorn.context().get(Worlds.class).getWorld(world.getName()).orNull();
//    }

//    public static Exceptional<BaseBlock> toWorldEdit(Item item, ParserContext context) {
//        if (!item.isBlock())
//            return Exceptional.of(new IllegalArgumentException("Cannot derive BaseBlock from non-block item"));
//        return Exceptional.of(() -> WorldEdit.getInstance()
//                .getBlockFactory()
//                .parseFromInput(item.getId() + ':' + item.getMeta(), context)
//        );
//    }

//    public static InventoryArchetype toSponge(InventoryType inventoryType) {
//        switch (inventoryType) {
//            case DOUBLE_CHEST:
//                return InventoryArchetypes.DOUBLE_CHEST;
//            case HOPPER:
//                return InventoryArchetypes.HOPPER;
//            case DISPENSER:
//                return InventoryArchetypes.DISPENSER;
//            case CHEST:
//            default:
//                return InventoryArchetypes.CHEST;
//        }
//    }

//    public static Clipboard fromSponge(com.sk89q.worldedit.extent.clipboard.Clipboard clipboard) {
//        Region region = fromWorldEdit(clipboard.getRegion());
//        Vector origin = clipboard.getOrigin();
//        return new Clipboard(region, Vector3N.of(origin.getX(), origin.getY(), origin.getZ()));
//    }

//    public static Region fromWorldEdit(com.sk89q.worldedit.regions.Region region) {
//        return new WrappedRegion(region);
//    }

//    public static com.sk89q.worldedit.extent.clipboard.Clipboard toWorldEdit(Clipboard clipboard) {
//        com.sk89q.worldedit.regions.Region region = toWorldEdit(clipboard.getRegion());
//        Vector3N origin = clipboard.getOrigin();
//        com.sk89q.worldedit.extent.clipboard.Clipboard worldEditClipboard = new BlockArrayClipboard(region);
//        worldEditClipboard.setOrigin(new Vector(origin.getXd(), origin.getYd(), origin.getZd()));
//        return worldEditClipboard;
//    }

//    public static com.sk89q.worldedit.regions.Region toWorldEdit(Region region) {
//        if (region instanceof WrappedRegion) {
//            return ((WrappedRegion) region).getReference().orNull();
//        }
//        else {
//            com.sk89q.worldedit.world.World world = toWorldEdit(region.getWorld());
//            Vector3N min = region.getMinimumPoint();
//            Vector3N max = region.getMaximumPoint();
//
//            return new com.sk89q.worldedit.regions.CuboidRegion(
//                    world,
//                    new Vector(min.getXd(), min.getYd(), min.getZd()),
//                    new Vector(max.getXd(), max.getYd(), max.getZd())
//            );
//        }
//    }

//    public static com.sk89q.worldedit.world.World toWorldEdit(org.dockbox.hartshorn.server.minecraft.dimension.world.World world) {
//        return SpongeWorldEdit.inst().getAdapter().getWorld(toSponge(world).orNull());
//    }

//    public static FawePlayer<?> toWorldEdit(Player player) {
//        return FawePlayer.wrap(toSponge(player).orNull());
//    }

//    public static org.dockbox.hartshorn.server.minecraft.inventory.Element fromSponge(Element element) {
//        Item item = fromSponge(element.getItem().createStack());
//        return org.dockbox.hartshorn.server.minecraft.inventory.Element.of(item); // Action is skipped here
//    }
}
