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

package org.dockbox.selene.api.util.images;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleFilter;
import com.mortennobel.imagescaling.ResampleOp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;

@SuppressWarnings("MagicNumber")
public final class ImageUtil
{

    public static final int[] INDEXED_COLORS = { 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0xFF5A7E28, 0xFF6E9A30, 0xFF7FB238, 0xFF435E1E,
            0xFFAEA473, 0xFFD5C98D, 0xFFF7E9A3, 0xFF837B56, 0xFF8C8C8C, 0xFFACACAC, 0xFFC7C7C7, 0xFF696969,
            0xFFB40000, 0xFFDC0000, 0xFFFF0000, 0xFF870000, 0xFF7171B4, 0xFF8A8ADC, 0xFFA0A0FF, 0xFF555587,
            0xFF767676, 0xFF909090, 0xFFA7A7A7, 0xFF585858, 0xFF005800, 0xFF006B00, 0xFF007C00, 0xFF004200,
            0xFFB4B4B4, 0xFFDCDCDC, 0xFFFFFFFF, 0xFF878787, 0xFF747782, 0xFF8D919F, 0xFFA4A8B8, 0xFF575961,
            0xFF6B4D36, 0xFF825E42, 0xFF976D4D, 0xFF503A29, 0xFF4F4F4F, 0xFF616161, 0xFF707070, 0xFF3B3B3B,
            0xFF2D2DB4, 0xFF3737DC, 0xFF4040FF, 0xFF222287, 0xFF655433, 0xFF7B673E, 0xFF8F7748, 0xFF4C3F26,
            0xFFB4B2AD, 0xFFDCD9D3, 0xFFFFFCF5, 0xFF878582, 0xFF985A24, 0xFFBA6E2C, 0xFFD87F33, 0xFF72431B,
            0xFF7E3698, 0xFF9A42BA, 0xFFB24CD8, 0xFF5E2872, 0xFF486C98, 0xFF5884BA, 0xFF6699D8, 0xFF365172,
            0xFFA2A224, 0xFFC6C62C, 0xFFE5E533, 0xFF79791B, 0xFF5A9012, 0xFF6EB016, 0xFF7FCC19, 0xFF436C0D,
            0xFFAB5A74, 0xFFD16E8E, 0xFFF27FA5, 0xFF804357, 0xFF363636, 0xFF424242, 0xFF4C4C4C, 0xFF282828,
            0xFF6C6C6C, 0xFF848484, 0xFF999999, 0xFF515151, 0xFF365A6C, 0xFF426E84, 0xFF4C7F99, 0xFF284351,
            0xFF5A2C7E, 0xFF6E369A, 0xFF7F3FB2, 0xFF43215E, 0xFF24367E, 0xFF2C429A, 0xFF334CB2, 0xFF1B285E,
            0xFF483624, 0xFF58422C, 0xFF664C33, 0xFF36281B, 0xFF485A24, 0xFF586E2C, 0xFF667F33, 0xFF36431B,
            0xFF6C2424, 0xFF842C2C, 0xFF993333, 0xFF511B1B, 0xFF121212, 0xFF161616, 0xFF191919, 0xFF0D0D0D,
            0xFFB0A836, 0xFFD8CD42, 0xFFFAEE4D, 0xFF847E29, 0xFF419B96, 0xFF4FBDB8, 0xFF5CDBD5, 0xFF317471,
            0xFF345AB4, 0xFF406EDC, 0xFF4A80FF, 0xFF274487, 0xFF009929, 0xFF00BB32, 0xFF00D93A, 0xFF00731F,
            0xFF5B3D23, 0xFF6F4A2A, 0xFF815631, 0xFF442E1A, 0xFF4F0100, 0xFF610200, 0xFF700200, 0xFF3B0100,
            0xFF947D72, 0xFFB4998B, 0xFFD1B1A1, 0xFF6F5E55, 0xFF703A19, 0xFF89471F, 0xFF9F5224, 0xFF542B13,
            0xFF693D4C, 0xFF814B5D, 0xFF95576C, 0xFF4F2E39, 0xFF4F4C61, 0xFF615D77, 0xFF706C8A, 0xFF3B3949,
            0xFF835E19, 0xFFA0731F, 0xFFBA8524, 0xFF624613, 0xFF495325, 0xFF59652E, 0xFF677535, 0xFF373E1C,
            0xFF713637, 0xFF8A4243, 0xFFA04D4E, 0xFF552929, 0xFF281D19, 0xFF31231E, 0xFF392923, 0xFF1E1613,
            0xFF5F4C45, 0xFF745C55, 0xFF876B62, 0xFF473934, 0xFF3D4141, 0xFF4B4F4F, 0xFF575C5C, 0xFF2E3131,
            0xFF56343E, 0xFF693F4C, 0xFF7A4958, 0xFF41272F, 0xFF362C41, 0xFF42354F, 0xFF4C3E5C, 0xFF282131,
            0xFF362319, 0xFF422B1E, 0xFF4C3223, 0xFF281A13, 0xFF363A1E, 0xFF424724, 0xFF4C522A, 0xFF282B16,
            0xFF642A20, 0xFF7B3428, 0xFF8E3C2E, 0xFF4B2018 };

