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

package com.io7m.jvgm.tests.interpreter.ym2612;

import com.io7m.jvgm.interpreter.ym2612.VGMYM2612Interpreter;
import org.junit.jupiter.api.Test;

public final class VGMYM2612InterpreterTest
{
  @Test
  public void testExhaustivePort0()
  {
    final VGMYM2612Interpreter interp = new VGMYM2612Interpreter();

    for (int register = 0; register < 0x100; ++register) {
      for (int value = 0; value < 0x100; ++value) {
        interp.writeRegisterPort0(register, value);
      }
    }
  }

  @Test
  public void testExhaustivePort1()
  {
    final VGMYM2612Interpreter interp = new VGMYM2612Interpreter();

    for (int register = 0; register < 0x100; ++register) {
      for (int value = 0; value < 0x100; ++value) {
        interp.writeRegisterPort1(register, value);
      }
    }
  }
}
