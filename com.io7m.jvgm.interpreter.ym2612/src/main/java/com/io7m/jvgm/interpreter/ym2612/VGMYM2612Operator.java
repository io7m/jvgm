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

import java.util.Objects;

/**
 * An operator.
 */

public final class VGMYM2612Operator
{
  private final int index;
  private final VGMYM2612Channel channel;
  private VGMYM2612Interpreter interpreter;
  private Logger log;
  private int pitch_multiply;
  private int pitch_detune;
  private int volume;
  private int rate_scale;
  private int rate_attack;
  private int amplitude_modulation_enabled;
  private int rate_decay_0;
  private int rate_decay_1;
  private int amplitude_secondary;
  private int rate_release;
  private boolean enabled;
  private int frequency_lsb;
  private int frequency_msb;
  private int frequency_octave;

  VGMYM2612Operator(
    final VGMYM2612Interpreter in_interpreter,
    final VGMYM2612Channel in_channel,
    final int in_index)
  {
    this.interpreter =
      Objects.requireNonNull(in_interpreter, "interpreter");
    this.channel =
      Objects.requireNonNull(in_channel, "channel");
    this.index = in_index;

    this.log = LoggerFactory.getLogger(
      new StringBuilder(128)
        .append(VGMYM2612Interpreter.class.getCanonicalName())
        .append(" [channel ")
        .append(this.channel.index())
        .append("] [operator ")
        .append(in_index)
        .append("]")
        .toString());
  }

  /**
   * @return A snapshot of the operator
   */

  public VGMYM2612OperatorSnapshot snapshot()
  {
    return VGMYM2612OperatorSnapshot.builder()
      .setEnvelopeDecay1Level(this.amplitude_secondary)
      .setEnvelopeDecay1Rate(this.rate_decay_0)
      .setEnvelopeDecay2Rate(this.rate_decay_1)
      .setEnvelopeRateAttack(this.rate_attack)
      .setEnvelopeReleaseRate(this.rate_release)
      .setIndex(this.index)
      .setPitchDetune(this.pitch_detune)
      .setPitchMultiply(this.pitch_multiply)
      .setVolumeInverse(this.volume)
      .build();
  }

  /**
   * Set the detune and pitch multiplication.
   *
   * @param value The packed detune and pitch multiplication
   */

  public void setDetuneAndMultiple(
    final int value)
  {
    final int mult = value & 0b1111;
    this.pitch_multiply = mult;
    final int detune = (value >>> 4) & 0b111;
    this.pitch_detune = detune;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setDetuneAndMultiple: multiply {} detune {}",
        Integer.valueOf(this.pitch_multiply),
        Integer.valueOf(this.pitch_detune));
    }
  }

  /**
   * Set the (inverse) volume
   *
   * @param value The inverse volume
   */

  public void setVolumeInverse(
    final int value)
  {
    this.volume = value;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setVolumeInverse: 0x{}",
        Integer.toUnsignedString(this.volume, 16));
    }
  }

  /**
   * Set the rate scaling and attack rate
   *
   * @param value The packed rate scaling and attack rate
   */

  public void setRateScalingAndAttackRate(
    final int value)
  {
    this.rate_scale = (value >>> 6) & 0b11;
    this.rate_attack = value & 0b11111;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setRateScalingAndAttackRate: scale {} attack {}",
        Integer.valueOf(this.rate_scale),
        Integer.valueOf(this.rate_attack));
    }
  }

  /**
   * Set the rate decay and amplitude modulation.
   *
   * @param value The packed rate decay and amplitude modulation
   */

  public void setRateDecayAndAmplitudeModulation(
    final int value)
  {
    this.amplitude_modulation_enabled = (value >>> 7) & 0b1;
    this.rate_decay_0 = value & 0b11111;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setRateDecayAndAmplitudeModulation: amp-mod {} decay-0 {}",
        Integer.valueOf(this.amplitude_modulation_enabled),
        Integer.valueOf(this.rate_decay_0));
    }
  }

  /**
   * Set the secondary rate decay.
   *
   * @param value The secondary rate decay
   */

  public void setRateDecaySecondary(
    final int value)
  {
    this.rate_decay_1 = value & 0b11111;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setRateDecaySecondary: decay-1 {}",
        Integer.valueOf(this.rate_decay_1));
    }
  }

  /**
   * Set the release rate and secondary amplitude.
   *
   * @param value The release rate and secondary amplitude
   */

  public void setRateReleaseAndSecondaryAmplitude(
    final int value)
  {
    this.amplitude_secondary = (value >>> 4) & 0b1111;
    this.rate_release = value & 0b1111;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setRateReleaseAndSecondaryAmplitude: amp-second {} release {}",
        Integer.valueOf(this.amplitude_secondary),
        Integer.valueOf(this.rate_release));
    }
  }

  /**
   * Set the least significant bit of the frequency
   *
   * @param value The least significant bit of the frequency
   */

  public void setFrequencyLSB(
    final int value)
  {
    this.frequency_lsb = value;

    if (this.log.isTraceEnabled()) {
      this.log.trace(
        "setFrequencyLSB: (special mode) lsb 0x{}",
        Integer.toUnsignedString(value, 16));
    }
  }

  /**
   * Set the most significant bit of the frequency
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
        "setFrequencyMSB: (special mode) msb 0x{} octave {}",
        Integer.toUnsignedString(value, 16),
        Integer.valueOf(this.frequency_octave));
    }
  }

  /**
   * Enable/disable the operator.
   *
   * @param e {@code true} iff the operator is enabled
   */

  public void setEnabled(
    final boolean e)
  {
    this.enabled = e;

    if (this.log.isTraceEnabled()) {
      this.log.trace("setEnabled: {}", Boolean.valueOf(this.enabled));
    }
  }
}
