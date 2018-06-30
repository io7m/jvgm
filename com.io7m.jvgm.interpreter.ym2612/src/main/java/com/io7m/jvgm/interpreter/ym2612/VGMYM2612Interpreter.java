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
import java.util.Optional;

/**
 * A YM2612 state interpreter.
 */

public final class VGMYM2612Interpreter
{
  private static final Logger LOG =
    LoggerFactory.getLogger(VGMYM2612Interpreter.class);

  private static final int LFO_ENABLE = 0x22;
  private static final int TIMER_B = 0x26;
  private static final int TIMERS_AND_CHANNEL_3_6_MODE = 0x27;
  private static final int KEY_ON_OFF = 0x28;
  private static final int DAC_DATA = 0x2A;
  private static final int DAC_ENABLE = 0x2b;

  private static final int CHANNEL_0_ALGORITHM_AND_FEEDBACK = 0xB0;
  private static final int CHANNEL_1_ALGORITHM_AND_FEEDBACK = 0xB1;
  private static final int CHANNEL_2_ALGORITHM_AND_FEEDBACK = 0xB2;

  private static final int CHANNEL_3_ALGORITHM_AND_FEEDBACK = 0xB0;
  private static final int CHANNEL_4_ALGORITHM_AND_FEEDBACK = 0xB1;
  private static final int CHANNEL_5_ALGORITHM_AND_FEEDBACK = 0xB2;

  private static final int CHANNEL_0_FREQUENCY_LSB = 0xA0;
  private static final int CHANNEL_0_FREQUENCY_MSB = 0xA4;

  private static final int CHANNEL_1_FREQUENCY_LSB = 0xA1;
  private static final int CHANNEL_1_FREQUENCY_MSB = 0xA5;

  private static final int CHANNEL_2_FREQUENCY_LSB = 0xA2;
  private static final int CHANNEL_2_FREQUENCY_MSB = 0xA6;

  private static final int CHANNEL_2_FREQUENCY_LSB_OPERATOR_1 = 0xA8;
  private static final int CHANNEL_2_FREQUENCY_MSB_OPERATOR_1 = 0xAC;

  private static final int CHANNEL_2_FREQUENCY_LSB_OPERATOR_2 = 0xA9;
  private static final int CHANNEL_2_FREQUENCY_MSB_OPERATOR_2 = 0xAD;

  private static final int CHANNEL_2_FREQUENCY_LSB_OPERATOR_3 = 0xAA;
  private static final int CHANNEL_2_FREQUENCY_MSB_OPERATOR_3 = 0xAE;

  private static final int CHANNEL_3_FREQUENCY_LSB = 0xA0;
  private static final int CHANNEL_3_FREQUENCY_MSB = 0xA4;

  private static final int CHANNEL_4_FREQUENCY_LSB = 0xA1;
  private static final int CHANNEL_4_FREQUENCY_MSB = 0xA5;

  private static final int CHANNEL_5_FREQUENCY_LSB = 0xA2;
  private static final int CHANNEL_5_FREQUENCY_MSB = 0xA6;

  private static final int CHANNEL_5_FREQUENCY_LSB_OPERATOR_1 = 0xA8;
  private static final int CHANNEL_5_FREQUENCY_MSB_OPERATOR_1 = 0xAC;

  private static final int CHANNEL_5_FREQUENCY_LSB_OPERATOR_2 = 0xA9;
  private static final int CHANNEL_5_FREQUENCY_MSB_OPERATOR_2 = 0xAD;

  private static final int CHANNEL_5_FREQUENCY_LSB_OPERATOR_3 = 0xAA;
  private static final int CHANNEL_5_FREQUENCY_MSB_OPERATOR_3 = 0xAE;

  private static final int CHANNEL_0_OPERATOR_0_DETUNE_MULTIPLE = 0x30;
  private static final int CHANNEL_0_OPERATOR_1_DETUNE_MULTIPLE = 0x34;
  private static final int CHANNEL_0_OPERATOR_2_DETUNE_MULTIPLE = 0x38;
  private static final int CHANNEL_0_OPERATOR_3_DETUNE_MULTIPLE = 0x3C;

  private static final int CHANNEL_1_OPERATOR_0_DETUNE_MULTIPLE = 0x31;
  private static final int CHANNEL_1_OPERATOR_1_DETUNE_MULTIPLE = 0x35;
  private static final int CHANNEL_1_OPERATOR_2_DETUNE_MULTIPLE = 0x39;
  private static final int CHANNEL_1_OPERATOR_3_DETUNE_MULTIPLE = 0x3D;

  private static final int CHANNEL_2_OPERATOR_0_DETUNE_MULTIPLE = 0x32;
  private static final int CHANNEL_2_OPERATOR_1_DETUNE_MULTIPLE = 0x36;
  private static final int CHANNEL_2_OPERATOR_2_DETUNE_MULTIPLE = 0x3A;
  private static final int CHANNEL_2_OPERATOR_3_DETUNE_MULTIPLE = 0x3E;

