/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.util;

import com.boydti.fawe.config.BBC;
import com.boydti.fawe.object.FawePlayer;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.command.MethodCommands;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.session.ClipboardHolder;

import org.dockbox.selene.worldedit.WorldEditService;
import org.dockbox.selene.worldedit.region.Mask;
import org.dockbox.selene.worldedit.region.Pattern;
import org.dockbox.selene.worldedit.region.Clipboard;
import org.dockbox.selene.worldedit.region.Region;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.sponge.external.WrappedMask;
import org.dockbox.selene.sponge.external.WrappedPattern;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"MagicNumber", "deprecation"})
public class SpongeWorldEditService extends MethodCommands implements WorldEditService {

    @Override
    public Exceptional<Region> getPlayerSelection(Player player) {
        return this.wrapPlayer(player)
                .map(FawePlayer::getSelection)
                .map(SpongeConversionUtil::fromWorldEdit);
    }

    @Override
    public void setPlayerSelection(Player player, Region region) {
        this.wrapPlayer(player).ifPresent(fawePlayer ->
                fawePlayer.setSelection(SpongeConversionUtil.toWorldEdit(region))
        );
    }

    @Override
    public Exceptional<Clipboard> getPlayerClipboard(Player player) {
        return this.wrapPlayer(player)
                .map(FawePlayer::getSession)
                .map(session -> {
                    try {
                        return session.getClipboard();
                    } catch (Exception e) {
                        //noinspection ReturnOfNull
                        return null;
                    }
                })
                .map(clipboardHolder -> clipboardHolder.getClipboards().get(0))
                .map(SpongeConversionUtil::fromSponge);
    }

    @Override
    public void setPlayerClipboard(Player player, Clipboard clipboard) {
        this.wrapPlayer(player).ifPresent(fawePlayer ->
                fawePlayer.getSession().setClipboard(new ClipboardHolder(
                        SpongeConversionUtil.toWorldEdit(clipboard),
                        SpongeConversionUtil.toWorldEdit(clipboard.getRegion().getWorld()).getWorldData()
                ))
        );
    }

    @Override
    public void replace(Region region, Mask mask, Pattern pattern, Player cause) {
        this.checkConfirmationRegion(cause, (player, session) -> session.replaceBlocks(
                SpongeConversionUtil.toWorldEdit(region),
                SpongeConversionUtil.toWorldEdit(mask),
                SpongeConversionUtil.toWorldEdit(pattern)
        ));
    }

    @Override
    public void set(Region region, Pattern pattern, Player cause) {
        this.checkConfirmationRegion(cause, (player, session) -> session.setBlocks(
                SpongeConversionUtil.toWorldEdit(region),
                SpongeConversionUtil.toWorldEdit(pattern)
        ));
    }

    @Override
    public Exceptional<Pattern> parsePattern(String pattern, Player cause) {
        return Exceptional.of(() -> {
            ParserContext context = prepareContext(cause);
            com.sk89q.worldedit.function.pattern.Pattern worldEditPattern =
                    WorldEdit.getInstance().getPatternFactory().parseFromInput(pattern, context);
            return new WrappedPattern(worldEditPattern);
        });
    }

    @Override
    public Exceptional<Mask> parseMask(String mask, Player cause) {
        return Exceptional.of(() -> {
            ParserContext context = prepareContext(cause);
            com.sk89q.worldedit.function.mask.Mask worldEditMask =
                    WorldEdit.getInstance().getMaskFactory().parseFromInput(mask, context);
            return new WrappedMask(worldEditMask);
        });
    }

    @Override
    public void replace(Region region, Collection<Item> mask, Collection<Item> pattern, Player cause) {
        this.checkConfirmationRegion(cause, (player, session) -> session.replaceBlocks(
                SpongeConversionUtil.toWorldEdit(region),
                toMask(mask, cause),
                toPattern(pattern, cause)
        ));
    }

    @Override
    public void set(Region region, Collection<Item> pattern, Player cause) {
        this.checkConfirmationRegion(cause, (player, session) -> session.setBlocks(
                SpongeConversionUtil.toWorldEdit(region),
                toPattern(pattern, cause)
        ));
    }

    private void checkConfirmationRegion(Player cause, RegionExecutor consumer) {
        try {
            FawePlayer<?> player = SpongeConversionUtil.toWorldEdit(cause);
            CommandContext context = new CommandContext("");
            EditSession session = player.getNewEditSession();
            session.setFastMode(false);
            player.checkConfirmationRegion(
                    () -> {
                        int affected = consumer.accept(player, session);
                        BBC.VISITOR_BLOCK.send(player, affected);
                    },
                    super.getArguments(context),
                    player.getSelection(),
                    context
            );
        } catch (CommandException | WorldEditException e) {
            Selene.handle(e);
        }
    }

    private Exceptional<FawePlayer<?>> wrapPlayer(Player player) {
        if (player instanceof SpongePlayer) {
            return ((SpongePlayer) player).getReference().map(FawePlayer::wrap);
        }
        return Exceptional.empty();
    }

    private static com.sk89q.worldedit.function.pattern.Pattern toPattern(Collection<Item> pattern, Player cause) {
        List<BaseBlock> baseBlocks = deriveBlocks(pattern, cause);
        double chance = 100d / (double) baseBlocks.size();
        RandomPattern blockPattern = new RandomPattern();
        baseBlocks.forEach(baseBlock -> blockPattern.add(baseBlock, chance));
        return blockPattern;
    }

    private static com.sk89q.worldedit.function.mask.Mask toMask(Collection<Item> mask, Player cause) {
        Extent extent = SpongeConversionUtil.toWorldEdit(cause.getWorld());
        List<BaseBlock> baseBlocks = deriveBlocks(mask, cause);
        return new BlockMask(extent, baseBlocks);
    }

    private static List<BaseBlock> deriveBlocks(Collection<Item> items, Player cause) {
        ParserContext context = prepareContext(cause);
        return items.stream()
                .map(item -> SpongeConversionUtil.toWorldEdit(item, context))
                .filter(Exceptional::isPresent)
                .map(Exceptional::get)
                .collect(Collectors.toList());
    }

    private static ParserContext prepareContext(Player cause) {
        ParserContext context = new ParserContext();
        context.setPreferringWildcard(true);
        if (null != cause) {
            FawePlayer<?> player = SpongeConversionUtil.toWorldEdit(cause);
            context.setActor(player.getPlayer());
            // setWorld also targets setExtent
            context.setWorld(player.getWorldForEditing());
            context.setSession(player.getSession());
        }
        return context;
    }

    @FunctionalInterface
    private interface RegionExecutor {
        int accept(FawePlayer<?> player, EditSession session);
    }
}
