package test.org.dockbox.hartshorn.components;

import org.dockbox.hartshorn.component.Component;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jakarta.inject.Inject;
import jakarta.inject.Named;

@Component
public class BeanAwareComponent {

    @Inject
    @Named("names")
    private List<String> names;

    @Inject
    @Named("ages")
    private Set<Integer> ages = new TreeSet<>();

    public List<String> names() {
        return this.names;
    }

    public Set<Integer> ages() {
        return this.ages;
    }
}
