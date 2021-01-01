/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.objects.bossbar;

import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.util.SeleneUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a server-controlled bossbar instance.
 */
public interface Bossbar {

    /**
     * Shows the bossbar instance to the given {@link Player} indefinitely.
     *
     * @param player
     *         The player to display the bossbar to.
     */
    void showTo(Player player);

    /**
     * Shows the bossbar instance to the given {@link Player} for the given {@code duration}.
     *
     * @param player
     *         The player to display the bossbar to.
     * @param duration
     *         The time duration to show the bossbar.
     */
    void showTo(Player player, Duration duration);

    /**
     * Hides/removes the bossbar instance from the given {@link Player}. If the bossbar was not visible to the player
     * typically nothing is done.
     *
     * @param player
     *         The player to hide the bossbar from.
     */
    void hideFrom(Player player);

    /**
     * Shows the bossbar instance to the given {@link Player players} indefinitely.
     *
     * @param players
     *         The collection of players to display the bossbar to.
     */
    void showTo(Collection<Player> players);

    /**
     * Hides/removes the bossbar instance from the given {@link Player players}. If the bossbar was not visible to one
     * or more of the players typically nothing is done for that specific player.
     *
     * @param players
     *         The collection of players to hide the bossbar from.
     */
    void hideFrom(Collection<Player> players);

    /**
     * Returns the ID of the bossbar, typically this is registered in {@link Bossbar#REGISTRY} and a instance can be
     * obtained using {@link Bossbar#get(String)}. The ID does not have to be unique, making it so the instance in the
     * {@link Bossbar#REGISTRY} may differ from the current instance.
     *
     * @return the id
     */
    String getId();

    /**
     * Gets the progress of the bossbar in a percentage between 0 and 100 (inclusive).
     *
     * @return The progress of the bossbar as a percentage.
     */
    float getPercent();

    /**
     * Sets the progress of the bossbar in a percentage between 0 and 100 (inclusive).
     *
     * @param percent
     *         The progress of the bossbar as a percentage.
     */
    void setPercent(float percent);

    /**
     * Gets the {@link Text} displayed by the current instance.
     *
     * @return The text displayed by the bossbar.
     */
    Text getText();

    /**
     * Sets the {@link Text} to be displayed by the current instance.
     *
     * @param text
     *         The text to be displayed.
     */
    void setText(Text text);

    /**
     * Gets the color of the current instance.
     *
     * @return The color of the bossbar.
     */
    BossbarColor getColor();

    /**
     * Sets the color of the current instance.
     *
     * @param color
     *         The color of the bossbar.
     */
    void setColor(BossbarColor color);

    /**
     * Gets the style of the current instance.
     *
     * @return The style of the bossbar.
     */
    BossbarStyle getStyle();

    /**
     * Sets the style of the current instance.
     *
     * @param style
     *         The style of the bossbar.
     */
    void setStyle(BossbarStyle style);

    /**
     * Returns all {@link Player players} to which the current instance is displayed to.
     *
     * @return All players which can see the bossbar.
     */
    Collection<Player> visibleTo();

    /**
     * Returns {@code true} if the current instance is visible/shown to the given {@link Player}, else {@code false}.
     *
     * @param player
     *         The player
     *
     * @return {@code true} of the given player can see the bossbar.
     */
    boolean isVisibleTo(Player player);

    /**
     * Returns {@code true} if the current instance is visible/shown to the {@link Player} associated with the given
     * {@link UUID}, else {@code false}.
     *
     * @param player
     *         The UUID representation of a player.
     *
     * @return {@code true} of the given player can see the bossbar.
     */
    boolean isVisibleTo(UUID player);

    /**
     * Returns {@code true} if the current instance is visible/shown to the {@link Player} associated with the given
     * {@code name}, else {@code false}.
     *
     * @param name
     *         The name of a player.
     *
     * @return {@code true} of the given player can see the bossbar.
     */
    boolean isVisibleTo(String name);

    /**
     * Returns a assisted {@link BossbarBuilder builder instance}.
     *
     * @return A new builder instance.
     */
    static BossbarBuilder builder() {
        return new BossbarBuilder();
    }

    /**
     * The registry of all active {@link Bossbar bossbars}. Typically this only contains bossbars which are currently
     * visible to at least one player.
     */
    static final Map<String, Bossbar> REGISTRY = SeleneUtils.COLLECTION.emptyConcurrentMap();

    /**
     * Returns a {@link Bossbar} instance based on a given {@code id}. This identifier typically matches with the one
     * returned by {@link Bossbar#getId()}. If no instance exists, a empty {@link Exceptional} is returned.
     *
     * @param id
     *         The identifier of the potential bossbar
     *
     * @return The bossbar wrapped in a {@link Exceptional}, or empty.
     */
    static Exceptional<Bossbar> get(String id) {
        return Exceptional.of(() -> REGISTRY.get(id));
    }

    /**
     * Returns a {@link Bossbar} instance based on a given {@link UUID uuid}. This identifier typically matches with the
     * one returned by {@link Bossbar#getId()}. If no instance exists, a empty {@link Exceptional} is returned.
     *
     * @param uuid
     *         The unique identifier of the potential bossbar
     *
     * @return The bossbar wrapped in a {@link Exceptional}, or empty.
     */
    static Exceptional<Bossbar> get(UUID uuid) {
        return get(uuid.toString());
    }
}
