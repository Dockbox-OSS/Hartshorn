package org.dockbox.darwin.core.util.inject

import com.google.inject.AbstractModule

abstract class AbstractCommonInjector : AbstractModule() {

    override fun configure() {
        super.configure()
        this.configureExceptionInject()
        this.configureModuleInject()
        this.configureUtilInject()
    }

    protected abstract fun configureExceptionInject()
    protected abstract fun configureModuleInject()
    protected abstract fun configureUtilInject()
}
