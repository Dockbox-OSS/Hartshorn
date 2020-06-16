package org.dockbox.darwin.sponge.objects.targets;

import com.google.inject.Singleton;

import org.dockbox.darwin.core.objects.targets.Console;
import org.dockbox.darwin.core.text.Text;
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
        // TODO : Text -> Sponge conversion
    }

    @Override
    public void send(@NotNull CharSequence text) {
        send(Text.Companion.of(text));
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        // TODO : Text -> Sponge conversion
    }

    @Override
    public void sendWithPrefix(@NotNull CharSequence text) {
        sendWithPrefix(Text.Companion.of(text));
    }
}
