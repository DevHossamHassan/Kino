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
    }
}

rootProject.name = "Kino"
include(":app")
include(":core:common")
include(":core:design-system")
include(":core:data")
include(":core:database")
include(":core:model")
include(":core:resources")
include(":navigation")
include(":feature:kanban")
include(":feature:notes")
include(":feature:media")
include(":feature:settings")
include(":feature:task-detail")
include(":feature:notifications")
include(":feature:ai-analysis")
include(":feature:gamification")
include(":feature:recurring-tasks")
 