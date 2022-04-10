/*
 * Copyright © 2018 <code@io7m.com> https://www.io7m.com
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

import ch.qos.logback.classic.Logger;
import com.beust.jcommander.Parameter;
import org.slf4j.LoggerFactory;

class JVGMCommandRoot implements JVGMCommandType
{
  @Parameter(
    names = "--verbose",
    converter = JVGMLogLevelConverter.class,
    description = "Set the minimum logging verbosity level")
  private JVGMLogLevel verbose = JVGMLogLevel.LOG_INFO;

  JVGMCommandRoot()
  {

  }

  @Override
  public JVGMCommandType.Status execute()
    throws Exception
  {
    final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(this.verbose.toLevel());
    return Status.SUCCESS;
  }
}
