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
import com.io7m.jvgm.interpreter.ym2612.VGMYM2612Callbacks;
import com.io7m.jvgm.interpreter.ym2612.VGMYM2612ChannelSnapshot;
import com.io7m.jvgm.interpreter.ym2612.VGMYM2612Interpreter;
import com.io7m.jvgm.interpreter.ym2612.VGMYM2612OperatorSnapshot;
import com.io7m.jvgm.parser.api.VGMParseError;
import com.io7m.jvgm.parser.api.VGMParserBodyType;
import com.io7m.jvgm.parser.api.VGMParserHeaderType;
import com.io7m.jvgm.parser.vanilla.VGMParserVanilla;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

  @Parameter(
    names = "--output",
    required = true,
    description = "The output directory")
  private Path output_directory;

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

    return command.type() == EOF;
  }

  private static final class ChannelState
  {
    private int count;
    private VGMYM2612ChannelSnapshot snapshot;

    ChannelState()
    {

    }
  }

  private void dumpPreset(
    final Map<Integer, ChannelState> channel_states,
    final VGMYM2612Interpreter interpreter)
  {
    LOG.debug("channels might need snapshots");

    for (int index = 0; index < 6; ++index) {
      final ChannelState channel_state = channel_states.get(Integer.valueOf(index));
      final VGMYM2612ChannelSnapshot new_snapshot = interpreter.channel(index).snapshot();
      if (!Objects.equals(new_snapshot, channel_state.snapshot)) {
        channel_state.snapshot = new_snapshot;
        ++channel_state.count;

        final String name =
          String.format(
            "ch%02d-%04d.txt",
            Integer.valueOf(index),
            Integer.valueOf(channel_state.count));

        final Path path = this.output_directory.resolve(name);
        LOG.debug("created snapshot: {}", path);

        try {
          Files.createDirectories(this.output_directory);
          try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            dumpChannel(writer, new_snapshot);
          } catch (final IOException e) {
            LOG.error("i/o error: {}: ", path, e);
          }
        } catch (final IOException e) {
          LOG.error("i/o error: {}: ", this.output_directory, e);
        }
      }
    }
  }

  private static void dumpChannel(
    final BufferedWriter writer,
    final VGMYM2612ChannelSnapshot channel)
    throws IOException
  {
    final String separator = System.lineSeparator();

    writer.append("[channel ")
      .append(separator);

    writer.append("  [index ")
      .append(Integer.toUnsignedString(channel.index()))
      .append("]")
      .append(separator);
    writer.append("  [algorithm ")
      .append(Integer.toUnsignedString(channel.algorithm()))
      .append("]")
      .append(separator);
    writer.append("  [feedback ")
      .append(Integer.toUnsignedString(channel.feedback()))
      .append("]")
      .append(separator);

    for (int op_index = 0; op_index < 4; ++op_index) {
      final VGMYM2612OperatorSnapshot operator = channel.operator(op_index);
      writer.append("  [op ");
      writer.append(separator);

      writer.append("    [index ");
      writer.append(Integer.toUnsignedString(op_index));
      writer.append("]");
      writer.append(separator);

      {
        writer.append("    [envelope ");
        writer.append(separator);

        {
          writer.append("      [attack-rate ");
          writer.append(Integer.toUnsignedString(operator.envelopeRateAttack()));
          writer.append("]");
          writer.append(separator);

          writer.append("      [decay-1-level ");
          writer.append(Integer.toUnsignedString(operator.envelopeDecay1Level()));
          writer.append("]");
          writer.append(separator);

          writer.append("      [decay-1-rate ");
          writer.append(Integer.toUnsignedString(operator.envelopeDecay1Rate()));
          writer.append("]");
          writer.append(separator);

          writer.append("      [decay-2-rate ");
          writer.append(Integer.toUnsignedString(operator.envelopeDecay2Rate()));
          writer.append("]");
          writer.append(separator);

          writer.append("      [release-rate ");
          writer.append(Integer.toUnsignedString(operator.envelopeReleaseRate()));
          writer.append("]");
        }

        writer.append("]");
        writer.append(separator);
      }

      writer.append("    [detune ");
      writer.append(Integer.toUnsignedString(operator.pitchDetune()));
      writer.append("]");
      writer.append(separator);

      writer.append("    [multiply ");
      writer.append(Integer.toUnsignedString(operator.pitchMultiply()));
      writer.append("]");
      writer.append(separator);

      writer.append("    [volume-inverse ");
      writer.append(Integer.toUnsignedString(operator.volumeInverse()));
      writer.append("]");

      writer.append("]");
      writer.append(separator);
    }

    writer.append("]");
    writer.append(separator);
    writer.append(separator);
  }

  @Override
  public Status execute()
    throws Exception
  {
    super.execute();

    final VGMParserVanilla parsers = new VGMParserVanilla();

    boolean failed = false;

    final MutableBoolean changed = new MutableBoolean(false);
    final Map<Integer, ChannelState> channel_states = new HashMap<>(6);
    for (int index = 0; index < 6; ++index) {
      channel_states.put(Integer.valueOf(index), new ChannelState());
    }

    final VGMYM2612Interpreter interpreter =
      new VGMYM2612Interpreter(
        VGMYM2612Callbacks.builder()
          .setOnInstructionReceived(
            (inter, preset_changed) ->
              this.maybeDumpPreset(
                channel_states,
                changed,
                inter,
                preset_changed))
          .build());

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
            this.dumpPreset(channel_states, interpreter);
            return failed ? FAILURE : SUCCESS;
          }
        }
      }
    }
  }

  private void maybeDumpPreset(
    final Map<Integer, ChannelState> previous_channels,
    final MutableBoolean changed,
    final VGMYM2612Interpreter inter,
    final boolean preset_changed)
  {
    if (preset_changed) {
      if (!changed.booleanValue()) {
        this.dumpPreset(previous_channels, inter);
      }
    }
    changed.setValue(preset_changed);
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
