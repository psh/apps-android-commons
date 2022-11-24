@file:Suppress("LocalVariableName", "PropertyName")

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-android-extensions")
    id("jacoco")
}
apply(from = "gitutils.gradle")
apply(from = "$rootDir/jacoco.gradle")

val isRunningOnTravisAndIsNotPRBuild = System.getenv("CI") == "true" && file("../play.p12").exists()
if (isRunningOnTravisAndIsNotPRBuild) {
    apply(plugin = "com.github.triplet.play")
}

val OKHTTP_VERSION: String by project
val BUTTERKNIFE_VERSION: String by project
val ADAPTER_DELEGATES_VERSION: String by project
val PAGING_VERSION: String by project
val DAGGER_VERSION: String by project
val KOTLIN_VERSION: String by project
val CORE_KTX_VERSION: String by project
val ROOM_VERSION: String by project
val PREFERENCE_VERSION: String by project
val MULTIDEX_VERSION: String by project

dependencies {

    implementation(project(":wikimedia-data-client"))
    // Utils
    implementation("in.yuvi:http.fluent:1.3")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.squareup.okhttp3:okhttp:$OKHTTP_VERSION") {
        isForce = true //API 19 support
    }
    implementation("com.squareup.okio:okio:3.2.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")  // Too big to change right now
    implementation("io.reactivex.rxjava2:rxjava:2.2.3")     // Too big to change right now
    implementation("com.jakewharton.rxbinding2:rxbinding:2.1.1")
    implementation("com.jakewharton.rxbinding3:rxbinding-appcompat:3.0.0")
    implementation("com.jakewharton.rxbinding2:rxbinding-support-v4:2.1.1")
    implementation("com.jakewharton.rxbinding2:rxbinding-appcompat-v7:2.1.1")
    implementation("com.jakewharton.rxbinding2:rxbinding-design:2.1.1")
    implementation("com.facebook.fresco:fresco:1.13.0")
    implementation("org.apache.commons:commons-lang3:3.8.1")

    // UI
    implementation("fr.avianey.com.viewpagerindicator:library:2.4.1.1@aar")
    implementation("com.github.chrisbanes:PhotoView:2.0.0")
    implementation("com.github.pedrovgs:renderers:3.3.3")
    implementation("com.mapbox.mapboxsdk:mapbox-android-sdk:9.2.1")
    implementation("com.mapbox.mapboxsdk:mapbox-android-plugin-localization-v9:0.12.0")
    implementation("com.mapbox.mapboxsdk:mapbox-android-plugin-scalebar-v9:0.5.0")
    implementation("com.mapbox.mapboxsdk:mapbox-android-telemetry:7.0.0")
    implementation("com.github.deano2390:MaterialShowcaseView:1.2.0")
    implementation("com.dinuscxj:circleprogressbar:1.1.1")
    implementation("com.karumi:dexter:5.0.0")
    implementation("com.jakewharton:butterknife:$BUTTERKNIFE_VERSION")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    kapt("com.jakewharton:butterknife-compiler:$BUTTERKNIFE_VERSION")
    implementation("com.hannesdorfmann:adapterdelegates4-kotlin-dsl-viewbinding:$ADAPTER_DELEGATES_VERSION")
    implementation("com.hannesdorfmann:adapterdelegates4-pagination:$ADAPTER_DELEGATES_VERSION")
    implementation("androidx.paging:paging-runtime-ktx:$PAGING_VERSION")
    testImplementation("androidx.paging:paging-common-ktx:$PAGING_VERSION")
    implementation("androidx.paging:paging-rxjava2-ktx:$PAGING_VERSION")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Logging
    implementation("ch.acra:acra-dialog:5.8.4")
    implementation("ch.acra:acra-mail:5.8.4")
    implementation("org.slf4j:slf4j-api:1.7.36")
    api("com.github.tony19:logback-android-classic:1.1.1-6") {
        exclude(group = "com.google.android", module = "android")
    }
    implementation("com.squareup.okhttp3:logging-interceptor:$OKHTTP_VERSION")

    // Dependency injector
    implementation("com.google.dagger:dagger-android:$DAGGER_VERSION")
    implementation("com.google.dagger:dagger-android-support:$DAGGER_VERSION")
    kapt("com.google.dagger:dagger-android-processor:$DAGGER_VERSION")
    kapt("com.google.dagger:dagger-compiler:$DAGGER_VERSION")
    kapt("com.google.dagger:dagger-android-processor:$DAGGER_VERSION")

    implementation("org.jetbrains.kotlin:kotlin-reflect:$KOTLIN_VERSION")

    //Mocking
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.mockito:mockito-inline:2.13.0")
    testImplementation("org.mockito:mockito-core:2.25.1")
    testImplementation("org.powermock:powermock-module-junit4:2.0.2")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.2")

    // Unit testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.9")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:$OKHTTP_VERSION")
    testImplementation("com.jraska.livedata:testing-ktx:1.1.2")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    testImplementation("com.facebook.soloader:soloader:0.10.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // Android testing
    androidTestImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$KOTLIN_VERSION")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.0")
    androidTestImplementation("androidx.test:runner:1.5.1")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.annotation:annotation:1.5.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:$OKHTTP_VERSION")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestUtil("androidx.test:orchestrator:1.4.2")

    // Debugging
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")

    // Support libraries
    implementation("com.google.android.material:material:1.8.0-alpha02")
    implementation("androidx.browser:browser:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.exifinterface:exifinterface:1.3.5")
    implementation("androidx.core:core-ktx:$CORE_KTX_VERSION")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.simplecityapps:recyclerview-fastscroll:2.0.1")

    //swipe_layout
    implementation("com.daimajia.swipelayout:library:1.2.0@aar")

    //Room
    implementation("androidx.room:room-runtime:$ROOM_VERSION")
    implementation("androidx.room:room-ktx:$ROOM_VERSION")
    implementation("androidx.room:room-rxjava2:$ROOM_VERSION")
    kapt("androidx.room:room-compiler:$ROOM_VERSION")
    // For Kotlin use kapt instead of annotationProcessor
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")

    // Pref
    // Java language implementation
    implementation("androidx.preference:preference:$PREFERENCE_VERSION")
    // Kotlin
    implementation("androidx.preference:preference-ktx:$PREFERENCE_VERSION")

    implementation("androidx.multidex:multidex:$MULTIDEX_VERSION")

    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    testImplementation("androidx.work:work-testing:2.7.1")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    implementation("io.github.coordinates2country:coordinates2country-android:1.3") {
        exclude(group = "com.google.android", module = "android")
    }
}

