/*
 * RecyclerViewAdapter.java
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

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AdapterListUpdateCallback;
import androidx.recyclerview.widget.BatchingListUpdateCallback;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.Callback;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

/** RecyclerView adapter. */
public abstract class RecyclerViewAdapter {

  /** {@link RecyclerView.Adapter} instance */
  @NonNull public final Adapter<ViewHolder> adapter;

  /**
   * @param value array of items
   * @param <T> items type
   *
   * @return differences-result
   */
  @SuppressWarnings({ "unchecked", "EmptyMethod" })
  @NonNull public abstract <T> Diffs diffs(@Nullable T... value);

  /**
   * Constructs a new {@link RecyclerViewAdapter}.
   *
   * @param adapter {@link RecyclerView.Adapter} instance
   */
  private RecyclerViewAdapter(@NonNull Adapter<ViewHolder> adapter)
  {this.adapter = adapter;}

  /**
   * @param inflater layout inflater
   * @param layout single layout resource id
   *
   * @return new created adapter
   */
  @NonNull public static RecyclerViewAdapter simple
    (@NonNull LayoutInflater inflater, @LayoutRes int layout)
  {return custom(inflater, (p, v) -> layout);}

  /**
   * @param inflater layout inflater
   * @param layout by-item based layout
   *
   * @return new created adapter
   */
  @SuppressWarnings("unchecked")
  @NonNull public static RecyclerViewAdapter byItem
    (@NonNull LayoutInflater inflater,
     @SuppressWarnings("rawtypes") @NonNull ToIntFunction layout)
  {return custom(inflater, (p, v) -> layout.applyAsInt(v));}

  /**
   * @param inflater layout inflater
   * @param layout by-index based layout
   *
   * @return new created adapter
   */
  @NonNull public static RecyclerViewAdapter byIndex
    (@NonNull LayoutInflater inflater, @NonNull IntUnaryOperator layout)
  {return custom(inflater, (p, v) -> layout.applyAsInt(p));}

  /**
   * @param inflater layout inflater
   * @param layout custom resolver
   *
   * @return new created adapter
   */
  @NonNull
  private static RecyclerViewAdapter custom(@NonNull LayoutInflater inflater,
                                            @NonNull LayoutMapper layout) {
    final AtomicReference<Item[]> aItems = new AtomicReference<>(Item.EMPTY);
    final Adapter<ViewHolder> adapter = new Adapter<ViewHolder>() {
      {setHasStableIds(false);}
      @NonNull @Override public final ViewHolder
      onCreateViewHolder(@NonNull ViewGroup parent, int layout)
      {return new ViewHolder(inflater.inflate(layout, parent, false)) {};}
      @Override public final void onBindViewHolder
        (@NonNull ViewHolder holder, int position)
      {holder.itemView.setTag(aItems.get()[position].value);}
      @SuppressLint("NewApi") @Override public final void onBindViewHolder
        (@NonNull ViewHolder holder, int pos, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) onBindViewHolder(holder, pos);
        else payloads.forEach(holder.itemView::setTag); }
      @Override public final void onViewRecycled(@NonNull ViewHolder holder)
      { holder.itemView.setTag(null); }
      @Override public final int getItemViewType(int position)
      {return aItems.get()[position].layout;}
      @Override public final long getItemId(int position)
      {return aItems.get()[position].hash;}
      @Override public final int getItemCount()
      {return aItems.get().length;}
    };

    final AdapterListUpdateCallback callback =
      new AdapterListUpdateCallback(adapter);
    return new RecyclerViewAdapter(adapter) {
      @NonNull @Override public final <T> Diffs diffs(@Nullable T[] value) {
        final Item[] items = Item.apply(callback, aItems.get(), layout, value);
        return new BatchingListUpdateCallback(callback) {
          @Override public final void dispatchLastEvent() {}
          @NonNull private Object[] apply()
          {aItems.set(items); super.dispatchLastEvent();return items;}
        }::apply;
      }
    };
  }

  /** Diffs result. */
  @SuppressWarnings({ "WeakerAccess", "RedundantSuppression" })
  public interface Diffs {

    /** Accept calculated diffs. */
    @NonNull Object[] accept();

    /** @return count of accepted items */
    default int count() {return accept().length;}

    /** @return true if accepted empty list */
    default boolean isEmpty() {return count() == 0;}

    /** Apply changes. */
    default void apply() {accept();}

  }

  /** Layout mapper. */
  @FunctionalInterface
  interface LayoutMapper {

    /**
     * @param position item position
     * @param value    item value
     *
     * @return resource layout
     */
    @LayoutRes
    int resolve(int position, @NonNull Object value);
  }

  /** Internal item holder. */
  private static final class Item {

    /** Empty items. */
    static final Item[] EMPTY = new Item[0];

    /** Detect moves. */
    private static final boolean MOVES = true;

    /** Layout id. */
    @LayoutRes final int layout;

    /** Object value. */
    @NonNull final Object value;

    /** Hash code. */
    final int hash;

    /**
     * Constructs a new {@link Item}.
     *
     * @param layout resource layout id
     * @param value  object value
     */
    Item(@LayoutRes int layout, @NonNull Object value) {
      this.layout = layout;
      this.value = value;
      hash = layout * 31 + value.hashCode();
    }

    /**
     * @param updateCallback adapter based update-callback
     * @param oldItems       previous items array
     * @param layouts        layout mapper
     * @param objects        new objects
     *
     * @return new items
     */
    @NonNull static Item[] apply(@NonNull ListUpdateCallback updateCallback,
                                 @NonNull Item[] oldItems,
                                 @NonNull LayoutMapper layouts,
                                 @Nullable Object[] objects) {
      final Item[] newItems = create(layouts, objects);
      final Callback diffsCallback = diffsCallback(oldItems, newItems);
      DiffUtil.calculateDiff(diffsCallback, MOVES).dispatchUpdatesTo(updateCallback);
      return newItems;
    }

    /**
     * @param layouts layouts mapper
     * @param value   objects array
     *
     * @return array of item
     */
    @NonNull private static Item[] create
    (@NonNull LayoutMapper layouts, @Nullable Object[] value) {
      return Optional.ofNullable(value)
        .map(v -> IntStream.range(0, v.length).parallel())
        .map(v -> v.mapToObj(i -> Item.create(layouts, i, value)))
        .map(v -> v.toArray(Item[]::new)).orElse(EMPTY);
    }

    /**
     * @param oldItems old items array
     * @param newItems new items array
     *
     * @return diff-util callback
     */
    @NonNull private static Callback diffsCallback(@NonNull Item[] oldItems, @NonNull Item[] newItems) {
      return new Callback() {
        @Override public final int getOldListSize() { return oldItems.length; }
        @Override public final int getNewListSize() { return newItems.length; }
        @Override public final boolean areItemsTheSame(int oldPos, int newPos) { return oldItems[oldPos].hash == newItems[newPos].hash; }
        @Override public final boolean areContentsTheSame(int oldPos, int newPos) { return Objects.equals(oldItems[oldPos].value, newItems[newPos].value); }
        @Override public final Object getChangePayload(int oldPos, int newPos) { return newItems[newPos].value; }
      };
    }

    /**
     * @param mapper layout mapper
     * @param index  array index
     * @param values objects
     *
     * @return wrapped item
     */
    @NonNull private static Item create
    (@NonNull LayoutMapper mapper, int index, @NonNull Object... values) {
      final Object value = values[index];
      final int layout = mapper.resolve(index, value);
      return new Item(layout, value);
    }
  }
}
