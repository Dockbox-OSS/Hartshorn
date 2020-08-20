package org.dockbox.darwin.sponge.objects.targets;

import com.google.inject.Singleton;

import org.dockbox.darwin.core.i18n.I18N;
import org.dockbox.darwin.core.objects.targets.Console;
import org.dockbox.darwin.core.text.Text;
import org.dockbox.darwin.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

@Singleton
public class SpongeConsole extends Console {

    public static final SpongeConsole instance = new SpongeConsole();

    private SpongeConsole() {
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
    public void send(@NotNull CharSequence text) {
        send(Text.Companion.of(text));
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        Sponge.getServer().getConsole().sendMessage(org.spongepowered.api.text.Text.of(
                SpongeConversionUtil.toSponge(I18N.PREFIX.asText()),
                SpongeConversionUtil.toSponge(text)
        ));
    }

    @Override
    public void sendWithPrefix(@NotNull CharSequence text) {
        sendWithPrefix(Text.Companion.of(text));
    }
}
