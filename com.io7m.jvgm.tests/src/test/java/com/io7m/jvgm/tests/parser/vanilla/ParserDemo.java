package com.io7m.jvgm.tests.parser.vanilla;

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
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

public final class ParserDemo
{
  private static final Logger LOG = LoggerFactory.getLogger(ParserDemo.class);

  private ParserDemo()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    boolean gzip = false;
    final Path path;
    if (args.length == 2) {
      gzip = true;
      path = Paths.get(args[1]);
    } else if (args.length == 1) {
      path = Paths.get(args[0]);
    } else {
      throw new IllegalArgumentException("Expected two arguments");
    }

    try (final InputStream stream = openStream(path, gzip)) {
      try (final VGMParserHeaderType parser =
             new VGMParserVanilla().open(path, stream)) {
        final Validation<Seq<VGMParseError>, Tuple2<VGMParserBodyType, VGMHeader>> r =
          parser.parse();
        if (r.isInvalid()) {
          r.getError().forEach(e -> {
            LOG.error(
              "{}:{}: {}",
              e.path().get(),
              Long.valueOf(e.offset()),
              e.message());
            e.exception().ifPresent(ex -> LOG.error("", ex));
          });
          System.exit(1);
        }

        final Tuple2<VGMParserBodyType, VGMHeader> pair = r.get();
        final VGMHeader header = pair._2;
        final VGMParserBodyType body = pair._1;
        final VGMYM2612Interpreter interp = new VGMYM2612Interpreter();

        while (true) {
          final Validation<Seq<VGMParseError>, VGMCommandType> body_r = body.parse();
          if (body_r.isValid()) {
            final VGMCommandType cmd = body_r.get();
            LOG.trace("command: {}", cmd);

            switch (cmd.type()) {
              case EOF: {
                System.exit(0);
              }
              case GAME_GEAR_PSG_STEREO_WRITE: {
                break;
              }
              case PSG_WRITE: {
                break;
              }
              case YM2413_WRITE: {
                break;
              }

              case YM2612_WRITE_PORT_0: {
                final VGMCommandYM2612WritePort0 e =
                  (VGMCommandYM2612WritePort0) cmd;
                interp.writeRegisterPort0(
                  (int) e.register() & 0xff,
                  (int) e.value() & 0xff);
                break;
              }

              case YM2612_WRITE_PORT_1: {
                final VGMCommandYM2612WritePort1 e =
                  (VGMCommandYM2612WritePort1) cmd;
                interp.writeRegisterPort1(
                  (int) e.register() & 0xff,
                  (int) e.value() & 0xff);
                break;
              }
              case YM2612_SEEK_PCM: {
                break;
              }
              case WAIT_LONG: {
                break;
              }
              case END_OF_SOUND_DATA: {
                break;
              }
              case DATA_BLOCK: {
                break;
              }
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

          } else {
            body_r.getError().forEach(e -> {
              LOG.error(
                "{}:{}: {}",
                e.path().get(),
                Long.valueOf(e.offset()),
                e.message());
              e.exception().ifPresent(ex -> LOG.error("", ex));
            });
            System.exit(1);
          }
        }
      }
    }
  }

  private static InputStream openStream(
    final Path path,
    final boolean gzip)
    throws IOException
  {
    if (gzip) {
      return new GZIPInputStream(Files.newInputStream(path));
    }
    return Files.newInputStream(path);
  }
}
