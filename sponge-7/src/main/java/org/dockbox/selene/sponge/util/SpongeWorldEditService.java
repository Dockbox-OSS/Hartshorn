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
import com.boydti.fawe.wrappers.FakePlayer;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.command.MethodCommands;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.session.ClipboardHolder;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.sponge.external.WrappedMask;
import org.dockbox.selene.sponge.external.WrappedPattern;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
import org.dockbox.selene.worldedit.WorldEditService;
import org.dockbox.selene.worldedit.region.Clipboard;
import org.dockbox.selene.worldedit.region.Mask;
import org.dockbox.selene.worldedit.region.Pattern;
import org.dockbox.selene.worldedit.region.Region;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({ "MagicNumber", "deprecation" })
public class SpongeWorldEditService extends MethodCommands implements WorldEditService {

    private static com.sk89q.worldedit.function.mask.Mask toMask(Collection<Item> mask, Player cause) {
        Extent extent = SpongeConversionUtil.toWorldEdit(cause.getWorld());
        List<BaseBlock> baseBlocks = deriveBlocks(mask, cause);
        return new BlockMask(extent, baseBlocks);
    }

    @Override
    public Exceptional<Region> getPlayerSelection(Player player) {
        return SpongeWorldEditService.wrapPlayer(player)
                .map(FawePlayer::getSelection)
                .map(SpongeConversionUtil::fromWorldEdit);
    }

    @Override
    public void setPlayerSelection(Player player, Region region) {
        SpongeWorldEditService.wrapPlayer(player).present(fawePlayer -> fawePlayer.setSelection(SpongeConversionUtil.toWorldEdit(region)));
    }

    @Override
    public Exceptional<Clipboard> getPlayerClipboard(Player player) {
        return SpongeWorldEditService.wrapPlayer(player)
                .map(FawePlayer::getSession)
                .map(session -> {
                    try {
                        return session.getClipboard();
                    }
                    catch (Exception e) {
                        //noinspection ReturnOfNull
                        return null;
                    }
                })
                .map(clipboardHolder -> clipboardHolder.getClipboards().get(0))
                .map(SpongeConversionUtil::fromSponge);
    }

    @Override
    public void setPlayerClipboard(Player player, Clipboard clipboard) {
        SpongeWorldEditService.wrapPlayer(player).present(fawePlayer -> fawePlayer.getSession()
                .setClipboard(new ClipboardHolder(
                        SpongeConversionUtil.toWorldEdit(clipboard),
                        SpongeConversionUtil.toWorldEdit(clipboard.getRegion().getWorld())
                                .getWorldData()))
        );
    }

    @Override
    public void replace(Region region, Mask mask, Pattern pattern, Player cause) {
        this.checkConfirmationRegion(cause, (player, session) -> session.replaceBlocks(
                SpongeConversionUtil.toWorldEdit(region),
                SpongeConversionUtil.toWorldEdit(mask),
                SpongeConversionUtil.toWorldEdit(pattern))
        );
    }

    @Override
    public void set(Region region, Pattern pattern, Player cause) {
        this.checkConfirmationRegion(cause, (player, session) -> session.setBlocks(
                SpongeConversionUtil.toWorldEdit(region),
                SpongeConversionUtil.toWorldEdit(pattern))
        );
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
        this.checkConfirmationRegion(cause, (player, session) ->
                session.setBlocks(SpongeConversionUtil.toWorldEdit(region), toPattern(pattern, cause)));
    }

    @Override
    public boolean hasActiveGmask(Player player) {
        return SpongeConversionUtil.toWorldEdit(player).getSession().getMask() != null;
    }

    @Override
    public Exceptional<Mask> getGmask(Player player) {
        FawePlayer<?> fawePlayer = SpongeConversionUtil.toWorldEdit(player);
        return Exceptional.of(fawePlayer.getSession().getMask()).map(WrappedMask::new);
    }

    @Override
    public void setGmask(Player player, Mask mask) {
        com.sk89q.worldedit.function.mask.Mask worldEditMask = null;
        if (mask != null) {
            worldEditMask = SpongeConversionUtil.toWorldEdit(mask);
        }
        SpongeConversionUtil.toWorldEdit(player).getSession().setMask(worldEditMask);
    }

    private static com.sk89q.worldedit.function.pattern.Pattern toPattern(Collection<Item> pattern, Player cause) {
        List<BaseBlock> baseBlocks = deriveBlocks(pattern, cause);
        double chance = 100d / (double) baseBlocks.size();
        RandomPattern blockPattern = new RandomPattern();
        baseBlocks.forEach(baseBlock -> blockPattern.add(baseBlock, chance));
        return blockPattern;
    }

    private static List<BaseBlock> deriveBlocks(Collection<Item> items, Player cause) {
        ParserContext context = prepareContext(cause);
        return items.stream()
                .map(item -> SpongeConversionUtil.toWorldEdit(item, context))
                .filter(Exceptional::present)
                .map(Exceptional::get)
                .collect(Collectors.toList());
    }

    public static ParserContext prepareContext(Player cause) {
        ParserContext context = new ParserContext();
        context.setPreferringWildcard(true);
        Actor actor = FakePlayer.getConsole();
        if (null != cause) {
            FawePlayer<?> player = SpongeConversionUtil.toWorldEdit(cause);
            // setWorld also targets setExtent
            context.setWorld(player.getWorldForEditing());
            context.setSession(player.getSession());
            actor = player.getPlayer();
        } else {
            context.setWorld(FakePlayer.getConsole().getWorld());
        }
        context.setActor(actor);
        return context;
    }

    private void checkConfirmationRegion(Player cause, RegionExecutor consumer) {
        try {
            FawePlayer<?> player = SpongeConversionUtil.toWorldEdit(cause);
            CommandContext context = new CommandContext("");
            EditSession session = player.getNewEditSession();
            // Disallow fast mode internally
            session.setFastMode(false);
            // Ensure the area is within the bounds, or request the player to confirm the action
            player.checkConfirmationRegion(() -> {
                        int affected = consumer.accept(player, session);
                        BBC.VISITOR_BLOCK.send(player, affected);
                    },
                    super.getArguments(context),
                    player.getSelection(),
                    context);
            // Flush the queue once we're done. This ensures the region update is also corrected on the
            // client.
            session.flushQueue();
        }
        catch (CommandException | WorldEditException e) {
            Selene.handle(e);
        }
    }

    private static Exceptional<FawePlayer<?>> wrapPlayer(Player player) {
        if (player instanceof SpongePlayer) {
            return ((SpongePlayer) player).getReference().map(FawePlayer::wrap);
        }
        return Exceptional.none();
    }

    @FunctionalInterface
    private interface RegionExecutor {
        int accept(FawePlayer<?> player, EditSession session);
    }
}
