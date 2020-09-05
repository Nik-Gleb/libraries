/*
 * ExtendedFragment.java
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

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.TransitionRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.ExtendedDialogFragment;
import androidx.fragment.app.ExtendedFragmentTransaction;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import static android.text.TextUtils.isEmpty;
import static androidx.fragment.app.ExtendedFragmentTransaction.addSharedElements;
import static androidx.fragment.app.ExtendedFragmentTransaction.getStack;
import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;
import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
import static java.util.Objects.deepEquals;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;


/**
 * Extended Fragment.
 *
 * @author Gleb Nikitenko
 * @since 13.06.20
 **/
@SuppressWarnings({ "unused", "CanBeFinal", "WeakerAccess" })
public class ExtendedFragment extends ExtendedDialogFragment {

  /** The attach to auth inflate mode. */
  private static final boolean ATTACH = false;

  /** Default transition. */
  private static final int DEFAULT_TRANSITION_DURATION = 200;

  /** The title resources. */
  @StringRes protected int title, subtitle = 0;

  /** Inflate container. */
  @IdRes protected int container = android.R.id.content;

  /** The content layout. */
  @LayoutRes protected int content = 0;

  /** Shared transitions. */
  @TransitionRes protected int
    sharedEnter = 0,
    sharedReturn = 0;

  /** Primary fragment. */
  protected boolean primary = true;

  /** Will retain root view or not. */
  protected boolean retainable = true;

  /** Transitions. */
  protected int
    enterTransition = 0,
    exitTransition = 0,
    returnTransition = 0,
    reenterTransition = 0,
    sharedEnterTransition = 0,
    sharedReturnTransition = 0;

  /** Transition duration. */
  protected int defTransitionDur = DEFAULT_TRANSITION_DURATION;

  /** Retainable view. */
  private View mRootView = null;

  /** Retainable inflater. */
  private LayoutInflater mLayoutInflater = null;

  /** No result by default. */
  @SuppressWarnings("FieldCanBeLocal")
  private ActivityResult mResult =
    new ActivityResult(Activity.RESULT_CANCELED, null);

  /** {@inheritDoc} */
  @NonNull
  @Override
  public final LayoutInflater onGetLayoutInflater(@Nullable Bundle state) {
    return mRootView != null ? getLayoutInflater(mRootView) :
      onCreateLayoutInflater(super.onGetLayoutInflater(state), state);
  }

  /** {@inheritDoc} */
  @Override public void setupDialog(@NonNull android.app.Dialog dialog, int style) {
    if (dialog instanceof AppCompatDialog) {

      // If the dialog is an AppCompatDialog, we'll handle it
      AppCompatDialog acd = (AppCompatDialog) dialog;

      switch (style) {
        case STYLE_NO_INPUT:
          //noinspection ConstantConditions
          dialog.getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
              WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
          // fall through...
        case STYLE_NO_FRAME:
        case STYLE_NO_TITLE:
          acd.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
      }
    }
    else {
      switch (style) {
        case STYLE_NO_INPUT:
          //noinspection ConstantConditions
          dialog.getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
              WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
          // fall through...
        case STYLE_NO_FRAME:
        case STYLE_NO_TITLE:
          dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      }
    }
  }

  /** {@inheritDoc} */
  @NonNull
  @Override
  public android.app.Dialog onCreateDialog(@Nullable Bundle state) {
    final AlertDialog.Builder builder = createAlertBuilder(state);
    return builder == null ? super.onCreateDialog(state) : builder.create();
  }

  /**
   * @param state the saved state
   *
   * @return the alert dialog
   */
  @SuppressWarnings("SameReturnValue")
  @Nullable protected AlertDialog.Builder createAlertBuilder(@Nullable Bundle state) {return null;}

  /**
   * @param view root view
   *
   * @return layout inflater
   */
  @NonNull private static LayoutInflater getLayoutInflater(@NonNull View view) {
    final Context context = view.getContext();
    final String name = Context.LAYOUT_INFLATER_SERVICE;
    final Object service = context.getSystemService(name);
    return requireNonNull((LayoutInflater) service);
  }

  /**
   * @param inflater super layout inflater
   * @param state    bundle saved state
   *
   * @return wrapped layout inflater
   */
  @NonNull protected LayoutInflater onCreateLayoutInflater
  (@NonNull LayoutInflater inflater, @Nullable Bundle state) {return inflater;}

  /** @param data result */
  protected final void finish(@Nullable Intent data) {
    mResult = new ActivityResult(Activity.RESULT_OK, data);
    dismiss();
  }

  /**
   * @param clazz class of fragment
   *
   * @return tag-name
   */
  protected static String getTag
  (@NonNull Class<? extends ExtendedFragment> clazz) {
    final String name = clazz.getName();
    final String[] parts = clazz.getName().replace(".", " ").split(" ");
    return parts[parts.length - 2];
  }

  /**
   * Open screen fragment
   *
   * @param fragments fragment manager
   * @param factory   fragment factory
   * @param name      fragment name
   * @param args      fragment arguments
   */
  public static void open(@NonNull FragmentManager fragments,
                          @NonNull Supplier<ExtendedFragment> factory,
                          @NonNull String name, @NonNull Bundle args,
                          @Nullable String... sharedViewsNames) {
    final ExtendedFragment[]
      stack = getStack(fragments)
      .map(ExtendedFragment.class::cast)
      .toArray(ExtendedFragment[]::new);

    final boolean addToStack = stack.length > 0;
    final ExtendedFragment fragment =
      Stream.of(stack)
        .filter(v -> Objects.equals(name, v.getTag()))
        .findFirst().orElseGet(factory);
    fragment.setArguments(args);
    final int stackId = getStack(fragment);
    if (stackId > -1)
      fragments.popBackStack
        (stackId + 1, POP_BACK_STACK_INCLUSIVE);
    else if (isEmpty(fragment.getTag()))
      show(fragments, fragment, name, !addToStack);
  }

