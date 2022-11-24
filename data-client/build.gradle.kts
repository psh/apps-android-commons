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

val OKHTTP_VERSION: String by project

dependencies {
    implementation("androidx.collection:collection-ktx:1.2.0")
    implementation("androidx.annotation:annotation:1.5.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.4.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.3")     // Too big to change right now
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")  // Too big to change right now
    implementation("org.apache.commons:commons-lang3:3.8.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:2.25.1")
    testImplementation("org.robolectric:robolectric:4.9")
    testImplementation("com.squareup.okhttp3:mockwebserver:$OKHTTP_VERSION")
    testImplementation("commons-io:commons-io:2.6")
}
