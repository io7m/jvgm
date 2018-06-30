/*
 * Copyright © 2018 <code@io7m.com> http://io7m.com
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

package com.io7m.jvgm.cmdline;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.io7m.jvgm.core.VGMCommandType;
import com.io7m.jvgm.core.VGMCommandYM2612WritePort0;
import com.io7m.jvgm.core.VGMCommandYM2612WritePort1;
import com.io7m.jvgm.core.VGMHeader;
import com.io7m.jvgm.interpreter.ym2612.VGMYM2612Interpreter;
import com.io7m.jvgm.parser.api.VGMParseError;
import com.io7m.jvgm.parser.api.VGMParserBodyType;
import com.io7m.jvgm.parser.api.VGMParserHeaderType;
import com.io7m.jvgm.parser.vanilla.VGMParserVanilla;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

import static com.io7m.jvgm.cmdline.JVGMCommandType.Status.FAILURE;
import static com.io7m.jvgm.cmdline.JVGMCommandType.Status.SUCCESS;
import static com.io7m.jvgm.core.VGMCommandType.Type.EOF;

/**
 * A command for interpreting VGM files.
 */

@Parameters(
  commandNames = "interpret",
  commandDescription = "Interpret the given VGM file")
public final class JVGMCommandInterpret extends JVGMCommandRoot
{
  private static final Logger LOG = LoggerFactory.getLogger(JVGMCommandInterpret.class);

  @Parameter(
    names = "--file",
    required = true,
    description = "The input file")
  private Path file_input;

  @Parameter(
    names = "--gzip",
    required = false,
    description = "The input is gzip compressed")
  private boolean gzip;

  /**
   * Construct a command.
   */

  public JVGMCommandInterpret()
  {

  }

  // CHECKSTYLE:OFF
  private static boolean doCommand(
    final VGMYM2612Interpreter interpreter,
    final VGMCommandType command)
  {
    switch (command.type()) {
      case EOF: {
        return true;
      }

      case YM2612_WRITE_PORT_0: {
        final VGMCommandYM2612WritePort0 cmd = (VGMCommandYM2612WritePort0) command;
        interpreter.writeRegisterPort0(
          (byte) (cmd.register() & 0xff),
          (byte) (cmd.value() & 0xff));
        break;
      }
      case YM2612_WRITE_PORT_1: {
        final VGMCommandYM2612WritePort1 cmd = (VGMCommandYM2612WritePort1) command;
        interpreter.writeRegisterPort1(
          (byte) (cmd.register() & 0xff),
          (byte) (cmd.value() & 0xff));
        break;
      }

      case GAME_GEAR_PSG_STEREO_WRITE:
      case PSG_WRITE:
      case YM2413_WRITE:
      case YM2612_SEEK_PCM:
      case WAIT_LONG:
      case END_OF_SOUND_DATA:
      case DATA_BLOCK:
      case WAIT_735:
      case WAIT_882:
      case WAIT_SHORT_0:
      case WAIT_SHORT_1:
      case WAIT_SHORT_2:
      case WAIT_SHORT_3:
      case WAIT_SHORT_4:
      case WAIT_SHORT_5:
      case WAIT_SHORT_6:
      case WAIT_SHORT_7:
      case WAIT_SHORT_8:
      case WAIT_SHORT_9:
      case WAIT_SHORT_A:
      case WAIT_SHORT_B:
      case WAIT_SHORT_C:
      case WAIT_SHORT_D:
      case WAIT_SHORT_E:
      case WAIT_SHORT_F:
      case YM2612_PCM_WRITE_WAIT_0:
      case YM2612_PCM_WRITE_WAIT_1:
      case YM2612_PCM_WRITE_WAIT_2:
      case YM2612_PCM_WRITE_WAIT_3:
      case YM2612_PCM_WRITE_WAIT_4:
      case YM2612_PCM_WRITE_WAIT_5:
      case YM2612_PCM_WRITE_WAIT_6:
      case YM2612_PCM_WRITE_WAIT_7:
      case YM2612_PCM_WRITE_WAIT_8:
      case YM2612_PCM_WRITE_WAIT_9:
      case YM2612_PCM_WRITE_WAIT_A:
      case YM2612_PCM_WRITE_WAIT_B:
      case YM2612_PCM_WRITE_WAIT_C:
      case YM2612_PCM_WRITE_WAIT_D:
      case YM2612_PCM_WRITE_WAIT_E:
      case YM2612_PCM_WRITE_WAIT_F: {
        break;
      }
    }

    if (command.type() == EOF) {
      return true;
    }
    return false;
  }

  @Override
  public Status execute()
    throws Exception
  {
    super.execute();

    final VGMParserVanilla parsers = new VGMParserVanilla();

    boolean failed = false;

    final VGMYM2612Interpreter interpreter = new VGMYM2612Interpreter();

    try (InputStream file = this.openStream()) {
      try (VGMParserHeaderType parser = parsers.open(this.file_input, file)) {
        final Validation<Seq<VGMParseError>, Tuple2<VGMParserBodyType, VGMHeader>> result = parser.parse();
        if (!result.isValid()) {
          final Seq<VGMParseError> errors = result.getError();
          errors.forEach(
            error -> LOG.error(
              "parse error: 0x{}: {}",
              Long.toUnsignedString(error.offset(), 16),
              error.message()));
          return FAILURE;
        }

        final Tuple2<VGMParserBodyType, VGMHeader> header_and_body = result.get();
        final VGMParserBodyType body_parser = header_and_body._1;
        final VGMHeader header = header_and_body._2;

        LOG.debug("header: {}", header);

        while (true) {
          final Validation<Seq<VGMParseError>, VGMCommandType> body_result = body_parser.parse();
          if (!body_result.isValid()) {
            final Seq<VGMParseError> errors = body_result.getError();
            errors.forEach(
              error -> LOG.error(
                "parse error: 0x{}: {}",
                Long.toUnsignedString(error.offset(), 16),
                error.message()));
            failed = true;
            continue;
          }

          if (doCommand(interpreter, body_result.get())) {
            return failed ? FAILURE : SUCCESS;
          }
        }
      }
    }
  }
  // CHECKSTYLE:ON

  private InputStream openStream()
    throws IOException
  {
    if (this.gzip) {
      return new GZIPInputStream(Files.newInputStream(this.file_input));
    }
    return Files.newInputStream(this.file_input);
  }
}
