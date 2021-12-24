package org.dockbox.hartshorn.core.types;

import org.dockbox.hartshorn.core.annotations.inject.Required;

import javax.inject.Inject;

import lombok.Getter;

public class SetterInjectedComponentWithAbsentBinding {

    @Getter
    @Inject
    private Person person;

    @Inject
    // Person is bound, so this will result in a null value
    public void setPerson(@Required final Person person) {
        this.person = person;
    }
}
