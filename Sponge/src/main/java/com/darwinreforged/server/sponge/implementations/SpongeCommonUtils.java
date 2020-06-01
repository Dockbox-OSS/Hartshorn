package com.darwinreforged.server.sponge.implementations;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.chat.ClickEvent;
import com.darwinreforged.server.core.chat.HoverEvent;
import com.darwinreforged.server.core.player.DarwinPlayer;
import com.darwinreforged.server.core.resources.translations.Translation;
import com.darwinreforged.server.core.types.virtual.BaseColor;
import com.darwinreforged.server.core.types.virtual.Bossbar;
import com.darwinreforged.server.core.util.CommonUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.scheduler.Task.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class SpongeCommonUtils extends CommonUtils<ServerBossBar> {

    static Text fromAPI(com.darwinreforged.server.core.chat.Text text) {
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
                    // TODO : Convert HOCON to Entity, requires !SpongeCommandBus@160
                    DarwinServer.getLog().warn("Attempted to set showEntity for text object, this is not implemented into Sponge! (yet)");
                    break;
            }
        }
        return builder.build();
    }

    @Override
    public void toggleBossbar(Bossbar bossbar, DarwinPlayer player) {
        Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(sp -> {
            if (visibleBossbarsPerPlayer.containsKey(player.getUniqueId())) {
                ServerBossBar bar = visibleBossbarsPerPlayer.get(player.getUniqueId());
                bar.removePlayer(sp);

            } else {
                ServerBossBar.Builder builder = ServerBossBar.builder()
                        .createFog(false)
                        .darkenSky(false)
                        .playEndBossMusic(false)
                        .visible(true);
                if (bossbar.getColor() != null) builder.color(convertBaseColorToBossBarColor(bossbar.getColor()));
                if (bossbar.getTitle() != null) builder.name(parseColors(bossbar.getTitle().toLegacy()));
                if (bossbar.getPercent() > -1) builder.percent(bossbar.getPercent());
                builder.build().addPlayer(sp);
            }
        });
    }

    static Text parseColors(String s) {
        return Text.of(Translation.parseColors(s));
    }

    private BossBarColor convertBaseColorToBossBarColor(BaseColor color) {
        switch (color) {
            case BLUE:
                return BossBarColors.BLUE;
            case GREEN:
                return BossBarColors.GREEN;
            case PINK:
                return BossBarColors.PINK;
            case PURPLE:
                return BossBarColors.PURPLE;
            case RED:
                return BossBarColors.RED;
            default:
            case WHITE:
                return BossBarColors.WHITE;
            case YELLOW:
                return BossBarColors.YELLOW;
        }
    }

    @Override
    public Scheduler scheduler() {
        return new SpongeScheduler();
    }

    public static final class SpongeScheduler extends Scheduler {

        private Builder builder;

        public SpongeScheduler() {
            this.builder = Sponge.getScheduler().createTaskBuilder();
        }

        @Override
        public Scheduler async() {
            builder = builder.async();
            return this;
        }

        @Override
        public Scheduler name(String name) {
            builder = builder.name(name);
            return this;
        }

        @Override
        public Scheduler delay(long delay, TimeUnit unit) {
            builder = builder.delay(delay, unit);
            return this;
        }

        @Override
        public Scheduler delayTicks(long delay) {
            builder = builder.delayTicks(delay);
            return this;
        }

        @Override
        public Scheduler interval(long delay, TimeUnit unit) {
            builder = builder.interval(delay, unit);
            return this;
        }

        @Override
        public Scheduler intervalTicks(long delay) {
            builder = builder.intervalTicks(delay);
            return this;
        }

        @Override
        public Scheduler execute(Runnable runnable) {
            builder = builder.execute(runnable);
            return this;
        }

        @Override
        public void submit() {
            builder.submit(DarwinServer.getServer());
        }
    }
}
