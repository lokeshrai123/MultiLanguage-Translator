# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-printmapping mapping.txt
-verbose
-dontoptimize
-dontpreverify
-dontshrink
-dontskipnonpubliclibraryclassmembers
-dontusemixedcaseclassnames
-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keepclassmembers class * {
    public void *test*(...);
}

-keep class * extends android.app.Activity
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

-keep class com.facebook.** { *; }
-keep class com.androidquery.** { *; }
-keep class com.google.** { *; }
-keep class org.acra.** { *; }
-keep class org.apache.** { *; }
-keep class com.mobileapptracker.** { *; }
-keep class com.nostra13.** { *; }
-keep class net.simonvt.** { *; }
-keep class android.support.** { *; }
-keep class com.nnacres.app.model.** { *; }
-keep class com.facebook.** { *; }
-keep class com.astuetz.** { *; }
-keep class twitter4j.** { *; }
-keep class com.actionbarsherlock.** { *; }
-keep class com.dg.libs.** { *; }
-keep class android.support.v4.** { *; }
-keep class com.bluetapestudio.templateproject.** { *; }
-keep class com.yourideatoreality.model.** { *; }
-keep interface com.yourideatoreality.model.** { *; }
-keep class com.bluetapestudio.** { *; }
-keep interface com.bluetapestudio.** { *; }
# Suppress warnings if you are NOT using IAP:
-dontwarn com.nnacres.app.**
-dontwarn com.androidquery.**
-dontwarn com.google.**
-dontwarn org.acra.**
-dontwarn org.apache.**
-dontwarn com.mobileapptracker.**
-dontwarn com.nostra13.**
-dontwarn net.simonvt.**
-dontwarn android.support.**
-dontwarn com.facebook.**
-dontwarn twitter4j.**
-dontwarn com.astuetz.**
-dontwarn com.actionbarsherlock.**
-dontwarn com.dg.libs.**
-dontwarn  com.bluetapestudio.templateproject.**

-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# The official support library.
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

#  Library JARs.
#-keep class de.greenrobot.dao.** { *; }
#-keep interface de.greenrobot.dao.** { *; }
# Library projects.
-keep class com.actionbarsherlock.** { *; }
-keep interface com.actionbarsherlock.** { *; }
#Keep native
-keepclasseswithmembernames class * {
    native <methods>;
}


-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault



-keep class com.jaredrummler.android.processes.**{ *; }
-keep interface com.jaredrummler.android.processes.**{ *; }
-keep enum com.jaredrummler.android.processes.**{ *; }


-keep class me.itangqi.waveloadingview.**{ *; }
-keep interface me.itangqi.waveloadingview.**{ *; }
-keep enum me.itangqi.waveloadingview.**{ *; }


-keep class antonkozyriatskyi.circularprogressindicator.**{ *; }
-keep interface antonkozyriatskyi.circularprogressindicator.**{ *; }
-keep enum antonkozyriatskyi.circularprogressindicator.**{ *; }


-keep class org.jetbrains.**{ *; }
-keep interface org.jetbrains.**{ *; }
-keep enum org.jetbrains.**{ *; }


-keep class com.goodiebag.protractorview.**{ *; }
-keep interface com.goodiebag.protractorview.**{ *; }
-keep enum com.goodiebag.protractorview.**{ *; }


-keep class com.karumi.dexter.**{ *; }
-keep interface com.karumi.dexter.**{ *; }
-keep enum com.karumi.dexter.**{ *; }


