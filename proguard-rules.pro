#
# proguard-rules.pro
# libraries
#
# Copyright (C) 2020, Gleb Nikitenko.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#

#-dontobfuscate
#-dontoptimize
#-dontshrink

-verbose
-android
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

#-printmapping 'mapping.txt'
#-printconfiguration 'configuration.txt'

#-optimizations !codeView/simplification/arithmetic
#-optimizations !codeView/simplification/cast
#-optimizations !code/allocation/variable
#-optimizations !field

-keepparameternames
#-renamesourcefileattribute SourceFile
-keepattributes *Annotation*, Signature
-keepattributes EnclosingMethod
#-keepattributes LineNumberTable, SourceFile
-keepattributes InnerClasses, Exceptions

-dontnote java.lang.invoke.*
-dontnote android.net.client.*
-dontnote org.apache.client.params.*
-dontnote org.apache.client.conn.*
-dontnote org.apache.client.conn.scheme.*

-dontnote com.google.android.gms.common.api.internal.BasePendingResult$ReleasableResultGuardian
-dontnote android.support.annotation.Keep
-dontnote android.support.v4.media.ParceledListSliceAdapterApi21
-dontnote androidx.multidex.MultiDex$V14
-dontnote androidx.appcompat.app.ResourcesFlusher
-dontnote androidx.appcompat.widget.DrawableUtils
-dontnote androidx.client.graphics.TypefaceCompatApi24Impl
-dontnote androidx.client.graphics.TypefaceCompatApi26Impl
-dontnote androidx.client.text.ICUCompat
-dontnote androidx.client.widget.TextViewCompat$OreoCallback
-dontnote androidx.transition.GhostViewApi21
-dontnote androidx.client.graphics.TypefaceCompatApi26Impl
-dontnote com.google.android.gms.common.util.WorkSourceUtil
-dontnote com.google.android.gms.internal.measurement.*
-dontnote com.google.android.gms.measurement.internal.*
-dontnote com.google.firebase.iid.FirebaseInstanceId$zza
-dontnote androidx.appcompat.widget.ViewUtils
-dontnote androidx.client.app.NotificationManagerCompat
-dontnote androidx.client.view.KeyEventDispatcher
-dontnote androidx.versionedparcelable.VersionedParcel
-dontnote com.google.android.gms.common.api.internal.**
-dontnote androidx.media.AudioAttributesImplApi21Parcelizer
-dontnote androidx.media.AudioAttributesImplBaseParcelizer
-dontnote androidx.vectordrawable.graphics.drawable.VectorDrawableCompat$VPath
-dontnote com.google.android.gms.common.api.internal.LifecycleCallback
-dontnote com.google.firebase.iid.FirebaseInstanceId

-dontwarn com.google.**
-dontnote android.support.v4.media.session.MediaSessionCompatApi21
-dontnote android.support.v4.media.session.MediaSessionCompatApi24
-dontnote androidx.appcompat.view.menu.MenuItemWrapperICS
-dontnote androidx.client.app.NotificationCompatJellybean
-dontnote androidx.client.graphics.drawable.IconCompat
-dontnote com.google.android.gms.dynamite.DynamiteModule
-dontnote com.google.android.gms.internal.firebase_messaging.zzc