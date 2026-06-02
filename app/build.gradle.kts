import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val exportOverrideProperties = Properties().apply {
    val file = rootProject.file("moyu.properties")
    if (file.isFile) {
        file.inputStream().use(::load)
    }
}

fun exportOrGradleProperty(name: String, fallback: String): String {
    return exportOverrideProperties.getProperty(name)
        ?: providers.gradleProperty(name).orNull
        ?: fallback
}

val releaseKeystorePath = providers.environmentVariable("MOYU_KEYSTORE_PATH").orNull
val releaseStorePassword = providers.environmentVariable("MOYU_STORE_PASSWORD").orNull
val releaseKeyAlias = providers.environmentVariable("MOYU_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("MOYU_KEY_PASSWORD").orNull
val hasReleaseSigning = !releaseKeystorePath.isNullOrBlank()
    && !releaseStorePassword.isNullOrBlank()
    && !releaseKeyAlias.isNullOrBlank()
    && !releaseKeyPassword.isNullOrBlank()

plugins {
    id("com.android.application")
}

android {
    compileSdk = 36

    defaultConfig {
        applicationId = exportOrGradleProperty("MOYU_APPLICATION_ID", "ink.momoyu.runtime")
        minSdk = 28
        targetSdk = 36
        versionCode = exportOrGradleProperty("MOYU_VERSION_CODE", "1").toInt()
        versionName = exportOrGradleProperty("MOYU_VERSION_NAME", "1.0.0")
        manifestPlaceholders["moyuScreenOrientation"] =
            exportOrGradleProperty("MOYU_ORIENTATION", "sensorLandscape")
    }

    signingConfigs {
        create("release") {
            if (hasReleaseSigning) {
                storeFile = file(releaseKeystorePath!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    namespace = "ink.momoyu.runtime"
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("com.google.android.material:material:1.14.0")

    // Android Game Development Kit
    implementation("androidx.games:games-activity:4.4.0")
}
