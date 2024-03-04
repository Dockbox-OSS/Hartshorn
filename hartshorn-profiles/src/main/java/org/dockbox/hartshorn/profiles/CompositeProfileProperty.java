package org.dockbox.hartshorn.profiles;

import java.util.List;

public non-sealed interface CompositeProfileProperty extends ProfileProperty {

    List<ValueProfileProperty> properties();

}
