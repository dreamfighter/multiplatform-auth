import com.android.build.gradle.internal.ide.kmp.KotlinAndroidSourceSetMarker.Companion.android
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinNativeCocoaPods)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.serializationPlugin)
    id("module.publication")
    id("io.github.ttypic.swiftklib") version "0.6.3"
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "multiplatform-auth"
        browser{
            testTask {
                useKarma{
                    useChrome()
                }
            }
        }
        binaries.executable()
    }
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    cocoapods {
        ios.deploymentTarget = "11.0"
        framework {
            baseName = "MultiplatformAuthGoogle"
            isStatic = true
        }
        pod("GoogleSignIn")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()

    sourceSets {
        iosMain.dependencies {
            //implementation(libs.github.mirzemehdi.google)
        }
        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.google.identity.googleid)
            implementation(libs.kotlinx.coroutines.android)
            implementation(compose.components.resources)
            //implementation(libs.github.mirzemehdi.google)
        }
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(compose.runtime)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.system.lambda)
            }
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.compilations {
            val main by getting {
                cinterops {
                    create("Utils")
                }
            }
        }
    }
}

android {
    namespace = "id.dreamfighter.multiplatform.auth"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

swiftklib {
    create("Utils") {
        path = file("native/Utils")
        packageName("id.dreamfighter.multiplatform.swift")
    }
}