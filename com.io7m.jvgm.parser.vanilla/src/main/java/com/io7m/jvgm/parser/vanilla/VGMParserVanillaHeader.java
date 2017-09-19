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

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jvgm.core.VGMHeader;
import com.io7m.jvgm.core.VGMVersion;
import com.io7m.jvgm.parser.api.VGMParseError;
import com.io7m.jvgm.parser.api.VGMParserBodyType;
import com.io7m.jvgm.parser.api.VGMParserHeaderType;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.input.SwappedDataInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

final class VGMParserVanillaHeader
  extends VGMParserVanillaBase implements VGMParserHeaderType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(VGMParserVanillaHeader.class);

  private static final byte[] MAGIC = {
    (byte) 0x56,
    (byte) 0x67,
    (byte) 0x6d,
    (byte) 0x20,
  };

  private final Path path;
  private final InputStream stream;
  private final SwappedDataInputStream data_stream;
  private final byte[] buffer;
  private final VGMHeader.Builder header_builder;
  private boolean header_done;

  VGMParserVanillaHeader(
    final Path in_path,
    final InputStream in_stream)
  {
    super(in_path, new CountingInputStream(in_stream));

    this.path = NullCheck.notNull(in_path, "Path");
    this.stream = NullCheck.notNull(in_stream, "Stream");
    this.data_stream = new SwappedDataInputStream(this.countingStream());
    this.header_done = false;
    this.buffer = new byte[4];
    this.header_builder = VGMHeader.builder();
  }

  @Override
  public Validation<Seq<VGMParseError>, Tuple2<VGMParserBodyType, VGMHeader>> parse()
  {
    if (this.header_done) {
      throw new IllegalStateException("Parser has already executed");
    }

    try {
      return this.parseHeader();
    } finally {
      this.header_done = true;
    }
  }

  private Validation<Seq<VGMParseError>, Tuple2<VGMParserBodyType, VGMHeader>> parseHeader()
  {
    return this.parseHeaderMagicNumber().flatMap(
      ignore0 -> this.parseHeaderEOFOffset().flatMap(
        ignore1 -> this.parseHeaderVersionNumber().flatMap(
          version -> this.parseHeaderVersioned(version.intValue()))));
  }

  private Validation<Seq<VGMParseError>, Unit> parseHeaderEOFOffset()
  {
    Invariants.checkInvariantL(
      this.countingStream().getByteCount(),
      this.countingStream().getByteCount() == 4L,
      position -> "Position must be 4");

    try {
      final int offset = this.data_stream.readInt();
      final long abs_offset = Integer.toUnsignedLong(offset) + 4L;
      this.header_builder.setEofOffset(abs_offset);

      if (LOG.isTraceEnabled()) {
        LOG.trace("eof offset: {}", Long.valueOf(abs_offset));
      }

    } catch (final IOException e) {
      return this.errorExceptionV(e);
    }

    return Validation.valid(Unit.unit());
  }

  private Validation<Seq<VGMParseError>, Tuple2<VGMParserBodyType, VGMHeader>> parseHeaderVersioned(
    final int version)
  {
    Invariants.checkInvariantL(
      this.countingStream().getByteCount(),
      this.countingStream().getByteCount() == 12L,
      position -> "Position must be 12");

    try {
      this.header_builder.setChipSN76489Clock(
        Integer.toUnsignedLong(this.data_stream.readInt()));
      this.header_builder.setChipYM2413Clock(
        Integer.toUnsignedLong(this.data_stream.readInt()));

      {
        final long o = Integer.toUnsignedLong(this.data_stream.readInt());
        final long offset_gd3;
        if (o > 0L) {
          offset_gd3 = o + 20L;
        } else {
          offset_gd3 = o;
        }

        if (LOG.isTraceEnabled()) {
          LOG.trace(
            "GD3 offset: {} ({})",
            Long.valueOf(o),
            Long.valueOf(offset_gd3));
        }

        this.header_builder.setOffsetGD3(offset_gd3);
      }

      this.header_builder.setSampleCount(
        Integer.toUnsignedLong(this.data_stream.readInt()));

      {
        final long o = Integer.toUnsignedLong(this.data_stream.readInt());
        final long offset_loop;
        if (o > 0L) {
          offset_loop = o + 28L;
        } else {
          offset_loop = o;
        }

        if (LOG.isTraceEnabled()) {
          LOG.trace(
            "Loop offset: {} ({})",
            Long.valueOf(o),
            Long.valueOf(offset_loop));
        }

        this.header_builder.setLoopOffset(offset_loop);
      }

      this.header_builder.setLoopSampleCount(
        Integer.toUnsignedLong(this.data_stream.readInt()));
      this.header_builder.setRate(
        Integer.toUnsignedLong(this.data_stream.readInt()));

      this.header_builder.setChipSN76489Feedback(
        this.data_stream.readUnsignedShort());
      this.header_builder.setChipSN76489ShiftRegisterWidth(
        this.data_stream.readUnsignedByte());
      this.header_builder.setChipSN76489Flags(
        this.data_stream.readUnsignedByte());

      this.header_builder.setChipYM2612Clock(
        Integer.toUnsignedLong(this.data_stream.readInt()));
      this.header_builder.setChipYM2151Clock(
        Integer.toUnsignedLong(this.data_stream.readInt()));

      {
        final long o = Integer.toUnsignedLong(this.data_stream.readInt());
        final long offset_data;
        if (o > 0L) {
          offset_data = o + 52L;
        } else {
          offset_data = 12L;
        }

        if (LOG.isTraceEnabled()) {
          LOG.trace(
            "Data offset: {} ({})",
            Long.valueOf(o),
            Long.valueOf(offset_data));
        }

        this.header_builder.setDataOffset(offset_data);
      }

      this.header_builder.setChipSegaPCMClock(
        Integer.toUnsignedLong(this.data_stream.readInt()));
      this.header_builder.setChipSegaPCMInterfaceRegister(
        Integer.toUnsignedLong(this.data_stream.readInt()));

      Invariants.checkInvariantL(
        this.countingStream().getByteCount(),
        this.countingStream().getByteCount() == 64L,
        position -> "Position must be 64");

    } catch (final IOException e) {
      return this.errorExceptionV(e);
    }

    final VGMHeader header =
      this.header_builder.build();

    if (Long.compareUnsigned(
      this.countingStream().getByteCount(), header.dataOffset()) < 0) {
      try {
        this.data_stream.skip(
          header.dataOffset() - this.countingStream().getByteCount());
      } catch (final IOException e) {
        return this.errorExceptionV(e);
      }
    }

    final VGMParserBodyType body =
      new VGMParserVanillaBody(
        header,
        this.path,
        this.stream,
        this.countingStream(),
        this.data_stream);

    Invariants.checkInvariantL(
      this.countingStream().getByteCount(),
      this.countingStream().getByteCount() == header.dataOffset(),
      position -> "Position must be at " + header.dataOffset());

    return Validation.valid(Tuple.of(body, header));
  }

  private Validation<Seq<VGMParseError>, Integer> parseHeaderVersionNumber()
  {
    final int header_version;

    try {
      header_version = this.data_stream.readInt();
    } catch (final IOException e) {
      return this.errorExceptionV(e);
    }

    if (!VGMParserVanillaSupported.SUPPORTED.contains(
      VGMVersion.of(header_version))) {
      return this.errorV(
        new StringBuilder(128)
          .append("Unsupported format version.")
          .append(System.lineSeparator())
          .append("  Position: ")
          .append(this.countingStream().getByteCount())
          .append(System.lineSeparator())
          .append("  Received: ")
          .append(Integer.toUnsignedString(header_version, 16))
          .append(System.lineSeparator())
          .append("  Expected: One of ")
          .append(
            VGMParserVanillaSupported.SUPPORTED
              .map(v -> Integer.toUnsignedString(v.version(), 16))
              .collect(Collectors.joining(" ")))
          .append(System.lineSeparator())
          .toString());
    }

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "header version: 0x{}",
        Integer.toUnsignedString(header_version, 16));
    }

    this.header_builder.setVersion(Integer.toUnsignedLong(header_version));
    return Validation.valid(Integer.valueOf(header_version));
  }

  private Validation<Seq<VGMParseError>, Unit> parseHeaderMagicNumber()
  {
    try {
      this.data_stream.readFully(this.buffer);
    } catch (final IOException e) {
      return this.errorExceptionV(e);
    }

    if (!Arrays.equals(MAGIC, this.buffer)) {
      return this.errorV(
        new StringBuilder(128)
          .append("Bad magic number.")
          .append(System.lineSeparator())
          .append("  Position: ")
          .append(this.countingStream().getByteCount())
          .append(System.lineSeparator())
          .append("  Received: ")
          .append(DatatypeConverter.printHexBinary(this.buffer))
          .append(System.lineSeparator())
          .append("  Expected: ")
          .append(DatatypeConverter.printHexBinary(MAGIC))
          .append(System.lineSeparator())
          .toString());
    }

    return Validation.valid(Unit.unit());
  }

  @Override
  public void close()
    throws IOException
  {

  }
}
