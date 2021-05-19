/*
 * AndroidThread.java
 * libraries
 *
 * Copyright (C) 2020, Gleb Nikitenko.
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

import android.os.Process;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Android Thread.
 * <p>
 * Allows to set linux-based thread priority.
 *
 * @author Gleb Nikitenko
 * @since 28.04.20
 **/
@SuppressWarnings({ "unused", "WeakerAccess", "RedundantSuppression" })
final class AndroidThread extends Thread {

  /** Process priority */
  private final int mPriority;

  /** Interruption listener. */
  private volatile Runnable mHook = null;

  /** {@inheritDoc} */
  private AndroidThread(int priority) {
    mPriority = priority;
  }

  /** {@inheritDoc} */
  private AndroidThread(Runnable runnable, int priority) {
    super(runnable);
    mPriority = priority;
  }

  /** {@inheritDoc} */
  private AndroidThread(ThreadGroup group, Runnable runnable, int priority) {
    super(group, runnable);
    mPriority = priority;
  }

  /** {@inheritDoc} */
  private AndroidThread(String name, int priority) {
    super(name);
    mPriority = priority;
  }

  /** {@inheritDoc} */
  private AndroidThread(ThreadGroup group, String name, int priority) {
    super(group, name);
    mPriority = priority;
  }

  /** {@inheritDoc} */
  private AndroidThread(Runnable runnable, String name, int priority) {
    super(runnable, name);
    mPriority = priority;
  }

  /** {@inheritDoc} */
  private AndroidThread(ThreadGroup group, Runnable runnable, String name, int priority) {
    super(group, runnable, name);
    mPriority = priority;
  }

  /** {@inheritDoc} */
  private AndroidThread(ThreadGroup group, Runnable runnable, String name, long stack,
                       int priority) {
    super(group, runnable, name, stack);
    mPriority = priority;
  }

  /** {@inheritDoc} */
  @Override public final void run() {
    final int priority = Process.getThreadPriority(Process.myTid());
    if (priority != mPriority) Process.setThreadPriority(mPriority);
    super.run();
  }

  /** {@inheritDoc} */
  @Override public final void interrupt() {
    final Runnable hook = mHook;
    if (hook != null) {
      hook.run();
      mHook = null;
    }
    super.interrupt();
  }

  /**
   * @param hook interrupt threadHook
   *
   * @return true if threadHook was attached, otherwise - false
   */
  static boolean hook(Runnable hook) {
    final Thread thread = Thread.currentThread();
    final boolean result = thread instanceof AndroidThread;
    if (result) ((AndroidThread) thread).mHook = hook; return result;
  }

  /**
   * @param name    thread-name prefix
   * @param thread  java-thread priority
   * @param process android-process priority
   * @param multi   multi-naming
   *
   * @return thread factory
   */
  static ThreadFactory factory(String name, int thread, int process, boolean multi) {
    final SecurityManager security = System.getSecurityManager(); final ThreadGroup group =
      security != null ? security.getThreadGroup() : Thread.currentThread().getThreadGroup();
    final AtomicInteger number = new AtomicInteger(0); return runnable -> {
      final String tName = multi ? name + "-" + number.getAndIncrement() : name;
      final Thread result = new AndroidThread(group, runnable, tName, 0, process);
      result.setDaemon(false); result.setPriority(thread); return result;
    };
  }
}