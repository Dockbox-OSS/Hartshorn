package org.dockbox.hartshorn.regions.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PersistentRegionFlag {
    long regionId;
    String flagId;
    String value;
}
