# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/djek-grif/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Shrinking

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimizing

-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

-dontwarn okio.**
-dontwarn com.squareup.**
-dontwarn com.squareup.okhttp.**
-dontwarn java.lang.invoke.*

# Output

-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt

# Keep =====================================================

-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.inject.* { *; }
-keep class org.apache.http.* { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.* { *; }
-keep class javax.inject.* { *; }

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Joda
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keep class **$$ViewInjector { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Models package (we have to keep it for firebase)
-keepattributes Signature
-keepclassmembers class com.djekgrif.alternativeradio.network.model.** {
  *;
}

# Dagger2 ================================================
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8

# Logs
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# ACRA
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# rxjava =================================================
-dontwarn sun.misc.**
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Common ==================================================

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