  private static final int CHANNEL_0_OPERATOR_0_VOLUME_INVERSE = 0x40;
  private static final int CHANNEL_0_OPERATOR_1_VOLUME_INVERSE = 0x44;
  private static final int CHANNEL_0_OPERATOR_2_VOLUME_INVERSE = 0x48;
  private static final int CHANNEL_0_OPERATOR_3_VOLUME_INVERSE = 0x4C;

  private static final int CHANNEL_1_OPERATOR_0_VOLUME_INVERSE = 0x41;
  private static final int CHANNEL_1_OPERATOR_1_VOLUME_INVERSE = 0x45;
  private static final int CHANNEL_1_OPERATOR_2_VOLUME_INVERSE = 0x49;
  private static final int CHANNEL_1_OPERATOR_3_VOLUME_INVERSE = 0x4D;

  private static final int CHANNEL_2_OPERATOR_0_VOLUME_INVERSE = 0x42;
  private static final int CHANNEL_2_OPERATOR_1_VOLUME_INVERSE = 0x46;
  private static final int CHANNEL_2_OPERATOR_2_VOLUME_INVERSE = 0x4A;
  private static final int CHANNEL_2_OPERATOR_3_VOLUME_INVERSE = 0x4E;

  private static final int CHANNEL_0_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE = 0x50;
  private static final int CHANNEL_0_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE = 0x54;
  private static final int CHANNEL_0_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE = 0x58;
  private static final int CHANNEL_0_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE = 0x5C;

  private static final int CHANNEL_1_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE = 0x51;
  private static final int CHANNEL_1_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE = 0x55;
  private static final int CHANNEL_1_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE = 0x59;
  private static final int CHANNEL_1_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE = 0x5D;

  private static final int CHANNEL_2_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE = 0x52;
  private static final int CHANNEL_2_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE = 0x56;
  private static final int CHANNEL_2_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE = 0x5A;
  private static final int CHANNEL_2_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE = 0x5E;

  private static final int CHANNEL_0_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x60;
  private static final int CHANNEL_0_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x64;
  private static final int CHANNEL_0_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x68;
  private static final int CHANNEL_0_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x6C;

  private static final int CHANNEL_1_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x61;
  private static final int CHANNEL_1_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x65;
  private static final int CHANNEL_1_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x69;
  private static final int CHANNEL_1_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x6D;

  private static final int CHANNEL_2_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x62;
  private static final int CHANNEL_2_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x66;
  private static final int CHANNEL_2_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x6A;
  private static final int CHANNEL_2_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x6E;

  private static final int CHANNEL_0_OPERATOR_0_RATE_DECAY_SECONDARY = 0x70;
  private static final int CHANNEL_0_OPERATOR_1_RATE_DECAY_SECONDARY = 0x74;
  private static final int CHANNEL_0_OPERATOR_2_RATE_DECAY_SECONDARY = 0x78;
  private static final int CHANNEL_0_OPERATOR_3_RATE_DECAY_SECONDARY = 0x7C;

  private static final int CHANNEL_1_OPERATOR_0_RATE_DECAY_SECONDARY = 0x71;
  private static final int CHANNEL_1_OPERATOR_1_RATE_DECAY_SECONDARY = 0x75;
  private static final int CHANNEL_1_OPERATOR_2_RATE_DECAY_SECONDARY = 0x79;
  private static final int CHANNEL_1_OPERATOR_3_RATE_DECAY_SECONDARY = 0x7D;

  private static final int CHANNEL_2_OPERATOR_0_RATE_DECAY_SECONDARY = 0x72;
  private static final int CHANNEL_2_OPERATOR_1_RATE_DECAY_SECONDARY = 0x76;
  private static final int CHANNEL_2_OPERATOR_2_RATE_DECAY_SECONDARY = 0x7A;
  private static final int CHANNEL_2_OPERATOR_3_RATE_DECAY_SECONDARY = 0x7E;

  private static final int CHANNEL_0_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x80;
  private static final int CHANNEL_0_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x84;
  private static final int CHANNEL_0_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x88;
  private static final int CHANNEL_0_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x8C;

  private static final int CHANNEL_1_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x81;
  private static final int CHANNEL_1_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x85;
  private static final int CHANNEL_1_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x89;
  private static final int CHANNEL_1_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x8D;

  private static final int CHANNEL_2_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x82;
  private static final int CHANNEL_2_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x86;
  private static final int CHANNEL_2_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x8A;
  private static final int CHANNEL_2_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x8E;

  private static final int CHANNEL_3_OPERATOR_0_DETUNE_MULTIPLE = 0x30;
  private static final int CHANNEL_3_OPERATOR_1_DETUNE_MULTIPLE = 0x34;
  private static final int CHANNEL_3_OPERATOR_2_DETUNE_MULTIPLE = 0x38;
  private static final int CHANNEL_3_OPERATOR_3_DETUNE_MULTIPLE = 0x3C;

