/*
 * ImmediateExecutor.java
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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Immediate Executor Service.
 *
 * Inspired by guava's DirectExecutorService.
 *
 * TODO: Perhaps in the future it would be preferable to replace
 * TODO: the blocking approach with a non-blocking one.
 *
 * @author Gleb Nikitenko
 * @since 19.06.19
 */
final class ImmediateExecutor extends AbstractExecutorService {

  /** Lock used whenever accessing the state variables (runningTasks, shutdown) of the executor. */
  private final Object mLock = new Object();

  /*
   * Conceptually, these two variables describe the executor being in
   * one of three states:
   *   - Active: shutdown == false
   *   - Shutdown: runningTasks > 0 and shutdown == true
   *   - Terminated: runningTasks == 0 and shutdown == true
   */
  private int runningTasks = 0;
  private boolean shutdown = false;

  /** {@inheritDoc} */
  @Override
  public final void shutdown() {
    synchronized (mLock) {
      shutdown = true;
      if (runningTasks == 0) {
        mLock.notifyAll();
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final List<Runnable> shutdownNow() {
    shutdown();
    return Collections.emptyList();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isShutdown() {
    synchronized (mLock) {
      return shutdown;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isTerminated() {
    synchronized (mLock) {
      return shutdown && runningTasks == 0;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final boolean awaitTermination(long timeout, TimeUnit unit)
    throws InterruptedException {
    long nanos = unit.toNanos(timeout);
    synchronized (mLock) {
      while (true) {
        if (shutdown && runningTasks == 0) {return true;}
        else if (nanos <= 0) {return false;}
        else {
          long now = System.nanoTime();
          TimeUnit.NANOSECONDS.timedWait(mLock, nanos);
          // subtract the actual time we waited
          nanos -= System.nanoTime() - now;
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void execute(Runnable command) {
    startTask();
    try {
      command.run();
    } finally {
      endTask();
    }
  }

  /**
   * Checks if the executor has been shut down and increments the running task count.
   *
   * @throws RejectedExecutionException if the executor has been previously shutdown
   */
  private void startTask() {
    synchronized (mLock) {
      if (shutdown)
        throw new RejectedExecutionException("Executor already shutdown");
      runningTasks++;
    }
  }

  /** Decrements the running task count. */
  private void endTask() {
    synchronized (mLock) {
      int numRunning = --runningTasks;
      if (numRunning == 0) mLock.notifyAll();
    }
  }

}
