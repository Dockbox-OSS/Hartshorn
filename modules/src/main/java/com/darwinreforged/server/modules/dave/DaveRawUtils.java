package com.darwinreforged.server.modules.dave;

import com.darwinreforged.server.sponge.DarwinServer;
import com.magitechserver.magibridge.MagiBridge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

public class DaveRawUtils {

    public static Entry<?,?> getAssociatedTrigger(String message) {
        Optional<DaveChatModule> chatModuleOptional = DarwinServer.getModule(DaveChatModule.class);
        if (chatModuleOptional.isPresent()) {
            for (Entry<Object, Object> entry : chatModuleOptional.get().getMessagesProperties().entrySet()) {
                String fullTrigger = entry.getKey().toString().replaceFirst("<!important>", "");

                for (String trigger : fullTrigger.split("<>")) {

                    boolean containsAll = true;

                    for (String keyword : trigger.split(","))
                        if (!message.toLowerCase().replaceAll(",", "").contains(keyword.toLowerCase()))
                            containsAll = (false);

                    if (containsAll) return entry;
                }
            }
        }

        return null;
    }

    public static String parsePlaceHolders(String message, String unparsedResponse, String playername) {
        String parsedResponse = unparsedResponse.replaceAll("<player>", playername);

        if (parsedResponse.contains("<mention>")) {
            boolean replaced = false;
            for (String partial : message.split(" ")) {
                if (partial.startsWith("<@") && partial.length() > 2) {
                    String mention = MagiBridge.jda.getUserById(partial.replaceFirst("<@", "").replaceFirst(">", "")).getName();
                    if (mention != null) {
                        parsedResponse = parsedResponse.replaceAll("<mention>", partial.replaceFirst("@", ""));
                        replaced = true;
                    }
                }

                if (partial.replaceAll("&.", "").startsWith("@") && partial.length() > 2) {
                    parsedResponse = parsedResponse.replaceAll("<mention>", partial.replaceFirst("@", ""));
                    replaced = true;
                }
            }

            if (!replaced) {
                parsedResponse = parsedResponse.replaceAll("<mention>", playername);
            }
        }

        if (parsedResponse.contains("<random>")) {
            int index = new Random().nextInt(Sponge.getServer().getOnlinePlayers().size());
            String randomPlayer = ((Player) Sponge.getServer().getOnlinePlayers().toArray()[index]).getName();

            if (randomPlayer != null) parsedResponse = parsedResponse.replaceAll("<random>", randomPlayer);
            else parsedResponse = parsedResponse.replaceAll("<random>", playername + "*");
        }

        return parsedResponse;
    }

    public static String parseWebsiteLink(String unparsedLink) {
        String parsedLink = unparsedLink.replaceAll("\\[", "").replaceAll("]", "");
        if (!parsedLink.startsWith("http://") && !parsedLink.startsWith("https://"))
            parsedLink = "http://" + parsedLink;
        return parsedLink;
    }
}
