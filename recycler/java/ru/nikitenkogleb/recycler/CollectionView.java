/*
 * CollectionView.java
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

package ru.nikitenkogleb.recycler;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;

import androidx.annotation.AttrRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.annotation.StyleableRes;
import androidx.recyclerview.widget.RecyclerView;

/** Universal Collection View. */
@SuppressWarnings("unused")
public final class CollectionView extends RecyclerView {

  /** View Name. */
  public static final String NAME = "CollectionView";

  /** The default attr resource. */
  @AttrRes private static final int DEFAULT_ATTRS = 0;

  /** The empty style resource. */
  @StyleRes private static final int DEFAULT_STYLE = 0;

  /** Default styleable attributes */
  @StyleableRes private static final int[] DEFAULT_STYLEABLE = {};

  /** RecyclerView Adapter. */
  private RecyclerViewAdapter mAdapter = null;

  /** {@inheritDoc} */
  public CollectionView(@NonNull Context context) {this(context, null);}

  /** {@inheritDoc} */
  public CollectionView(@NonNull Context context, @Nullable AttributeSet attr)
  {this(context, attr, DEFAULT_STYLE);}

  /** {@inheritDoc} */
  @SuppressWarnings("EmptyTryBlock")
  public CollectionView(@NonNull Context context, @Nullable AttributeSet attr, int attrs) {
    super(context, attr, attrs);
    final Resources.Theme theme = context.getTheme();
    final TypedArray attributes = theme.obtainStyledAttributes
      (attr, DEFAULT_STYLEABLE, attrs, DEFAULT_STYLE);
    try {} finally {attributes.recycle();}

    setItemViewCacheSize(0);
    setHasFixedSize(true);
  }

  /** @param layout layout resolver */
  public final void byIndex(@NonNull IntUnaryOperator layout) {
    if (mAdapter != null) throw new IllegalStateException("Already initialized");
    mAdapter = RecyclerViewAdapter.byIndex(LayoutInflater.from(getContext()), layout);
    super.setAdapter(mAdapter.adapter);
  }

  /** @param layout layout resolver */
  @SuppressWarnings("rawtypes")
  public final void byItem(@NonNull ToIntFunction layout) {
    if (mAdapter != null) throw new IllegalStateException("Already initialized");
    mAdapter = RecyclerViewAdapter.byItem(LayoutInflater.from(getContext()), layout);
    super.setAdapter(mAdapter.adapter);
  }

  /** @param layout layout resolver */
  public void simple(@LayoutRes int layout) {
    if (mAdapter != null) throw new IllegalStateException("Already initialized");
    mAdapter = RecyclerViewAdapter.simple(LayoutInflater.from(getContext()), layout);
    super.setAdapter(mAdapter.adapter);
  }

  /**
   * @param value array of new items
   * @param <T> items type
   *
   * @return calculated diffs with a previous set
   */
  @SafeVarargs
  @NonNull
  public final <T> RecyclerViewAdapter.Diffs diffs(T... value)
  {return mAdapter.diffs(value);}

  /** {@inheritDoc} */
  @Override
  public final void setAdapter(@Nullable Adapter adapter)
  {throw new UnsupportedOperationException("Custom adapters not allowed");}

  /** {@inheritDoc} */
  @Override
  public final void setLayoutManager(@Nullable LayoutManager layout) {
    if (layout != null) layout.setItemPrefetchEnabled(false);
    super.setLayoutManager(layout);
  }

}
