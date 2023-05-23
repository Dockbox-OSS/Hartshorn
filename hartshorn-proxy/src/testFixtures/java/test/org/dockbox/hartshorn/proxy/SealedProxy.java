package test.org.dockbox.hartshorn.proxy;

import test.org.dockbox.hartshorn.proxy.SealedProxy.SealedProxy1;

public sealed class SealedProxy permits SealedProxy1 {
    public static final class SealedProxy1 extends SealedProxy {}
}
