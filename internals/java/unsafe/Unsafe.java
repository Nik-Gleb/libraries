/*
 * Unsafe.java
 * internals
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

package unsafe;

/**
 * Unsafe tools.
 *
 * @author Gleb Nikitenko
 * @since 28.04.20
 **/
@SuppressWarnings({
  "unused",
  "WeakerAccess",
  "RedundantSuppression"
  , "ConstantConditions"})
public final class Unsafe {

  /**
   * Local unsafe instance
   */
  private static final sun.misc.Unsafe UNSAFE;
  public static final int ARRAY_BOOLEAN_BASE_OFFSET = arrayBaseOffset(boolean[].class);
  public static final int ARRAY_BYTE_BASE_OFFSET = arrayBaseOffset(byte[].class);
  public static final int ARRAY_SHORT_BASE_OFFSET = arrayBaseOffset(short[].class);
  public static final int ARRAY_CHAR_BASE_OFFSET = arrayBaseOffset(char[].class);
  public static final int ARRAY_INT_BASE_OFFSET = arrayBaseOffset(int[].class);
  public static final int ARRAY_LONG_BASE_OFFSET = arrayBaseOffset(long[].class);
  public static final int ARRAY_FLOAT_BASE_OFFSET = arrayBaseOffset(float[].class);
  public static final int ARRAY_DOUBLE_BASE_OFFSET = arrayBaseOffset(double[].class);
  public static final int ARRAY_OBJECT_BASE_OFFSET = arrayBaseOffset(Object[].class);
  public static final int ARRAY_BOOLEAN_INDEX_SCALE = arrayIndexScale(boolean[].class);
  public static final int ARRAY_BYTE_INDEX_SCALE = arrayIndexScale(byte[].class);
  public static final int ARRAY_SHORT_INDEX_SCALE = arrayIndexScale(short[].class);
  public static final int ARRAY_CHAR_INDEX_SCALE = arrayIndexScale(char[].class);
  public static final int ARRAY_INT_INDEX_SCALE = arrayIndexScale(int[].class);
  public static final int ARRAY_LONG_INDEX_SCALE = arrayIndexScale(long[].class);
  public static final int ARRAY_FLOAT_INDEX_SCALE = arrayIndexScale(float[].class);
  public static final int ARRAY_DOUBLE_INDEX_SCALE = arrayIndexScale(double[].class);
  public static final int ARRAY_OBJECT_INDEX_SCALE = arrayIndexScale(Object[].class);
  public static final int ADDRESS_SIZE = addressSize();

  /* Static initialization. */
  static {
    final java.lang.reflect.Field field;
    try {
      field = sun.misc.Unsafe.class
        .getDeclaredField("theUnsafe");
    } catch (NoSuchFieldException exception) {
      throw new RuntimeException(exception);
    }
    field.setAccessible(true);
    try {
      UNSAFE = (sun.misc.Unsafe) field.get(null);
    } catch (IllegalAccessException exception) {
      throw new RuntimeException(exception);
    }
  }


  /**
   * The caller should be prevented from constructing objects of this class.
   * Also, this prevents even the static class from calling this constructor.
   **/
  private Unsafe() {
    throw new AssertionError();
  }

  public static int getInt(long address) {
    return UNSAFE.getInt(address);
  }

  public static void putInt(long address, int value) {
    UNSAFE.putInt(address, value);
  }

  public static <T> int getInt(T object, long offset) {
    return UNSAFE.getInt(object, offset);
  }

  public static <T> void putInt(T object, long offset, int value) {
    UNSAFE.putInt(object, offset, value);
  }

  public static <T> int getIntVolatile(T object, long offset) {
    return UNSAFE.getIntVolatile(object, offset);
  }

  public static <T> void putIntVolatile(T object, long offset, int value) {
    UNSAFE.putIntVolatile(object, offset, value);
  }

  public static <T> void putOrderedInt(T object, long offset, int value) {
    UNSAFE.putOrderedInt(object, offset, value);
  }

  public static <T> boolean compareAndSwapInt(T object, long offset, int value1, int value2) {
    return UNSAFE.compareAndSwapInt(object, offset, value1, value2);
  }

  public static byte getByte(long address) {
    return UNSAFE.getByte(address);
  }

  public static void putByte(long address, byte value) {
    UNSAFE.putByte(address, value);
  }

  public static <T> byte getByte(T object, long offset) {
    return UNSAFE.getByte(object, offset);
  }

