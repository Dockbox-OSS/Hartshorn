package org.dockbox.selene.sponge.objects.targets;

import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.targets.Console;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.navigation.Pagination;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

public class SpongeConsole extends Console {

    // TODO: Move to Console
    public static final Console instance = new SpongeConsole();

    private SpongeConsole() {}

    public static Console getInstance() {
        return instance;
    }

    @Override
    public void execute(@NotNull String command) {
        Sponge.getCommandManager().process(
                Sponge.getServer().getConsole(), command);
    }

    @Override
    public void send(@NotNull Text text) {
        Sponge.getServer().getConsole().sendMessage(SpongeConversionUtil.toSponge(text));
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        Sponge.getServer().getConsole().sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(IntegratedResource.PREFIX.asText()),
                SpongeConversionUtil.toSponge(text)
        ));
    }

    @Override
    public void sendPagination(@NotNull Pagination pagination) {
        SpongeConversionUtil.toSponge(pagination).sendTo(Sponge.getServer().getConsole());
    }
}
