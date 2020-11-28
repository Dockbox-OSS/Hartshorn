package org.dockbox.selene.sponge.objects.discord;

import com.magitechserver.magibridge.util.BridgeCommandSource;

import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.objects.discord.DiscordCommandSource;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.navigation.Pagination;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

public class MagiBridgeCommandSource extends DiscordCommandSource {

    private final BridgeCommandSource bridge;

    public MagiBridgeCommandSource(BridgeCommandSource bridge) {this.bridge = bridge;}

    @Override
    public void execute(@NotNull String command) {
        Sponge.getCommandManager().process(bridge, command);
    }

    @Override
    public void send(@NotNull ResourceEntry text) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(Text.of(text)));
    }

    @Override
    public void send(@NotNull Text text) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(text));
    }

    @Override
    public void send(@NotNull CharSequence text) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(Text.of(text)));
    }

    @Override
    public void sendWithPrefix(@NotNull ResourceEntry text) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(Text.of(text)));
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(text));
    }

    @Override
    public void sendWithPrefix(@NotNull CharSequence text) {
        bridge.sendMessage(SpongeConversionUtil.toSponge(Text.of(text)));
    }

    @Override
    public void sendPagination(@NotNull Pagination pagination) {
        // TODO
    }


}
