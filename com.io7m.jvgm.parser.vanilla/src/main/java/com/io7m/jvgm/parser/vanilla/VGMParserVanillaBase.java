/*
 * Copyright © 2017 <code@io7m.com> https://www.io7m.com
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

package com.io7m.jvgm.parser.vanilla;

import com.io7m.jvgm.parser.api.VGMParseError;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import io.vavr.control.Validation;
import org.apache.commons.io.input.CountingInputStream;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

abstract class VGMParserVanillaBase
{
  private final Path path;
  private final CountingInputStream count_stream;

  protected VGMParserVanillaBase(
    final Path in_path,
    final CountingInputStream in_count_stream)
  {
    this.path =
      Objects.requireNonNull(in_path, "Path");
    this.count_stream =
      Objects.requireNonNull(in_count_stream, "Count stream");
  }

  protected final <T> Validation<Seq<VGMParseError>, T> errorV(
    final String message)
  {
    return Validation.invalid(Vector.of(this.error(message)));
  }

  protected final VGMParseError error(
    final String message)
  {
    return VGMParseError.of(
      this.countingStream().getByteCount(),
      Optional.of(this.path()),
      message,
      Optional.empty());
  }

  protected final <T> Validation<Seq<VGMParseError>, T> errorExceptionV(
    final Exception e)
  {
    return Validation.invalid(Vector.of(this.errorException(e)));
  }

  protected final VGMParseError errorException(
    final Exception e)
  {
    return VGMParseError.of(
      this.countingStream().getByteCount(),
      Optional.of(this.path()),
      e.getMessage(),
      Optional.of(e));
  }

  protected final CountingInputStream countingStream()
  {
    return this.count_stream;
  }

  protected final Path path()
  {
    return this.path;
  }
}
