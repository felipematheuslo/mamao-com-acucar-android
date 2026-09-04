# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve line numbers and source file names for readable stack traces in crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve annotations for runtime reflection
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Firebase Firestore models
# Preserves data classes, fields, and constructors required for Firestore reflection (toObject)
-keepclassmembers class com.felipelaurindo.mamaocomacucar.data.model.** {
    public <init>(...);
    public *;
}
-keep class com.felipelaurindo.mamaocomacucar.data.model.** { *; }

# OSMDroid (OpenStreetMap)
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# Google Mobile Ads (AdMob)
-keep public class com.google.android.gms.ads.** {
    public *;
}
-keep class com.google.ads.** {
    public *;
}