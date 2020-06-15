package org.dockbox.darwin.core.util.module

import org.dockbox.darwin.core.objects.module.ModuleClassCandidate
import org.dockbox.darwin.core.objects.module.ModuleJarCandidate
import java.nio.file.Path
import java.util.stream.Stream

interface ModuleScanner {

    fun collectJarCandidates(path: Path): ModuleScanner
    fun collectClassCandidates(pkg: String): ModuleScanner

    fun getJarCandidates(): Iterable<ModuleJarCandidate>
    fun getClassCandidates(): Iterable<ModuleClassCandidate>

    fun getScannedClasses(): Iterable<Class<*>>
    fun getAnnotatedCandidates(): Stream<Class<*>>?

}
