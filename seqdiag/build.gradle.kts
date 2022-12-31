import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("app.cash.paparazzi")
    id("com.vanniktech.maven.publish")
}

group = "com.zachklipp.seqdiag"
version = "0.2.0-SNAPSHOT"

kotlin {
    android {
        publishLibraryVariants("release")
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(compose.preview)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(project(":samples"))
                implementation(kotlin("reflect"))
                implementation("junit:junit:4.13.2")
                implementation("com.google.testparameterinjector:test-parameter-injector:1.10")
            }
        }

        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting
    }
}

android {
    namespace = "com.zachklipp.seqdiag"
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT)
    signAllPublications()

    pom {
        name.set("Compose Sequence Diagram")
        description.set("Sequence diagram renderer for Compose Multiplatform.")
        inceptionYear.set("2022")
        url.set("https://github.com/zach-klippenstein/compose-seqdiag")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("zach-klippenstein")
                name.set("Zach Klippenstein")
                url.set("https://github.com/zach-klippenstein/")
            }
        }
        scm {
            url.set("https://github.com/zach-klippenstein/compose-seqdiag")
            connection.set("scm:git:git://github.com/zach-klippenstein/compose-seqdiag.git")
            developerConnection.set("scm:git:ssh://git@github.com/zach-klippenstein/compose-seqdiag.git")
        }
    }
}