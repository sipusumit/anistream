plugins {
    alias(libs.plugins.android.library)
    id("com.apollographql.apollo") version "4.4.1"
}

apollo {
    service("AllAnime") {
        packageName.set("in.sipusumit.aniapi.source.allanime.graphql")

        introspection {
            endpointUrl.set("https://api.allanime.day/api")
            headers.put("Referer", "https://allanime.to")
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }
}

android {
    namespace = "in.sipusumit.aniapi"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 28

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // --- Core ---
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.apollo.runtime)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // --- JVM UNIT TESTS (FAST) ---
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // --- ANDROID TESTS (SLOW, KEEP FOR LATER) ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}