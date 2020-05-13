package com.darwinreforged.server.modules.extensions.chat.dave;

import com.darwinreforged.server.core.types.living.DarwinPlayer;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.util.DiscordUtils;
import com.darwinreforged.server.core.util.PlayerUtils;

import java.util.Optional;
import java.util.Random;

/**
 The type Dave raw utils.
 */
public class DaveRawUtils {

    /**
     Gets associated trigger.

     @param message
     the message

     @return the associated trigger
     */
    public static DaveTrigger getAssociatedTrigger(String message) {
        Optional<DaveChatModule> chatModuleOptional = DarwinServer.getModule(DaveChatModule.class);
        if (chatModuleOptional.isPresent()) {
            for (DaveTrigger entry : chatModuleOptional.get().getConfigurationUtil().getTriggers()) {
                for (String trigger : entry.getTrigger()) {
                    boolean containsAll = true;

                    for (String keyword : trigger.split(","))
                        if (!message.toLowerCase().replaceAll(",", "").contains(keyword.toLowerCase())) {
                            containsAll = (false);
                            break;
                        }

                    if (containsAll) return entry;
                }
            }
        }

        return null;
    }

    /**
     Parse place holders string.

     @param message
     the message
     @param unparsedResponse
     the unparsed response
     @param playername
     the playername

     @return the string
     */
    public static String parsePlaceHolders(String message, String unparsedResponse, String playername) {
        String parsedResponse = unparsedResponse.replaceAll("<player>", playername);
        DiscordUtils du = DarwinServer.getUtilChecked(DiscordUtils.class);


        if (parsedResponse.contains("<mention>")) {
            boolean replaced = false;
            for (String partial : message.split(" ")) {
                if (partial.startsWith("<@") && partial.length() > 2) {
                    String mention = du.getUserById(partial.replaceFirst("<@", "").replaceFirst(">", "")).getName();
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
            PlayerUtils pu = DarwinServer.getUtilChecked(PlayerUtils.class);
            int index = new Random().nextInt(pu.getOnlinePlayers().size());
            String randomPlayer = ((DarwinPlayer) pu.getOnlinePlayers().toArray()[index]).getName();

            if (randomPlayer != null) parsedResponse = parsedResponse.replaceAll("<random>", randomPlayer);
            else parsedResponse = parsedResponse.replaceAll("<random>", playername + "*");
        }

        return parsedResponse;
    }

    /**
     Parse website link string.

     @param unparsedLink
     the unparsed link

     @return the string
     */
    public static String parseWebsiteLink(String unparsedLink) {
        String parsedLink = unparsedLink.replaceAll("\\[", "").replaceAll("]", "");
        if (!parsedLink.startsWith("http://") && !parsedLink.startsWith("https://"))
            parsedLink = "http://" + parsedLink;
        return parsedLink;
    }
}
