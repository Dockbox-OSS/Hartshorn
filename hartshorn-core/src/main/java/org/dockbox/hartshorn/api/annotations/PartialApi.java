package org.dockbox.hartshorn.api.annotations;

/**
 * Indicates a declaration is part of a public API, and should therefore
 * be ignored by the IDE if the declaration is unused in the current
 * context.
 */
@PartialApi
public @interface PartialApi {
}
