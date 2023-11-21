package org.dockbox.hartshorn.profiles;

/**
 * A {@link ProfileHolder} that allows access to a composed {@link ProfilePropertyRegistry}
 * made up of all the {@link ApplicationProfile}s that are active.
 *
 * @see ProfileHolder
 * @see ProfilePropertyRegistry
 *
 * @since 0.5.1
 *
 * @author Guus Lieben
 */
public interface ComposableProfileHolder extends ProfileHolder {

    /**
     * Returns the composed {@link ProfilePropertyRegistry} made up of all the {@link ApplicationProfile}s
     * that are active.
     *
     * @return the composed {@link ProfilePropertyRegistry}
     */
    ProfilePropertyRegistry registry();

}
