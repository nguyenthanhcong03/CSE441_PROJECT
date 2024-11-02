plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.cse441_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cse441_project"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes.add("META-INF/NOTICE.md")
            excludes.add("META-INF/LICENSE.md")
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // https://mvnrepository.com/artifact/at.favre.lib/bcrypt
    implementation("at.favre.lib:bcrypt:0.10.2")
    // https://mvnrepository.com/artifact/com.github.bumptech.glide/glide
    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
    // https://mvnrepository.com/artifact/com.github.bumptech.glide/annotations
    implementation("com.github.bumptech.glide:annotations:5.0.0-rc01")



    // Declare the dependency for Firebase Storage
    implementation(platform("com.google.firebase:firebase-bom:32.0.0")) // BOM version
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-appcheck")

    //Lombok

    implementation("org.projectlombok:lombok:1.18.24")
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    implementation(libs.android.mail)
    implementation(libs.android.activation)



    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(kotlin("script-runtime"))

}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}