    private ImageUtil() {}

    public static MultiSizedImage split(BufferedImage image, int width, int height)
    {
        return new MultiSizedImage(image, width, height);
    }

    public static BufferedImage scaleToFit(BufferedImage image, int width, int height)
    {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImage.createGraphics();
        //This could be changed, Cf. http://stackoverflow.com/documentation/java/5482/creating-images-programmatically/19498/specifying-image-rendering-quality
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        double horizontalRatio = ratio(image.getWidth(), width);
        double verticalRatio = ratio(image.getHeight(), height);
        double horizontalPadding = horizontalRatio > verticalRatio ? calculatePadding(image.getWidth(), width) : 0;
        double verticalPadding = verticalRatio > horizontalRatio ? calculatePadding(image.getHeight(), height) : 0;

        g2.drawImage(
                image,
                (int) horizontalPadding,
                (int) verticalPadding,
                (int) (width - (horizontalPadding * 2)),
                (int) (height - (verticalPadding * 2)),
                null);
        g2.dispose();
        return resizedImage;
    }

    private static double ratio(int actual, int target)
    {
        return (double) target / (double) actual;
    }

    private static double calculatePadding(int current, int target)
    {
        int ratio = 1;
        int scaledTarget = target;
        while (current > scaledTarget)
        {
            scaledTarget *= 2;
            ratio++;
        }
        double scaledCurrent = (double) current / ratio;
        double difference = target - scaledCurrent;
        return difference / 2;
    }

    // Note for CustomMaps, targetWidth/Height are 128*map size (e.g. 128*3 , 128*2)
    public static BufferedImage scale(BufferedImage image, BufferedImage background, ScaleMode scaleMode)
    {
        BufferedImage scaledImg = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        double imgAspectRatio = 1.0d * image.getWidth() / image.getHeight();
        double mapsAspectRatio = 1.0d * scaledImg.getWidth() / scaledImg.getHeight();

        int scaledW = imgAspectRatio > mapsAspectRatio ? scaledImg.getWidth() : (int) Math.round(scaledImg.getHeight() * imgAspectRatio);
        int scaledH = imgAspectRatio > mapsAspectRatio ? (int) Math.round(scaledImg.getWidth() / imgAspectRatio) : scaledImg.getHeight();
        BufferedImage rawScaledImg = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_4BYTE_ABGR);

