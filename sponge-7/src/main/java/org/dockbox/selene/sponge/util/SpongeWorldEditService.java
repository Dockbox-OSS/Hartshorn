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
import com.sk89q.worldedit.session.ClipboardHolder;

import org.dockbox.selene.core.external.WorldEditService;
import org.dockbox.selene.core.external.region.Clipboard;
import org.dockbox.selene.core.external.region.Region;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;

public class SpongeWorldEditService implements WorldEditService {

    @Override
    public Exceptional<Region> getPlayerSelection(Player player) {
        return this.wrapPlayer(player)
                .map(FawePlayer::getSelection)
                .map(SpongeConversionUtil::fromSponge);
    }

    @Override
    public void setPlayerSelection(Player player, Region region) {
        this.wrapPlayer(player).ifPresent(fawePlayer ->
                fawePlayer.setSelection(SpongeConversionUtil.toSponge(region))
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
                    SpongeConversionUtil.toSponge(clipboard),
                    SpongeConversionUtil.toWorldEdit(clipboard.getRegion().getWorld()).getWorldData()
            ));
        });
    }

    private Exceptional<FawePlayer<?>> wrapPlayer(Player player) {
        if (player instanceof SpongePlayer) {
            return ((SpongePlayer) player).getReference().map(FawePlayer::wrap);
        }
        return Exceptional.empty();
    }
}
