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
import com.io7m.jvgm.core.VGMHeader;
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
 * A command for dumping VGM files as plain text.
 */

@Parameters(
  commandNames = "dump",
  commandDescription = "Dump the given file as plain text")
public final class JVGMCommandDump extends JVGMCommandRoot
{
  private static final Logger LOG = LoggerFactory.getLogger(JVGMCommandDump.class);

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

  public JVGMCommandDump()
  {

  }

  @Override
  public Status execute()
    throws Exception
  {
    super.execute();

    final VGMParserVanilla parsers = new VGMParserVanilla();

    boolean failed = false;

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

          final VGMCommandType command = body_result.get();
          System.out.println(command.toString());

          if (command.type() == EOF) {
            return failed ? FAILURE : SUCCESS;
          }
        }
      }
    }
  }

  private InputStream openStream()
    throws IOException
  {
    if (this.gzip) {
      return new GZIPInputStream(Files.newInputStream(this.file_input));
    }
    return Files.newInputStream(this.file_input);
  }
}
