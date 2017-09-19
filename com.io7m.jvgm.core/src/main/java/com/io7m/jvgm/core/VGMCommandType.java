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

/**
 * The type of VGM commands.
 */

public interface VGMCommandType
{
  /**
   * @return The VGM command type
   */

  Type type();

  /**
   * The type of commands.
   */

  enum Type
  {
    // CHECKSTYLE:OFF
    EOF(0x0),
    GAME_GEAR_PSG_STEREO_WRITE(0x4F),
    PSG_WRITE(0x50),
    YM2413_WRITE(0x51),
    YM2612_WRITE_PORT_0(0x52),
    YM2612_WRITE_PORT_1(0x53),
    YM2612_SEEK_PCM(0xE0),
    WAIT_LONG(0x61),
    END_OF_SOUND_DATA(0x66),
    DATA_BLOCK(0x67),
    WAIT_735(0x62),
    WAIT_882(0x63),
    WAIT_SHORT_0(0x70),
    WAIT_SHORT_1(0x71),
    WAIT_SHORT_2(0x72),
    WAIT_SHORT_3(0x73),
    WAIT_SHORT_4(0x74),
    WAIT_SHORT_5(0x75),
    WAIT_SHORT_6(0x76),
    WAIT_SHORT_7(0x77),
    WAIT_SHORT_8(0x78),
    WAIT_SHORT_9(0x79),
    WAIT_SHORT_A(0x7A),
    WAIT_SHORT_B(0x7B),
    WAIT_SHORT_C(0x7C),
    WAIT_SHORT_D(0x7D),
    WAIT_SHORT_E(0x7E),
    WAIT_SHORT_F(0x7F),
    YM2612_PCM_WRITE_WAIT_0(0x80),
    YM2612_PCM_WRITE_WAIT_1(0x81),
    YM2612_PCM_WRITE_WAIT_2(0x82),
    YM2612_PCM_WRITE_WAIT_3(0x83),
    YM2612_PCM_WRITE_WAIT_4(0x84),
    YM2612_PCM_WRITE_WAIT_5(0x85),
    YM2612_PCM_WRITE_WAIT_6(0x86),
    YM2612_PCM_WRITE_WAIT_7(0x87),
    YM2612_PCM_WRITE_WAIT_8(0x88),
    YM2612_PCM_WRITE_WAIT_9(0x89),
    YM2612_PCM_WRITE_WAIT_A(0x8A),
    YM2612_PCM_WRITE_WAIT_B(0x8B),
    YM2612_PCM_WRITE_WAIT_C(0x8C),
    YM2612_PCM_WRITE_WAIT_D(0x8D),
    YM2612_PCM_WRITE_WAIT_E(0x8E),
    YM2612_PCM_WRITE_WAIT_F(0x8F);
    // CHECKSTYLE:ON

    private final int tag;

    Type(final int t)
    {
      this.tag = t;
    }

    /**
     * @param t The tag
     *
     * @return The type associated with the given tag
     */

    public static Type ofInt(
      final int t)
    {
      switch (t) {
        case 0x4f:
          return GAME_GEAR_PSG_STEREO_WRITE;
        case 0x50:
          return PSG_WRITE;
        case 0x51:
          return YM2413_WRITE;
        case 0x52:
          return YM2612_WRITE_PORT_0;
        case 0x53:
          return YM2612_WRITE_PORT_1;
        case 0x61:
          return WAIT_LONG;
        case 0x62:
          return WAIT_735;
        case 0x63:
          return WAIT_882;
        case 0x66:
          return END_OF_SOUND_DATA;
        case 0x67:
          return DATA_BLOCK;
        case 0x70:
          return WAIT_SHORT_0;
        case 0x71:
          return WAIT_SHORT_1;
        case 0x72:
          return WAIT_SHORT_2;
        case 0x73:
          return WAIT_SHORT_3;
        case 0x74:
          return WAIT_SHORT_4;
        case 0x75:
          return WAIT_SHORT_5;
        case 0x76:
          return WAIT_SHORT_6;
        case 0x77:
          return WAIT_SHORT_7;
        case 0x78:
          return WAIT_SHORT_8;
        case 0x79:
          return WAIT_SHORT_9;
        case 0x7a:
          return WAIT_SHORT_A;
        case 0x7b:
          return WAIT_SHORT_B;
        case 0x7c:
          return WAIT_SHORT_C;
        case 0x7d:
          return WAIT_SHORT_D;
        case 0x7e:
          return WAIT_SHORT_E;
        case 0x7f:
          return WAIT_SHORT_F;
        case 0x80:
          return YM2612_PCM_WRITE_WAIT_0;
        case 0x81:
          return YM2612_PCM_WRITE_WAIT_1;
        case 0x82:
          return YM2612_PCM_WRITE_WAIT_2;
        case 0x83:
          return YM2612_PCM_WRITE_WAIT_3;
        case 0x84:
          return YM2612_PCM_WRITE_WAIT_4;
        case 0x85:
          return YM2612_PCM_WRITE_WAIT_5;
        case 0x86:
          return YM2612_PCM_WRITE_WAIT_6;
        case 0x87:
          return YM2612_PCM_WRITE_WAIT_7;
        case 0x88:
          return YM2612_PCM_WRITE_WAIT_8;
        case 0x89:
          return YM2612_PCM_WRITE_WAIT_9;
        case 0x8A:
          return YM2612_PCM_WRITE_WAIT_A;
        case 0x8B:
          return YM2612_PCM_WRITE_WAIT_B;
        case 0x8C:
          return YM2612_PCM_WRITE_WAIT_C;
        case 0x8D:
          return YM2612_PCM_WRITE_WAIT_D;
        case 0x8E:
          return YM2612_PCM_WRITE_WAIT_E;
        case 0x8F:
          return YM2612_PCM_WRITE_WAIT_F;
        case 0xe0:
          return YM2612_SEEK_PCM;

        default: {
          throw new IllegalArgumentException(
            "Unrecognized tag: 0x" + Integer.toUnsignedString(t, 16));
        }
      }
    }

    /**
     * @return The numeric tag for the command
     */

    public int tag()
    {
      return this.tag;
    }
  }
}