  private static final int CHANNEL_4_OPERATOR_0_DETUNE_MULTIPLE = 0x31;
  private static final int CHANNEL_4_OPERATOR_1_DETUNE_MULTIPLE = 0x35;
  private static final int CHANNEL_4_OPERATOR_2_DETUNE_MULTIPLE = 0x39;
  private static final int CHANNEL_4_OPERATOR_3_DETUNE_MULTIPLE = 0x3D;

  private static final int CHANNEL_5_OPERATOR_0_DETUNE_MULTIPLE = 0x32;
  private static final int CHANNEL_5_OPERATOR_1_DETUNE_MULTIPLE = 0x36;
  private static final int CHANNEL_5_OPERATOR_2_DETUNE_MULTIPLE = 0x3A;
  private static final int CHANNEL_5_OPERATOR_3_DETUNE_MULTIPLE = 0x3E;

  private static final int CHANNEL_3_OPERATOR_0_VOLUME_INVERSE = 0x40;
  private static final int CHANNEL_3_OPERATOR_1_VOLUME_INVERSE = 0x44;
  private static final int CHANNEL_3_OPERATOR_2_VOLUME_INVERSE = 0x48;
  private static final int CHANNEL_3_OPERATOR_3_VOLUME_INVERSE = 0x4C;

  private static final int CHANNEL_4_OPERATOR_0_VOLUME_INVERSE = 0x41;
  private static final int CHANNEL_4_OPERATOR_1_VOLUME_INVERSE = 0x45;
  private static final int CHANNEL_4_OPERATOR_2_VOLUME_INVERSE = 0x49;
  private static final int CHANNEL_4_OPERATOR_3_VOLUME_INVERSE = 0x4D;

  private static final int CHANNEL_5_OPERATOR_0_VOLUME_INVERSE = 0x42;
  private static final int CHANNEL_5_OPERATOR_1_VOLUME_INVERSE = 0x46;
  private static final int CHANNEL_5_OPERATOR_2_VOLUME_INVERSE = 0x4A;
  private static final int CHANNEL_5_OPERATOR_3_VOLUME_INVERSE = 0x4E;

  private static final int CHANNEL_3_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE = 0x50;
  private static final int CHANNEL_3_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE = 0x54;
  private static final int CHANNEL_3_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE = 0x58;
  private static final int CHANNEL_3_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE = 0x5C;

  private static final int CHANNEL_4_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE = 0x51;
  private static final int CHANNEL_4_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE = 0x55;
  private static final int CHANNEL_4_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE = 0x59;
  private static final int CHANNEL_4_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE = 0x5D;

  private static final int CHANNEL_5_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE = 0x52;
  private static final int CHANNEL_5_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE = 0x56;
  private static final int CHANNEL_5_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE = 0x5A;
  private static final int CHANNEL_5_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE = 0x5E;

  private static final int CHANNEL_3_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x60;
  private static final int CHANNEL_3_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x64;
  private static final int CHANNEL_3_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x68;
  private static final int CHANNEL_3_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x6C;

  private static final int CHANNEL_4_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x61;
  private static final int CHANNEL_4_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x65;
  private static final int CHANNEL_4_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x69;
  private static final int CHANNEL_4_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x6D;

  private static final int CHANNEL_5_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x62;
  private static final int CHANNEL_5_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x66;
  private static final int CHANNEL_5_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x6A;
  private static final int CHANNEL_5_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION = 0x6E;

  private static final int CHANNEL_3_OPERATOR_0_RATE_DECAY_SECONDARY = 0x70;
  private static final int CHANNEL_3_OPERATOR_1_RATE_DECAY_SECONDARY = 0x74;
  private static final int CHANNEL_3_OPERATOR_2_RATE_DECAY_SECONDARY = 0x78;
  private static final int CHANNEL_3_OPERATOR_3_RATE_DECAY_SECONDARY = 0x7C;

  private static final int CHANNEL_4_OPERATOR_0_RATE_DECAY_SECONDARY = 0x71;
  private static final int CHANNEL_4_OPERATOR_1_RATE_DECAY_SECONDARY = 0x75;
  private static final int CHANNEL_4_OPERATOR_2_RATE_DECAY_SECONDARY = 0x79;
  private static final int CHANNEL_4_OPERATOR_3_RATE_DECAY_SECONDARY = 0x7D;

  private static final int CHANNEL_5_OPERATOR_0_RATE_DECAY_SECONDARY = 0x72;
  private static final int CHANNEL_5_OPERATOR_1_RATE_DECAY_SECONDARY = 0x76;
  private static final int CHANNEL_5_OPERATOR_2_RATE_DECAY_SECONDARY = 0x7A;
  private static final int CHANNEL_5_OPERATOR_3_RATE_DECAY_SECONDARY = 0x7E;

