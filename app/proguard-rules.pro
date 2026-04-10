# ExifInterface
-keep class androidx.exifinterface.** { *; }
-keepclassmembers class androidx.exifinterface.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keepclassmembers class com.google.gson.** { *; }

# Coil
-keep class coil.** { *; }
-keepclassmembers class coil.** { *; }

# Model classes
-keep class com.katafract.exifarmor.models.** { *; }
-keepclassmembers class com.katafract.exifarmor.models.** { *; }

# Services
-keep class com.katafract.exifarmor.services.** { *; }
-keepclassmembers class com.katafract.exifarmor.services.** { *; }

# ViewModel
-keep class com.katafract.exifarmor.viewmodel.** { *; }
-keepclassmembers class com.katafract.exifarmor.viewmodel.** { *; }

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

-dontwarn sun.misc.Unsafe
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn com.google.errorprone.annotations.InlineMe
