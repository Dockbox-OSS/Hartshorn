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

import com.boydti.fawe.object.FawePlayer;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.session.ClipboardHolder;

import org.dockbox.selene.core.external.WorldEditService;
import org.dockbox.selene.core.external.pattern.Mask;
import org.dockbox.selene.core.external.pattern.Pattern;
import org.dockbox.selene.core.external.region.Clipboard;
import org.dockbox.selene.core.external.region.Region;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.sponge.external.WrappedMask;
import org.dockbox.selene.sponge.external.WrappedPattern;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;

import java.util.Collection;

public class SpongeWorldEditService implements WorldEditService {

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
        this.wrapPlayer(player).ifPresent(fawePlayer -> {
            fawePlayer.getSession().setClipboard(new ClipboardHolder(
                    SpongeConversionUtil.toWorldEdit(clipboard),
                    SpongeConversionUtil.toWorldEdit(clipboard.getRegion().getWorld()).getWorldData()
            ));
        });
    }

    @Override
    public void replace(Region region, Mask mask, Pattern pattern, Player cause) {
        FawePlayer<?> player = SpongeConversionUtil.toWorldEdit(cause);
        EditSession session = player.getNewEditSession();
        session.replaceBlocks(
                SpongeConversionUtil.toWorldEdit(region),
                SpongeConversionUtil.toWorldEdit(mask),
                SpongeConversionUtil.toWorldEdit(pattern)
        );
    }

    @Override
    public void set(Region region, Pattern pattern, Player cause) {
        FawePlayer<?> player = SpongeConversionUtil.toWorldEdit(cause);
        EditSession session = player.getNewEditSession();
        session.setBlocks(
                SpongeConversionUtil.toWorldEdit(region),
                SpongeConversionUtil.toWorldEdit(pattern)
        );
    }

    @Override
    public Exceptional<Pattern> parsePattern(String pattern) {
        return Exceptional.of(() -> {
            ParserContext context = new ParserContext();
            context.setPreferringWildcard(true);
            context.setRestricted(false);
            com.sk89q.worldedit.function.pattern.Pattern worldEditPattern =
                    WorldEdit.getInstance().getPatternFactory().parseFromInput(pattern, context);
            return new WrappedPattern(worldEditPattern);
        });
    }

    @Override
    public Exceptional<Mask> parseMask(String mask) {
        return Exceptional.of(() -> {
            ParserContext context = new ParserContext();
            context.setPreferringWildcard(true);
            context.setRestricted(false);
            com.sk89q.worldedit.function.mask.Mask worldEditMask =
                    WorldEdit.getInstance().getMaskFactory().parseFromInput(mask, context);
            return new WrappedMask(worldEditMask);
        });
    }

    @Override
    public void replace(Region region, Collection<Item> mask, Collection<Item> pattern, Player cause) {
        FawePlayer<?> player = SpongeConversionUtil.toWorldEdit(cause);
        EditSession session = player.getNewEditSession();
        session.replaceBlocks(
                SpongeConversionUtil.toWorldEdit(region),
                toMask(mask),
                toPattern(pattern)
        );
    }

    @Override
    public void set(Region region, Collection<Item> pattern, Player cause) {
        FawePlayer<?> player = SpongeConversionUtil.toWorldEdit(cause);
        EditSession session = player.getNewEditSession();
        session.setBlocks(
                SpongeConversionUtil.toWorldEdit(region),
                toPattern(pattern)
        );
    }

    private Exceptional<FawePlayer<?>> wrapPlayer(Player player) {
        if (player instanceof SpongePlayer) {
            return ((SpongePlayer) player).getReference().map(FawePlayer::wrap);
        }
        return Exceptional.empty();
    }

    private static com.sk89q.worldedit.function.pattern.Pattern toPattern(Collection<Item> pattern) {
        // TODO GuusLieben
        return null;
    }

    private static com.sk89q.worldedit.function.mask.Mask toMask(Collection<Item> mask) {
        // TODO GuusLieben
        return null;
    }
}