  private static final int CHANNEL_3_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x80;
  private static final int CHANNEL_3_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x84;
  private static final int CHANNEL_3_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x88;
  private static final int CHANNEL_3_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x8C;

  private static final int CHANNEL_4_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x81;
  private static final int CHANNEL_4_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x85;
  private static final int CHANNEL_4_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x89;
  private static final int CHANNEL_4_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x8D;

  private static final int CHANNEL_5_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x82;
  private static final int CHANNEL_5_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x86;
  private static final int CHANNEL_5_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x8A;
  private static final int CHANNEL_5_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE = 0x8E;

  private static final int CHANNEL_0_STEREO_AND_LFO_SENSITIVITY = 0xb4;
  private static final int CHANNEL_1_STEREO_AND_LFO_SENSITIVITY = 0xb5;
  private static final int CHANNEL_2_STEREO_AND_LFO_SENSITIVITY = 0xb6;

  private static final int CHANNEL_3_STEREO_AND_LFO_SENSITIVITY = 0xb4;
  private static final int CHANNEL_4_STEREO_AND_LFO_SENSITIVITY = 0xb5;
  private static final int CHANNEL_5_STEREO_AND_LFO_SENSITIVITY = 0xb6;

  private final HashMap<Integer, VGMYM2612Channel> channels;
  private int lfo_enable;
  private int lfo_frequency;
  private int dac_enable;
  private int timer_b_reset;
  private int timer_a_reset;
  private int timer_b_enable;
  private int timer_a_enable;
  private int timer_b_load;
  private int timer_a_load;
  private boolean channel_3_6_special_mode;
  private int timer_b_time;

  /**
   * Construct an interpreter.
   */

  public VGMYM2612Interpreter()
  {
    this.channels = new HashMap<>(6);
    for (int index = 0; index < 6; ++index) {
      this.channels.put(Integer.valueOf(index), new VGMYM2612Channel(this, index));
    }
  }

  /**
   * @param in_index The channel index
   *
   * @return The channel with the given index
   */

  public VGMYM2612Channel channel(
    final int in_index)
  {
    return Optional.ofNullable(this.channels.get(Integer.valueOf(in_index)))
      .orElseThrow(() -> new IllegalArgumentException("Illegal channel index: " + in_index));
  }

  /**
   * Write {@code value} to {@code register} in port 1.
   *
   * @param register The register (in the range {@code [0x0, 0xff]})
   * @param value    The value (in the range {@code [0x0, 0xff]})
   */