  public static <T> void putByte(T object, long offset, byte value) {
    UNSAFE.putByte(object, offset, value);
  }

  public static <T> byte getByteVolatile(T object, long offset) {
    return UNSAFE.getByteVolatile(object, offset);
  }

  public static <T> void putByteVolatile(T object, long offset, byte value) {
    UNSAFE.putByteVolatile(object, offset, value);
  }

  public static short getShort(long address) {
    return UNSAFE.getShort(address);
  }

  public static void putShort(long address, short value) {
    UNSAFE.putShort(address, value);
  }

  public static <T> short getShort(T object, long offset) {
    return UNSAFE.getShort(object, offset);
  }

  public static <T> void putShort(T object, long offset, short value) {
    UNSAFE.putShort(object, offset, value);
  }

  public static <T> short getShortVolatile(T object, long offset) {
    return UNSAFE.getShortVolatile(object, offset);
  }

  public static <T> void putShortVolatile(T object, long offset, short value) {
    UNSAFE.putShortVolatile(object, offset, value);
  }

  public static char getChar(long address) {
    return UNSAFE.getChar(address);
  }

  public static void putChar(long address, char value) {
    UNSAFE.putChar(address, value);
  }

  public static <T> char getChar(T object, long offset) {
    return UNSAFE.getChar(object, offset);
  }

  public static <T> void putChar(T object, long offset, char value) {
    UNSAFE.putChar(object, offset, value);
  }

  public static <T> char getCharVolatile(T object, long offset) {
    return UNSAFE.getCharVolatile(object, offset);
  }

  public static <T> void putCharVolatile(T object, long offset, char value) {
    UNSAFE.putCharVolatile(object, offset, value);
  }

  public static long getLong(long address) {
    return UNSAFE.getLong(address);
  }

  public static void putLong(long address, long value) {
    UNSAFE.putLong(address, value);
  }

  public static <T> long getLong(T object, long offset) {
    return UNSAFE.getLong(object, offset);
  }

  public static <T> void putLong(T object, long offset, long value) {
    UNSAFE.putLong(object, offset, value);
  }

  public static <T> long getLongVolatile(T object, long offset) {
    return UNSAFE.getLongVolatile(object, offset);
  }

  public static <T> void putLongVolatile(T object, long offset, long value) {
    UNSAFE.putLongVolatile(object, offset, value);
  }

  public static <T> void putOrderedInt(T object, long offset, long value) {
    UNSAFE.putOrderedLong(object, offset, value);
  }

  public static <T> boolean compareAndSwapLong(T object, long offset, long value1, long value2) {
    return UNSAFE.compareAndSwapLong(object, offset, value1, value2);
  }

  public static float getFloat(long address) {
    return UNSAFE.getFloat(address);
  }

  public static void putFloat(long address, float value) {
    UNSAFE.putFloat(address, value);
  }

  public static <T> float getFloat(T object, long offset) {
    return UNSAFE.getFloat(object, offset);
  }

  public static <T> void putFloat(T object, long offset, float value) {
    UNSAFE.putFloat(object, offset, value);
  }

  public static <T> float getFloatVolatile(T object, long offset) {
    return UNSAFE.getFloatVolatile(object, offset);
  }

  public static <T> void putFloatVolatile(T object, long offset, float value) {
    UNSAFE.putFloatVolatile(object, offset, value);
  }

  public static double getDouble(long address) {
    return UNSAFE.getDouble(address);
  }

  public static void putDouble(long address, double value) {
    UNSAFE.putDouble(address, value);
  }

  public static <T> double getDouble(T object, long offset) {
    return UNSAFE.getDouble(object, offset);
  }

  public static <T> void putDouble(T object, long offset, double value) {
    UNSAFE.putDouble(object, offset, value);
  }

  public static <T> double getDoubleVolatile(T object, long offset) {
    return UNSAFE.getDoubleVolatile(object, offset);
  }

  public static <T> void putDoubleVolatile(T object, long offset, double value) {
    UNSAFE.putDoubleVolatile(object, offset, value);
  }

  public static <T> boolean getBoolean(T object, long offset) {
    return UNSAFE.getBoolean(object, offset);
  }

  public static <T> void putBoolean(T object, long offset, boolean value) {
    UNSAFE.putBoolean(object, offset, value);
  }

  public static <T> boolean getBooleanVolatile(T object, long offset) {
    return UNSAFE.getBooleanVolatile(object, offset);
  }

