pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()

        mavenCentral()
        jcenter()
        maven("https://repository.map.naver.com/archive/maven")
        maven("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")
    }
}

rootProject.name = "My Application"
include(":app")