package test.org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.profiles.ComposableProfileHolder;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;

public class SampleProfileApplication {

    public static void main(String[] args) {
        ComposableProfileHolder holder = null;
        ProfilePropertyRegistry registry = holder.registry();
        String hello = registry.property("test")
                .map(ProfileProperty::rawValue)
                .orElse("Hello");
        System.out.println(hello);
    }

}