        ResampleOp resampleOp = new ResampleOp(background.getWidth(), background.getHeight());
        resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.None);
        ResampleFilter filter = scaleMode.getResampleFilter();
        resampleOp.setFilter(filter);
        resampleOp.filter(image, background);
        placeCentered(scaledImg, background);
        return background;
    }

    private static void placeCentered(BufferedImage image, BufferedImage background)
    {
        Graphics2D printOnImgGraphics = background.createGraphics();
        try
        {
            Color oldColor = printOnImgGraphics.getColor();
            printOnImgGraphics.fillRect(0, 0, background.getWidth(), background.getHeight());
            printOnImgGraphics.setColor(oldColor);
            printOnImgGraphics
                    .drawImage(image, null, background.getWidth() / 2 - image.getWidth() / 2, background.getHeight() / 2 - image.getHeight() / 2);
        }
        finally
        {
            printOnImgGraphics.dispose();
        }
    }

    public static void applyDitherPalette(BufferedImage image, DitherMode mode, double colorBleedReduction)
    {
        byte[][] nontiledMapData = new byte[image.getWidth()][image.getHeight()];

        {
            byte[] nontiledMapPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

            if (!mode.isEnabled())
            {
                for (int i = 0; i < image.getHeight(); i++)
                {
                    for (int j = 0; j < image.getWidth(); j++)
                    {
                        int w = image.getWidth();
                        boolean opaque = 128 < (nontiledMapPixels[i * w * 4 + j * 4] & 0xFF);
                        int colorIndex = closestMapColorIndex(nontiledMapPixels[i * w * 4 + j * 4 + 3] & 0xFF, nontiledMapPixels[i * w * 4 + j * 4 + 2] & 0xFF, nontiledMapPixels[i * w * 4 + j * 4 + 1] & 0xFF);
                        nontiledMapData[j][i] = opaque ? (byte) colorIndex : 0;
                    }
                }
            }
            else
            {
                if (mode.isOrdered()) applyBayer(image, mode, nontiledMapData, nontiledMapPixels);
                else applyErrorDiffusion(image, mode, colorBleedReduction, nontiledMapData, nontiledMapPixels);
            }
        }
    }

    private static void applyBayer(RenderedImage image, DitherMode mode, byte[][] nontiledMapData, byte[] nontiledMapPixels)
    {
        double[][] bayerMatrix = mode.getBayerMatrix();

        for (int i = 0; i < image.getHeight(); i++)
        {
            for (int j = 0; j < image.getWidth(); j++)
            {
                int w = image.getWidth();
                boolean opaque = 128 < (nontiledMapPixels[i * w * 4 + j * 4] & 0xFF);

                if (opaque)
                {
                    double map_value = bayerMatrix[i % bayerMatrix.length][j % bayerMatrix[0].length];
                    int R = nontiledMapPixels[i * w * 4 + j * 4 + 3] & 0xFF;
                    int G = nontiledMapPixels[i * w * 4 + j * 4 + 2] & 0xFF;
                    int B = nontiledMapPixels[i * w * 4 + j * 4 + 1] & 0xFF;
                    int newR = (int) Math.round(R + (256 / 6.5) * (map_value - 0.5));  // This is really bad for indexed palette,
                    int newG = (int) Math.round(G + (256 / 7.0) * (map_value - 0.5));  // and not really optimized for it, but it
                    int newB = (int) Math.round(B + (256 / 12.5) * (map_value - 0.5)); // will make do. Yliluoma1 is too slow for
                    int colorIndex = closestMapColorIndex(newR, newG, newB);      // our purposes anyway.
                    nontiledMapData[j][i] = (byte) colorIndex;
                }
                else nontiledMapData[j][i] = 0;
            }
        }
    }

    private static void applyErrorDiffusion(RenderedImage image, DitherMode mode, double colorBleedReduction, byte[][] nontiledMapData,
                                            byte[] nontiledMapPixels)
    {
        double[][] errorDiffusionMatrix = mode.getErrorDiffusionMatrix();
        double[][] errorsR = new double[image.getHeight()][image.getWidth()];
        double[][] errorsG = new double[image.getHeight()][image.getWidth()];
        double[][] errorsB = new double[image.getHeight()][image.getWidth()];

        for (int i = 0; i < image.getHeight(); i++)
        {
            for (int j = 0; j < image.getWidth(); j++)
            {
                int w = image.getWidth();
                boolean opaque = 128 < (nontiledMapPixels[i * w * 4 + j * 4] & 0xFF);

                if (opaque)
                {
                    int R = constrainInt((nontiledMapPixels[i * w * 4 + j * 4 + 3] & 0xFF) + (int) Math.round(errorsR[i][j]));
                    int G = constrainInt((nontiledMapPixels[i * w * 4 + j * 4 + 2] & 0xFF) + (int) Math.round(errorsG[i][j]));
                    int B = constrainInt((nontiledMapPixels[i * w * 4 + j * 4 + 1] & 0xFF) + (int) Math.round(errorsB[i][j]));
                    int colorIndex = closestMapColorIndex(R, G, B);
                    int newR = (INDEXED_COLORS[colorIndex] >> 16) & 0xFF;
                    int newG = (INDEXED_COLORS[colorIndex] >> 8) & 0xFF;
                    int newB = INDEXED_COLORS[colorIndex] & 0xFF;
                    int errR = R - newR;
                    int errG = G - newG;
                    int errB = B - newB;
                    for (int g = 0; g < errorDiffusionMatrix.length; g++)
                    {
                        for (int h = 0; h < errorDiffusionMatrix[g].length; h++)
                        {
                            int y = i + g;
                            int x = j - (errorDiffusionMatrix[g].length - 1) / 2 + h;
                            if (y >= errorsR.length || 0 > x || x >= errorsR[i].length) continue;
                            errorsR[y][x] += errR * colorBleedReduction * errorDiffusionMatrix[g][h];
                            errorsG[y][x] += errG * colorBleedReduction * errorDiffusionMatrix[g][h];
                            errorsB[y][x] += errB * colorBleedReduction * errorDiffusionMatrix[g][h];
                        }
                    }
                    nontiledMapData[j][i] = (byte) colorIndex;
                }
                else nontiledMapData[j][i] = 0;
            }
        }
    }

    private static int constrainInt(int val)
    {
        if (0 > val) return 0;
        return Math.min(val, 255);
    }

    private static int closestMapColorIndex(int R, int G, int B)
    {
        int closestIndex = 0;
        double closestDistance = 1E100;
        for (int i = 4; i < INDEXED_COLORS.length; i++)
        { //0-3 are reserved for transparent
            double curDist = colorDistance(R, G, B, (INDEXED_COLORS[i] & 0x00FF0000) >> 16, (INDEXED_COLORS[i] & 0x0000FF00) >> 8, INDEXED_COLORS[i] & 0x000000FF);
            if (curDist < closestDistance)
            {
                closestIndex = i;
                closestDistance = curDist;
            }
        }
        return closestIndex;
    }

    static double colorDistance(Color c1, Color c2)
    {
        int red1 = c1.getRed();
        int red2 = c2.getRed();
        int rmean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return Math.sqrt((((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8));
    }

    private static double colorDistance(int R1, int G1, int B1, int R2, int G2, int B2)
    {
        int rmean = (R1 + R2) >> 1;
        int r = R1 - R2;
        int g = G1 - G2;
        int b = B1 - B2;
        return Math.sqrt((((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8));
    }

}
