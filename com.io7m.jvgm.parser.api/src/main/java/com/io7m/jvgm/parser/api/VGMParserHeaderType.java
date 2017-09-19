/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jvgm.parser.api;

import com.io7m.jvgm.core.VGMHeader;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import java.io.Closeable;

/**
 * The type of header parsers.
 */

public interface VGMParserHeaderType extends Closeable
{
  /**
   * Parse a header. This method must be called exactly once.
   *
   * @return A body parser and a parsed header, or a list of parse errors
   */

  Validation<Seq<VGMParseError>, Tuple2<VGMParserBodyType, VGMHeader>> parse();
}
