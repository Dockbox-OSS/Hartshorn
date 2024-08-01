/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.org.dockbox.hartshorn.components;

import org.dockbox.hartshorn.inject.Inject;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.inject.LifecycleType;

public class LongCycles {

    @Component(lifecycle = LifecycleType.SINGLETON)
    public static class LongCycleA {
        @Inject
        public LongCycleA(LongCycleB cycle) {}
    }

    @Component(lifecycle = LifecycleType.SINGLETON)
    public static class LongCycleB {
        @Inject
        public LongCycleB(LongCycleC cycle) {}
    }

    @Component(lifecycle = LifecycleType.SINGLETON)
    public static class LongCycleC {
        @Inject
        public LongCycleC(LongCycleD cycle) {}
    }

    @Component(lifecycle = LifecycleType.SINGLETON)
    public static class LongCycleD {
        @Inject
        public LongCycleD(LongCycleA cycle) {}
    }
}
