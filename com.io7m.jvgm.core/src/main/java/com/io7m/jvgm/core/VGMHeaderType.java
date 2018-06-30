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

package com.io7m.jvgm.core;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.util.OptionalLong;

/**
 * A parsed VGM header.
 */

@ImmutablesStyleType
@Value.Immutable
public interface VGMHeaderType
{
  /**
   * @return The absolute offset of EOF
   */

  long eofOffset();

  /**
   * @return The version of the VGM file format
   */

  long version();

  /**
   * @return The absolute offset of GD3 tag data, if any
   */

  OptionalLong offsetGD3();

  /**
   * @return The number of samples in the file
   */

  long sampleCount();

  /**
   * @return The absolute offset of the loop point, if any
   */

  OptionalLong loopOffset();

  /**
   * @return The number of samples in one loop
   */

  OptionalLong loopSampleCount();

  /**
   * @return The recording rate in hz
   */

  long rate();

  /**
   * @return The YM2413 clock rate in hz, if any
   */

  OptionalLong chipYM2413Clock();

  /**
   * @return The SN76489 clock rate in hz, if any
   */

  OptionalLong chipSN76489Clock();

  /**
   * @return The white noise feedback pattern for the SN76489 PSG
   */

  @Value.Default
  default int chipSN76489Feedback()
  {
    return 0x0009;
  }

  /**
   * @return The noise feedback shift register width, in bits
   */

  @Value.Default
  default int chipSN76489ShiftRegisterWidth()
  {
    return 16;
  }

  /**
   * @return Misc flags for the SN76489
   */

  @Value.Default
  default int chipSN76489Flags()
  {
    return 0;
  }

  /**
   * @return The YM2612 clock rate in hz, if any
   */

  OptionalLong chipYM2612Clock();

  /**
   * @return The YM2151 clock rate in hz, if any
   */

  OptionalLong chipYM2151Clock();

  /**
   * @return The absolute offset of the start of the command data
   */

  long dataOffset();

  /**
   * @return The Sega PCM clock rate in hz, if any
   */

  OptionalLong chipSegaPCMClock();

  /**
   * @return The interface register for the Sega PCM chip
   */

  @Value.Default
  default long chipSegaPCMInterfaceRegister()
  {
    return 0L;
  }
}
