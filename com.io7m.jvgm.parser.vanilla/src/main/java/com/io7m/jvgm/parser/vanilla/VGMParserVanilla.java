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

import com.io7m.jvgm.core.VGMVersion;
import com.io7m.jvgm.parser.api.VGMParserHeaderType;
import com.io7m.jvgm.parser.api.VGMParserProviderType;
import io.vavr.collection.SortedSet;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The vanilla parser provider.
 */

public final class VGMParserVanilla implements VGMParserProviderType
{
  /**
   * Construct a parser provider.
   */

  public VGMParserVanilla()
  {

  }

  @Override
  public VGMParserHeaderType open(
    final Path path,
    final InputStream stream)
  {
    Objects.requireNonNull(path, "Path");
    Objects.requireNonNull(stream, "Stream");

    return new VGMParserVanillaHeader(path, stream);
  }

  @Override
  public SortedSet<VGMVersion> supportedVersions()
  {
    return VGMParserVanillaSupported.SUPPORTED;
  }
}
