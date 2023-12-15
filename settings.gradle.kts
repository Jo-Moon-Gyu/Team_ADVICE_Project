pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public") }
        maven { url=uri("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")  }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url=uri("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/") }
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public") }
    }
}

dependencies {
    implementation 'com.kakao.sdk:v2-all:2.17.0'
    implementation 'com.kakao.sdk:v2-user:2.17.0'
    implementation 'com.kakao.sdk:v2-talk:2.17.0'
    implementation 'com.kakao.sdk:v2-story:2.17.0'
    implementation 'com.kakao.sdk:v2-share:2.17.0'
    implementation 'com.kakao.sdk:v2-friend:2.17.0'
    implementation 'com.kakao.sdk:v2-navi:2.17.0'
    implementation 'com.kakao.sdk:v2-cert:2.17.0'
}
rootProject.name = "ADVISER"
include ':app'
include ':OpenCV'