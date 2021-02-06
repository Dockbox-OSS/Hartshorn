package org.dockbox.selene.core.objects.item.maps;

import org.dockbox.selene.core.objects.targets.Identifiable;

import java.awt.image.BufferedImage;
import java.util.Collection;

public interface CustomMapService
{
    CustomMap create(BufferedImage image, Identifiable source);
    CustomMap create(byte[] image, Identifiable source);

    CustomMap getById(int id);

    Collection<CustomMap> getFrom(Identifiable source);
}
