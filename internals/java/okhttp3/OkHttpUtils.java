/*
 * OkHttpUtils.java
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

package okhttp3;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import okhttp3.MultipartBody.Part;
import okio.ByteString;

import static okhttp3.Protocol.HTTP_1_1;
import static okhttp3.Protocol.HTTP_2;
import static okhttp3.internal.Util.immutableList;

/**
 * @author Gleb Nikitenko
 * @since 28.04.20
 **/
@SuppressWarnings({
  "unused",
  "WeakerAccess",
  "RedundantSuppression"
})
public final class OkHttpUtils {
  /**
   * The caller should be prevented from constructing objects of this class.
   * Also, this prevents even the native class from calling this constructor.
   **/
  private OkHttpUtils() {
    throw new AssertionError();
  }

  /**
   * @param client okHttp client
   * @param check  wss checker
   * @return call factory
   */
  public static Call.Factory factory(OkHttpClient.Builder client, Predicate<HttpUrl> check) {
    final OkHttpClient
      rest = client.protocols(immutableList(HTTP_2, HTTP_1_1)).build(),
      wss = client.protocols(immutableList(HTTP_1_1)).build();
    return request -> {
      final boolean isWebSocket = check.test(request.url);
      return RealCall.newRealCall(isWebSocket ? wss : rest, request, isWebSocket);
    };
  }

  /**
   * @param boundary multipart boundary
   * @param type     content type
   * @param parts    body parts
   * @return request body
   */
  public static RequestBody multipart(ByteString boundary, MediaType type, Part... parts) {
    return new MultipartBody(boundary, type, Arrays.asList(parts));
  }

  /**
   * @param builder http url builder
   * @param query   list of query parameters
   */
  public static void query(HttpUrl.Builder builder, List<String> query) {
    builder.encodedQueryNamesAndValues = query;
  }

  /**
   * @param input       input content
   * @param pos         position of start
   * @param limit       size of input
   * @param set         encode set
   * @param encoded     true to leave '%' as-is; false to convert it to '%25'.
   * @param strict      true to encode '%' if it is not the prefix of a valid percent encoding.
   * @param plusIsSpace true to encode '+' as "%2B" if it is not already encoded.
   * @param asciiOnly   true to encode all non-ASCII codePoints.
   * @param charset     which charset to use, null equals UTF-8.
   * @return substring of {@code input} on the range {@code [pos..limit)}
   * with the following transformations:
   * <ul>
   *   <li>Tabs, newlines, form feeds and carriage returns are skipped.
   *   <li>In queries, ' ' is encoded to '+' and '+' is encoded to "%2B".
   *   <li>Characters in {@code encodeSet} are percent-encoded.
   *   <li>Control characters and non-ASCII characters are percent-encoded.
   *   <li>All other characters are copied without transformation.
   * </ul>
   */
  public static String canonicalize(String input, int pos, int limit, String set, boolean encoded,
                                    boolean strict, boolean plusIsSpace, boolean asciiOnly, Charset charset) {
    return HttpUrl.canonicalize(input, pos, limit, set, encoded, strict, plusIsSpace, asciiOnly, charset);
  }
}
