package org.dockbox.hartshorn.web.util;

public final class RouterUtilities {

    private RouterUtilities() {
    }

    /**
     * Combines multiple path segments into a single path, ensuring that there are no duplicate
     * slashes and that the resulting path is properly formatted. A prefix slash is added if the
     * first segment does not start with one. No trailing slash is added unless the last segment
     * ends with one.
     *
     * @param paths the path segments to combine
     * @return the combined path
     */
    public static String combinePaths(String... paths) {
        StringBuilder combined = new StringBuilder("/");
        for (String path : paths) {
            if (path == null || path.isEmpty()) continue;
            if (!combined.isEmpty() && combined.charAt(combined.length() - 1) != '/') {
                combined.append('/');
            }
            combined.append(path.startsWith("/") ? path.substring(1) : path);
        }
        return combined.toString();
    }
}
