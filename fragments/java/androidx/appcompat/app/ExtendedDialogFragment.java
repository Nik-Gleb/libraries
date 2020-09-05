/*
 * ExtendedDialogFragment.java
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

package androidx.appcompat.app;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.FragmentActivity;


/**
 * Extended Dialog Fragment.
 *
 * @author Gleb Nikitenko
 * @since 13.06.20
 **/
class ExtendedDialogFragment extends androidx.fragment.app.ExtendedDialogFragment {

  /** The theme resource field name. */
  private static final String THEME_RESOURCE_FIELD_NAME = "mThemeResource";

  /** The theme resource field. */
  private static final Field THEME_RESOURCE_FIELD = getThemeResourceField();

  /** The menu resource id. */
  @SuppressWarnings("WeakerAccess")
  @MenuRes protected int menu = 0;

  /** {@inheritDoc} */
  @Override public void onCreate(@Nullable Bundle state) {
    super.onCreate(state); setHasOptionsMenu(menu != 0);
  }

  /** {@inheritDoc} */
  @Override public void setupDialog(@NonNull android.app.Dialog dialog, int style) {
    if (dialog instanceof AppCompatDialog) {
      // If the dialog is an AppCompatDialog, we'll handle it
      final AppCompatDialog appCompatDialog = (AppCompatDialog) dialog;
      switch (style) {
        case STYLE_NO_INPUT:
          final Window window = dialog.getWindow();
          if (window != null)
            dialog.getWindow().addFlags(
              WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
          break;
        // fall through...
        case STYLE_NO_FRAME:
        case STYLE_NO_TITLE:
          appCompatDialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
          break;
      }
    }
  }

  /** {@inheritDoc} */
  @Override @NonNull public android.app.Dialog onCreateDialog(@Nullable Bundle state) {
    final int theme = getTheme();
    final FragmentActivity activity = getActivity();
    /*return new AppCompatDialog(getContext(),
      theme == 0 && activity != null ?
        getThemeResourceId(activity) : theme);*/
    return onCreateDialog(requireContext(),
      theme == 0 && activity != null ?
        getThemeResourceId(activity) : theme, this
    );
  }

  @NonNull protected android.app.Dialog onCreateDialog
    (@NonNull Context context, @StyleRes int theme,
     @NonNull ExtendedDialogFragment fragment) {return new Dialog(context, theme, fragment);}

  /** The id of theme-resource. */
  private static int getThemeResourceId(ContextThemeWrapper wrapper) {
    if (THEME_RESOURCE_FIELD != null)
      try {
        return (int) THEME_RESOURCE_FIELD.get(wrapper);
      } catch (IllegalAccessException ignored) {}
    return 0;
  }

  /** {@inheritDoc} */
  @Override public void onCreateOptionsMenu
  (@NonNull Menu menu, @NonNull MenuInflater inflater) {inflater.inflate(this.menu, menu);}

  /** {@inheritDoc} */
  @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    return item.getItemId() == android.R.id.home ? onBackPressed() :
      super.onOptionsItemSelected(item);
  }

  /** On back pressed. */
  @SuppressWarnings({ "WeakerAccess" })
  protected boolean onBackPressed() {return false;}

  /** The theme resource field. */
  @SuppressWarnings("JavaReflectionMemberAccess")
  private static Field getThemeResourceField() {
    try {
      final Field result = ContextThemeWrapper.class
        .getDeclaredField(THEME_RESOURCE_FIELD_NAME);
      result.setAccessible(true); return result;
    } catch (NoSuchFieldException e) {return null;}
  }

  /** Extended dialog. */
  public static class Dialog extends AppCompatDialog {

    /** The fragment reference . */
    @Nullable private ExtendedDialogFragment mFragment;

    /**
     * Constructs a new {@link Dialog}.
     *
     * @param context  the app-context
     * @param fragment the fragment reference
     */
    public Dialog(@NonNull Context context, @NonNull ExtendedDialogFragment fragment) {
      super(context); mFragment = fragment;
    }

    /**
     * Constructs a new {@link Dialog}.
     *
     * @param context  the app-context
     * @param theme    the dialog theme
     * @param fragment the fragment reference
     */
    public Dialog(@NonNull Context context, @StyleRes int theme,
                  @NonNull ExtendedDialogFragment fragment) {
      super(context, theme);
      mFragment = fragment;
    }

    /**
     * Constructs a new {@link Dialog}.
     *
     * @param context        the app-context
     * @param cancelable     the cancelable mode
     * @param cancelListener the cancel listener
     * @param fragment       the fragment reference
     */
    private Dialog(@NonNull Context context, boolean cancelable,
                   @Nullable OnCancelListener cancelListener,
                   @NonNull ExtendedDialogFragment fragment) {
      super(context, cancelable, cancelListener);
      mFragment = fragment;
    }

    /** {@inheritDoc} */
    @Override public final void dismiss() {super.dismiss(); mFragment = null;}

    /** {@inheritDoc} */
    @Override public final void onBackPressed() {back();}

    /** @return true if handled */
    private boolean back() {
      final boolean result = mFragment == null || !mFragment.onBackPressed();
      if (result) super.onBackPressed(); return result;
    }

    /** {@inheritDoc} */
    @Override public final boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
      return (featureId == Window.FEATURE_OPTIONS_PANEL) ? onOptionsItemSelected(item) :
        super.onMenuItemSelected(featureId, item);
    }

    /** {@inheritDoc} */
    @Override public final boolean onCreateOptionsMenu(@NonNull Menu menu) {
      if (mFragment == null || mFragment.menu == 0) return super.onCreateOptionsMenu(menu);
      mFragment.onCreateOptionsMenu(menu, getDelegate().getMenuInflater()); return true;
    }

    /** {@inheritDoc} */
    @Override public final boolean onPrepareOptionsMenu(@NonNull Menu menu) {
      if (mFragment == null) return super.onPrepareOptionsMenu(menu);
      mFragment.onPrepareOptionsMenu(menu); return true;
    }

    /** {@inheritDoc} */
    @Override public final boolean onOptionsItemSelected(@NonNull MenuItem item) {
      return
        (item.getItemId() == android.R.id.home && !back()) ||
          (mFragment != null && mFragment.onOptionsItemSelected(item)) ||
          super.onOptionsItemSelected(item);
    }

    /** {@inheritDoc} */
    @Override public void setContentView(View view) {
      if (getWindow() == null || getWindow().getDecorView() == view) return;
      super.setContentView(view);
    }

    /** {@inheritDoc} */
    @Override public final void invalidateOptionsMenu() {getDelegate().invalidateOptionsMenu();}
  }

}
