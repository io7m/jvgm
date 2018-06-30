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

package com.io7m.jvgm.parser.vanilla;

import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.jvgm.core.VGMCommandDataBlock;
import com.io7m.jvgm.core.VGMCommandEOF;
import com.io7m.jvgm.core.VGMCommandEndOfSoundData;
import com.io7m.jvgm.core.VGMCommandGameGearPSGStereoWrite;
import com.io7m.jvgm.core.VGMCommandPSGWrite;
import com.io7m.jvgm.core.VGMCommandType;
import com.io7m.jvgm.core.VGMCommandWait735;
import com.io7m.jvgm.core.VGMCommandWait882;
import com.io7m.jvgm.core.VGMCommandWaitLong;
import com.io7m.jvgm.core.VGMCommandWaitShort0;
import com.io7m.jvgm.core.VGMCommandWaitShort1;
import com.io7m.jvgm.core.VGMCommandWaitShort2;
import com.io7m.jvgm.core.VGMCommandWaitShort3;
import com.io7m.jvgm.core.VGMCommandWaitShort4;
import com.io7m.jvgm.core.VGMCommandWaitShort5;
import com.io7m.jvgm.core.VGMCommandWaitShort6;
import com.io7m.jvgm.core.VGMCommandWaitShort7;
import com.io7m.jvgm.core.VGMCommandWaitShort8;
import com.io7m.jvgm.core.VGMCommandWaitShort9;
import com.io7m.jvgm.core.VGMCommandWaitShortA;
import com.io7m.jvgm.core.VGMCommandWaitShortB;
import com.io7m.jvgm.core.VGMCommandWaitShortC;
import com.io7m.jvgm.core.VGMCommandWaitShortD;
import com.io7m.jvgm.core.VGMCommandWaitShortE;
import com.io7m.jvgm.core.VGMCommandWaitShortF;
import com.io7m.jvgm.core.VGMCommandYM2413Write;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWait0;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWait1;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWait2;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWait3;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWait4;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWait5;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWait6;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWait7;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWait8;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWait9;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWaitA;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWaitB;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWaitC;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWaitD;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWaitE;
import com.io7m.jvgm.core.VGMCommandYM2612PCMWriteWaitF;
import com.io7m.jvgm.core.VGMCommandYM2612SeekPCM;
import com.io7m.jvgm.core.VGMCommandYM2612WritePort0;
import com.io7m.jvgm.core.VGMCommandYM2612WritePort1;
import com.io7m.jvgm.core.VGMHeader;
import com.io7m.jvgm.parser.api.VGMParseError;
import com.io7m.jvgm.parser.api.VGMParserBodyType;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.io7m.jvgm.core.VGMCommandType.Type.DATA_BLOCK;

