plugins {
    id("com.android.application") version "8.6.0" apply false
    id("com.android.library") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false // Firebase eklentisi
}

// Bu seviyede "classpath" kullanılmaz, çünkü Kotlin DSL'de yerine "plugins" bloğu kullanılır.
