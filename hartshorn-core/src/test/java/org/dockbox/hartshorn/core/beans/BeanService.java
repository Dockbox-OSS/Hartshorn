package org.dockbox.hartshorn.core.beans;

import org.dockbox.hartshorn.beans.Bean;
import org.dockbox.hartshorn.component.Service;

@Service
public class BeanService {

    @Bean(id = "user")
    public static BeanObject userBean() {
        return new BeanObject("user");
    }

    @Bean(id = "admin")
    public static BeanObject adminBean() {
        return new BeanObject("admin");
    }

    @Bean(id = "guest")
    public static BeanObject guestBean() {
        return new BeanObject("guest");
    }
}
