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

package com.io7m.jvgm.interpreter.ym2612;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * A channel.
 */

public final class VGMYM2612Channel
{
  private final int index;
  private final HashMap<Integer, VGMYM2612Operator> operators;
  private final Logger log;
  private VGMYM2612Interpreter interpreter;
  private int frequency_lsb;
  private int frequency_msb;
  private int frequency_octave;
  private int feedback;
  private int algorithm;
  private boolean stereo_left_enabled;
  private boolean stereo_right_enabled;
  private int lfo_amplitude_sensitivity;
  private int lfo_frequency_sensitivity;

  VGMYM2612Channel(
    final VGMYM2612Interpreter in_interpreter,
    final int in_index)
  {
    this.interpreter =
      Objects.requireNonNull(in_interpreter, "interpreter");
    this.index = in_index;

    this.operators = new HashMap<>(6);
    for (int op_index = 0; op_index < 4; ++op_index) {
      this.operators.put(
        Integer.valueOf(op_index), new VGMYM2612Operator(
          in_interpreter,
          this, op_index));
    }

    this.log = LoggerFactory.getLogger(
      new StringBuilder(128)
        .append(VGMYM2612Interpreter.class.getCanonicalName())
        .append(" [channel ")
        .append(this.index)
        .append("]")
        .toString());
  }

  /**
   * @param in_index The operator index
   *
   * @return The operator with the given index
   */

  public VGMYM2612Operator operator(
    final int in_index)
  {
    return Optional.ofNullable(this.operators.get(Integer.valueOf(in_index)))
      .orElseThrow(() -> new IllegalArgumentException("Invalid operator index: " + in_index));
  }

  /**
   * @return The channel index
   */

  public int index()
  {
    return this.index;
  }

  /**
   * Set the frequency.
   *
   * @param value The least significant bit of the frequency
   */

  public void setFrequencyLSB(final int value)
  {
    this.frequency_lsb = value;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setFrequencyLSB: lsb 0x{}",
        Integer.toUnsignedString(value, 16));
    }
  }

  /**
   * Set the frequency.
   *
   * @param value The most significant bit of the frequency
   */

  public void setFrequencyMSB(
    final int value)
  {
    this.frequency_msb = value & 0b111;
    this.frequency_octave = (value >>> 3) & 0b111;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setFrequencyMSB: msb 0x{} octave {}",
        Integer.toUnsignedString(value, 16),
        Integer.valueOf(this.frequency_octave));
    }
  }

  /**
   * Set the algorithm and feedback.
   *
   * @param value The packed algorithm and feedback
   */

  public void setAlgorithmAndFeedback(
    final int value)
  {
    this.feedback = (value >>> 3) & 0b111;
    this.algorithm = value & 0b111;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setAlgorithmAndFeedback: feedback {} algorithm {}",
        Integer.valueOf(this.feedback),
        Integer.valueOf(this.algorithm));
    }
  }

  /**
   * Set the pan and LFO sensitivity
   *
   * @param value The packed pan and LFO sensitivity
   */

  public void setStereoAndLFOSensitivity(
    final int value)
  {
    this.stereo_left_enabled = ((value >>> 7) & 0b1) == 0b1;
    this.stereo_right_enabled = ((value >>> 6) & 0b1) == 0b1;
    this.lfo_amplitude_sensitivity = (value >>> 3) & 0b111;
    this.lfo_frequency_sensitivity = value & 0b11;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setStereoAndLFOSensitivity: L/R ({}/{}) amp-sensitivity {} freq-sensitivity {}",
        Boolean.valueOf(this.stereo_left_enabled),
        Boolean.valueOf(this.stereo_right_enabled),
        Integer.valueOf(this.lfo_amplitude_sensitivity),
        Integer.valueOf(this.lfo_frequency_sensitivity));
    }
  }

  /**
   * @return A snapshot of the current channel state
   */

  public VGMYM2612ChannelSnapshot snapshot()
  {
    return VGMYM2612ChannelSnapshot.builder()
      .setAlgorithm(this.algorithm)
      .setFeedback(this.feedback)
      .setIndex(this.index)
      .putOperators(0, this.operator(0).snapshot())
      .putOperators(1, this.operator(1).snapshot())
      .putOperators(2, this.operator(2).snapshot())
      .putOperators(3, this.operator(3).snapshot())
      .build();
  }
}