  public static <T> void putBooleanVolatile(T object, long offset, boolean value) {
    UNSAFE.putBooleanVolatile(object, offset, value);
  }

  @SuppressWarnings("unchecked")
  public static <T, U> U getObject(T object, long offset) {
    return (U) UNSAFE.getObject(object, offset);
  }

  public static <T, U> void putObject(T object, long offset, U value) {
    UNSAFE.putObject(object, offset, value);
  }

  @SuppressWarnings("unchecked")
  public static <T, U> U getObjectVolatile(T object, long offset) {
    return (U) UNSAFE.getObjectVolatile(object, offset);
  }

  public static <T, U> void putObjectVolatile(T object, long offset, U value) {
    UNSAFE.putObjectVolatile(object, offset, value);
  }

  public static <T, U> void putOrderedObject(T object, long offset, U value) {
    UNSAFE.putOrderedObject(object, offset, value);
  }

  public static <T, U> boolean compareAndSwapObject(T object, long offset, U value1, U value2) {
    return UNSAFE.compareAndSwapObject(object, offset, value1, value2);
  }

  public static long getAddress(long address) {
    return UNSAFE.getAddress(address);
  }

  public static void putAddress(long address, long value) {
    UNSAFE.putAddress(address, value);
  }

  public static int addressSize() {
    return UNSAFE.addressSize();
  }

  public static int pageSize() {
    return UNSAFE.pageSize();
  }

  public static long allocateMemory(long bytes) {
    return UNSAFE.allocateMemory(bytes);
  }

  public static long reallocateMemory(long address, long bytes) {
    return UNSAFE.reallocateMemory(address, bytes);
  }

  public static <T> void copyMemory(T srcObject, long srcOffset, T dstObject, long dstOffset, long bytes) {
    UNSAFE.copyMemory(srcObject, srcOffset, dstObject, dstOffset, bytes);
  }

  public static void freeMemory(long address) {
    UNSAFE.freeMemory(address);
  }

  public static <T> void setMemory(T object, long offset, long bytes, byte value) {
    UNSAFE.setMemory(object, offset, bytes, value);
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> defineClass(String name, byte[] data, int offset, int length, ClassLoader loader, java.security.ProtectionDomain domain) {
    return (Class<T>) UNSAFE.defineClass(name, data, offset, length, loader, domain);
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> defineAnonymousClass(Class<T> host, byte[] data, Object[] patches) {
    return (Class<T>) UNSAFE.defineAnonymousClass(host, data, patches);
  }

  public static <T> void ensureClassInitialized(Class<T> clazz) {
    UNSAFE.ensureClassInitialized(clazz);
  }

  @SuppressWarnings("unchecked")
  public static <T> T allocateInstance(Class<T> clazz) {
    try {
      return (T) UNSAFE.allocateInstance(clazz);
    } catch (InstantiationException exception) {
      exception.printStackTrace();
      return null;
    }
  }

  public static long staticFieldOffset(java.lang.reflect.Field field) {
    return UNSAFE.staticFieldOffset(field);
  }

  public static long objectFieldOffset(java.lang.reflect.Field field) {
    return UNSAFE.objectFieldOffset(field);
  }

  @SuppressWarnings("unchecked")
  public static <T> T staticFieldBase(java.lang.reflect.Field field) {
    return (T) UNSAFE.staticFieldBase(field);
  }

  public static boolean shouldBeInitialized(Class<?> clazz) {
    return UNSAFE.shouldBeInitialized(clazz);
  }

  public static int arrayBaseOffset(Class<?> clazz) {
    return UNSAFE.arrayBaseOffset(clazz);
  }

  public static int arrayIndexScale(Class<?> clazz) {
    return UNSAFE.arrayIndexScale(clazz);
  }

  public static void throwException(Throwable exception) {
    UNSAFE.throwException(exception);
  }

  public static <T> void unpark(T object) {
    UNSAFE.unpark(object);
  }

  public static void park(boolean absolute, long time) {
    UNSAFE.park(absolute, time);
  }

  public static int getLoadAverage(double[] loadavg, int nelem) {
    return UNSAFE.getLoadAverage(loadavg, nelem);
  }

  public static void loadFence() {
    UNSAFE.loadFence();
  }

  public static void storeFence() {
    UNSAFE.storeFence();
  }

  public static void fullFence() {
    UNSAFE.fullFence();
  }

}
