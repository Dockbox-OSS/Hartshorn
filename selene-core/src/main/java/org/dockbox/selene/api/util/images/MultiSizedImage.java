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

import org.dockbox.selene.api.util.SeleneUtils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;

public class MultiSizedImage
{
    private Map<Integer[], BufferedImage> imageMap = SeleneUtils.emptyMap();
    private final BufferedImage image;
    private final int xSize;
    private final int ySize;

    public MultiSizedImage(BufferedImage image, int xSize, int ySize){
        this.image = image;
        this.xSize = xSize;
        this.ySize = ySize;
        this.genSubImages();
    }


    private BufferedImage getResizedImage(){
        BufferedImage bufferedImage = new BufferedImage(xSize * 128, ySize * 128, BufferedImage.TRANSLUCENT);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, xSize * 128, ySize * 128, null);
        graphics2D.dispose();
        return bufferedImage;
    }

    private void genSubImages(){
        for (int i = 0; i < xSize ; i++) {
            for (int j = 0; j < ySize ; j++) {
                BufferedImage sub = this.getResizedImage().getSubimage(i * 128, j * 128, 128, 128);
                Integer[] pos = new Integer[]{i, j};
                imageMap.put(pos, sub);
            }
        }
    }

    public Map<Integer[], BufferedImage> getImageMap() {
        return imageMap;
    }
}
