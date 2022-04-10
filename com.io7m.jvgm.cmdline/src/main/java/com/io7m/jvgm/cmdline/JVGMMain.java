/*
 * Copyright © 2018 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Main command line entry point.
 */

public final class JVGMMain implements Runnable
{
  private static final Logger LOG = LoggerFactory.getLogger(JVGMMain.class);

  private final Map<String, JVGMCommandType> commands;
  private final JCommander commander;
  private final String[] args;
  private int exit_code;

  private JVGMMain(
    final String[] in_args)
  {
    this.args =
      Objects.requireNonNull(in_args, "Command line arguments");

    final JVGMCommandRoot r = new JVGMCommandRoot();
    final JVGMCommandDump cmd_dump = new JVGMCommandDump();
    final JVGMCommandInterpret cmd_interpret = new JVGMCommandInterpret();

    this.commands = new HashMap<>(8);
    this.commands.put("dump", cmd_dump);
    this.commands.put("interpret", cmd_interpret);

    this.commander = new JCommander(r);
    this.commander.setProgramName("jvgm");
    this.commander.addCommand("dump", cmd_dump);
    this.commander.addCommand("interpret", cmd_interpret);
  }

  /**
   * The main entry point.
   *
   * @param args Command line arguments
   */

  public static void main(final String[] args)
  {
    final JVGMMain cm = new JVGMMain(args);
    cm.run();
    System.exit(cm.exitCode());
  }

  /**
   * @return The program exit code
   */

  public int exitCode()
  {
    return this.exit_code;
  }

  @Override
  public void run()
  {
    final var console = new JVGMStringConsole();

    try {
      this.commander.setConsole(console);
      this.commander.parse(this.args);

      final String cmd = this.commander.getParsedCommand();
      if (cmd == null) {
        this.commander.usage();
        LOG.info("Arguments required.\n{}", console.text());
        this.exit_code = 1;
        return;
      }

      final JVGMCommandType command = this.commands.get(cmd);
      final JVGMCommandType.Status status = command.execute();
      this.exit_code = status.exitCode();
    } catch (final ParameterException e) {
      this.commander.usage();
      LOG.error("{}\n{}", e.getMessage(), console.text());
      this.exit_code = 1;
    } catch (final Exception e) {
      LOG.error("{}", e.getMessage(), e);
      this.exit_code = 1;
    }
  }
}
