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

package com.io7m.jvgm.interpreter.ym2612;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

/**
 * A snapshot of an operator.
 */

@ImmutablesStyleType
@Value.Immutable
public interface VGMYM2612OperatorSnapshotType
{
  /**
   * @return The inverse volume
   */

  @Value.Parameter
  int volumeInverse();

  /**
   * @return The volume
   */

  default int volume()
  {
    return 127 - this.volumeInverse();
  }

  /**
   * @return The rate of attack
   */

  @Value.Parameter
  int envelopeRateAttack();

  /**
   * @return The rate of the initial amplitude decay
   */

  @Value.Parameter
  int envelopeDecay1Rate();

  /**
   * @return The level of the initial amplitude decay
   */

  @Value.Parameter
  int envelopeDecay1Level();

  /**
   * @return The rate of the secondary amplitude decay
   */

  @Value.Parameter
  int envelopeDecay2Rate();

  /**
   * @return The rate of the release
   */

  @Value.Parameter
  int envelopeReleaseRate();

  /**
   * @return The pitch multiplication value for the operator
   */

  @Value.Parameter
  int pitchMultiply();

  /**
   * @return The pitch detune value for the operator
   */

  @Value.Parameter
  int pitchDetune();

  /**
   * @return The operator index
   */

  @Value.Parameter
  int index();
}