  // Switch-based interpreters suffer from high cyclomatic complexity
  // CHECKSTYLE:OFF
  public void writeRegisterPort1(
    final int register,
    final int value)
  {
    LOG.trace(
      "write: port 1: 0x{} 0x{}",
      Integer.toUnsignedString(register, 16),
      Integer.toUnsignedString(value, 16));

    switch (register & 0xff) {
      case 0x90:
      case 0x91:
      case 0x92:
      case 0x93:
      case 0x94:
      case 0x95:
      case 0x96:
      case 0x97:
      case 0x98:
      case 0x99:
      case 0x9a:
      case 0x9b:
      case 0x9c:
      case 0x9d:
      case 0x9e: {
        this.setProprietaryRegister(register, value);
        break;
      }

      case CHANNEL_3_STEREO_AND_LFO_SENSITIVITY: {
        this.channel(3).setStereoAndLFOSensitivity(value);
        break;
      }
      case CHANNEL_4_STEREO_AND_LFO_SENSITIVITY: {
        this.channel(4).setStereoAndLFOSensitivity(value);
        break;
      }
      case CHANNEL_5_STEREO_AND_LFO_SENSITIVITY: {
        this.channel(5).setStereoAndLFOSensitivity(value);
        break;
      }
      case CHANNEL_3_ALGORITHM_AND_FEEDBACK: {
        this.channel(3).setAlgorithmAndFeedback(value);
        break;
      }
      case CHANNEL_4_ALGORITHM_AND_FEEDBACK: {
        this.channel(4).setAlgorithmAndFeedback(value);
        break;
      }
      case CHANNEL_5_ALGORITHM_AND_FEEDBACK: {
        this.channel(5).setAlgorithmAndFeedback(value);
        break;
      }
      case CHANNEL_3_FREQUENCY_LSB: {
        this.channel(3).setFrequencyLSB(value);
        break;
      }
      case CHANNEL_3_FREQUENCY_MSB: {
        this.channel(3).setFrequencyMSB(value);
        break;
      }
      case CHANNEL_4_FREQUENCY_LSB: {
        this.channel(4).setFrequencyLSB(value);
        break;
      }
      case CHANNEL_4_FREQUENCY_MSB: {
        this.channel(4).setFrequencyMSB(value);
        break;
      }
      case CHANNEL_5_FREQUENCY_LSB: {
        final VGMYM2612Channel channel = this.channel(5);
        if (this.channel_3_6_special_mode) {
          channel.operator(0).setFrequencyLSB(value);
        } else {
          channel.setFrequencyLSB(value);
        }
        break;
      }
      case CHANNEL_5_FREQUENCY_MSB: {
        final VGMYM2612Channel channel = this.channel(5);
        if (this.channel_3_6_special_mode) {
          channel.operator(0).setFrequencyMSB(value);
        } else {
          channel.setFrequencyMSB(value);
        }
        break;
      }
      case CHANNEL_5_FREQUENCY_LSB_OPERATOR_1: {
        this.channel(5).operator(1).setFrequencyLSB(value);
        break;
      }
      case CHANNEL_5_FREQUENCY_MSB_OPERATOR_1: {
        this.channel(5).operator(1).setFrequencyMSB(value);
        break;
      }
      case CHANNEL_5_FREQUENCY_LSB_OPERATOR_2: {
        this.channel(5).operator(2).setFrequencyLSB(value);
        break;
      }
      case CHANNEL_5_FREQUENCY_MSB_OPERATOR_2: {
        this.channel(5).operator(2).setFrequencyMSB(value);
        break;
      }
      case CHANNEL_5_FREQUENCY_LSB_OPERATOR_3: {
        this.channel(5).operator(3).setFrequencyLSB(value);
        break;
      }
      case CHANNEL_5_FREQUENCY_MSB_OPERATOR_3: {
        this.channel(5).operator(3).setFrequencyMSB(value);
        break;
      }
      case CHANNEL_3_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channel(3).operator(0).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_3_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channel(3).operator(1).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_3_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channel(3).operator(2).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_3_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channel(3).operator(3).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_3_OPERATOR_0_VOLUME_INVERSE: {
        this.channel(3).operator(0).setVolumeInverse(value);
        break;
      }
      case CHANNEL_3_OPERATOR_1_VOLUME_INVERSE: {
        this.channel(3).operator(1).setVolumeInverse(value);
        break;
      }
      case CHANNEL_3_OPERATOR_2_VOLUME_INVERSE: {
        this.channel(3).operator(2).setVolumeInverse(value);
        break;
      }
      case CHANNEL_3_OPERATOR_3_VOLUME_INVERSE: {
        this.channel(3).operator(3).setVolumeInverse(value);
        break;
      }
      case CHANNEL_3_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(3).operator(0).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_3_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(3).operator(1).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_3_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(3).operator(2).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_3_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(3).operator(3).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_3_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(3).operator(0).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_3_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(3).operator(1).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_3_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(3).operator(2).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_3_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(3).operator(3).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_3_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channel(3).operator(0).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_3_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channel(3).operator(1).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_3_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channel(3).operator(2).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_3_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channel(3).operator(3).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_3_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(3).operator(0).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_3_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(3).operator(1).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_3_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(3).operator(2).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_3_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(3).operator(3).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_4_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channel(4).operator(0).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_4_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channel(4).operator(1).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_4_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channel(4).operator(2).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_4_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channel(4).operator(3).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_4_OPERATOR_0_VOLUME_INVERSE: {
        this.channel(4).operator(0).setVolumeInverse(value);
        break;
      }
      case CHANNEL_4_OPERATOR_1_VOLUME_INVERSE: {
        this.channel(4).operator(1).setVolumeInverse(value);
        break;
      }
      case CHANNEL_4_OPERATOR_2_VOLUME_INVERSE: {
        this.channel(4).operator(2).setVolumeInverse(value);
        break;
      }
      case CHANNEL_4_OPERATOR_3_VOLUME_INVERSE: {
        this.channel(4).operator(3).setVolumeInverse(value);
        break;
      }
      case CHANNEL_4_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(4).operator(0).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_4_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(4).operator(1).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_4_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(4).operator(2).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_4_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(4).operator(3).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_4_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(4).operator(0).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_4_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(4).operator(1).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_4_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(4).operator(2).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_4_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(4).operator(3).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_4_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channel(4).operator(0).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_4_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channel(4).operator(1).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_4_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channel(4).operator(2).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_4_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channel(4).operator(3).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_4_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(4).operator(0).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_4_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(4).operator(1).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_4_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(4).operator(2).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_4_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(4).operator(3).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_5_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channel(5).operator(0).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_5_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channel(5).operator(1).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_5_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channel(5).operator(2).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_5_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channel(5).operator(3).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_5_OPERATOR_0_VOLUME_INVERSE: {
        this.channel(5).operator(0).setVolumeInverse(value);
        break;
      }
      case CHANNEL_5_OPERATOR_1_VOLUME_INVERSE: {
        this.channel(5).operator(1).setVolumeInverse(value);
        break;
      }
      case CHANNEL_5_OPERATOR_2_VOLUME_INVERSE: {
        this.channel(5).operator(2).setVolumeInverse(value);
        break;
      }
      case CHANNEL_5_OPERATOR_3_VOLUME_INVERSE: {
        this.channel(5).operator(3).setVolumeInverse(value);
        break;
      }
      case CHANNEL_5_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(5).operator(0).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_5_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(5).operator(1).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_5_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(5).operator(2).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_5_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(5).operator(3).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_5_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(5).operator(0).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_5_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(5).operator(1).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_5_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(5).operator(2).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_5_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(5).operator(3).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_5_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channel(5).operator(0).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_5_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channel(5).operator(1).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_5_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channel(5).operator(2).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_5_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channel(5).operator(3).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_5_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(5).operator(0).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_5_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(5).operator(1).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_5_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(5).operator(2).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_5_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(5).operator(3).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      default: {
        LOG.warn(
          "write: port 1: unrecognized or unimplemented command 0x{} 0x{}",
          Integer.toUnsignedString(register, 16),
          Integer.toUnsignedString(value, 16));
        break;
      }
    }
  }
  // CHECKSTYLE:ON

