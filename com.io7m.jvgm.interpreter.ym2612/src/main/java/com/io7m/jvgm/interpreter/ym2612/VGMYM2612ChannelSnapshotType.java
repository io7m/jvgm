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

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

/**
 * A snapshot of a channel.
 */

@ImmutablesStyleType
@Value.Immutable
public interface VGMYM2612ChannelSnapshotType
{
  /**
   * @return The algorithm number
   */

  @Value.Parameter
  int algorithm();

  /**
   * @return The channel operators
   */

  @Value.Parameter
  Map<Integer, VGMYM2612OperatorSnapshot> operators();

  /**
   * @param in_index The operator index
   *
   * @return The operator with the given index
   */

  default VGMYM2612OperatorSnapshot operator(
    final int in_index)
  {
    return Optional.ofNullable(this.operators().get(Integer.valueOf(in_index)))
      .orElseThrow(() -> new IllegalArgumentException(
        new StringBuilder(32)
          .append("Invalid operator index: ")
          .append(in_index)
          .toString()));
  }

  /**
   * @return The operator index
   */

  @Value.Parameter
  int index();

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    final int op_count = this.operators().size();
    if (op_count != 4) {
      throw new IllegalArgumentException(
        new StringBuilder(32)
          .append("Must supply four operators, got ")
          .append(op_count)
          .toString());
    }

    this.operator(0);
    this.operator(1);
    this.operator(2);
    this.operator(3);
  }
}
