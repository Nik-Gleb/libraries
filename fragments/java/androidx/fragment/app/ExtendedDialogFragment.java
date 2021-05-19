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

package androidx.fragment.app;

import android.content.Context;
import android.os.Bundle;

import java.util.List;


/**
 * Extended Dialog Fragment.
 *
 * @author Gleb Nikitenko
 * @since 13.06.20
 **/
public class ExtendedDialogFragment extends androidx.fragment.app.DialogFragment {

  /** The state saved flag. */
  private boolean mSavedState = false;

  /** This is a dialog. */
  @SuppressWarnings("unused") final void dialog() {
    mDismissed = false;
    mShownByMe = true;
    mViewDestroyed = false;
  }

  /** @param backStackId setup back-stack entry */
  @SuppressWarnings("unused") final void commit(int backStackId) {
    mBackStackId = backStackId;
  }

  /** Set not saved state */
  @SuppressWarnings({ "WeakerAccess", "unused" })
  void setStateNotSaved() {setStateNotSaved(getChildFragmentManager()); setSavedState(false);}

  /** {@inheritDoc} */
  @Override public void onSaveInstanceState(@SuppressWarnings("NullableProblems") Bundle outState) {
    super.onSaveInstanceState(outState); setSavedState(true);
  }

  /** @param value saved state value */
  private void setSavedState(boolean value) {
    if (mSavedState == value) return;
    mSavedState = value;
    onSavedStateChanged(mSavedState);
  }

  /** @param value true when state saved */
  @SuppressWarnings({ "unused", "EmptyMethod", "WeakerAccess" })
  protected void onSavedStateChanged(boolean value) {}

  /** @param fragmentManager the fragment manager for apply saved state */
  @SuppressWarnings({ "WeakerAccess", "unused" })
  public static void setStateNotSaved(FragmentManager fragmentManager) {
    final List<Fragment> fragments = getFragments(fragmentManager);
    for (final Fragment fragment : fragments)
      if (fragment instanceof ExtendedDialogFragment)
        ((ExtendedDialogFragment) fragment).setStateNotSaved();
  }

  /**
   * @param fragments the fragment manager
   *
   * @return active fragments
   */
  @SuppressWarnings("WeakerAccess")
  public static List<Fragment> getFragments(
    FragmentManager fragments) {return ((FragmentManagerImpl) fragments).getActiveFragments();}

  public static Context context(FragmentManager manager) {
    return ((FragmentManagerImpl) manager).mHost.getContext();
  }

  /**
   * @param fragments the fragment manager instance
   *
   * @return the attached activity
   */
  @SuppressWarnings("unused")
  public static FragmentActivity getActivity(FragmentManager fragments) {
    @SuppressWarnings("rawtypes") final FragmentHostCallback fragmentHostCallback =
      ((FragmentManagerImpl) fragments).mHost;
    return fragmentHostCallback == null ? null :
      (FragmentActivity) fragmentHostCallback.getActivity();
  }
}
