package com.darwinreforged.server.modules.extensions.plotsquared.oldplot;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.chat.Pagination;
import com.darwinreforged.server.core.chat.Pagination.PaginationBuilder;
import com.darwinreforged.server.core.chat.Text;
import com.darwinreforged.server.core.commands.annotations.Command;
import com.darwinreforged.server.core.commands.annotations.Permission;
import com.darwinreforged.server.core.commands.context.CommandArgument;
import com.darwinreforged.server.core.commands.context.CommandContext;
import com.darwinreforged.server.core.files.FileManager;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.types.living.CommandSender;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Module(id = "oldplots", name = "OldPlots", description = "Retrieves a list of OldPlots for a given player", authors = "GuusLieben")
public class OldPlotModule {

    @Command(aliases = {"oldplots", "olp"}, usage = "oldplots <player>", desc = "Retrieves the list of old plots for a given player", context = "oldplots <player{String}>")
    @Permission(Permissions.OLP_LIST)
    public void getOldPlotList(CommandSender src, CommandContext ctx) {
        Optional<CommandArgument<String>> optionalPlayerArg = ctx.getStringArgument("player");
        if (!optionalPlayerArg.isPresent()) {
            src.sendMessage(Translations.ARGUMENT_NOT_PROVIDED.f("player"), false);
            return;
        }

        String playerName = optionalPlayerArg.get().getValue();

        FileManager fm = DarwinServer.get(FileManager.class);
        Path dataDir = fm.getDataDirectory(this);
        File file = new File(dataDir.toFile(), "oldplots.db");

        if (!file.exists()) {
            src.sendMessage(Translations.OLP_NO_STORAGE_FILE.s(), false);
            return;
        }

        Dao<PlotStorageModel, Integer> plotStorageModelDao = fm.getDataDb(PlotStorageModel.class, file);

        Map<String, Object> res = fm.getYamlDataForUrl(String.format("https://api.mojang.com/users/profiles/minecraft/%s", playerName));
        if (res.containsKey("id")) {
            Map<String, Object> playerToFind = new HashMap<>();
            UUID playerUuid = UUID.fromString(
                    res.get("id").toString()
                            .replaceFirst(
                                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                            ));
            playerToFind.put("owner", playerUuid);
            try {
                List<PlotStorageModel> plotStorageModels = plotStorageModelDao.queryForFieldValues(playerToFind);
                List<String> foundPlots = new ArrayList<>();
                List<Text> paginationContent = new ArrayList<>();
                plotStorageModels.forEach(psm -> {
                    String plotLoc = String.format("%s,%s;%s", psm.getWorld(), psm.getPlot_id_x(), psm.getPlot_id_z());
                    if (!psm.getWorld().equals("*") && !foundPlots.contains(plotLoc)) {
                        Text singlePlot = Text.of(Translations.OLP_LIST_ITEM.f(psm.getId(), psm.getWorld(), psm.getPlot_id_x(), psm.getPlot_id_z()));
                        paginationContent.add(singlePlot);
                        foundPlots.add(plotLoc);
                    }
                });
                Pagination pagination = PaginationBuilder.builder().contents(paginationContent).title(Text.of(Translations.OLP_LIST_HEADER.f(playerName))).build();
                pagination.sendTo(src);
            } catch (SQLException e) {
                DarwinServer.error("Failed to read OldPlots database", e);
                src.sendMessage(Translations.OLP_FAILED_READ, false);
            }
        } else {
            src.sendMessage(Translations.PLAYER_NOT_FOUND.f(playerName), false);
        }

    }

}
