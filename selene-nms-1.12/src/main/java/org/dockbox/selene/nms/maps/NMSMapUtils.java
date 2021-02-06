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

package org.dockbox.selene.nms.maps;

import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.server.FMLServerHandler;

import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class NMSMapUtils
{

    private static final Map<Integer, int[]> COLORS = SeleneUtils.emptyMap();
    public static final int MAX_MAP_SIZE = 128;
    public static final int Z_CENTER = 999_999;
    public static final int DIMENSION = Z_CENTER;

    private NMSMapUtils() {}

    public static int populateColoredMap(BufferedImage image)
    {
        return populateColoredMap(getColorData(image));
    }

    public static int populateColoredMap(byte[] colors)
    {
        World world = FMLServerHandler.instance().getServer().getWorld(0); // Root world
        int id = world.getUniqueDataId("map");
        MapData mapData = new MapData("map_" + id);
        mapData.scale = (byte) 0;
        mapData.xCenter = 0;
        mapData.zCenter = Z_CENTER;
        mapData.dimension = DIMENSION; // Target dimension ID, realistically no server will ever reach this number
        mapData.unlimitedTracking = false;
        mapData.trackingPosition = false;
        mapData.colors = colors;
        mapData.markDirty();
        world.setData("map_" + id, mapData);
        Objects.requireNonNull(world.getMapStorage()).saveAllData();
        return id;
    }

    private static BufferedImage getResizedImage(BufferedImage originImage)
    {
        BufferedImage image = new BufferedImage(MAX_MAP_SIZE, MAX_MAP_SIZE, BufferedImage.TRANSLUCENT);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.drawImage(originImage, 0, 0, MAX_MAP_SIZE, MAX_MAP_SIZE, null);
        graphics2D.dispose();
        return image;
    }

    private static int getIndexOfTheRGB(int red, int green, int blue)
    {
        Map<Integer, Double> similarities = new HashMap<>();
        for (Map.Entry<Integer, int[]> entry : verifyColorMap().entrySet())
        {
            int r = entry.getValue()[0];
            int g = entry.getValue()[1];
            int b = entry.getValue()[2];

            double similarity = Math.pow((red - r), 2) + Math.pow((green - g), 2) + Math.pow((blue - b), 2);
            similarities.put(entry.getKey(), similarity);
        }

        double min = Collections.min(similarities.values());

        for (Map.Entry<Integer, Double> entry : similarities.entrySet())
        {
            if (entry.getValue() <= min)
            {
                return entry.getKey();
            }
        }

        return 4;
    }

    private static byte[] getColorData(BufferedImage image)
    {
        BufferedImage sizedImage = getResizedImage(image);
        byte[] colors = new byte[MAX_MAP_SIZE * MAX_MAP_SIZE];
        int n = 0;
        for (int i = 0; MAX_MAP_SIZE > i; i++)
        {
            for (int j = 0; MAX_MAP_SIZE > j; j++)
            {
                Color color = new Color(sizedImage.getRGB(j, i));
                int index = getIndexOfTheRGB(color.getRed(), color.getGreen(), color.getBlue());
                colors[n] = (byte) index;
                n += 1;
            }
        }
        return colors;
    }

    private static Map<Integer, int[]> verifyColorMap()
    {
        if (COLORS.isEmpty())
        {
            try
            {
                Path colorMap = Selene.getResourceFile("colormap.raw").get();
                for (String line : Files.readAllLines(colorMap))
                {
                    String[] ints = line.split(",");
                    int[] rgb = { Integer.parseInt(ints[1]), Integer.parseInt(ints[2]), Integer.parseInt(ints[3]) };
                    COLORS.put(Integer.parseInt(ints[0]), rgb);
                }
            }
            catch (IOException | NumberFormatException e)
            {
                Selene.handle("Colormap could not be read", e);
                COLORS.clear();
            }
        }
        return COLORS;
    }

}
