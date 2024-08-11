package org.dockbox.hartshorn.properties.loader;

import org.dockbox.hartshorn.properties.loader.path.PropertyFieldPathNode;
import org.dockbox.hartshorn.properties.loader.path.PropertyIndexPathNode;
import org.dockbox.hartshorn.properties.loader.path.PropertyPathFormatter;
import org.dockbox.hartshorn.properties.loader.path.PropertyPathNode;
import org.dockbox.hartshorn.properties.loader.path.PropertyRootPathNode;

public class StandardPropertyPathFormatter implements PropertyPathFormatter {

    @Override
    public String formatPath(PropertyPathNode pathNode) {
        StringBuilder builder = new StringBuilder();
        return this.formatPath(pathNode, builder);
    }

    private String formatPath(PropertyPathNode pathNode, StringBuilder builder) {
        return switch(pathNode) {
            case PropertyFieldPathNode fieldPathNode -> {
                builder.insert(0, formatField(fieldPathNode));
                yield this.formatPath(fieldPathNode.parent(), builder);
            }
            case PropertyIndexPathNode indexPathNode -> {
                builder.insert(0, formatIndex(indexPathNode));
                yield this.formatPath(indexPathNode.parent(), builder);
            }
            case PropertyRootPathNode ignored -> builder.toString();
        };
    }

    protected String formatField(PropertyFieldPathNode node) {
        boolean isTopLevel = node.parent() instanceof PropertyRootPathNode;
        return isTopLevel ? node.name() : ".%s".formatted(node.name());
    }

    protected String formatIndex(PropertyIndexPathNode node) {
        return "[%d]".formatted(node.index());
    }
}
