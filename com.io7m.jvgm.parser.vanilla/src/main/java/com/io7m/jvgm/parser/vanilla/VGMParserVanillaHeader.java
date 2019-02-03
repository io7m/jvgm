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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
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
  private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  private final Path path;
  private final SwappedDataInputStream data_stream;
  private final byte[] buffer;
  private final VGMHeader.Builder header_builder;
  private boolean header_done;

  VGMParserVanillaHeader(
    final Path in_path,
    final InputStream in_stream)
  {
    super(in_path, new CountingInputStream(in_stream));

    this.path = Objects.requireNonNull(in_path, "Path");
    this.data_stream = new SwappedDataInputStream(this.countingStream());
    this.header_done = false;
    this.buffer = new byte[4];
    this.header_builder = VGMHeader.builder();
  }

  private static String bytesToHex(
    final byte[] bytes)
  {
    final char[] chars = new char[bytes.length * 2];
    for (int index = 0; index < bytes.length; ++index) {
      final int v = bytes[index] & 0xFF;
      chars[index * 2] = HEX_ARRAY[v >>> 4];
      chars[index * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return String.valueOf(chars);
  }

  private static void parseHeaderDataOffset(
    final SwappedDataInputStream data_stream,
    final VGMHeader.Builder header_builder)
    throws IOException
  {
    final long o = Integer.toUnsignedLong(data_stream.readInt());
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

    header_builder.setDataOffset(offset_data);
  }

  private static void parseHeaderLoopOffset(
    final SwappedDataInputStream data_stream,
    final VGMHeader.Builder header_builder)
    throws IOException
  {
    final long o = Integer.toUnsignedLong(data_stream.readInt());
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

    header_builder.setLoopOffset(offset_loop);
  }

  private static void parseHeaderGD3Offset(
    final SwappedDataInputStream data_stream,
    final VGMHeader.Builder header_builder)
    throws IOException
  {
    final long o = Integer.toUnsignedLong(data_stream.readInt());
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

    header_builder.setOffsetGD3(offset_gd3);
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
          version -> this.parseHeaderVersioned())));
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

  private Validation<Seq<VGMParseError>, Tuple2<VGMParserBodyType, VGMHeader>> parseHeaderVersioned()
  {
    final CountingInputStream counting_stream = this.countingStream();

    {
      final long count = counting_stream.getByteCount();
      Invariants.checkInvariantL(
        count,
        count == 12L,
        position -> "Position must be 12");
    }

    try {
      this.header_builder.setChipSN76489Clock(
        Integer.toUnsignedLong(this.data_stream.readInt()));
      this.header_builder.setChipYM2413Clock(
        Integer.toUnsignedLong(this.data_stream.readInt()));

      parseHeaderGD3Offset(this.data_stream, this.header_builder);

      this.header_builder.setSampleCount(
        Integer.toUnsignedLong(this.data_stream.readInt()));

      parseHeaderLoopOffset(this.data_stream, this.header_builder);

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

      parseHeaderDataOffset(this.data_stream, this.header_builder);

      this.header_builder.setChipSegaPCMClock(
        Integer.toUnsignedLong(this.data_stream.readInt()));
      this.header_builder.setChipSegaPCMInterfaceRegister(
        Integer.toUnsignedLong(this.data_stream.readInt()));

      {
        final long count = counting_stream.getByteCount();
        Invariants.checkInvariantL(
          count,
          count == 64L,
          position -> "Position must be 64");
      }

    } catch (final IOException e) {
      return this.errorExceptionV(e);
    }

    final VGMHeader header = this.header_builder.build();
    final long header_data_offset = header.dataOffset();

    {
      final long count = counting_stream.getByteCount();
      if (Long.compareUnsigned(count, header_data_offset) < 0) {
        try {
          this.data_stream.skip(header_data_offset - count);
        } catch (final IOException e) {
          return this.errorExceptionV(e);
        }
      }
    }

    final VGMParserBodyType body =
      new VGMParserVanillaBody(
        header,
        this.path,
        counting_stream,
        this.data_stream);

    {
      final long count = counting_stream.getByteCount();
      Invariants.checkInvariantL(
        count,
        count == header_data_offset,
        position -> "Position must be at " + header_data_offset);
    }

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
      final String separator = System.lineSeparator();
      return this.errorV(
        new StringBuilder(128)
          .append("Unsupported format version.")
          .append(separator)
          .append("  Position: ")
          .append(this.countingStream().getByteCount())
          .append(separator)
          .append("  Received: ")
          .append(Integer.toUnsignedString(header_version, 16))
          .append(separator)
          .append("  Expected: One of ")
          .append(
            VGMParserVanillaSupported.SUPPORTED
              .map(v -> Integer.toUnsignedString(v.version(), 16))
              .collect(Collectors.joining(" ")))
          .append(separator)
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
      final String separator = System.lineSeparator();
      return this.errorV(
        new StringBuilder(128)
          .append("Bad magic number.")
          .append(separator)
          .append("  Position: ")
          .append(this.countingStream().getByteCount())
          .append(separator)
          .append("  Received: ")
          .append(bytesToHex(this.buffer))
          .append(separator)
          .append("  Expected: ")
          .append(bytesToHex(MAGIC))
          .append(separator)
          .toString());
    }

    return Validation.valid(Unit.unit());
  }

  @Override
  public void close()
  {

  }
}
