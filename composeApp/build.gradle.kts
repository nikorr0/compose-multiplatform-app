import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.ComposeHotRun
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.hotReload)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.buildConfig)

    // id("app.cash.sqldelight") version "2.1.0"
}



kotlin {
    androidTarget {
        //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    jvm()

    sourceSets {
        val voyagerVersion = "1.1.0-beta03"
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kermit)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.multiplatformSettings)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kstore)
            implementation(libs.materialKolor)

            implementation("io.ktor:ktor-client-auth:3.1.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

            // implementation(libs.material.icons.extended)
            // implementation("androidx.compose.material:material-icons-extended:1.8.0")

            implementation("io.coil-kt.coil3:coil-compose:3.2.0")

            // Multiplatform
            // Navigator
            implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")

            // Screen Model
            implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")

            // BottomSheetNavigator
            implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:$voyagerVersion")

            // TabNavigator
            implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")

            // Transitions
            implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")

            // Koin integration
            implementation("cafe.adriel.voyager:voyager-koin:$voyagerVersion")

            // implementation("app.cash.sqldelight:runtime:2.1.0")
            // implementation("app.cash.sqldelight:coroutines-extensions:2.1.0")

            // Hilt integration
//            implementation("cafe.adriel.voyager:voyager-hilt:$voyagerVersion")
//
//            // LiveData integration
//            implementation("cafe.adriel.voyager:voyager-livedata:$voyagerVersion")


            // Kodein integration
            implementation("cafe.adriel.voyager:voyager-kodein:$voyagerVersion")

            // RxJava integration
            implementation("cafe.adriel.voyager:voyager-rxjava:$voyagerVersion")

        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kstore.file)
            implementation("androidx.activity:activity-compose:1.9.0")

            implementation("cafe.adriel.voyager:voyager-hilt:${voyagerVersion}")
            // LiveData integration
            implementation("cafe.adriel.voyager:voyager-livedata:${voyagerVersion}")

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
            implementation("io.coil-kt:coil-compose:3.3.0")

            // implementation("app.cash.sqldelight:android-driver:2.1.0")
            // implementation("androidx.activity:activity-ktx:1.10.1")
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kstore.file)

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")

            // implementation("app.cash.sqldelight:sqlite-driver:2.1.0")

        }

    }
}

android {
    namespace = "org.company.app"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 35

        applicationId = "org.company.app.androidApp"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

//https://developer.android.com/develop/ui/compose/testing#setup
dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.ui.core)
    androidTestImplementation(libs.androidx.uitest.junit4)
    debugImplementation(libs.androidx.uitest.testManifest)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CountryCardsApp"
            packageVersion = "1.0.0"

            linux {
                iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
            }
            windows {
                iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
            }
            macOS {
                iconFile.set(project.file("desktopAppIcons/MacosIcon.icns"))
                bundleID = "org.company.app.desktopApp"
            }
        }
    }
}

//https://github.com/JetBrains/compose-hot-reload
composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}
tasks.withType<ComposeHotRun>().configureEach {
    mainClass.set("MainKt")
}

buildConfig {
    // BuildConfig configuration here.
    // https://github.com/gmazzo/gradle-buildconfig-plugin#usage-in-kts
}

//sqldelight {
//    databases {
//        create("SessionDatabase") {
//            packageName.set("org.company.app")
//        }
//    }
//}