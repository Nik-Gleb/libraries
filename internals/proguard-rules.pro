#
# proguard-rules.pro
# internals
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
#noinspection ShrinkerUnresolvedReference
-dontnote android.support.annotation.Keep
-keep public class com.google.android.gms.tasks.TasksUtils {public protected *;}
-keep public class okhttp3.internal.ws.WSUtils {public protected *;}
-keep public class okhttp3.internal.ws.WSUtils$Frame {public protected *;}
-keep public class okhttp3.internal.ws.WSUtils$Text {public protected *;}
-keep public class okhttp3.internal.ws.WSUtils$Binary {public protected *;}
-keep public class okhttp3.internal.ws.WSUtils$Ping {public protected *;}
-keep public class okhttp3.internal.ws.WSUtils$Pong {public protected *;}
-keep public class okhttp3.internal.ws.WSUtils$Close {public protected *;}
-keep public interface okhttp3.internal.ws.WSUtils$IORunnable {public protected *;}
-keep public class okhttp3.OkHttpUtils {public protected *;}
-keep public class okio.OkioUtils {public protected *;}