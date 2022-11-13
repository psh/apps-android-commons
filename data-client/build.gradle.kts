plugins {
    id("kotlin-android")
    id("com.android.library")
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 19
        targetSdk = 33
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    lint {
        abortOnError = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.collection:collection-ktx:1.2.0")
    implementation("androidx.annotation:annotation:1.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.4.0")
    implementation("com.squareup.retrofit2:converter-gson:2.4.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.4.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.3")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
    implementation("org.apache.commons:commons-lang3:3.8.1")

    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:2.8.9")
    testImplementation("org.robolectric:robolectric:3.8")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.12.1")
    testImplementation("commons-io:commons-io:2.6")
}
