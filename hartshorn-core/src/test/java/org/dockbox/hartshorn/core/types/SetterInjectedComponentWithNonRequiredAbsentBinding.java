package org.dockbox.hartshorn.core.types;

import javax.inject.Inject;

import lombok.Getter;

public class SetterInjectedComponentWithNonRequiredAbsentBinding {

    @Getter
    @Inject
    private Person person;

    @Inject
    // Person is bound, so this will result in a null value
    public void setPerson(final Person person) {
        this.person = person;
    }
}
