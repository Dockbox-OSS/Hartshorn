package org.dockbox.selene.api.objects.item.maps;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.targets.Identifiable;
import org.dockbox.selene.api.util.images.MultiSizedImage;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;

public interface CustomMapService
{
    CustomMap create(BufferedImage image, Identifiable source);
    CustomMap create(byte[] image, Identifiable source);

    CustomMap getById(int id);

    Map<Integer[], CustomMap> create(BufferedImage image, int width, int height, Identifiable source);

    Map<Integer[], CustomMap> create(MultiSizedImage image, Identifiable source);

    Collection<CustomMap> getFrom(Identifiable source);

    Exceptional<CustomMap> derive(Item item);
}