final class VGMParserVanillaBody
  extends VGMParserVanillaBase implements VGMParserBodyType
{
  private final SwappedDataInputStream data_stream;
  private final VGMHeader header;
  private boolean finished;

  VGMParserVanillaBody(
    final VGMHeader in_header,
    final Path in_path,
    final CountingInputStream in_count_stream,
    final SwappedDataInputStream in_data_stream)
  {
    super(in_path, in_count_stream);

    this.header =
      Objects.requireNonNull(in_header, "Header");
    this.data_stream =
      Objects.requireNonNull(in_data_stream, "Data stream");
    this.finished = false;
  }

  // Excessive cyclomatic complexity due to unavoidable switching
  //CHECKSTYLE:OFF
  @Override
  public Validation<Seq<VGMParseError>, VGMCommandType> parse()
  {
    if (this.finished) {
      return Validation.valid(VGMCommandEOF.builder().build());
    }

    try {
      while (true) {
        final int tag = this.data_stream.readUnsignedByte();

        if (this.skipReservedCommand(tag)) {
          continue;
        }

        final VGMCommandType.Type type = VGMCommandType.Type.ofInt(tag);

        switch (type) {
          case EOF: {
            throw new UnreachableCodeException();
          }

          case GAME_GEAR_PSG_STEREO_WRITE: {
            return Validation.valid(
              VGMCommandGameGearPSGStereoWrite.of(this.readByte()));
          }

          case PSG_WRITE: {
            return Validation.valid(VGMCommandPSGWrite.of(this.readByte()));
          }

          case YM2413_WRITE: {
            final byte reg = this.readByte();
            final byte val = this.readByte();
            return Validation.valid(VGMCommandYM2413Write.of(reg, val));
          }

          case YM2612_WRITE_PORT_0: {
            final byte reg = this.readByte();
            final byte val = this.readByte();
            return Validation.valid(VGMCommandYM2612WritePort0.of(reg, val));
          }

          case YM2612_WRITE_PORT_1: {
            final byte reg = this.readByte();
            final byte val = this.readByte();
            return Validation.valid(VGMCommandYM2612WritePort1.of(reg, val));
          }

          case YM2612_SEEK_PCM: {
            final long size = Integer.toUnsignedLong(this.data_stream.readInt());
            return Validation.valid(VGMCommandYM2612SeekPCM.of(size));
          }

          case WAIT_LONG: {
            final int val = this.data_stream.readUnsignedShort();
            return Validation.valid(VGMCommandWaitLong.of(val));
          }

          case END_OF_SOUND_DATA: {
            this.finished = true;
            return Validation.valid(VGMCommandEndOfSoundData.builder().build());
          }

          case DATA_BLOCK: {
            // Skip 0x66 compatibility byte
            final byte pad = this.readByte();
            if ((int) pad != 0x66) {
              final String separator = System.lineSeparator();
              final CountingInputStream stream = this.countingStream();
              final String tag_text = Integer.toUnsignedString(DATA_BLOCK.tag(), 16);
              return this.errorV(
                new StringBuilder(128)
                  .append("  Position: ")
                  .append(stream.getByteCount())
                  .append(separator)
                  .append("  Received: ")
                  .append(tag_text)
                  .append(" ")
                  .append(Integer.toUnsignedString(pad, 16))
                  .append(separator)
                  .append("  Expected: ")
                  .append(tag_text)
                  .append(" 0x66")
                  .append(separator)
                  .toString());
            }

            final byte data_type = this.readByte();
            final long size = Integer.toUnsignedLong(this.data_stream.readInt());
            this.data_stream.skip(size);
            return Validation.valid(VGMCommandDataBlock.of(data_type, size));
          }

          case WAIT_735:
            return Validation.valid(VGMCommandWait735.builder().build());
          case WAIT_882:
            return Validation.valid(VGMCommandWait882.builder().build());
          case WAIT_SHORT_0:
            return Validation.valid(VGMCommandWaitShort0.builder().build());
          case WAIT_SHORT_1:
            return Validation.valid(VGMCommandWaitShort1.builder().build());
          case WAIT_SHORT_2:
            return Validation.valid(VGMCommandWaitShort2.builder().build());
          case WAIT_SHORT_3:
            return Validation.valid(VGMCommandWaitShort3.builder().build());
          case WAIT_SHORT_4:
            return Validation.valid(VGMCommandWaitShort4.builder().build());
          case WAIT_SHORT_5:
            return Validation.valid(VGMCommandWaitShort5.builder().build());
          case WAIT_SHORT_6:
            return Validation.valid(VGMCommandWaitShort6.builder().build());
          case WAIT_SHORT_7:
            return Validation.valid(VGMCommandWaitShort7.builder().build());
          case WAIT_SHORT_8:
            return Validation.valid(VGMCommandWaitShort8.builder().build());
          case WAIT_SHORT_9:
            return Validation.valid(VGMCommandWaitShort9.builder().build());
          case WAIT_SHORT_A:
            return Validation.valid(VGMCommandWaitShortA.builder().build());
          case WAIT_SHORT_B:
            return Validation.valid(VGMCommandWaitShortB.builder().build());
          case WAIT_SHORT_C:
            return Validation.valid(VGMCommandWaitShortC.builder().build());
          case WAIT_SHORT_D:
            return Validation.valid(VGMCommandWaitShortD.builder().build());
          case WAIT_SHORT_E:
            return Validation.valid(VGMCommandWaitShortE.builder().build());
          case WAIT_SHORT_F:
            return Validation.valid(VGMCommandWaitShortF.builder().build());

          case YM2612_PCM_WRITE_WAIT_0:
            return Validation.valid(VGMCommandYM2612PCMWriteWait0.builder().build());
          case YM2612_PCM_WRITE_WAIT_1:
            return Validation.valid(VGMCommandYM2612PCMWriteWait1.builder().build());
          case YM2612_PCM_WRITE_WAIT_2:
            return Validation.valid(VGMCommandYM2612PCMWriteWait2.builder().build());
          case YM2612_PCM_WRITE_WAIT_3:
            return Validation.valid(VGMCommandYM2612PCMWriteWait3.builder().build());
          case YM2612_PCM_WRITE_WAIT_4:
            return Validation.valid(VGMCommandYM2612PCMWriteWait4.builder().build());
          case YM2612_PCM_WRITE_WAIT_5:
            return Validation.valid(VGMCommandYM2612PCMWriteWait5.builder().build());
          case YM2612_PCM_WRITE_WAIT_6:
            return Validation.valid(VGMCommandYM2612PCMWriteWait6.builder().build());
          case YM2612_PCM_WRITE_WAIT_7:
            return Validation.valid(VGMCommandYM2612PCMWriteWait7.builder().build());
          case YM2612_PCM_WRITE_WAIT_8:
            return Validation.valid(VGMCommandYM2612PCMWriteWait8.builder().build());
          case YM2612_PCM_WRITE_WAIT_9:
            return Validation.valid(VGMCommandYM2612PCMWriteWait9.builder().build());
          case YM2612_PCM_WRITE_WAIT_A:
            return Validation.valid(VGMCommandYM2612PCMWriteWaitA.builder().build());
          case YM2612_PCM_WRITE_WAIT_B:
            return Validation.valid(VGMCommandYM2612PCMWriteWaitB.builder().build());
          case YM2612_PCM_WRITE_WAIT_C:
            return Validation.valid(VGMCommandYM2612PCMWriteWaitC.builder().build());
          case YM2612_PCM_WRITE_WAIT_D:
            return Validation.valid(VGMCommandYM2612PCMWriteWaitD.builder().build());
          case YM2612_PCM_WRITE_WAIT_E:
            return Validation.valid(VGMCommandYM2612PCMWriteWaitE.builder().build());
          case YM2612_PCM_WRITE_WAIT_F:
            return Validation.valid(VGMCommandYM2612PCMWriteWaitF.builder().build());
        }

        throw new UnreachableCodeException();
      }

    } catch (final EOFException e) {
      this.finished = true;
      return Validation.valid(VGMCommandEOF.builder().build());
    } catch (final IllegalArgumentException | IOException e) {
      return this.errorExceptionV(e);
    }
  }

  /**
   * Skip reserved opcodes.
   */

  private boolean skipReservedCommand(
    final int tag)
    throws IOException
  {
    if (tag >= 0x30 && tag <= 0x3f) {
      this.readByte();
      return true;
    }

    if (tag >= 0x40 && tag <= 0x4e) {
      if (this.header.version() < 0x160L) {
        this.readByte();
      } else {
        this.readByte();
        this.readByte();
      }
      return true;
    }

    if (tag >= 0xA1 && tag <= 0xAF) {
      this.readByte();
      this.readByte();
      return true;
    }

    if (tag >= 0xC9 && tag <= 0xCF) {
      this.readByte();
      this.readByte();
      this.readByte();
      return true;
    }

    if (tag >= 0xD7 && tag <= 0xDF) {
      this.readByte();
      this.readByte();
      this.readByte();
      return true;
    }

    if (tag >= 0xE2 && tag <= 0xFF) {
      this.readByte();
      this.readByte();
      this.readByte();
      this.readByte();
      return true;
    }

    return false;
  }
  //CHECKSTYLE:ON

  private byte readByte()
    throws IOException
  {
    final int r = this.data_stream.readUnsignedByte();
    if (r == -1) {
      throw new EOFException("Unexpected EOF");
    }

    return (byte) (r & 0xff);
  }
}