tasks.register<Exec>("disableAnimations") {
    group = "verification"

    val home = System.getenv("ANDROID_HOME")
    val adb = "$home/platform-tools/adb"

    commandLine(adb, "shell", "settings", "put", "global", "window_animation_scale", "0")
    commandLine(adb, "shell", "settings", "put", "global", "transition_animation_scale", "0")
    commandLine(adb, "shell", "settings", "put", "global", "animator_duration_scale", "0")
}

afterEvaluate {
    tasks.named("connectedBetaDebugAndroidTest") { dependsOn(":disableAnimations") }
    tasks.named("connectedProdDebugAndroidTest") { dependsOn(":disableAnimations") }
}

android {
    namespace = "fr.free.nrw.commons"

    compileSdk = 32

    defaultConfig {
        versionCode = 1029
        versionName = "4.0.3"
        setProperty("archivesBaseName", "app-commons-v$versionName-" + getBranchName())

        minSdk = 19
        targetSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        val TEST_USER_NAME: String? by project
        val TEST_USER_PASSWORD: String? by project
        buildConfigField ("String", "TEST_USERNAME", "\"" + (TEST_USER_NAME ?: "") + "\"")
        buildConfigField ("String", "TEST_PASSWORD", "\"" + (TEST_USER_PASSWORD ?: "") + "\"")
        buildConfigField ("String", "COMMIT_SHA", "\"" + getBuildVersion() + "\"")
    }

    packagingOptions {
        resources.excludes.add("META-INF/androidx.*")
        resources.excludes.add("META-INF/proguard/androidx-annotations.pro")
    }

    testOptions {
        animationsDisabled = true

        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }

        unitTests.all {
            it.jvmArgs?.add("-noverify")
        }
    }

    sourceSets {
        val test by getting
        // use kotlin only in tests (for now)
        test.java.srcDir("src/test/kotlin")

        // use main assets and resources in test
        test.assets.srcDir("src/main/assets")
        test.resources.srcDir("src/main/resoures")
    }

    signingConfigs {
        // configure keystore based on env vars in Travis for automated alpha builds
        if (isRunningOnTravisAndIsNotPRBuild) {
            getByName("release") {
                storeFile = file("../nr-commons.keystore")
                storePassword = System.getenv("keystore_password")
                keyAlias = System.getenv("key_alias")
                keyPassword = System.getenv("key_password")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            if (isRunningOnTravisAndIsNotPRBuild) {
                signingConfig = signingConfigs.getByName("release")
            }
        }

        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            versionNameSuffix = "-debug-" + getBranchName()
        }

        all {
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.txt"
            )
            testProguardFile("test-proguard-rules.txt")
        }
    }

    flavorDimensions += "tier"
    productFlavors {
        create("prod") {
            dimension = "tier"
            applicationId = "fr.free.nrw.commons"
            buildConfigField ("String", "WIKIMEDIA_API_POTD", "\"https://commons.wikimedia.org/w/api.php?action=featuredfeed&feed=potd&feedformat=rss&language=en\"")
            buildConfigField ("String", "WIKIMEDIA_API_HOST", "\"https://commons.wikimedia.org/w/api.php\"")
            buildConfigField ("String", "WIKIDATA_API_HOST", "\"https://www.wikidata.org/w/api.php\"")
            buildConfigField ("String", "WIKIDATA_URL", "\"https://www.wikidata.org\"")
            buildConfigField ("String", "WIKIMEDIA_FORGE_API_HOST", "\"https://tools.wmflabs.org/\"")
            buildConfigField ("String", "WIKIMEDIA_CAMPAIGNS_URL", "\"https://raw.githubusercontent.com/commons-app/campaigns/master/campaigns.json\"")
            buildConfigField ("String", "IMAGE_URL_BASE", "\"https://upload.wikimedia.org/wikipedia/commons\"")
            buildConfigField ("String", "HOME_URL", "\"https://commons.wikimedia.org/wiki/\"")
            buildConfigField ("String", "COMMONS_URL", "\"https://commons.wikimedia.org\"")
            buildConfigField ("String", "WIKIDATA_URL", "\"https://www.wikidata.org\"")
            buildConfigField ("String", "MOBILE_HOME_URL", "\"https://commons.m.wikimedia.org/wiki/\"")
            buildConfigField ("String", "SIGNUP_LANDING_URL", "\"https://commons.m.wikimedia.org/w/index.php?title=Special:CreateAccount&returnto=Main+Page&returntoquery=welcome%3Dyes\"")
            buildConfigField ("String", "SIGNUP_SUCCESS_REDIRECTION_URL", "\"https://commons.m.wikimedia.org/w/index.php?title=Main_Page&welcome=yes\"")
            buildConfigField ("String", "FORGOT_PASSWORD_URL", "\"https://commons.wikimedia.org/wiki/Special:PasswordReset\"")
            buildConfigField ("String", "PRIVACY_POLICY_URL", "\"https://github.com/commons-app/commons-app-documentation/blob/master/android/Privacy-policy.md\"")
            buildConfigField ("String", "ACCOUNT_TYPE", "\"fr.free.nrw.commons\"")
            buildConfigField ("String", "CONTRIBUTION_AUTHORITY", "\"fr.free.nrw.commons.contributions.contentprovider\"")
            buildConfigField ("String", "MODIFICATION_AUTHORITY", "\"fr.free.nrw.commons.modifications.contentprovider\"")
            buildConfigField ("String", "CATEGORY_AUTHORITY", "\"fr.free.nrw.commons.categories.contentprovider\"")
            buildConfigField ("String", "RECENT_SEARCH_AUTHORITY", "\"fr.free.nrw.commons.explore.recentsearches.contentprovider\"")
            buildConfigField ("String", "RECENT_LANGUAGE_AUTHORITY", "\"fr.free.nrw.commons.recentlanguages.contentprovider\"")
            buildConfigField ("String", "BOOKMARK_AUTHORITY", "\"fr.free.nrw.commons.bookmarks.contentprovider\"")
            buildConfigField ("String", "BOOKMARK_LOCATIONS_AUTHORITY", "\"fr.free.nrw.commons.bookmarks.locations.contentprovider\"")
            buildConfigField ("String", "BOOKMARK_ITEMS_AUTHORITY", "\"fr.free.nrw.commons.bookmarks.items.contentprovider\"")
            buildConfigField ("String", "DEPICTS_PROPERTY", "\"P180\"")
        }
        create("beta") {
            dimension = "tier"
            applicationId = "fr.free.nrw.commons.beta"
            // What values do we need to hit the BETA versions of the site / api ?
            buildConfigField ("String", "WIKIMEDIA_API_POTD", "\"https://commons.wikimedia.org/w/api.php?action=featuredfeed&feed=potd&feedformat=rss&language=en\"")
            buildConfigField ("String", "WIKIMEDIA_API_HOST", "\"https://commons.wikimedia.beta.wmflabs.org/w/api.php\"")
            buildConfigField ("String", "WIKIDATA_API_HOST", "\"https://www.wikidata.org/w/api.php\"")
            buildConfigField ("String", "WIKIDATA_URL", "\"https://www.wikidata.org\"")
            buildConfigField ("String", "WIKIMEDIA_FORGE_API_HOST", "\"https://tools.wmflabs.org/\"")
            buildConfigField ("String", "WIKIMEDIA_CAMPAIGNS_URL", "\"https://raw.githubusercontent.com/commons-app/campaigns/master/campaigns_beta_active.json\"")
            buildConfigField ("String", "IMAGE_URL_BASE", "\"https://upload.beta.wmflabs.org/wikipedia/commons\"")
            buildConfigField ("String", "HOME_URL", "\"https://commons.wikimedia.beta.wmflabs.org/wiki/\"")
            buildConfigField ("String", "COMMONS_URL", "\"https://commons.wikimedia.beta.wmflabs.org\"")
            buildConfigField ("String", "WIKIDATA_URL", "\"https://www.wikidata.org\"")
            buildConfigField ("String", "MOBILE_HOME_URL", "\"https://commons.m.wikimedia.beta.wmflabs.org/wiki/\"")
            buildConfigField ("String", "SIGNUP_LANDING_URL", "\"https://commons.m.wikimedia.beta.wmflabs.org/w/index.php?title=Special:CreateAccount&returnto=Main+Page&returntoquery=welcome%3Dyes\"")
            buildConfigField ("String", "SIGNUP_SUCCESS_REDIRECTION_URL", "\"https://commons.m.wikimedia.beta.wmflabs.org/w/index.php?title=Main_Page&welcome=yes\"")
            buildConfigField ("String", "FORGOT_PASSWORD_URL", "\"https://commons.wikimedia.beta.wmflabs.org/wiki/Special:PasswordReset\"")
            buildConfigField ("String", "PRIVACY_POLICY_URL", "\"https://github.com/commons-app/commons-app-documentation/blob/master/android/Privacy-policy.md\"")
            buildConfigField ("String", "ACCOUNT_TYPE", "\"fr.free.nrw.commons.beta\"")
            buildConfigField ("String", "CONTRIBUTION_AUTHORITY", "\"fr.free.nrw.commons.beta.contributions.contentprovider\"")
            buildConfigField ("String", "MODIFICATION_AUTHORITY", "\"fr.free.nrw.commons.beta.modifications.contentprovider\"")
            buildConfigField ("String", "CATEGORY_AUTHORITY", "\"fr.free.nrw.commons.beta.categories.contentprovider\"")
            buildConfigField ("String", "RECENT_SEARCH_AUTHORITY", "\"fr.free.nrw.commons.beta.explore.recentsearches.contentprovider\"")
            buildConfigField ("String", "RECENT_LANGUAGE_AUTHORITY", "\"fr.free.nrw.commons.beta.recentlanguages.contentprovider\"")
            buildConfigField ("String", "BOOKMARK_AUTHORITY", "\"fr.free.nrw.commons.beta.bookmarks.contentprovider\"")
            buildConfigField ("String", "BOOKMARK_LOCATIONS_AUTHORITY", "\"fr.free.nrw.commons.beta.bookmarks.locations.contentprovider\"")
            buildConfigField ("String", "BOOKMARK_ITEMS_AUTHORITY", "\"fr.free.nrw.commons.beta.bookmarks.items.contentprovider\"")
            buildConfigField ("String", "DEPICTS_PROPERTY", "\"P245962\"")
        }
    }

    lint {
        disable += "MissingTranslation"
        disable += "ExtraTranslation"
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

// TODO
fun getBranchName(): String = ""
fun getBuildVersion(): String = ""

/*
ext.getBuildVersion = { ->
    // Short-term fix for #1157. Should ideally look into why method is failing.
    try {
        def stdout = new ByteArrayOutputStream()
        exec {
            commandLine 'git' , 'rev-parse' , '--short' , 'HEAD'
            standardOutput = stdout
        }
        return stdout.toString().trim()
    } catch (ignored) {
        return null
    }
}

ext.getBranchName = { ->
    try {
        def stdOut = new ByteArrayOutputStream()
        exec {
            commandLine 'git', 'rev-parse', '--abbrev-ref', 'HEAD'
            standardOutput = stdOut
        }
        return stdOut.toString().trim()
    } catch (ignored) {
        return null
    }
}

 */