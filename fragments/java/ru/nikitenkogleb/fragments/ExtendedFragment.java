/*
 * ExtendedFragment.java
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

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
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
   * @param clazz     fragment class
   * @param args      fragment arguments
   */
  public static void open(@NonNull FragmentManager fragments,
                          @NonNull Supplier<? extends Fragment> factory,
                          @NonNull Class<? extends Fragment> clazz,
                          @NonNull Bundle args,
                          @Nullable String... sharedViewsNames) {
    final String name = clazz.getSimpleName();

    final Fragment[]
      stack = getStack(fragments)
      .map(Fragment.class::cast)
      .toArray(Fragment[]::new);

    final boolean addToStack = stack.length > 0;
    final Fragment fragment =
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
    final View view = getView();
    if (view != null) view.setTag(args);
  }

  /** {@inheritDoc} */
  @Nullable @Override public final Animation
  onCreateAnimation(int transition, boolean enter, int next) {
    return defTransitionDur == DEFAULT_TRANSITION_DURATION ?
      super.onCreateAnimation(transition, enter, next) :
      DefaultAnimations.animation(transition, enter, next, defTransitionDur);
  }

  /**
   * @param transition transition index
   * @param enter      enter or exit
   * @param next       next transition
   *
   * @return new created animation
   */
  @Nullable public static Animation animation(int transition, boolean enter, int next)
  { return DefaultAnimations.animation(transition, enter, next, DEFAULT_TRANSITION_DURATION); }

  /** {@inheritDoc} */
  @Nullable
  @Override
  public final View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle state) {
    return content != 0 ? inflate(inflater, container, state) :
      getDialog() != null && getDialog().getWindow() != null ?
        getDialog().getWindow().getDecorView() :
        super.onCreateView(inflater, container, state);
  }

  /**
   * @param inflater  layout inflater
   * @param container view container
   *
   * @return inflated or retained root view
   */
  @NonNull private View inflate(@NonNull LayoutInflater inflater,
                                @Nullable ViewGroup container,
                                @Nullable Bundle state) {
    if (mRootView == null) {
      mRootView = inflater.inflate(content, container, ATTACH);
      if (state == null) mRootView.setTag(super.getArguments());
    } try {return mRootView;} finally {if (!retainable) mRootView = null;}
  }

  /** {@inheritDoc} */
  @Override public void onDestroy() {
    if (mRootView != null) {
      if (mRootView instanceof Closeable)
        try {((Closeable) mRootView).close();}
        catch (IOException e) {throw new UncheckedIOException(e);}
      mRootView = null;
    }
    super.onDestroy();
  }

  /** Show screen */
  private static void show(@NonNull FragmentManager manager,
                           @NonNull Fragment fragment,
                           @NonNull String name, boolean replace,
                           @Nullable String... sharedViewsNames) {
    if (!(fragment instanceof ExtendedFragment)) {
      final FragmentTransaction transaction = manager.beginTransaction();
      if (replace) transaction.setReorderingAllowed(true);
      else transaction.addToBackStack(null);
      transaction
        .setTransition(replace ? TRANSIT_FRAGMENT_FADE : TRANSIT_FRAGMENT_OPEN)
        .replace(android.R.id.content, fragment, name)
        .commit();
    } else {
      final ExtendedFragment extended = (ExtendedFragment) fragment;
      if (extended.content == 0) return;
      final FragmentTransaction transaction =
        new ExtendedFragmentTransaction(manager)
          .setTransition(replace ? TRANSIT_FRAGMENT_FADE : TRANSIT_FRAGMENT_OPEN);
      if (replace) transaction.setReorderingAllowed(true);
      final boolean inflate = extended.container != 0;
      if (!replace) {
        if (!inflate) transaction.add(extended, name);
        else transaction.replace(extended.container, extended, name);
        transaction.addToBackStack(null);
      }
      else transaction.replace(extended.container, extended, name);
      if (extended.title != 0) transaction.setBreadCrumbShortTitle(extended.title);
      if (extended.subtitle != 0) transaction.setBreadCrumbTitle(extended.subtitle);
      if (replace) transaction.setPrimaryNavigationFragment(extended);

      Transition transition; addSharedElements(transaction, sharedViewsNames);
      final Context context = ExtendedDialogFragment.context(manager);
      final TransitionInflater inflater = TransitionInflater.from(context);

      if (extended.sharedEnterTransition != 0) extended.setSharedElementEnterTransition
        (inflater.inflateTransition(extended.sharedEnterTransition));

      if (extended.sharedReturnTransition != 0) extended.setSharedElementReturnTransition
        (inflater.inflateTransition(extended.sharedReturnTransition));

      if (extended.enterTransition != 0) extended.setEnterTransition
        (inflater.inflateTransition(extended.enterTransition));

      if (extended.exitTransition != 0) extended.setExitTransition
        (inflater.inflateTransition(extended.exitTransition));

      if (extended.returnTransition != 0) extended.setReturnTransition
        (inflater.inflateTransition(extended.returnTransition));

      if (extended.reenterTransition != 0) extended.setReenterTransition
        (inflater.inflateTransition(extended.reenterTransition));

      transaction.commit();

    }
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
