plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
  id("com.google.gms.google-services")
}

android {
  namespace = "com.openclassrooms.hexagonal.games"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.openclassrooms.hexagonal.games"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.11"
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
}

dependencies {
  //kotlin
  implementation(platform(libs.kotlin.bom))

  //DI
  implementation(libs.hilt)
    implementation(libs.runtime.livedata)
  implementation(libs.ui.test.junit4.android)
  ksp(libs.hilt.compiler)
  implementation(libs.hilt.navigation.compose)

  //compose
  implementation(platform(libs.compose.bom))
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.material)
  implementation(libs.compose.material3)
  implementation(libs.lifecycle.runtime.compose)
  debugImplementation(libs.compose.ui.tooling)
  debugImplementation(libs.compose.ui.test.manifest)

  implementation(libs.activity.compose)
  implementation(libs.navigation.compose)
  
  implementation(libs.kotlinx.coroutines.android)
  
  implementation(libs.coil.compose)
  implementation(libs.accompanist.permissions)

  testImplementation(libs.junit)
  androidTestImplementation(libs.ext.junit)
  androidTestImplementation(libs.espresso.core)


  //firebase sdk
  implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
  implementation("com.google.firebase:firebase-analytics")
  //Firebase Auth UI
  implementation("com.firebaseui:firebase-ui-auth:8.0.2")
  implementation("com.google.firebase:firebase-auth-ktx")
  //Firebase Firestore
  implementation("com.google.firebase:firebase-firestore-ktx")
  implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
  //Firebase Messaging
  implementation("com.google.firebase:firebase-messaging")
  //lifecycle
  implementation(libs.lifecycle.runtime.compose.v261)

  //test
  testImplementation("junit:junit:4.13.2")
  testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
  testImplementation ("androidx.arch.core:core-testing:2.1.0")
  testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
  //logback
  testImplementation("ch.qos.logback:logback-classic:1.4.8")
  //mockk
  testImplementation("io.mockk:mockk:1.13.5")

}