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

#-verbose
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

-dontnote java.lang.invoke.CallSite
-dontnote java.lang.invoke.LambdaConversionException
-dontnote java.lang.invoke.MethodHandle
-dontnote java.lang.invoke.MethodHandles$Lookup
-dontnote java.lang.invoke.MethodHandles
-dontnote java.lang.invoke.MethodType