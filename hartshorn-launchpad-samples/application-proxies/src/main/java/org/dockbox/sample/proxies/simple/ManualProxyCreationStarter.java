package org.dockbox.sample.proxies.simple;

import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.ApplicationStarter;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.stub.MethodStub;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.sample.proxies.processing.Loggable;
import org.slf4j.Logger;

@Component
public class ManualProxyCreationStarter implements ApplicationStarter {

    private final Logger logger;

    public ManualProxyCreationStarter(Logger logger) {
        this.logger = logger;
    }

    @Override
    @Loggable
    public void run(ApplicationContext applicationContext) throws ApplicationException {
        ProxyOrchestrator proxyOrchestrator = applicationContext.environment().proxyOrchestrator();
        Introspector introspector = applicationContext.environment().introspector();
        TypeView<Greeter> greeterType = introspector.introspect(Greeter.class);

        StateAwareProxyFactory<Greeter> proxyFactory = proxyOrchestrator.factory(Greeter.class)
                .implement(Fareweller.class)
                .advisors(advisors -> advisors
                        // Greeter.sayHello()
                        .method(greeterType.methods().named("sayHello").get())
                        .intercept(MethodInterceptor.withoutReturnValue(context -> {
                            context.instance().sayHello("world");
                        }))
                        // Greeter.sayHello(name)
                        .method(greeterType.methods().named("sayHello", String.class).get())
                        .intercept(MethodInterceptor.withoutReturnValue(context -> {
                            logger.info("Hello {}!", context.args()[0]);
                        }))
                        // All non-configured methods
                        .defaultStub(MethodStub.withoutReturnValue(stubContext -> {
                            logger.info("Stubbing method {}", stubContext.target());
                        })));

        Option<Greeter> greeterOption = proxyFactory.proxy();
        greeterOption.peek(greeter -> {
            greeter.sayHello();
            greeter.sayHello("Hartshorn");

            Fareweller fareweller = (Fareweller) greeter;
            fareweller.sayFarewell();

            logger.info("Greeter: {}", greeter);
        });
    }
}
