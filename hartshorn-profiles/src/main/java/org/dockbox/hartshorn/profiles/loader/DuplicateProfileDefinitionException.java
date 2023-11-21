package org.dockbox.hartshorn.profiles.loader;

import org.dockbox.hartshorn.util.ApplicationException;

public class DuplicateProfileDefinitionException extends ApplicationException {

    public DuplicateProfileDefinitionException(String profileName) {
        super("Profile '" + profileName + "' is defined in multiple locations");
    }
}
