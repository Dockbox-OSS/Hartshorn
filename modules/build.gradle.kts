plugins {
    application
    kotlin("jvm")
}

group = "org.dockbox.darwin.integrated"

application {
    mainClassName = "org.dockbox.darwin.integrated.IntegratedServer"
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib"))
}