-keep class com.inmobi.** { *; }
-dontwarn com.inmobi.**
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-dontwarn com.squareup.picasso.**
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{public *;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{public *;}
#skip the Picasso library classes
-keep class com.squareup.picasso.** {*;}
-dontwarn com.squareup.picasso.**
-dontwarn com.squareup.okhttp.**
#skip Moat classes
-keep class com.moat.** {*;}
-dontwarn com.moat.**
#skip AVID classes
-keep class com.integralads.avid.library.** {*;}


-keep class com.arthenica..**{ *; }
-keep interface com.arthenica..**{ *; }
-keep enum com.arthenica..**{ *; }


-keep class com.integralads.**{ *; }
-keep interface com.integralads.**{ *; }
-keep enum com.integralads.**{ *; }


-dontwarn com.applovin.**
-keep class com.applovin.** { *; }
-keep class com.google.android.gms.ads.identifier.** { *; }




-keep class org.greenrobot.eventbus.**{ *; }
-keep interface org.greenrobot.eventbus.**{ *; }
-keep enum org.greenrobot.eventbus.**{ *; }



-keep class com.intuit.sdp.**{ *; }
-keep interface com.intuit.sdp.**{ *; }
-keep enum com.intuit.sdp.**{ *; }



-keep class * implements com.coremedia.iso.boxes.Box { *; }
-dontwarn com.coremedia.iso.boxes.**
-dontwarn com.googlecode.mp4parser.authoring.tracks.mjpeg.**
-dontwarn com.googlecode.mp4parser.authoring.tracks.ttml.**







# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keep class com.github.mikephil.**{ *; }
-keep interface com.github.mikephil.**{ *; }
-keep enum com.github.mikephil.**{ *; }

-keep class net.danlew.android.joda.**{ *; }
-keep interface net.danlew.android.joda.**{ *; }
-keep enum net.danlew.android.joda.**{ *; }

-keep class butterknife.**{ *; }
-keep interface butterknife.**{ *; }
-keep enum butterknife.**{ *; }

-keep class com.github.machinarius.preferencefragment.**{ *; }
-keep interface com.github.machinarius.preferencefragment.**{ *; }
-keep enum com.github.machinarius.preferencefragment.**{ *; }

-keep class okhttp3.**{ *; }
-keep interface okhttp3.**{ *; }
-keep enum okhttp3.**{ *; }


-keep class okio.**{ *; }
-keep interface okio.**{ *; }
-keep enum okio.**{ *; }


-keep class org.robolectric.**{ *; }
-keep interface org.robolectric.**{ *; }
-keep enum org.robolectric.**{ *; }


-keep class org.intellij.lang.annotations.**{ *; }
-keep interface org.intellij.lang.annotations.**{ *; }
-keep enum org.intellij.lang.annotations.**{ *; }


-keep class org.jetbrains.annotations.**{ *; }
-keep interface org.jetbrains.annotations.**{ *; }
-keep enum org.jetbrains.annotations.**{ *; }


-keep class org.fest.assertions.api.**{ *; }
-keep interface org.fest.assertions.api.**{ *; }
-keep enum org.fest.assertions.api.**{ *; }


-keep class com.mikepenz.**{ *; }
-keep interface com.mikepenz.**{ *; }
-keep enum com.mikepenz.**{ *; }


-keep class com.vanniktech.vntfontlistpreference.**{ *; }
-keep interface com.vanniktech.vntfontlistpreference.**{ *; }
-keep enum com.vanniktech.vntfontlistpreference.**{ *; }


-keep class com.vanniktech.vntnumberpickerpreference.**{ *; }
-keep interface com.vanniktech.vntnumberpickerpreference.**{ *; }
-keep enum com.vanniktech.vntnumberpickerpreference.**{ *; }

-keep class me.grantland.widget.**{ *; }
-keep interface me.grantland.widget.**{ *; }
-keep enum me.grantland.widget.**{ *; }


-keep class com.j256.ormlite.**{ *; }
-keep interface com.j256.ormlite.**{ *; }
-keep enum com.j256.ormlite.**{ *; }


-keep class org.ocpsoft.prettytime.**{ *; }
-keep interface org.ocpsoft.prettytime.**{ *; }
-keep enum org.ocpsoft.prettytime.**{ *; }


-keep class de.hdodenhof.circleimageview.**{ *; }
-keep interface de.hdodenhof.circleimageview.**{ *; }
-keep enum de.hdodenhof.circleimageview.**{ *; }


-keep class info.hoang8f.**{ *; }
-keep interface info.hoang8f.**{ *; }
-keep enum info.hoang8f.**{ *; }


-keep class com.kobakei.ratethisapp.**{ *; }
-keep interface com.kobakei.ratethisapp.**{ *; }
-keep enum com.kobakei.ratethisapp.**{ *; }


-keep class io.github.douglasjunior.androidSimpleTooltip.**{ *; }
-keep interface io.github.douglasjunior.androidSimpleTooltip.**{ *; }
-keep enum io.github.douglasjunior.androidSimpleTooltip.**{ *; }


-keep class retrofit2.http.**{ *; }
-keep interface retrofit2.http.**{ *; }
-keep enum retrofit2.http.**{ *; }


-keep class retrofit2.converter.gson.**{ *; }
-keep interface retrofit2.converter.gson.**{ *; }
-keep enum retrofit2.converter.gson.**{ *; }


-keep class retrofit2.adapter.rxjava.**{ *; }
-keep interface retrofit2.adapter.rxjava.**{ *; }
-keep enum retrofit2.adapter.rxjava.**{ *; }


-keep class com.squareup.javawriter.**{ *; }
-keep interface com.squareup.javawriter.**{ *; }
-keep enum com.squareup.javawriter.**{ *; }


-keep class bolts.**{ *; }
-keep interface bolts.**{ *; }
-keep enum bolts.**{ *; }


-keep class de.greenrobot.event.**{ *; }
-keep interface de.greenrobot.event.**{ *; }
-keep enum de.greenrobot.event.**{ *; }


-keep class rx.android.**{ *; }
-keep interface rx.android.**{ *; }
-keep enum rx.android.**{ *; }


-keep class rx.**{ *; }
-keep interface rx.**{ *; }
-keep enum rx.**{ *; }


-keep class javax.annotation.**{ *; }
-keep interface javax.annotation.**{ *; }
-keep enum javax.annotation.**{ *; }


-keep class javax.inject.**{ *; }
-keep interface javax.inject.**{ *; }
-keep enum javax.inject.**{ *; }


-keep class com.anjlab.android.iab.v3.**{ *; }
-keep interface com.anjlab.android.iab.v3.**{ *; }
-keep enum com.anjlab.android.iab.v3.**{ *; }


-keep class com.bartoszlipinski.recyclerviewheader2.**{ *; }
-keep interface com.bartoszlipinski.recyclerviewheader2.**{ *; }
-keep enum com.bartoszlipinski.recyclerviewheader2.**{ *; }


-keep class com.h6ah4i.android.widget.advrecyclerview.**{ *; }
-keep interface com.h6ah4i.android.widget.advrecyclerview.**{ *; }
-keep enum com.h6ah4i.android.widget.advrecyclerview.**{ *; }


-keep class android.arch.**{ *; }
-keep interface android.arch.**{ *; }
-keep enum android.arch.**{ *; }


-keep class edu.emory.mathcs.backport.java.util.**{ *; }
-keep interface edu.emory.mathcs.backport.java.util.**{ *; }
-keep enum edu.emory.mathcs.backport.java.util.**{ *; }


-keep class org.codehaus.classworlds.**{ *; }
-keep interface org.codehaus.classworlds.**{ *; }
-keep enum org.codehaus.classworlds.**{ *; }


-keep class android.support.**{ *; }
-keep interface android.support.**{ *; }
-keep enum android.support.**{ *; }


-keep class com.ximpleware.**{ *; }
-keep interface com.ximpleware.**{ *; }
-keep enum com.ximpleware.**{ *; }


-keep class java_cup.**{ *; }
-keep interface java_cup.**{ *; }
-keep enum java_cup.**{ *; }


-keep class org.apache.xerces.**{ *; }
-keep interface org.apache.xerces.**{ *; }
-keep enum org.apache.xerces.**{ *; }


-keep class net.steamcrafted.materialiconlib.**{ *; }
-keep interface net.steamcrafted.materialiconlib.**{ *; }
-keep enum net.steamcrafted.materialiconlib.**{ *; }


-keep class org.codehaus.**{ *; }
-keep interface org.codehaus.**{ *; }
-keep enum org.codehaus.**{ *; }


-keep class org.fest.**{ *; }
-keep interface org.fest.**{ *; }
-keep enum org.fest.**{ *; }


-keep class org.hamcrest.**{ *; }
-keep interface org.hamcrest.**{ *; }
-keep enum org.hamcrest.**{ *; }


-keep class org.objenesis.**{ *; }
-keep interface org.objenesis.**{ *; }
-keep enum org.objenesis.**{ *; }


-keep class org.objectweb.**{ *; }
-keep interface org.objectweb.**{ *; }
-keep enum org.objectweb.**{ *; }


-keep class org.robolectric.**{ *; }
-keep interface org.robolectric.**{ *; }
-keep enum org.robolectric.**{ *; }


-keep class org.ibex.**{ *; }
-keep interface org.ibex.**{ *; }
-keep enum org.ibex.**{ *; }


-keep class vc908.stickerfactory.**{ *; }
-keep interface vc908.stickerfactory.**{ *; }
-keep enum vc908.stickerfactory.**{ *; }


-keep class io.realm.**{ *; }
-keep interface io.realm.**{ *; }
-keep enum io.realm.**{ *; }

-dontwarn io.realm.**


-keep class yuku.ambilwarna.**{ *; }
-keep interface yuku.ambilwarna.**{ *; }
-keep enum yuku.ambilwarna.**{ *; }


-keep class com.isseiaoki.simplecropview.**{ *; }
-keep interface com.isseiaoki.simplecropview.**{ *; }
-keep enum com.isseiaoki.simplecropview.**{ *; }


-keep class com.esafirm.imagepicker.**{ *; }
-keep interface com.esafirm.imagepicker.**{ *; }
-keep enum com.esafirm.imagepicker.**{ *; }


#### ads library

-keep class rebus.permissionutils.**{ *; }
-keep interface rebus.permissionutils.**{ *; }
-keep enum rebus.permissionutils.**{ *; }

-keep class com.loopj.android.http.**{ *; }
-keep interface com.loopj.android.http.**{ *; }
-keep enum com.loopj.android.http.**{ *; }


-keep class com.github.chrisbanes.photoview.**{ *; }
-keep interface com.github.chrisbanes.photoview.**{ *; }
-keep enum com.github.chrisbanes.photoview.**{ *; }


-keep class com.crashlytics.android.**{ *; }
-keep interface com.crashlytics.android.**{ *; }
-keep enum com.crashlytics.android.**{ *; }


-keep class fabric.**{ *; }
-keep interface fabric.**{ *; }
-keep enum fabric.**{ *; }


-keep class com.applovin.mediation.**{ *; }
-keep interface com.applovin.mediation.**{ *; }
-keep enum com.applovin.mediation.**{ *; }


-keep class com.google.ads.mediation.**{ *; }
-keep interface com.google.ads.mediation.**{ *; }
-keep enum com.google.ads.mediation.**{ *; }


-keep class com.caverock.androidsvg.**{ *; }
-keep interface com.caverock.androidsvg.**{ *; }
-keep enum com.caverock.androidsvg.**{ *; }

-keep class it.sephiroth.android.library.imagezoom.**{ *; }
-keep interface it.sephiroth.android.library.imagezoom.**{ *; }
-keep enum it.sephiroth.android.library.imagezoom.**{ *; }


-keep class com.theartofdev.edmodo.cropper.**{ *; }
-keep interface com.theartofdev.edmodo.cropper.**{ *; }
-keep enum com.theartofdev.edmodo.cropper.**{ *; }



-keep class com.xw.repo.**{ *; }
-keep interface com.xw.repo.**{ *; }
-keep enum com.xw.repo.**{ *; }



-keep class io.fabric.sdk.android.**{ *; }
-keep interface io.fabric.sdk.android.**{ *; }
-keep enum io.fabric.sdk.android.**{ *; }


### app


-keep class com.theartofdev.edmodo.cropper.**{ *; }
-keep interface com.theartofdev.edmodo.cropper.**{ *; }
-keep enum com.theartofdev.edmodo.cropper.**{ *; }


-keep class com.hanks.**{ *; }
-keep interface com.hanks.**{ *; }
-keep enum com.hanks.**{ *; }


-keep class com.prolificinteractive.materialcalendarview.**{ *; }
-keep interface com.prolificinteractive.materialcalendarview.**{ *; }
-keep enum com.prolificinteractive.materialcalendarview.**{ *; }


-keep class com.flask.colorpicker.**{ *; }
-keep interface com.flask.colorpicker.**{ *; }
-keep enum com.flask.colorpicker.**{ *; }


-keep class com.andrognito.pinlockview.**{ *; }
-keep interface com.andrognito.pinlockview.**{ *; }
-keep enum com.andrognito.pinlockview.**{ *; }


-keep class com.bumptech.glide.**{ *; }
-keep interface com.bumptech.glide.**{ *; }
-keep enum com.bumptech.glide.**{ *; }


-keep class me.itangqi.waveloadingview.**{ *; }
-keep interface me.itangqi.waveloadingview.**{ *; }
-keep enum me.itangqi.waveloadingview.**{ *; }


### app 2


-keep class okhttp3.**{ *; }
-keep interface okhttp3.**{ *; }
-keep enum okhttp3.**{ *; }


-keep class okio.**{ *; }
-keep interface okio.**{ *; }
-keep enum okio.**{ *; }


-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**


-keep class org.jsoup.**{ *; }
-keep interface org.jsoup.**{ *; }
-keep enum org.jsoup.**{ *; }


-keep class com.nineoldandroids.**{ *; }
-keep interface com.nineoldandroids.**{ *; }
-keep enum com.nineoldandroids.**{ *; }


-keep class com.github.lzyzsd.circleprogress.**{ *; }
-keep interface com.github.lzyzsd.circleprogress.**{ *; }
-keep enum com.github.lzyzsd.circleprogress.**{ *; }


-keep class github.nisrulz.easydeviceinfo.**{ *; }
-keep interface github.nisrulz.easydeviceinfo.**{ *; }
-keep enum github.nisrulz.easydeviceinfo.**{ *; }


-keep class com.google.android.exoplayer2.**{ *; }
-keep interface com.google.android.exoplayer2.**{ *; }
-keep enum com.google.android.exoplayer2.**{ *; }


-keep class com.xiaopo.flying.sticker.**{ *; }
-keep interface com.xiaopo.flying.sticker.**{ *; }
-keep enum com.xiaopo.flying.sticker.**{ *; }


-keep class com.warkiz.widget.**{ *; }
-keep interface com.warkiz.widget.**{ *; }
-keep enum com.warkiz.widget.**{ *; }


-keep class me.shaohui.bottomdialog.**{ *; }
-keep interface me.shaohui.bottomdialog.**{ *; }
-keep enum me.shaohui.bottomdialog.**{ *; }


-keep class org.kxml2.**{ *; }
-keep interface org.kxml2.**{ *; }
-keep enum org.kxml2.**{ *; }


-keep class org.xmlpull.**{ *; }
-keep interface org.xmlpull.**{ *; }
-keep enum org.xmlpull.**{ *; }


-keep class jp.co.recruit_lifestyle.android.floatingview.**{ *; }
-keep interface jp.co.recruit_lifestyle.android.floatingview.**{ *; }
-keep enum jp.co.recruit_lifestyle.android.floatingview.**{ *; }


-keep class org.aopalliance.**{ *; }
-keep interface org.aopalliance.**{ *; }
-keep enum org.aopalliance.**{ *; }


-keep class com.fasterxml.jackson.core.**{ *; }
-keep interface com.fasterxml.jackson.core.**{ *; }
-keep enum com.fasterxml.jackson.core.**{ *; }


-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }


# Needed by google-api-client to keep generic types and @Key annotations accessed via reflection
-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
-keep class com.google.api.** { public *; }
-dontwarn com.google.api.**
-keep class com.google.common.** { public *; }
-dontwarn com.google.common.**
-keep class com.mycompany.cloud.Cloud.** { *; }

-dontwarn com.google.android.gms.**
-dontwarn com.google.common.cache.**
-dontwarn com.google.common.primitives.**
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}