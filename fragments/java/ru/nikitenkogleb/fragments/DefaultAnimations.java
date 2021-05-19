/*
 * DefaultAnimations.java
 * fragments
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

package ru.nikitenkogleb.fragments;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;
import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;
import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;


/**
 * Default animations helper.
 *
 * @author Gleb Nikitenko
 * @since 13.06.20
 **/
final class DefaultAnimations {

  /** Transition styles. */
  private static final int
    UNKNOWN = 0,
    OPEN_ENTER = 1,
    OPEN_EXIT = 2,
    CLOSE_ENTER = 3,
    CLOSE_EXIT = 4,
    FADE_ENTER = 5,
    FADE_EXIT = 6;

  /** Animation Interpolator's */
  @SuppressWarnings("unused")
  private static final Interpolator
    DECELERATE_QUINT = new DecelerateInterpolator(2.5f),
    DECELERATE_CUBIC = new DecelerateInterpolator(1.5f),
    ACCELERATE_QUINT = new AccelerateInterpolator(2.5f),
    ACCELERATE_CUBIC = new AccelerateInterpolator(1.5f);

  /**
   * The caller should be prevented from constructing objects of this class.
   * Also, this prevents even the native class from calling this constructor.
   **/
  private DefaultAnimations() {throw new AssertionError();}

  /**
   * @param transition transition index
   * @param enter      enter or exit
   * @param next       next transition
   * @param duration   animation duration
   *
   * @return new created animation
   */
  @Nullable static Animation animation(int transition, boolean enter, int next, int duration) {
    final int index = index(transition, enter);
    switch (index) {
      case OPEN_ENTER:
        return openClose(1.125f, 1.0f, 0, 1, duration);
      case OPEN_EXIT:
        return openClose(1.0f, 0.975f, 1, 0, duration);
      case CLOSE_ENTER:
        return openClose(0.975f, 1.0f, 0, 1, duration);
      case CLOSE_EXIT:
        return openClose(1.0f, 1.075f, 1, 0, duration);
      case FADE_ENTER:
        return fade(0, 1, duration);
      case FADE_EXIT:
        return fade(1, 0, duration);
      default:
        return null;
    }
  }

  /**
   * @param transition transition
   * @param enter      enter or exit
   *
   * @return style index
   */
  private static int index(int transition, boolean enter) {
    switch (transition) {
      case TRANSIT_FRAGMENT_OPEN:
        return enter ? OPEN_ENTER : OPEN_EXIT;
      case TRANSIT_FRAGMENT_CLOSE:
        return enter ? CLOSE_ENTER : CLOSE_EXIT;
      case TRANSIT_FRAGMENT_FADE:
        return enter ? FADE_ENTER : FADE_EXIT;
      default:
        return UNKNOWN;
    }
  }

  /**
   * @param startScale start scale value
   * @param endScale   end scale value
   * @param startAlpha start alpha value
   * @param endAlpha   end alpha value
   * @param duration   animation duration
   *
   * @return open-close animation
   */
  @NonNull private static AnimationSet openClose(float startScale,
                                                 float endScale,
                                                 float startAlpha,
                                                 float endAlpha,
                                                 int duration) {
    return new AnimationSet(false) {{
      addAnimation(new ScaleAnimation(startScale, endScale, startScale, endScale,
        Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f) {{
        setInterpolator(DECELERATE_QUINT);
        setDuration(duration);
      }});
      addAnimation(fade(startAlpha, endAlpha, duration));
    }};
  }

  /**
   * @param start    start value
   * @param end      end value
   * @param duration animation duration
   *
   * @return fade animation
   */
  @NonNull private static AlphaAnimation fade(float start, float end, int duration) {
    return new AlphaAnimation(start, end) {{
      setInterpolator(DECELERATE_CUBIC);
      setDuration(duration);
    }};
  }
}