  /** {@inheritDoc} */
  @Override public final void setArguments(@Nullable Bundle args) {
    super.setArguments(args);
    ofNullable(getView())
      .ifPresent(it -> it.setTag(args));
  }

  /** {@inheritDoc} */
  @Nullable @Override public final Animation
  onCreateAnimation(int transition, boolean enter, int next) {
    return defTransitionDur == DEFAULT_TRANSITION_DURATION ?
      super.onCreateAnimation(transition, enter, next) :
      DefaultAnimations.animation(transition, enter, next, defTransitionDur);
  }

  /** {@inheritDoc} */
  @Nullable
  @Override
  public final View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle state) {
    return fixInsets(content != 0 ? inflate(inflater, container) :
      getDialog() != null && getDialog().getWindow() != null ?
        getDialog().getWindow().getDecorView() :
        super.onCreateView(inflater, container, state), this);
  }

  /**
   * @param view     root view
   * @param fragment fragment
   *
   * @return view
   */
  private static View fixInsets(@Nullable View view, @NonNull Fragment fragment) {
    if (view != null) {
      view.setTag(fragment.getArguments());
      view.addOnAttachStateChangeListener(
        new View.OnAttachStateChangeListener() {
          @Override
          public final void onViewAttachedToWindow(View v) {
            view.removeOnAttachStateChangeListener(this); v.requestApplyInsets();
          }

          @Override public final void onViewDetachedFromWindow(View v) {}
        });
    }
    return view;
  }

  /**
   * @param inflater  layout inflater
   * @param container view container
   *
   * @return inflated or retained root view
   */
  @NonNull private View inflate(@NonNull LayoutInflater inflater,
                                @Nullable ViewGroup container) {
    if (mRootView == null) mRootView = inflater.inflate(content, container, ATTACH);
    try {return mRootView;} finally {if (!retainable) mRootView = null;}
  }

  /** {@inheritDoc} */
  @Override public void onDestroy() { mRootView = null; super.onDestroy();}

  /** Show screen */
  private static void show(@NonNull FragmentManager manager,
                           @NonNull ExtendedFragment fragment,
                           @NonNull String name, boolean replace,
                           @Nullable String... sharedViewsNames) {
    final FragmentTransaction transaction =
      new ExtendedFragmentTransaction(manager)
        .setTransition(replace ? TRANSIT_FRAGMENT_FADE : TRANSIT_FRAGMENT_OPEN);
    if (replace) transaction.setReorderingAllowed(true);
    final Bundle args = fragment.getArguments();
    final boolean inflate = fragment.container != 0;
    if (!replace) {
      if (!inflate) transaction.add(fragment, name);
      else transaction.replace(fragment.container, fragment, name);
      transaction.addToBackStack(null);
    }
    else transaction.replace(fragment.container, fragment, name);
    if (fragment.title != 0) transaction.setBreadCrumbShortTitle(fragment.title);
    if (fragment.subtitle != 0) transaction.setBreadCrumbTitle(fragment.subtitle);
    if (replace) transaction.setPrimaryNavigationFragment(fragment);

    Transition transition; addSharedElements(transaction, sharedViewsNames);
    final Context context = ExtendedDialogFragment.context(manager);
    final TransitionInflater inflater = TransitionInflater.from(context);

    if (fragment.sharedEnterTransition != 0) fragment.setSharedElementEnterTransition
      (inflater.inflateTransition(fragment.sharedEnterTransition));

    if (fragment.sharedReturnTransition != 0) fragment.setSharedElementReturnTransition
      (inflater.inflateTransition(fragment.sharedReturnTransition));

    if (fragment.enterTransition != 0) fragment.setEnterTransition
      (inflater.inflateTransition(fragment.enterTransition));

    if (fragment.exitTransition != 0) fragment.setExitTransition
      (inflater.inflateTransition(fragment.exitTransition));

    if (fragment.returnTransition != 0) fragment.setReturnTransition
      (inflater.inflateTransition(fragment.returnTransition));

    if (fragment.reenterTransition != 0) fragment.setReenterTransition
      (inflater.inflateTransition(fragment.reenterTransition));

    transaction.commit();
  }

  /**
   * @param fragments fragment manager
   * @param onChanged on stack changed
   *
   * @return cancellation signal (unsubscribe)
   */
  @NonNull public static Runnable listen(@NonNull FragmentManager fragments,
                                         @NonNull Consumer<Fragment[]> onChanged) {
    Fragment[][] currentStack = new Fragment[1][];
    final OnBackStackChangedListener listener = () -> {
      final Fragment[] newStack =
        ExtendedFragmentTransaction
          .getStack(fragments)
          .toArray(Fragment[]::new);
      if (!deepEquals(currentStack[0], newStack))
        onChanged.accept(currentStack[0] = newStack);
    };
    fragments.addOnBackStackChangedListener(listener);
    return () -> fragments.removeOnBackStackChangedListener(listener);
  }
}
