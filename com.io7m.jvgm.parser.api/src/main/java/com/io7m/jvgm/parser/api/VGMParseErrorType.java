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

import com.io7m.jvgm.core.VGMImmutableStyleType;
import org.immutables.value.Value;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The type of parse errors.
 */

@VGMImmutableStyleType
@Value.Immutable
public interface VGMParseErrorType
{
  /**
   * @return The offset in bytes at which the error was encountered
   */

  @Value.Parameter
  long offset();

  /**
   * @return The path to the parsed file, if any
   */

  @Value.Parameter
  Optional<Path> path();

  /**
   * @return The error message
   */

  @Value.Parameter
  String message();

  /**
   * @return The exception raised, if any
   */

  @Value.Parameter
  Optional<Exception> exception();
}
