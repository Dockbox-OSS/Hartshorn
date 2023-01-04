/*
 * Copyright 2019-2023 the original author or authors.
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

rootProject.name = "Hartshorn"

gradle.startParameter.isContinueOnFailure = true

includeBuild("gradle/plugins")

includeAll(rootDir, "")
configureChildren(rootProject)

/**
 * Include all subprojects in the given directory. This method will recursively
 * search for all subdirectories containing a file with the same name as the directory,
 * appended with ".gradle.kts". If such a file is found, it will be included as a subproject.
 *
 * This only includes the projects, it is expected that [configureChildren] is called
 * to configure the subprojects.
 *
 * @see configureChildren
 */
fun includeAll(dir: File, prefix: String) {
    dir.listFiles()?.forEach { file ->
        if (file.isDirectory && File(file, "${file.name}.gradle.kts").exists()) {
            include("${prefix}:${file.name}")
            // Include all nested projects
            includeAll(file, ":${file.name}")
        }
    }
}

/**
 * Configure all children of the given project. The [includeAll] method will include
 * all subprojects, but this method will configure them.
 *
 * This method follows the rule where each project is expected to have a file with the
 * same name as the project, appended with ".gradle.kts". As a result, the
 * [ProjectDescriptor.getBuildFileName] is replaced with the name of the project.
 *
 * @see includeAll
 */
fun configureChildren(project: ProjectDescriptor) {
    if (project.children.isNotEmpty()) {
        project.children.forEach { child ->
            child.buildFileName = "${child.name}.gradle.kts"
            configureChildren(child)
        }
    }
}
