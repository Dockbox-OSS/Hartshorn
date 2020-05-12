package com.darwinreforged.server.modules.extensions.chat.dave;

import com.darwinreforged.server.core.types.chat.LegacyText;
import com.darwinreforged.server.core.types.chat.Text;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.util.DiscordUtils;
import com.darwinreforged.server.core.util.FileUtils;
import com.darwinreforged.server.modules.extensions.chat.dave.DaveTrigger.Response;

import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DaveConfigurationUtil {

    private List<DaveTrigger> triggers = new ArrayList<>();
    private final List<UUID> muted = new ArrayList<>();
    private final TextChannel channel;
    private final Text prefix;
    private final String messageDefaultColor;

    @SuppressWarnings("unchecked")
    public DaveConfigurationUtil() {
        FileUtils fu = DarwinServer.getUtilChecked(FileUtils.class);

        // Load triggers
        File triggerFile = new File(fu.getDataDirectory(DaveChatModule.class).toFile(), "triggers.yml");
        TriggerConfig triggerData = fu.getYamlDataFromFile(triggerFile, TriggerConfig.class, null);
        if (triggerData != null) {
            this.triggers = triggerData.getTriggers();
        } else {
            List<DaveTrigger> triggers = Collections.singletonList(
                    new DaveTrigger(
                            Arrays.asList("sample,trigger", "second,trigger"),
                            false,
                            Arrays.asList(
                                    new Response("This is a chat message", "message"),
                                    new Response("https://example.com", "url"),
                                    new Response("help", "cmd"))));
            TriggerConfig triggerConfig = new TriggerConfig(triggers);
            fu.writeYamlDataToFile(triggerConfig, triggerFile);
        }

        // Load mutes
        File mutedFile = fu.createFileIfNotExists(new File(fu.getDataDirectory(DaveChatModule.class).toFile(), "muted.yml"));
        Map<String, Object> mutedData = fu.getYamlDataFromFile(mutedFile);
        List<String> mutedStrings = (List<String>) mutedData.getOrDefault("muted", Collections.emptyList());
        muted.addAll(mutedStrings.stream().map(UUID::fromString).collect(Collectors.toList()));

        // Load config
        Map<String, Object> daveConfig = fu.getYamlDataForConfig(DaveChatModule.class);
        boolean updateConfig = false;
        DiscordUtils du = DarwinServer.getUtilChecked(DiscordUtils.class);
        String channelId;
        if (daveConfig.containsKey("channel")) {
            channelId = String.valueOf(daveConfig.get("channel"));
        } else {
            channelId = "123456789123456789";
            daveConfig.put("channel", channelId);
            updateConfig = true;
        }
        this.channel = du.getChannel(channelId);

        if (daveConfig.containsKey("prefix")) {
            this.prefix = LegacyText.fromLegacy(String.valueOf(daveConfig.get("prefix")));
        } else {
            this.prefix = new Text("Dave : ");
            daveConfig.put("prefix", "Dave : ");
            updateConfig = true;
        }

        if (daveConfig.containsKey("messageColor")) {
            this.messageDefaultColor = String.valueOf(daveConfig.get("messageColor"));
        } else {
            this.messageDefaultColor = "&f";
            daveConfig.put("messageColor", "&f");
            updateConfig = true;
        }

        if (updateConfig) {
            fu.writeYamlDataForConfig(daveConfig, DaveChatModule.class);
        }
    }

    public List<DaveTrigger> getTriggers() {
        return triggers;
    }

    public List<UUID> getMuted() {
        return muted;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public Text getPrefix() {
        return prefix;
    }

    public String getMessageDefaultColor() {
        return messageDefaultColor;
    }

    private static class TriggerConfig {
        private List<DaveTrigger> triggers;

        public TriggerConfig() {
        }

        public TriggerConfig(List<DaveTrigger> triggers) {
            this.triggers = triggers;
        }

        public List<DaveTrigger> getTriggers() {
            return triggers;
        }

        public void setTriggers(List<DaveTrigger> triggers) {
            this.triggers = triggers;
        }
    }
}
