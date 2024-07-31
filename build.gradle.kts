import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

group = "xyz.acrylicstyle"
version = "1.20.2+1.0.0"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/") }
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")
}

paperweight.reobfArtifactConfiguration.set(ReobfArtifactConfiguration.REOBF_PRODUCTION)

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(sourceSets.main.get().resources.srcDirs) {
            filter(ReplaceTokens::class, mapOf("tokens" to mapOf("version" to project.version.toString())))
            filteringCharset = "UTF-8"
        }
    }
}
