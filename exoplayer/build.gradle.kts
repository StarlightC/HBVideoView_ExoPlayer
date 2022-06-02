import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("maven-publish")
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("jniLibs")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    api("com.google.android.exoplayer:exoplayer-core:2.17.1")
    api("com.google.android.exoplayer:exoplayer-hls:2.17.1")
    implementation("com.starlightc.video:hbvideoview_core:0.0.6")
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                url = uri("https://maven.pkg.github.com/StarlightC/HBVideoView_ExoPlayer")
                credentials {
                    username = gradleLocalProperties(rootDir).getProperty("GITHUB_USER").toString()
                    password = gradleLocalProperties(rootDir).getProperty("GITHUB_TOKEN").toString()
                }
            }
        }
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.starlightc.exoplayer"
                artifactId = "hbvideoview_exoplayer"
                version = "0.0.11"
            }
        }
    }
}