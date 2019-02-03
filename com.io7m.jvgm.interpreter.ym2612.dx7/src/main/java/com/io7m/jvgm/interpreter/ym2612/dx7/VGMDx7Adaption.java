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

package com.io7m.jvgm.interpreter.ym2612.dx7;

import com.io7m.jdextrosa.core.Dx7AlgorithmID;
import com.io7m.jdextrosa.core.Dx7Voice;
import com.io7m.jdextrosa.core.Dx7VoiceNamed;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.jvgm.interpreter.ym2612.VGMYM2612ChannelSnapshot;

import java.util.Objects;
import java.util.Optional;

/**
 * Functions to produce Dx7 patches from YM2612 patches.
 */

public final class VGMDx7Adaption
{
  private VGMDx7Adaption()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Adapt a snapshot to a DX7 voice.
   *
   * @param name     The name
   * @param snapshot The snapshot
   *
   * @return A DX7 voice
   */

  public static Dx7VoiceNamed adaptSnapshot(
    final String name,
    final VGMYM2612ChannelSnapshot snapshot)
  {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(snapshot, "snapshot");

    final Dx7Voice.Builder builder = Dx7Voice.builder();
    builder.setAlgorithm(adaptAlgorithm(snapshot.algorithm()));

    return Dx7VoiceNamed.of(name, builder.build(), Optional.empty());
  }

  private static Dx7AlgorithmID adaptAlgorithm(
    final int algorithm)
  {
    switch (algorithm) {
      case 0:
        return Dx7AlgorithmID.of(1);
      case 1:
        return Dx7AlgorithmID.of(14);
      case 2:
        return Dx7AlgorithmID.of(8);
      case 3:
        return Dx7AlgorithmID.of(7);
      case 4:
        return Dx7AlgorithmID.of(29);
      case 5:
        return Dx7AlgorithmID.of(22);
      case 6:
        return Dx7AlgorithmID.of(31);
      case 7:
        return Dx7AlgorithmID.of(32);
      default:
        throw new UnimplementedCodeException();
    }
  }
}
