package com.darwinreforged.server.modules.extensions.chat.dave;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.chat.ClickEvent;
import com.darwinreforged.server.core.chat.ClickEvent.ClickAction;
import com.darwinreforged.server.core.chat.HoverEvent;
import com.darwinreforged.server.core.chat.HoverEvent.HoverAction;
import com.darwinreforged.server.core.chat.Pagination.PaginationBuilder;
import com.darwinreforged.server.core.chat.Text;
import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.commands.annotations.Permission;
import com.darwinreforged.server.core.commands.context.CommandContext;
import com.darwinreforged.server.core.events.internal.server.ServerInitEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.player.DarwinPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 The type Dave chat module.
 */
@Command(aliases = "dave", usage = "/dave [mute|reload]", desc = "Dave commands", context = "dave")
@Module(id = "dave", name = "Dave", version = "2.0.1", description = "Read chat, send players a message if it picks up a configured message", authors = "GuusLieben")
public class DaveChatModule {

    /**
     Instantiates a new Dave chat module.
     */
    public DaveChatModule() {
    }

    private DaveConfigurationUtil configurationUtil;

    /**
     Gets configuration util.

     @return the configuration util
     */
    public DaveConfigurationUtil getConfigurationUtil() {
        return configurationUtil;
    }

    /**
     On server init.

     @param event
     the event
     */
    @Listener
    public void onServerInit(ServerInitEvent event) {
        DarwinServer.getServer().registerListener(new DaveChatListeners());
        setupConfigurations();
    }

    private void setupConfigurations() {
        configurationUtil = new DaveConfigurationUtil();
    }

    /**
     Gets player who muted dave.

     @return the player who muted dave
     */
    public List<UUID> getPlayerWhoMutedDave() {
        return playerWhoMutedDave;
    }

    private final List<UUID> playerWhoMutedDave = new ArrayList<>();

    /**
     Mute.

     @param src
     the src
     */
    @Command(aliases = "mute", usage = "dave mute", desc = "Mutes or unmutes Dave for the executing player", context = "dave mute")
    @Permission(Permissions.DAVE_MUTE)
    public void mute(DarwinPlayer src) {
        if (!configurationUtil.getMuted().contains(src.getUniqueId())) {
            configurationUtil.getMuted().add(src.getUniqueId());
            src.sendMessage(Translations.DAVE_MUTED.s());
        } else {
            playerWhoMutedDave.remove(src.getUniqueId());
            src.sendMessage(Translations.DAVE_UNMUTED.s());
        }
    }

    /**
     Reload.

     @param src
     the src
     */
    @Command(aliases = "reload", usage = "dave reload", desc = "Reloads Dave", context = "dave reload")
    @Permission(Permissions.DAVE_RELOAD)
    public void reload(DarwinPlayer src) {
        setupConfigurations();
        src.sendMessage(Translations.DAVE_RELOADED_USER.f(configurationUtil.getPrefix().getText()));
    }

    @Command(aliases = "triggers", usage = "dave triggers", desc = "Lists Dave's triggers to the executing player", context = "dave triggers")
    @Permission(Permissions.DAVE_TRIGGERS)
    public void triggers(DarwinPlayer src) {
        List<Text> triggerMessages = new ArrayList<>();
        configurationUtil.getTriggers().forEach(trigger -> {
            List<String> textResponses = new ArrayList<>();
            trigger.getResponses().forEach(response -> {
                if (response.getType().equals("message")) {
                    String text = Translations.DAVE_TRIGGER_LIST_ITEM.fsh(response.getMessage().replaceAll("\n", " : "));
                    textResponses.add(text);
                }
            });
            String fullResponse = String.join(" : ", textResponses);
            Text responseText = Text.of(fullResponse);
            ClickEvent clickEvent = new ClickEvent(ClickAction.RUN_COMMAND, "/dave run " + trigger.getId());
            HoverEvent hoverEvent = new HoverEvent(HoverAction.SHOW_TEXT, Translations.DAVE_TRIGGER_HOVER.s());
            responseText.setClickEvent(clickEvent);
            responseText.setHoverEvent(hoverEvent);
            triggerMessages.add(responseText);
        });
        PaginationBuilder.builder()
                .padding(Text.of(Translations.DARWIN_MODULE_PADDING.s()))
                .title(Text.of("&bTriggers"))
                .contents(triggerMessages).build().sendTo(src);
    }

    @Command(
            aliases = "run", usage = "dave run <triggerId>", desc = "Executes a specific trigger manually", min = 1, max = 1, context = "dave run <triggerId:String>")
    @Permission(Permissions.DAVE_TRIGGERS)
    public void run(DarwinPlayer src, CommandContext ctx) {
        ctx.getStringArgument(0).ifPresent(message -> {
            String id = message.getValue();
            Optional<DaveTrigger> candidate = configurationUtil.getTriggers().stream().filter(t -> t.getId().equals(id)).findFirst();
            boolean sent = false;
            if (candidate.isPresent()) {
                Optional<DaveChatModule> chatModuleOptional = DarwinServer.getModule(DaveChatModule.class);
                if (chatModuleOptional.isPresent()) {
                    Text botPrefix = chatModuleOptional.get().getConfigurationUtil().getPrefix();
                    String botDefaultColor = chatModuleOptional.get().getConfigurationUtil().getMessageDefaultColor().replaceAll("&", "\u00A7");
                    DaveChatListeners.Executor.beforeExecution(src.getName(), candidate.get().getTrigger().get(0).replace(",", " "), botPrefix, botDefaultColor, src);
                    DaveChatListeners.Executor.handleTrigger(candidate.get());
                    sent = true;
                }
            }

            if (!sent) src.sendMessage("Could not find trigger with Id '" + id + "'");
        });
    }

    @Command(
            aliases = "", usage = "dave", desc = "Main Dave command", max = 0, context = "dave")
    @Permission(Permissions.DAVE_TRIGGERS)
    public void main(DarwinPlayer src) {
        src.sendMessage("This command was performed by magic");
    }
}
