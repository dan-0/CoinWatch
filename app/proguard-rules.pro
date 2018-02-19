-optimizationpasses 5
-allowaccessmodification
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose

#Retrofit Rules
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-dontwarn okio.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepclassmembers class com.idleoffice.coinwatch.data.** {
    <init>(...);
    <fields>;
}

# For crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
-dontwarn io.realm.**

# dontwarns
-dontwarn retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
-dontwarn rx.Observable
-dontwarn com.google.errorprone.annotations.**
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

# rxjava
-keep class io.reactivex.schedulers.Schedulers {
    public static <methods>;
}

# moshi
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-dontwarn org.jetbrains.annotations.**
-keep class kotlin.Metadata { *; }

# kotlin
# See: https://youtrack.jetbrains.com/issue/KT-7652#comment=27-993857
-keep class kotlin.** {
    public protected *;
}
-keep enum ** {
    public protected *;
}
-keepattributes InnerClasses,EnclosingMethod