pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "HBVideoView-Core"
            url = uri("https://maven.pkg.github.com/StarlightC/HBVideoView_Core")
            credentials {
                username = "StarlightC"
                password = "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"
            }
        }
    }
}
rootProject.name = "HBVideoView_ExoPlayer"
include(":exoplayer")