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

/**
 * A YM2612 state interpreter.
 */

public final class VGMInterpreterYM2612
{
  private static final Logger LOG =
    LoggerFactory.getLogger(VGMInterpreterYM2612.class);

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

  private final HashMap<Integer, Channel> channels;
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

  public VGMInterpreterYM2612()
  {
    this.channels = new HashMap<>(6);
    for (int index = 0; index < 6; ++index) {
      this.channels.put(Integer.valueOf(index), new Channel(index));
    }
  }

  /**
   * Write {@code value} to {@code register} in port 1.
   *
   * @param register The register (in the range {@code [0x0, 0xff]})
   * @param value    The value (in the range {@code [0x0, 0xff]})
   */

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
        this.channels.get(Integer.valueOf(3)).setStereoAndLFOSensitivity(value);
        break;
      }
      case CHANNEL_4_STEREO_AND_LFO_SENSITIVITY: {
        this.channels.get(Integer.valueOf(4)).setStereoAndLFOSensitivity(value);
        break;
      }
      case CHANNEL_5_STEREO_AND_LFO_SENSITIVITY: {
        this.channels.get(Integer.valueOf(5)).setStereoAndLFOSensitivity(value);
        break;
      }

      case CHANNEL_3_ALGORITHM_AND_FEEDBACK: {
        this.channels.get(Integer.valueOf(3)).setAlgorithmAndFeedback(value);
        break;
      }

      case CHANNEL_4_ALGORITHM_AND_FEEDBACK: {
        this.channels.get(Integer.valueOf(4)).setAlgorithmAndFeedback(value);
        break;
      }

      case CHANNEL_5_ALGORITHM_AND_FEEDBACK: {
        this.channels.get(Integer.valueOf(5)).setAlgorithmAndFeedback(value);
        break;
      }

      case CHANNEL_3_FREQUENCY_LSB: {
        this.channels.get(Integer.valueOf(3)).setFrequencyLSB(value);
        break;
      }

      case CHANNEL_3_FREQUENCY_MSB: {
        this.channels.get(Integer.valueOf(3)).setFrequencyMSB(value);
        break;
      }

      case CHANNEL_4_FREQUENCY_LSB: {
        this.channels.get(Integer.valueOf(4)).setFrequencyLSB(value);
        break;
      }

      case CHANNEL_4_FREQUENCY_MSB: {
        this.channels.get(Integer.valueOf(4)).setFrequencyMSB(value);
        break;
      }

      case CHANNEL_5_FREQUENCY_LSB: {
        final Channel channel = this.channels.get(Integer.valueOf(5));
        if (this.channel_3_6_special_mode) {
          channel.operators.get(Integer.valueOf(0)).setFrequencyLSB(value);
        } else {
          channel.setFrequencyLSB(value);
        }
        break;
      }

      case CHANNEL_5_FREQUENCY_MSB: {
        final Channel channel = this.channels.get(Integer.valueOf(5));
        if (this.channel_3_6_special_mode) {
          channel.operators.get(Integer.valueOf(0)).setFrequencyMSB(value);
        } else {
          channel.setFrequencyMSB(value);
        }
        break;
      }

      case CHANNEL_5_FREQUENCY_LSB_OPERATOR_1: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(1))
          .setFrequencyLSB(value);
        break;
      }

      case CHANNEL_5_FREQUENCY_MSB_OPERATOR_1: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(1))
          .setFrequencyMSB(value);
        break;
      }

      case CHANNEL_5_FREQUENCY_LSB_OPERATOR_2: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(2))
          .setFrequencyLSB(value);
        break;
      }

      case CHANNEL_5_FREQUENCY_MSB_OPERATOR_2: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(2))
          .setFrequencyMSB(value);
        break;
      }

      case CHANNEL_5_FREQUENCY_LSB_OPERATOR_3: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(3))
          .setFrequencyLSB(value);
        break;
      }

      case CHANNEL_5_FREQUENCY_MSB_OPERATOR_3: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(3))
          .setFrequencyMSB(value);
        break;
      }

      case CHANNEL_3_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(0))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_3_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(1))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_3_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(2))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_3_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(3))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_3_OPERATOR_0_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(0))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_3_OPERATOR_1_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(1))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_3_OPERATOR_2_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(2))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_3_OPERATOR_3_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(3))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_3_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(0))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_3_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(1))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_3_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(2))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_3_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(3))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_3_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(0))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_3_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(1))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_3_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(2))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_3_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(3))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_3_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(0))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_3_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(1))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_3_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(2))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_3_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(3))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_3_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(0))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_3_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(1))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_3_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(2))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_3_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(3))
          .operators.get(Integer.valueOf(3))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      // Channel 4

      case CHANNEL_4_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(0))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_4_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(1))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_4_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(2))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_4_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(3))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_4_OPERATOR_0_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(0))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_4_OPERATOR_1_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(1))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_4_OPERATOR_2_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(2))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_4_OPERATOR_3_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(3))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_4_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(0))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_4_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(1))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_4_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(2))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_4_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(3))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_4_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(0))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_4_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(1))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_4_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(2))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_4_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(3))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_4_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(0))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_4_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(1))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_4_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(2))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_4_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(3))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_4_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(0))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_4_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(1))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_4_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(2))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_4_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(4))
          .operators.get(Integer.valueOf(3))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      // Channel 5

      case CHANNEL_5_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(0))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_5_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(1))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_5_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(2))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_5_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(3))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_5_OPERATOR_0_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(0))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_5_OPERATOR_1_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(1))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_5_OPERATOR_2_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(2))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_5_OPERATOR_3_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(3))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_5_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(0))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_5_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(1))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_5_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(2))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_5_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(3))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_5_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(0))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_5_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(1))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_5_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(2))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_5_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(3))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_5_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(0))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_5_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(1))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_5_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(2))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_5_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(3))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_5_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(0))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_5_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(1))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_5_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(2))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_5_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(5))
          .operators.get(Integer.valueOf(3))
          .setRateReleaseAndSecondaryAmplitude(value);
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

  /**
   * Write {@code value} to {@code register} in port 0.
   *
   * @param register The register (in the range {@code [0x0, 0xff]})
   * @param value    The value (in the range {@code [0x0, 0xff]})
   */

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
        this.channels.get(Integer.valueOf(0)).setStereoAndLFOSensitivity(value);
        break;
      }
      case CHANNEL_1_STEREO_AND_LFO_SENSITIVITY: {
        this.channels.get(Integer.valueOf(1)).setStereoAndLFOSensitivity(value);
        break;
      }
      case CHANNEL_2_STEREO_AND_LFO_SENSITIVITY: {
        this.channels.get(Integer.valueOf(2)).setStereoAndLFOSensitivity(value);
        break;
      }

      case CHANNEL_0_ALGORITHM_AND_FEEDBACK: {
        this.channels.get(Integer.valueOf(0)).setAlgorithmAndFeedback(value);
        break;
      }

      case CHANNEL_1_ALGORITHM_AND_FEEDBACK: {
        this.channels.get(Integer.valueOf(1)).setAlgorithmAndFeedback(value);
        break;
      }

      case CHANNEL_2_ALGORITHM_AND_FEEDBACK: {
        this.channels.get(Integer.valueOf(2)).setAlgorithmAndFeedback(value);
        break;
      }

      case CHANNEL_0_FREQUENCY_LSB: {
        this.channels.get(Integer.valueOf(0)).setFrequencyLSB(value);
        break;
      }

      case CHANNEL_0_FREQUENCY_MSB: {
        this.channels.get(Integer.valueOf(0)).setFrequencyMSB(value);
        break;
      }

      case CHANNEL_1_FREQUENCY_LSB: {
        this.channels.get(Integer.valueOf(1)).setFrequencyLSB(value);
        break;
      }

      case CHANNEL_1_FREQUENCY_MSB: {
        this.channels.get(Integer.valueOf(1)).setFrequencyMSB(value);
        break;
      }

      case CHANNEL_2_FREQUENCY_LSB: {
        final Channel channel = this.channels.get(Integer.valueOf(2));
        if (this.channel_3_6_special_mode) {
          channel.operators.get(Integer.valueOf(0)).setFrequencyLSB(value);
        } else {
          channel.setFrequencyLSB(value);
        }
        break;
      }

      case CHANNEL_2_FREQUENCY_MSB: {
        final Channel channel = this.channels.get(Integer.valueOf(2));
        if (this.channel_3_6_special_mode) {
          channel.operators.get(Integer.valueOf(0)).setFrequencyMSB(value);
        } else {
          channel.setFrequencyMSB(value);
        }
        break;
      }

      case CHANNEL_2_FREQUENCY_LSB_OPERATOR_1: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(1))
          .setFrequencyLSB(value);
        break;
      }

      case CHANNEL_2_FREQUENCY_MSB_OPERATOR_1: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(1))
          .setFrequencyMSB(value);
        break;
      }

      case CHANNEL_2_FREQUENCY_LSB_OPERATOR_2: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(2))
          .setFrequencyLSB(value);
        break;
      }

      case CHANNEL_2_FREQUENCY_MSB_OPERATOR_2: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(2))
          .setFrequencyMSB(value);
        break;
      }

      case CHANNEL_2_FREQUENCY_LSB_OPERATOR_3: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(3))
          .setFrequencyLSB(value);
        break;
      }

      case CHANNEL_2_FREQUENCY_MSB_OPERATOR_3: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(3))
          .setFrequencyMSB(value);
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

      // Channel 0

      case CHANNEL_0_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(0))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_0_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(1))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_0_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(2))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_0_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(3))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_0_OPERATOR_0_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(0))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_0_OPERATOR_1_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(1))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_0_OPERATOR_2_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(2))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_0_OPERATOR_3_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(3))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_0_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(0))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_0_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(1))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_0_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(2))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_0_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(3))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_0_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(0))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_0_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(1))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_0_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(2))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_0_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(3))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_0_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(0))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_0_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(1))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_0_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(2))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_0_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(3))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_0_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(0))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_0_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(1))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_0_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(2))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_0_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(0))
          .operators.get(Integer.valueOf(3))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      // Channel 1

      case CHANNEL_1_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(0))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_1_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(1))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_1_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(2))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_1_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(3))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_1_OPERATOR_0_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(0))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_1_OPERATOR_1_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(1))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_1_OPERATOR_2_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(2))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_1_OPERATOR_3_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(3))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_1_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(0))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_1_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(1))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_1_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(2))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_1_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(3))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_1_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(0))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_1_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(1))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_1_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(2))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_1_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(3))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_1_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(0))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_1_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(1))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_1_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(2))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_1_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(3))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_1_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(0))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_1_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(1))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_1_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(2))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_1_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(1))
          .operators.get(Integer.valueOf(3))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      // Channel 2

      case CHANNEL_2_OPERATOR_0_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(0))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_2_OPERATOR_1_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(1))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_2_OPERATOR_2_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(2))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_2_OPERATOR_3_DETUNE_MULTIPLE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(3))
          .setDetuneAndMultiple(value);
        break;
      }

      case CHANNEL_2_OPERATOR_0_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(0))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_2_OPERATOR_1_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(1))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_2_OPERATOR_2_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(2))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_2_OPERATOR_3_VOLUME_INVERSE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(3))
          .setVolumeInverse(value);
        break;
      }

      case CHANNEL_2_OPERATOR_0_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(0))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_2_OPERATOR_1_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(1))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_2_OPERATOR_2_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(2))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_2_OPERATOR_3_RATE_SCALING_AND_ATTACK_RATE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(3))
          .setRateScalingAndAttackRate(value);
        break;
      }

      case CHANNEL_2_OPERATOR_0_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(0))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_2_OPERATOR_1_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(1))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_2_OPERATOR_2_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(2))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_2_OPERATOR_3_RATE_DECAY_AND_AMPLITUDE_MODULATION: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(3))
          .setRateDecayAndAmplitudeModulation(value);
        break;
      }

      case CHANNEL_2_OPERATOR_0_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(0))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_2_OPERATOR_1_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(1))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_2_OPERATOR_2_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(2))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_2_OPERATOR_3_RATE_DECAY_SECONDARY: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(3))
          .setRateDecaySecondary(value);
        break;
      }

      case CHANNEL_2_OPERATOR_0_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(0))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_2_OPERATOR_1_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(1))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_2_OPERATOR_2_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(2))
          .setRateReleaseAndSecondaryAmplitude(value);
        break;
      }

      case CHANNEL_2_OPERATOR_3_RATE_RELEASE_SECONDARY_AMPLITUDE: {
        this.channels.get(Integer.valueOf(2))
          .operators.get(Integer.valueOf(3))
          .setRateReleaseAndSecondaryAmplitude(value);
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
    final Channel channel;
    final int channel_index = value & 0b111;
    if (channel_index == 0b000) {
      channel = this.channels.get(Integer.valueOf(0));
    } else if (channel_index == 0b001) {
      channel = this.channels.get(Integer.valueOf(1));
    } else if (channel_index == 0b010) {
      channel = this.channels.get(Integer.valueOf(2));
      // This is not a typo: The operator numbering is discontinuous
    } else if (channel_index == 0b100) {
      channel = this.channels.get(Integer.valueOf(3));
    } else if (channel_index == 0b101) {
      channel = this.channels.get(Integer.valueOf(4));
    } else if (channel_index == 0b110) {
      channel = this.channels.get(Integer.valueOf(5));
    } else {
      LOG.warn(
        "setKeyOnOff: channel 0x{} is invalid",
        Integer.toUnsignedString(channel_index, 16));
      return;
    }

    final int ops = (value >>> 4) & 0b1111;
    final boolean op_0 = (ops & 0b0001) == 0b0001;
    channel.operators.get(Integer.valueOf(0)).enabled = op_0;
    final boolean op_1 = (ops & 0b0010) == 0b0010;
    channel.operators.get(Integer.valueOf(1)).enabled = op_1;
    final boolean op_2 = (ops & 0b0100) == 0b0100;
    channel.operators.get(Integer.valueOf(2)).enabled = op_2;
    final boolean op_3 = (ops & 0b1000) == 0b1000;
    channel.operators.get(Integer.valueOf(3)).enabled = op_3;

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "setKeyOnOff: channel {} operators 0/1/2/3 ({}/{}/{}/{})",
        Integer.valueOf(channel.index),
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

  /**
   * An operator.
   */

  public final class Operator
  {
    private Logger log;
    private final int index;
    private final Channel channel;
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

    Operator(
      final Channel in_channel,
      final int in_index)
    {
      this.channel = in_channel;
      this.index = in_index;
      this.log = LoggerFactory.getLogger(
        new StringBuilder(128)
          .append(VGMInterpreterYM2612.class.getCanonicalName())
          .append(" [channel ")
          .append(this.channel.index)
          .append("] [operator ")
          .append(in_index)
          .append("]")
          .toString());
    }

    void setDetuneAndMultiple(
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

    void setVolumeInverse(
      final int value)
    {
      this.volume = value;

      if (this.log.isTraceEnabled()) {
        this.log.trace(
          "setVolumeInverse: 0x{}",
          Integer.toUnsignedString(this.volume, 16));
      }
    }

    void setRateScalingAndAttackRate(
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

    void setRateDecayAndAmplitudeModulation(
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

    void setRateDecaySecondary(
      final int value)
    {
      this.rate_decay_1 = value & 0b11111;

      if (this.log.isTraceEnabled()) {
        this.log.trace(
          "setRateDecaySecondary: decay-1 {}",
          Integer.valueOf(this.rate_decay_1));
      }
    }

    void setRateReleaseAndSecondaryAmplitude(
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

    void setFrequencyLSB(
      final int value)
    {
      this.frequency_lsb = value;

      if (this.log.isTraceEnabled()) {
        this.log.trace(
          "setFrequencyLSB: (special mode) lsb 0x{}",
          Integer.toUnsignedString(value, 16));
      }
    }

    void setFrequencyMSB(
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
  }

  /**
   * A channel.
   */

  public final class Channel
  {
    private final int index;
    private final HashMap<Integer, Operator> operators;
    private final Logger log;
    private int frequency_lsb;
    private int frequency_msb;
    private int frequency_octave;
    private int feedback;
    private int algorithm;
    private boolean stereo_left_enabled;
    private boolean stereo_right_enabled;
    private int lfo_amplitude_sensitivity;
    private int lfo_frequency_sensitivity;

    Channel(
      final int in_index)
    {
      this.index = in_index;
      this.operators = new HashMap<>(6);
      for (int op_index = 0; op_index < 4; ++op_index) {
        this.operators.put(
          Integer.valueOf(op_index), new Operator(this, op_index));
      }

      this.log = LoggerFactory.getLogger(
        new StringBuilder(128)
          .append(VGMInterpreterYM2612.class.getCanonicalName())
          .append(" [channel ")
          .append(this.index)
          .append("]")
          .toString());
    }

    void setFrequencyLSB(final int value)
    {
      this.frequency_lsb = value;

      if (this.log.isTraceEnabled()) {
        this.log.trace(
          "setFrequencyLSB: lsb 0x{}",
          Integer.toUnsignedString(value, 16));
      }
    }

    void setFrequencyMSB(
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

    void setAlgorithmAndFeedback(
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

    void setStereoAndLFOSensitivity(
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
  }
}
