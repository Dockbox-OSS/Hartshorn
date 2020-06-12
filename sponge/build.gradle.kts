plugins {
    application
    kotlin("jvm")
}

group = "org.dockbox.darwin.sponge"

repositories {
    maven {
        url = uri("http://repo.drnaylor.co.uk/artifactory/list/minecraft")
    }
    maven {
        url = uri("https://repo.spongepowered.org/maven")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib"))
    compileOnly("me.lucko.luckperms:luckperms-api:4.4")
    compileOnly("org.spongepowered:spongeapi:7.1.0")
    compileOnly("com.github.Eufranio:MagiBridge:2.10")
    compileOnly("com.github.MultiChat:Development:1.8.1")
    compileOnly("io.github.nucleuspowered:nucleus-api:1.14.3-SNAPSHOT-S7.1")
}
