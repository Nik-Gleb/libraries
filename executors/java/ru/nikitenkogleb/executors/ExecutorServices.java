/*
 * ExecutorServices.java
 * libraries
 *
 * Copyright (C) 2021, Gleb Nikitenko.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.nikitenkogleb.executors;

import android.os.Looper;
import android.os.Process;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Gleb Nikitenko
 * @since 20.03.19
 */
public final class ExecutorServices {

  /** Thread Names. */
  private static final String
    MAIN_NAME = prefix("main"),
    WORK_NAME = prefix("work"),
    SCHE_NAME = prefix("sche"),
    ELAS_NAME = prefix("elas");

  /** Thread Priorities. */
  private static final int
    IO_THREAD = Thread.NORM_PRIORITY, WORK_THREAD = Thread.NORM_PRIORITY,
    IO_PROCESS = Process.THREAD_PRIORITY_DEFAULT + Process.THREAD_PRIORITY_LESS_FAVORABLE,
    WORK_PROCESS = Process.THREAD_PRIORITY_DEFAULT + Process.THREAD_PRIORITY_LESS_FAVORABLE;

  /** Thread Factories. */
  private static final ThreadFactory
    SCHE_FACTORY = AndroidThread.factory(SCHE_NAME, IO_THREAD, IO_PROCESS, true),
    ELAS_FACTORY = AndroidThread.factory(ELAS_NAME, IO_THREAD, IO_PROCESS, true),
    WORK_FACTORY = AndroidThread.factory(WORK_NAME, WORK_THREAD, WORK_PROCESS, false);

  /** Looper's. */
  public static final Looper
    MAIN_LOOPER = setLooperName(Looper.getMainLooper(), MAIN_NAME),
    WORK_LOOPER = setLooperName(newLooper(WORK_FACTORY), WORK_NAME);

  /** Executor services. */
  public static final ExecutorService
    MAIN_EXECUTOR = new LooperExecutor(MAIN_LOOPER),
    WORK_EXECUTOR = new LooperExecutor(WORK_LOOPER),
    IMME_EXECUTOR = new ImmediateExecutor(),
    SCHE_EXECUTOR = createIO(SCHE_FACTORY, true),
    ELAS_EXECUTOR = createIO(SCHE_FACTORY, false);

  public static final ExecutorService main() {
    return new LooperExecutor(MAIN_LOOPER);
  }

  public static final ExecutorService io() {
    return createIO(SCHE_FACTORY, false);
  }

  /**
   * @param name the name of thread
   *
   * @return full prefixed thread name
   */
  private static String prefix(String name) {
    return /*"thread-" + */name;
  }

  /**
   * @param factory thread factory
   *
   * @return thread looper
   */
  @SuppressWarnings("SameParameterValue")
  private static Looper newLooper(ThreadFactory factory) {
    return new CompletableFuture<Looper>() {{
      factory.newThread(() -> {
          Looper.prepare();
          complete(Looper.myLooper());
          Looper.loop();
        }
      ).start();
    }}.join();
  }

  /**
   * @param looper source looper
   * @param name   the name of thread
   *
   * @return named looper
   */
  private static Looper setLooperName(Looper looper, String name)
  {try {return looper;} finally {looper.getThread().setName(name);}}

  /**
   * @param factory   thread factory
   * @param scheduled scheduled mode
   *
   * @return executor service
   */
  @SuppressWarnings("SameParameterValue")
  private static ExecutorService createIO(ThreadFactory factory, boolean scheduled) {
    final int core = 0; final long time = 30L; final TimeUnit unit = SECONDS;
    final ThreadPoolExecutor result = !scheduled ? new ThreadPoolExecutor(core,
      Integer.MAX_VALUE, time, unit, new LinkedBlockingQueue<>(1/*28*/), factory) :
      new ScheduledThreadPoolExecutor(core + 4, factory);
    result.allowCoreThreadTimeOut(false);
    result.setKeepAliveTime(time, unit);
    return result;
  }

  /**
   * @param hook interrupt threadHook
   *
   * @return true if hook was attached, otherwise - false
   */
  public static boolean threadHook(Runnable hook)
  {return AndroidThread.hook(hook);}
}
