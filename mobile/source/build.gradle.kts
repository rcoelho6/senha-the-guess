buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // AGP 9 usa Kotlin integrado; esta dependência mantém o compilador alinhado ao plugin Compose.
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
