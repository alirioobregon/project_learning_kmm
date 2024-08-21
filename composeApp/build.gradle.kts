import org.jetbrains.compose.desktop.application.dsl.*
import org.jetbrains.compose.ExperimentalComposeLibrary


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)

            implementation("io.ktor:ktor-client-android:2.0.3")

            implementation("com.mapbox.extension:maps-compose:11.6.0")
            implementation("com.mapbox.maps:android:11.6.0")
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation("io.ktor:ktor-client-cio:2.0.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0") // Dependencia para corrutinas en JVM
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)

            implementation(compose.materialIconsExtended)

//            voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.tabNavigator)

            implementation("io.ktor:ktor-client-core:2.0.3")
            implementation("io.ktor:ktor-client-cio:2.0.3")
            implementation("io.ktor:ktor-network:2.0.3")

            implementation(libs.ktor.serialization.kotlinx.json)

        }
    }
}

android {
    namespace = "com.ali.examplefirst"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.ali.examplefirst"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}
dependencies {
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.constraintlayout)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AppExample"
            packageVersion = "1.0.0"
        }
    }
}
