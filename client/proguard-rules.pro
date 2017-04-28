# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /apps/android-sdk-macosx/tools/proguard/proguard-android.txt
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
-dontobfuscate
-optimizations !code/simplification/variable,!code/allocation/variable

-dontwarn okio.**
-dontwarn sun.misc.Unsafe

################################ Begin: Protobuf ################################
-dontwarn com.google.protobuf.**
-keep public class ir.asparsa.common.net.dto.** { *; }
-keep public class * extends com.google.protobuf.GeneratedMessage { *; }
################################ End: Protobuf   ################################

################################ Begin: Picasso  ################################
-dontwarn com.squareup.okhttp.**
################################ End: Picasso    ################################

################################ Begin: Retrofit ################################
-keep class retrofit2.** { *; }
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
################################ End: Retrofit   ################################

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment



# The Maps API uses custom Parcelables.
# Use this rule (which is slightly broader than the standard recommended one)
# to avoid obfuscating them.
-keepclassmembers class * implements android.os.Parcelable {
    static *** CREATOR;
}

# The Maps API uses serialization.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Begin view holders
-keepclassmembers public class ir.asparsa.hobbytaste.ui.list.holder.** {
    public <init>(...);
}
-keepclassmembers public class ir.asparsa.android.ui.list.holder.** {
    public <init>(...);
}
# End view holders

# Begin ir.asparsa.android.ui.view.NonSwipeableViewPager
-keepclassmembers class android.support.v4.view.ViewPager {
    private <fields>;
}
# End ir.asparsa.android.ui.view.NonSwipeableViewPager