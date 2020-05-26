package com.darwinreforged.server.sponge.implementations;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.chat.ClickEvent;
import com.darwinreforged.server.core.chat.HoverEvent;
import com.darwinreforged.server.core.chat.Pagination;
import com.darwinreforged.server.core.math.Vector3d;
import com.darwinreforged.server.core.player.DarwinPlayer;
import com.darwinreforged.server.core.player.PlayerManager;
import com.darwinreforged.server.core.player.inventory.DarwinItem;
import com.darwinreforged.server.core.player.state.GameModes;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.translations.DefaultTranslations;
import com.darwinreforged.server.core.resources.translations.Translation;
import com.darwinreforged.server.core.types.living.Console;
import com.darwinreforged.server.core.types.living.MessageReceiver;
import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.types.location.DarwinLocation;
import com.darwinreforged.server.core.types.location.DarwinWorld;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongePlayerManager extends PlayerManager {

    private static Text parseColors(String s) {
        return Text.of(Translation.parseColors(s));
    }

    private static Text prefix() {
        return fromAPI(com.darwinreforged.server.core.chat.Text.of(DefaultTranslations.PREFIX.s()));
    }

    @Override
    public void broadcast(com.darwinreforged.server.core.chat.Text message) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(prefix(), fromAPI(message)));
    }

    @Override
    public void broadcastIfPermitted(com.darwinreforged.server.core.chat.Text message, String permission) {
        Sponge.getServer().getOnlinePlayers().parallelStream().filter(p -> p.hasPermission(permission) ||
                p.hasPermission(Permissions.ADMIN_BYPASS.p())).forEach(p -> p.sendMessage(Text.of(prefix(), fromAPI(message))));
    }

    @Override
    public void tell(MessageReceiver receiver, com.darwinreforged.server.core.chat.Text message) {
        if (receiver instanceof DarwinPlayer) {
            Sponge.getServer().getPlayer(((DarwinPlayer) receiver).getUniqueId())
                    .ifPresent(spp -> spp.sendMessage(Text.of(prefix(), fromAPI(message))));
        } else if (receiver instanceof Console) {
            Sponge.getServer().getConsole().sendMessage(Text.of(prefix(), fromAPI(message)));
        } else {
            DarwinServer.getLog().warn("Failed to get receiver for : " + receiver);
        }
    }

    @Override
    public void tellNoMarkup(MessageReceiver receiver, com.darwinreforged.server.core.chat.Text message) {
        if (receiver instanceof DarwinPlayer) {
            Sponge.getServer().getPlayer(((Target) receiver).getUniqueId()).ifPresent(spp -> spp.sendMessage(fromAPI(message)));
        } else if (receiver instanceof Console) {
            Sponge.getServer().getConsole().sendMessage(fromAPI(message));
        }
    }

    @Override
    public boolean isOnline(UUID uuid) {
        return Sponge.getServer().getPlayer(uuid).map(User::isOnline).orElse(false);
    }

    @Override
    public void kick(DarwinPlayer player) {
        Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(Player::kick);
    }

    @Override
    public boolean hasPermission(DarwinPlayer player, String permission) {
        if (permission.equals(Permissions.ADMIN_BYPASS.p())) DarwinServer.getLog().warn("Received direct permission check for global bypass for player '" + player.getName() + "'");
        return Sponge.getServer().getPlayer(player.getUniqueId()).map(u -> u.hasPermission(permission) || u.hasPermission(Permissions.ADMIN_BYPASS.p())).orElse(false);
    }

    @Override
    public Optional<DarwinLocation> getLocation(DarwinPlayer player) {
        return Sponge.getServer().getPlayer(player.getUniqueId()).map(p -> {
            Location<World> worldLocation = p.getLocation();
            DarwinWorld darwinWorld = new DarwinWorld(worldLocation.getExtent().getUniqueId(), worldLocation.getExtent().getName());
            Vector3d vector3d = new Vector3d(worldLocation.getX(), worldLocation.getY(), worldLocation.getZ());
            return Optional.of(new DarwinLocation(darwinWorld, vector3d));
        }).orElse(Optional.empty());
    }

    @Override
    public DarwinItem<?> getItemInHand(DarwinPlayer player, boolean primaryHand) {
        Optional<ItemStack> itemStack = Sponge.getServer().getPlayer(player.getUniqueId())
                .flatMap(spp -> spp.getItemInHand(primaryHand ? HandTypes.MAIN_HAND : HandTypes.OFF_HAND));
        return itemStack.map(DarwinItem::new).orElse(null);
    }

    @Override
    public GameModes getGameMode(DarwinPlayer player) {
        return Sponge.getServer().getPlayer(player.getUniqueId()).flatMap(sp -> sp.getGameModeData().get(Keys.GAME_MODE)).map(gm -> {
            try {
                return Enum.valueOf(GameModes.class, gm.getName().toUpperCase());
            } catch (NullPointerException | IllegalArgumentException e) {
                return GameModes.UNKNOWN;
            }
        }).orElse(GameModes.UNKNOWN);
    }

    @Override
    public void setGameMode(GameModes mode, DarwinPlayer player) {
        Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(sp -> {
            org.spongepowered.api.entity.living.player.gamemode.GameMode spMode;
            switch (mode) {
                case CREATIVE:
                case UNKNOWN:
                default:
                    spMode = org.spongepowered.api.entity.living.player.gamemode.GameModes.CREATIVE;
                    break;
                case SURVIVAL:
                    spMode = org.spongepowered.api.entity.living.player.gamemode.GameModes.SURVIVAL;
                    break;
                case SPECTATOR:
                    spMode = org.spongepowered.api.entity.living.player.gamemode.GameModes.SPECTATOR;
                    break;
                case ADVENTURE:
                    spMode = org.spongepowered.api.entity.living.player.gamemode.GameModes.ADVENTURE;
                    break;
            }
            sp.getGameModeData().set(Keys.GAME_MODE, spMode);
        });
    }

    @Override
    public void executeCmd(String cmd, Target target) {
        if (target instanceof DarwinPlayer) {
            Sponge.getServer().getPlayer(target.getUniqueId()).ifPresent(sp -> Sponge.getCommandManager().process(sp, cmd));
        } else if (target instanceof Console) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
        } else {
            DarwinServer.getLog().warn(String.format("Tried executing '%s' as non-player source (%s)%n", cmd, target.getClass()));
        }
    }

    @Override
    public List<DarwinPlayer> getOnlinePlayers() {
        return Sponge.getServer().getOnlinePlayers().stream().map(sp -> super.getPlayer(sp.getUniqueId(), sp.getName())).collect(Collectors.toList());
    }

    @Override
    public Optional<DarwinPlayer> getPlayer(String player) {
        return Sponge.getServer().getPlayer(player).map(sp -> super.getPlayer(sp.getUniqueId(), sp.getName()));
    }

    @Override
    public void sendPagination(MessageReceiver receiver, Pagination pagination) {
        org.spongepowered.api.text.channel.MessageReceiver spongeReceiver = null;
        if (receiver.equals(Console.instance)) spongeReceiver = Sponge.getServer().getConsole();
        else if (receiver instanceof DarwinPlayer)
            spongeReceiver = Sponge.getServer().getPlayer(((DarwinPlayer) receiver).getUniqueId()).orElse(null);

        if (spongeReceiver != null) {
            PaginationList.Builder builder = PaginationList.builder();

            if (pagination.getPadding() != null) builder.padding(fromAPI(pagination.getPadding()));
            if (pagination.getLinesPerPage() > -1) builder.linesPerPage(pagination.getLinesPerPage());
            if (pagination.getHeader() != null) builder.header(fromAPI(pagination.getHeader()));
            if (pagination.getFooter() != null) builder.footer(fromAPI(pagination.getFooter()));
            if (pagination.getTitle() != null) builder.title(fromAPI(pagination.getTitle()));
            if (pagination.getContents() != null) {
                builder.contents(
                        pagination.getContents().stream().map(SpongePlayerManager::fromAPI).collect(Collectors.toList())
                );
            }

            builder.build().sendTo(spongeReceiver);
        }
    }

    @Override
    public String getPlayerName(UUID uuid) {
        return Sponge.getServer().getPlayer(uuid).map(Player::getName).orElse(DefaultTranslations.UNKNOWN_PLAYER.s());
    }

    private static Text fromAPI(com.darwinreforged.server.core.chat.Text text) {
        Text spT = parseColors(text.toLegacy());
        Text.Builder builder = spT.toBuilder();
        if (text.getClickEvent() != null) {
            try {
                ClickEvent clickEvent = text.getClickEvent();
                switch (clickEvent.getClickAction()) {
                    case OPEN_URL:
                        builder.onClick(TextActions.openUrl(new URL(clickEvent.getValue())));
                        break;
                    case RUN_COMMAND:
                        builder.onClick(TextActions.runCommand(clickEvent.getValue()));
                        break;
                    case SUGGEST_COMMAND:
                        builder.onClick(TextActions.suggestCommand(clickEvent.getValue()));
                        break;
                    case CHANGE_PAGE:
                        builder.onClick(TextActions.changePage(Integer.parseInt(clickEvent.getValue())));
                        break;
                }
            } catch (MalformedURLException | NumberFormatException e) {
                builder.onClick(null);
            }
        }
        if (text.getHoverEvent() != null) {
            HoverEvent hoverEvent = text.getHoverEvent();
            switch (hoverEvent.getHoverAction()) {
                case SHOW_TEXT:
                    builder.onHover(TextActions.showText(TextSerializers.FORMATTING_CODE.deserializeUnchecked(hoverEvent.getValue())));
                    break;
                case SHOW_ITEM:
                    // TODO : Convert HOCON to ItemStack
                    DarwinServer.getLog().warn("Attempted to set showItem for text object, this is not implemented into Sponge! (yet)");
                    break;
                case SHOW_ENTITY:
                    // TODO : Convert HOCON to Entity
                    DarwinServer.getLog().warn("Attempted to set showEntity for text object, this is not implemented into Sponge! (yet)");
                    break;
            }
        }
        return builder.build();
    }
}
