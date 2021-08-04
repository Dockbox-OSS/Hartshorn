package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.persistence.properties.Remote;

public class HibernateSqlServiceTests {

    public static class HibernateDerbyServiceTests extends SqlServiceTest {

        @Override
        protected Remote remote() {
            return Remote.DERBY;
        }

        @Override
        protected Object target() {
            return directory("derby");
        }
    }

}
