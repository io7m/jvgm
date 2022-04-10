/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import com.beust.jcommander.internal.Console;

/**
 * A console implementation that appends to a string builder.
 */

public final class JVGMStringConsole implements Console
{
  private final StringBuilder stringBuilder;

  /**
   * A console implementation that appends to a string builder.
   */

  public JVGMStringConsole()
  {
    this.stringBuilder = new StringBuilder(128);
  }

  @Override
  public void print(
    final String text)
  {
    this.stringBuilder.append(text);
  }

  @Override
  public void println(
    final String text)
  {
    this.stringBuilder.append(text);
    this.stringBuilder.append('\n');
  }

  @Override
  public char[] readPassword(
    final boolean b)
  {
    return new char[0];
  }

  /**
   * @return The text written so far
   */

  public String text()
  {
    return this.stringBuilder.toString();
  }
}
