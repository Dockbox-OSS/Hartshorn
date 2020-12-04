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

package org.dockbox.selene.core.impl.command.convert;

import com.boydti.fawe.object.FawePlayer;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;

import org.dockbox.selene.core.command.context.CommandValue;
import org.dockbox.selene.core.command.parsing.ArgumentParser;
import org.dockbox.selene.core.command.parsing.TypeParser;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.i18n.common.ResourceService;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.PlayerStorageService;
import org.dockbox.selene.core.WorldStorageService;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class TypeArgumentParsers {

    public static class CharacterParser extends TypeParser<Character> {
        @NotNull
        @Override
        public Exceptional<Character> parse(@NotNull CommandValue<String> commandValue) {
            int length = commandValue.getValue().length();
            return 1 == length ? Exceptional.of(commandValue.getValue().charAt(0)) : Exceptional.empty();
        }
    }

    public static class BooleanParser extends TypeParser<Boolean> {
        @NotNull
        @Override
        public Exceptional<Boolean> parse(@NotNull CommandValue<String> commandValue) {
            return Exceptional.of(commandValue.getValue()).map(Boolean::parseBoolean);
        }
    }

    public static class DoubleParser extends TypeParser<Double> {
        @NotNull
        @Override
        public Exceptional<Double> parse(@NotNull CommandValue<String> commandValue) {
            return Exceptional.of(commandValue.getValue()).map(Double::parseDouble);
        }
    }

    public static class EnumParser extends ArgumentParser {
        @NotNull
        @SuppressWarnings({"unchecked", "CallToSuspiciousStringMethod"})
        public <A> Exceptional<A> parse(@NotNull CommandValue<String> commandValue, @NotNull Class<A> type) {
            if (type.isEnum()) {
                Enum<?>[] constants = (Enum<?>[]) type.getEnumConstants();
                return Exceptional.of(Arrays.stream(constants)
                        .filter(c -> c.name().equals(commandValue.getValue()))
                        .map(c -> (A) c)
                        .findFirst());
            }
            return Exceptional.empty();
        }
    }

    public static class FloatParser extends TypeParser<Float> {
        @NotNull
        @Override
        public Exceptional<Float> parse(@NotNull CommandValue<String> commandValue) {
            return Exceptional.of(commandValue.getValue()).map(Float::parseFloat);
        }
    }

    public static class IntegerParser extends TypeParser<Integer> {
        @NotNull
        @Override
        public Exceptional<Integer> parse(@NotNull CommandValue<String> commandValue) {
            return Exceptional.of(commandValue.getValue()).map(Integer::parseInt);
        }
    }

    public static class LanguageParser extends TypeParser<Language> {
        @NotNull
        @Override
        public Exceptional<Language> parse(@NotNull CommandValue<String> commandValue) {
            @NonNls String code = commandValue.getValue();
            Language lang;
            try {
                lang = Language.valueOf(code);
            } catch (NullPointerException | IllegalArgumentException e) {
                lang = Arrays.stream(Language.values())
                        .filter(l -> l.getNameEnglish().equals(code) || l.getNameLocalized().equals(code))
                        .findFirst()
                        .orElse(Selene.getInstance(GlobalConfig.class).getDefaultLanguage());
            }
            return Exceptional.of(lang);
        }
    }

    public static class LongParser extends TypeParser<Long> {
        @NotNull
        @Override
        public Exceptional<Long> parse(@NotNull CommandValue<String> commandValue) {
            return Exceptional.of(commandValue.getValue()).map(Long::parseLong);
        }
    }

    public static class ShortParser extends TypeParser<Short> {
        @NotNull
        @Override
        public Exceptional<Short> parse(@NotNull CommandValue<String> commandValue) {
            return Exceptional.of(commandValue.getValue()).map(Short::parseShort);
        }
    }

    public static class UuidParser extends TypeParser<UUID> {
        @NotNull
        @Override
        public Exceptional<UUID> parse(@NotNull CommandValue<String> commandValue) {
            return Exceptional.of(() -> UUID.fromString(commandValue.getValue()));
        }
    }

    public static class VectorParser extends TypeParser<Vector3N> {
        @NotNull
        @Override
        public Exceptional<Vector3N> parse(@NotNull CommandValue<String> commandValue) {
            return Exceptional.of(() -> {
                String[] xyz = commandValue.getValue().split(",");
                // IndexOutOfBounds is caught by Callable handle in Exceptional
                double x = Double.parseDouble(xyz[0]);
                double y = Double.parseDouble(xyz[1]);
                double z = Double.parseDouble(xyz[2]);
                return new Vector3N(x, y, z);
            });
        }
    }

    public static class LocationParser extends TypeParser<Location> {
        @NotNull
        @Override
        public Exceptional<Location> parse(@NotNull CommandValue<String> commandValue) {
            String[] xyzw = commandValue.getValue().split(",");
            String xyz = String.join(",", xyzw[0], xyzw[1], xyzw[2]);
            Vector3N vec = new VectorParser().parse(new CommandValue<String>(xyz, commandValue.getKey()) {
            }).orElse(new Vector3N(0, 0, 0));

            World world = new WorldParser().parse(new CommandValue<String>(xyzw[3], commandValue.getKey()) {
            }).orElse(World.empty());

            return Exceptional.of(new Location(vec, world));
        }
    }

    public static class WorldParser extends TypeParser<World> {
        @NotNull
        @Override
        public Exceptional<World> parse(@NotNull CommandValue<String> commandValue) {
            WorldStorageService wss = Selene.getInstance(WorldStorageService.class);
            Exceptional<World> world = wss.getWorld(commandValue.getValue());
            return world.orElseSupply(() -> {
                UUID uuid = UUID.fromString(commandValue.getValue());
                return wss.getWorld(uuid).orNull();
            });
        }
    }

    public static class ResourceEntryParser extends TypeParser<ResourceEntry> {
        @NotNull
        @Override
        public Exceptional<ResourceEntry> parse(@NotNull CommandValue<String> commandValue) {
            String value = commandValue.getValue();
            ResourceService rs = Selene.getInstance(ResourceService.class);
            value = rs.createValidKey(value);

            Exceptional<? extends ResourceEntry> or = rs.getExternalResource(value);
            if (or.isPresent()) return or.map(r -> (ResourceEntry) r);

            String finalValue = value;
            return Exceptional.of(() -> IntegratedResource.valueOf(finalValue));
        }
    }

    public static class PlayerParser extends TypeParser<Player> {
        @NotNull
        @Override
        public Exceptional<Player> parse(@NotNull CommandValue<String> commandValue) {
            PlayerStorageService pss = Selene.getInstance(PlayerStorageService.class);
            Exceptional<Player> player = pss.getPlayer(commandValue.getValue());
            return player.orElseSupply(() -> {
                UUID uuid = UUID.fromString(commandValue.getValue());
                return pss.getPlayer(uuid).orNull();
            });
        }
    }

    /**
     Simple implementation which allows parsing String arguments directly into a List. Uses a configurable delimiter
     to decide when to create a new entry, by default this is ','. Also allows you to use @MinMax attributes to set
     a minimum/maximum for sublist sizes.
     <p>
     Additionally, allows you to pass a {@link Function Function[String, R]} which parses the String values before they are are returned
     as a List.

     @param <R>
     The return type
     */
    public static class ListParser<R> extends TypeParser<List<R>> {

        private final Function<String, R> converter;
        private char delimiter = ',';

        public ListParser(Function<String, R> converter) {
            this.converter = converter;
        }

        public void setDelimiter(char delimiter) {
            this.delimiter = delimiter;
        }

        @SuppressWarnings("unchecked")
        @NotNull
        @Override
        public Exceptional<List<R>> parse(@NotNull CommandValue<String> commandValue) {
            String[] list = commandValue.getValue().split(this.delimiter + "");

            if (null != this.converter) {
                return Exceptional.of(Arrays.stream(list).map(this.converter).collect(Collectors.toList()));
            }

            // ClassCastException caught by Exceptional supplier
            return Exceptional.of(() -> (List<R>) SeleneUtils.asList(list));
        }
    }

    public static class MapParser extends TypeParser<Map<String, String>> {

        private char rowDelimiter = ',';
        private char valueDelimiter = '=';

        public void setRowDelimiter(char rowDelimiter) {
            if (this.valueDelimiter != rowDelimiter)
                this.rowDelimiter = rowDelimiter;
        }

        public void setValueDelimiter(char valueDelimiter) {
            if (this.rowDelimiter != valueDelimiter)
                this.valueDelimiter = valueDelimiter;
        }

        @NotNull
        @Override
        public Exceptional<Map<String, String>> parse(@NotNull CommandValue<String> commandValue) {
            if (this.rowDelimiter == this.valueDelimiter) {
                return Exceptional.of(new IllegalArgumentException("Row and value delimiters were equal while parsing map"));
            }

            Map<String, String> map = SeleneUtils.emptyConcurrentMap();
            for (String entry : commandValue.getValue().split(this.rowDelimiter + "")) {
                if (entry.contains(this.valueDelimiter + "")) {
                    String[] kv = entry.split(this.valueDelimiter + "");
                    if (2 == kv.length) {
                        map.put(kv[0], kv[1]);
                    }
                }
            }

            return Exceptional.of(map);
        }
    }

    /**
     Parses a list of block ID's, separated by ',' into a list of [BaseBlock] instances. If the block ID is in the format
     'id:data' it will use the data from the block ID, otherwise it defaults to zero (0).
     <p>
     Delimiter is always ','
     <p>
     Does not support named ID's like 'minecraft:stone'. Does not support patterns or masks.
     */
    public static class WorldEditBlockParser extends ListParser<BaseBlock> {

        public WorldEditBlockParser() {
            super(value -> {
                String[] idData = value.replace(" ", "").split(":");
                if (idData.length > 0) {
                    int data = 0;
                    if (idData.length == 2) data = Integer.parseInt(idData[1]);
                    return new BaseBlock(Integer.parseInt(idData[0]), data);
                }
                //noinspection ReturnOfNull
                return null;
            });
        }
    }

    public static class WorldEditMaskParser extends TypeParser<Mask> {

        private final FawePlayer<?> player;

        public WorldEditMaskParser(FawePlayer<?> player) {
            this.player = player;
        }

        @NotNull
        @Override
        public Exceptional<Mask> parse(@NotNull CommandValue<String> commandValue) {
            ParserContext ctx = new ParserContext();
            ctx.setActor(this.player.getPlayer());
            ctx.setWorld(this.player.getWorld());
            ctx.setSession(this.player.getSession());
            return Exceptional.of(() -> WorldEdit.getInstance()
                    .getMaskFactory()
                    .parseFromInput(commandValue.getValue(), ctx)
            );
        }
    }

    public static class WorldEditPatternParser extends TypeParser<Pattern> {

        private final FawePlayer<?> player;

        public WorldEditPatternParser(FawePlayer<?> player) {
            this.player = player;
        }

        @NotNull
        @Override
        public Exceptional<Pattern> parse(@NotNull CommandValue<String> commandValue) {
            ParserContext ctx = new ParserContext();
            ctx.setActor(this.player.getPlayer());
            ctx.setWorld(this.player.getWorld());
            ctx.setSession(this.player.getSession());
            return Exceptional.of(() -> WorldEdit.getInstance()
                    .getPatternFactory()
                    .parseFromInput(commandValue.getValue(), ctx)
            );
        }
    }

    public static class DurationParser extends TypeParser<Duration> {

        private final java.util.regex.Pattern minorTimeString =
                java.util.regex.Pattern.compile("^\\d+$");
        private final java.util.regex.Pattern timeString =
                java.util.regex.Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");

        private static final int secondsInMinute = 60;
        private static final int secondsInHour = 60 * DurationParser.secondsInMinute;
        private static final int secondsInDay = 24 * DurationParser.secondsInHour;
        private static final int secondsInWeek = 7 * DurationParser.secondsInDay;

        @NotNull
        @Override
        public Exceptional<Duration> parse(@NotNull CommandValue<String> commandValue) {
            String s = commandValue.getValue();
            // First, if just digits, return the number in seconds.

            if (this.minorTimeString.matcher(s).matches()) {
                return Exceptional.of(Duration.ofSeconds(Long.parseUnsignedLong(s)));
            }

            Matcher m = this.timeString.matcher(s);
            if (m.matches()) {
                long time = this.amount(m.group(2), DurationParser.secondsInWeek);
                time += this.amount(m.group(4), DurationParser.secondsInDay);
                time += this.amount(m.group(6), DurationParser.secondsInHour);
                time += this.amount(m.group(8), DurationParser.secondsInMinute);
                time += this.amount(m.group(10), 1);

                if (time > 0) {
                    return Exceptional.of(Duration.ofSeconds(time));
                }
            }
            return Exceptional.empty();
        }

        private long amount(@Nullable String g, int multipler) {
            if (null != g && !g.isEmpty()) {
                return multipler * Long.parseUnsignedLong(g);
            }

            return 0;
        }
    }
}
