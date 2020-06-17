package org.dockbox.darwin.core.objects.module

import java.io.File
import java.util.jar.JarEntry

class ModuleJarCandidate(var entry: JarEntry?, var sourceFile: File) : ModuleCandidate
