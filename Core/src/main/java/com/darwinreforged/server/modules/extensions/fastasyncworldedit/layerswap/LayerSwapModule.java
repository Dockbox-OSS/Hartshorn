package com.darwinreforged.server.modules.extensions.fastasyncworldedit.layerswap;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.config.BBC;
import com.boydti.fawe.object.FawePlayer;
import com.darwinreforged.server.core.events.internal.server.ServerStartedEvent;
import com.darwinreforged.server.core.events.util.Listener;
import com.darwinreforged.server.core.modules.Module;
import com.darwinreforged.server.core.tuple.Tuple;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.Logging;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.command.MethodCommands;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.patterns.Pattern;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.command.parametric.Optional;

import java.util.Arrays;
import java.util.List;

import static com.sk89q.minecraft.util.commands.Logging.LogMode.REGION;

// TODO : Remove WorldEdit command annotations
@Module(id = "layerswap", name = "LayerSwap", description = "Quick way to replace layers with layers", authors = "GuusLieben")
@Command(aliases = {}, desc = "Commands that operate on regions: [More Info](http://wiki.sk89q.com/wiki/WorldEdit/Region_operations)")
public class LayerSwapModule extends MethodCommands {

    private static final List<Integer> vanillaRenderIds = Arrays.asList(78, 8, 9, 10, 11);

    @Listener
    public void onServerStart(ServerStartedEvent event) {
        FaweAPI.registerCommands(this);
    }

    @SuppressWarnings("rawtypes")
    @Command(
            aliases = {"layerswap", "/ls", "/layerswap", "/lswap"},
            usage = "<from> <to>",
            desc = "Replace all layer blocks in the selection",
            flags = "f",
            min = 2,
            max = 2
    )
    @CommandPermissions("worldedit.region.replace")
    @Logging(REGION)
    public void replace(
            FawePlayer player,
            @Selection Region region,
            EditSession editSession,
            @Optional({"0"}) String from,
            @Optional({"0"}) String to,
            CommandContext context) {
        try {
            player.checkConfirmationRegion(() -> {
                int cubes = player.getSelection().getChunkCubes().size();
                player.sendMessage(String.format("§7[] §3Calculating LayerSwap actions §b(%d cube" + (cubes == 1 ? ")" : "s)"), cubes));

                String blockFormatNoPattern = to.replaceFirst("#.+\\[", "");
                while (blockFormatNoPattern.endsWith("]"))
                    blockFormatNoPattern = blockFormatNoPattern.substring(0, blockFormatNoPattern.length() - 1);
                String[] blockFormatNoPatterns = blockFormatNoPattern.split(",");

                int affected = 0;
                for (int data = 0; data < 16; data += 2) {
                    BlockMask fromMask = getFromMask(from, data, player);
                    if (fromMask == null) {
                        player.sendMessage("&7[] &cMask &4from &chas to include block data (e.g. 78:0)");
                        return;
                    }

                    Pattern toPattern = getToPattern(blockFormatNoPatterns, to, prepareParserContext(player), data);
                    if (toPattern == null) {
                        player.sendMessage("&7[] &cPattern &4to &chas to include block data (e.g. 78:0)");
                        return;
                    }
                    affected += editSession.replaceBlocks(region, fromMask, toPattern);
                }
                BBC.VISITOR_BLOCK.send(player, affected);
            }, getArguments(context), region, context);
        } catch (WorldEditException e) {
            player.sendMessage(String.format("§7[] §cAn error occurred : %s", e.getMessage()));
        }
    }

    @SuppressWarnings("rawtypes")
    public BlockMask getFromMask(String from, int data, FawePlayer player) {
        String[] fromBlocks = from.split(",");
        BaseBlock[] baseBlocks = new BaseBlock[fromBlocks.length];
        for (int i = 0; i < fromBlocks.length; i++) {
            String fromBlock = fromBlocks[i];
            Tuple<Integer, Integer> fromBlockTuple = getBaseData(fromBlock);

            if (fromBlockTuple == null) return null;

            int fromBlockId = fromBlockTuple.getFirst();
            int fromBlockData = fromBlockTuple.getSecond();

            boolean fromVanilla = vanillaRenderIds.contains(fromBlockId);
            boolean inverse = (fromBlockId >= 8 && fromBlockId <= 11);

            BaseBlock fromBaseBlock = new BaseBlock(fromBlockId, inverse ? (7-(data/2)) : ((fromVanilla ? data / 2 : data) + fromBlockData));
            baseBlocks[i] = fromBaseBlock;
        }

        return new BlockMask(player.getWorldForEditing(), baseBlocks);
    }

    public Pattern getToPattern(String[] blockFormatNoPatterns, String blockFormat, ParserContext parserContext, int iteration) throws InputParseException {
        for (int i = 0; i < blockFormatNoPatterns.length; i++) {
            String blockFormatNoPattern = blockFormatNoPatterns[i];
            Tuple<Integer, Integer> blockBaseData = getBaseData(blockFormatNoPattern);

            if (blockBaseData == null) return null;

            int baseBlockId = blockBaseData.getFirst();
            int baseBlockData = blockBaseData.getSecond();

            boolean isVanilla = vanillaRenderIds.contains(baseBlockId);
            boolean inverse = (baseBlockId >= 8 && baseBlockId <= 11);

            String iterationFormat = String.format("%d:%d", baseBlockId, inverse ? (7-(iteration/2)) : (baseBlockData + (isVanilla ? iteration / 2 : iteration)));

            if (blockFormatNoPatterns.length > 1 && i < (blockFormatNoPatterns.length-1)) // We have more than one in the list, and we are not in the last iteration
                blockFormat = blockFormat.replaceFirst(blockFormatNoPattern + ",", iterationFormat + ",");
            else blockFormat = blockFormat.replaceFirst(blockFormatNoPattern, iterationFormat); // We have one in the list or are in the last iteration
        }

        return (Pattern) WorldEdit.getInstance().getPatternFactory().parseFromInput(blockFormat, parserContext);
    }

    @SuppressWarnings("rawtypes")
    public ParserContext prepareParserContext(FawePlayer player) {
        ParserContext parserContext = new ParserContext();
        parserContext.setActor(player.getPlayer());
        parserContext.setExtent(player.getWorld());
        parserContext.setSession(player.getSession());
        parserContext.setWorld(player.getWorld());
        return parserContext;
    }

    public Tuple<Integer, Integer> getBaseData(String blockFormat) {
        if (blockFormat.contains("%") && blockFormat.split("%").length > 1)
            blockFormat = blockFormat.split("%")[1];

        if (blockFormat.equals("snow")) return new Tuple<>(78, 0);
        if (blockFormat.equals("water")) return new Tuple<>(8, 0);
        if (blockFormat.equals("lava")) return new Tuple<>(10, 0);

        String[] blockMaskParts = blockFormat.split(":");
        if (blockMaskParts.length != 2) return null;

        String blockIdMaskStr = blockMaskParts[0];
        String blockDataMaskStr = blockMaskParts[1];
        int blockIdMask = Integer.parseInt(blockIdMaskStr);
        int blockDataMask = Integer.parseInt(blockDataMaskStr);
        if (blockDataMask % 2 == 0) blockDataMask = 0;
        else blockDataMask = 1;

        return new Tuple<>(blockIdMask, blockDataMask);
    }

}