  /**
   * Write {@code value} to {@code register} in port 0.
   *
   * @param register The register (in the range {@code [0x0, 0xff]})
   * @param value    The value (in the range {@code [0x0, 0xff]})
   */

  // Switch-based interpreters suffer from high cyclomatic complexity
  // CHECKSTYLE:OFF
  public void writeRegisterPort0(
    final int register,
    final int value)
  {
    LOG.trace(
      "write: port 0: 0x{} 0x{}",
      Integer.toUnsignedString(register, 16),
      Integer.toUnsignedString(value, 16));

    switch (register & 0xff) {

      case 0x90:
      case 0x91:
      case 0x92:
      case 0x93:
      case 0x94:
      case 0x95:
      case 0x96:
      case 0x97:
      case 0x98:
      case 0x99:
      case 0x9a:
      case 0x9b:
      case 0x9c:
      case 0x9d:
      case 0x9e: {
        this.setProprietaryRegister(register, value);
        break;
      }

      case TIMER_B: {
        this.setTimerBTime(value);
        break;
      }

      case CHANNEL_0_STEREO_AND_LFO_SENSITIVITY: {
        this.channel(0).setStereoAndLFOSensitivity(value);
        break;
      }
      case CHANNEL_1_STEREO_AND_LFO_SENSITIVITY: {
        this.channel(1).setStereoAndLFOSensitivity(value);
        break;
      }
      case CHANNEL_2_STEREO_AND_LFO_SENSITIVITY: {
        this.channel(2).setStereoAndLFOSensitivity(value);
        break;
      }
      case CHANNEL_0_ALGORITHM_AND_FEEDBACK: {
        this.channel(0).setAlgorithmAndFeedback(value);
        break;
      }
      case CHANNEL_1_ALGORITHM_AND_FEEDBACK: {
        this.channel(1).setAlgorithmAndFeedback(value);
        break;
      }
      case CHANNEL_2_ALGORITHM_AND_FEEDBACK: {
        this.channel(2).setAlgorithmAndFeedback(value);
        break;
      }
      case CHANNEL_0_FREQUENCY_LSB: {
        this.channel(0).setFrequencyLSB(value);
        break;
      }
      case CHANNEL_0_FREQUENCY_MSB: {
        this.channel(0).setFrequencyMSB(value);
        break;
      }
      case CHANNEL_1_FREQUENCY_LSB: {
        this.channel(1).setFrequencyLSB(value);
        break;
      }
      case CHANNEL_1_FREQUENCY_MSB: {
        this.channel(1).setFrequencyMSB(value);
        break;
      }
      case CHANNEL_2_FREQUENCY_LSB: {
        final VGMYM2612Channel channel = this.channel(2);
        if (this.channel_3_6_special_mode) {
          channel.operator(0).setFrequencyLSB(value);
        } else {
          channel.setFrequencyLSB(value);
        }
        break;
      }
      case CHANNEL_2_FREQUENCY_MSB: {
        final VGMYM2612Channel channel = this.channel(2);
        if (this.channel_3_6_special_mode) {
          channel.operator(0).setFrequencyMSB(value);
        } else {
          channel.setFrequencyMSB(value);
        }
        break;
      }
      case CHANNEL_2_FREQUENCY_LSB_OPERATOR_1: {
        this.channel(2).operator(1).setFrequencyLSB(value);
        break;
      }
      case CHANNEL_2_FREQUENCY_MSB_OPERATOR_1: {
        this.channel(2).operator(1).setFrequencyMSB(value);
        break;
      }
      case CHANNEL_2_FREQUENCY_LSB_OPERATOR_2: {
        this.channel(2).operator(2).setFrequencyLSB(value);
        break;
      }
      case CHANNEL_2_FREQUENCY_MSB_OPERATOR_2: {
        this.channel(2).operator(2).setFrequencyMSB(value);
        break;
      }
      case CHANNEL_2_FREQUENCY_LSB_OPERATOR_3: {
        this.channel(2).operator(3).setFrequencyLSB(value);
        break;
      }
      case CHANNEL_2_FREQUENCY_MSB_OPERATOR_3: {
        this.channel(2).operator(3).setFrequencyMSB(value);
        break;
      }
      case DAC_DATA: {
        this.setDACData(value);
        break;
      }
      case DAC_ENABLE: {
        this.setDAC(value);
        break;
      }
      case KEY_ON_OFF: {
        this.setKeyOnOff(value);
        break;
      }
      case TIMERS_AND_CHANNEL_3_6_MODE: {
        this.setTimersAndChannel3_6Mode(value);
        break;
      }
      case LFO_ENABLE: {
        this.setLFO(value);
        break;
      }
      case CHANNEL_0_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channel(0).operator(0).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_0_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channel(0).operator(1).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_0_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channel(0).operator(2).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_0_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channel(0).operator(3).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_0_OPERATOR_0_VOLUME_INVERSE: {
        this.channel(0).operator(0).setVolumeInverse(value);
        break;
      }
      case CHANNEL_0_OPERATOR_1_VOLUME_INVERSE: {
        this.channel(0).operator(1).setVolumeInverse(value);
        break;
      }
      case CHANNEL_0_OPERATOR_2_VOLUME_INVERSE: {
        this.channel(0).operator(2).setVolumeInverse(value);
        break;
      }
      case CHANNEL_0_OPERATOR_3_VOLUME_INVERSE: {
        this.channel(0).operator(3).setVolumeInverse(value);
        break;
      }
      case CHANNEL_0_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(0).operator(0).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_0_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(0).operator(1).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_0_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(0).operator(2).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_0_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(0).operator(3).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_0_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(0).operator(0).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_0_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(0).operator(1).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_0_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(0).operator(2).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_0_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(0).operator(3).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_0_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channel(0).operator(0).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_0_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channel(0).operator(1).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_0_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channel(0).operator(2).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_0_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channel(0).operator(3).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_0_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(0).operator(0).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_0_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(0).operator(1).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_0_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(0).operator(2).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_0_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(0).operator(3).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_1_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channel(1).operator(0).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_1_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channel(1).operator(1).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_1_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channel(1).operator(2).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_1_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channel(1).operator(3).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_1_OPERATOR_0_VOLUME_INVERSE: {
        this.channel(1).operator(0).setVolumeInverse(value);
        break;
      }
      case CHANNEL_1_OPERATOR_1_VOLUME_INVERSE: {
        this.channel(1).operator(1).setVolumeInverse(value);
        break;
      }
      case CHANNEL_1_OPERATOR_2_VOLUME_INVERSE: {
        this.channel(1).operator(2).setVolumeInverse(value);
        break;
      }
      case CHANNEL_1_OPERATOR_3_VOLUME_INVERSE: {
        this.channel(1).operator(3).setVolumeInverse(value);
        break;
      }
      case CHANNEL_1_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(1).operator(0).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_1_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(1).operator(1).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_1_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(1).operator(2).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_1_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(1).operator(3).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_1_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(1).operator(0).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_1_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(1).operator(1).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_1_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(1).operator(2).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_1_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(1).operator(3).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_1_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channel(1).operator(0).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_1_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channel(1).operator(1).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_1_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channel(1).operator(2).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_1_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channel(1).operator(3).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_1_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(1).operator(0).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_1_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(1).operator(1).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_1_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(1).operator(2).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_1_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(1).operator(3).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_2_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channel(2).operator(0).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_2_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channel(2).operator(1).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_2_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channel(2).operator(2).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_2_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channel(2).operator(3).setDetuneAndMultiple(value);
        break;
      }
      case CHANNEL_2_OPERATOR_0_VOLUME_INVERSE: {
        this.channel(2).operator(0).setVolumeInverse(value);
        break;
      }
      case CHANNEL_2_OPERATOR_1_VOLUME_INVERSE: {
        this.channel(2).operator(1).setVolumeInverse(value);
        break;
      }
      case CHANNEL_2_OPERATOR_2_VOLUME_INVERSE: {
        this.channel(2).operator(2).setVolumeInverse(value);
        break;
      }
      case CHANNEL_2_OPERATOR_3_VOLUME_INVERSE: {
        this.channel(2).operator(3).setVolumeInverse(value);
        break;
      }
      case CHANNEL_2_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(2).operator(0).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_2_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(2).operator(1).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_2_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(2).operator(2).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_2_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channel(2).operator(3).setRateScalingAndAttackRate(value);
        break;
      }
      case CHANNEL_2_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(2).operator(0).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_2_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(2).operator(1).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_2_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(2).operator(2).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_2_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channel(2).operator(3).setRateDecayAndAmplitudeModulation(value);
        break;
      }
      case CHANNEL_2_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channel(2).operator(0).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_2_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channel(2).operator(1).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_2_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channel(2).operator(2).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_2_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channel(2).operator(3).setRateDecaySecondary(value);
        break;
      }
      case CHANNEL_2_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(2).operator(0).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_2_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(2).operator(1).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_2_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(2).operator(2).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }
      case CHANNEL_2_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channel(2).operator(3).setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      default: {
        LOG.warn(
          "write: port 0: unrecognized or unimplemented command 0x{} 0x{}",
          Integer.toUnsignedString(register, 16),
          Integer.toUnsignedString(value, 16));
        break;
      }
    }
  }
  // CHECKSTYLE:ON

  private void setProprietaryRegister(
    final int register,
    final int value)
  {
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "setProprietaryRegister: 0x{} 0x{}",
        Integer.toUnsignedString(register & 0xff, 16),
        Integer.toUnsignedString(value & 0xff, 16));
    }
  }

  private void setTimerBTime(
    final int value)
  {
    this.timer_b_time = value;

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "setTimerBTime: 0x{}",
        Integer.toUnsignedString(this.timer_b_time, 16));
    }
  }

  private void setDACData(
    final int value)
  {
    if (LOG.isTraceEnabled()) {
      LOG.trace("setDACData: 0x{}", Integer.toUnsignedString(value, 16));
    }
  }

  private void setKeyOnOff(
    final int value)
  {
    final VGMYM2612Channel channel;
    final int channel_index = value & 0b111;
    if (channel_index == 0b000) {
      channel = this.channel(0);
    } else if (channel_index == 0b001) {
      channel = this.channel(1);
    } else if (channel_index == 0b010) {
      channel = this.channel(2);
      // This is not a typo: The operator numbering is discontinuous
    } else if (channel_index == 0b100) {
      channel = this.channel(3);
    } else if (channel_index == 0b101) {
      channel = this.channel(4);
    } else if (channel_index == 0b110) {
      channel = this.channel(5);
    } else {
      LOG.warn(
        "setKeyOnOff: channel 0x{} is invalid",
        Integer.toUnsignedString(channel_index, 16));
      return;
    }

    final int ops = (value >>> 4) & 0b1111;
    final boolean op_0 = (ops & 0b0001) == 0b0001;
    channel.operator(0).setEnabled(op_0);
    final boolean op_1 = (ops & 0b0010) == 0b0010;
    channel.operator(1).setEnabled(op_1);
    final boolean op_2 = (ops & 0b0100) == 0b0100;
    channel.operator(2).setEnabled(op_2);
    final boolean op_3 = (ops & 0b1000) == 0b1000;
    channel.operator(3).setEnabled(op_3);

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "setKeyOnOff: channel {} operators 0/1/2/3 ({}/{}/{}/{})",
        Integer.valueOf(channel.index()),
        Boolean.valueOf(op_0),
        Boolean.valueOf(op_1),
        Boolean.valueOf(op_2),
        Boolean.valueOf(op_3));
    }
  }

  private void setTimersAndChannel3_6Mode(
    final int value)
  {
    /*
     * There are 2 bits that specify the channel 3/6 mode, but only bit patterns
     * 0b00 and 0b01 are valid.
     */

    final int mode_raw = (value >>> 6) & 0b11;
    if (mode_raw == 0b00) {
      this.channel_3_6_special_mode = false;
    } else if (mode_raw == 0b01) {
      this.channel_3_6_special_mode = true;
    } else {
      LOG.warn(
        "setTimersAndChannel3_6Mode: mode 0x{} is invalid",
        Integer.toUnsignedString(mode_raw, 16));
    }

    this.timer_b_reset = (value >>> 5) & 0b1;
    this.timer_a_reset = (value >>> 4) & 0b1;
    this.timer_b_enable = (value >>> 3) & 0b1;
    this.timer_a_enable = (value >>> 2) & 0b1;
    this.timer_b_load = (value >>> 1) & 0b1;
    this.timer_a_load = value & 0b1;

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "setTimersAndChannel3_6Mode: channel 3/6 mode {}",
        this.channel_3_6_special_mode ? "special" : "normal");
      LOG.trace(
        "setTimersAndChannel3_6Mode: timer A reset/enable/load ({}/{}/{})",
        Integer.valueOf(this.timer_a_reset),
        Integer.valueOf(this.timer_a_enable),
        Integer.valueOf(this.timer_a_load));
      LOG.trace(
        "setTimersAndChannel3_6Mode: timer B reset/enable/load ({}/{}/{})",
        Integer.valueOf(this.timer_b_reset),
        Integer.valueOf(this.timer_b_enable),
        Integer.valueOf(this.timer_b_load));
    }
  }

  private void setDAC(
    final int value)
  {
    this.dac_enable = (value >>> 7) & 0b1;

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "setDAC: enable {}",
        Boolean.valueOf(this.dac_enable == 1));
    }
  }

  private void setLFO(
    final int value)
  {
    this.lfo_enable = (value >>> 3) & 0b1;
    this.lfo_frequency = value & 0b111;

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "setLFO: enable {} frequency 0x{}",
        Boolean.valueOf(this.lfo_enable == 1),
        Integer.toUnsignedString(this.lfo_frequency, 16));
    }
  }
}
