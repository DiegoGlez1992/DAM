/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package EDU.purdue.cs.bloat.benchmark;

/**
 * <tt>Stats</tt> is used to run a benchmark using an instrumented Java
 * Virtual machine that counts the number of each kind of bytecodes executed.
 * The counts are maintained in two C variables, <tt>instruction_count</tt>
 * and <tt>redundant_count</tt>.
 */
public class Stats {

	static {
		System.loadLibrary("stats");
	}

	public static native void run(Class main, String[] args);

	public static void main(final String[] args) throws Exception {
		int runs = 1;
		int eat = 0;

		if (args.length <= 1) {
			Stats.usage();
		}

		for (eat = 0; eat < args.length; eat++) {
			if (args[eat].equals("-run")) {
				if (++eat >= args.length) {
					Stats.usage();
				}

				runs = Integer.parseInt(args[eat]);

				if (runs <= 0) {
					Stats.usage();
				}

			} else {
				// The main class
				eat++;
				break;
			}
		}

		/* Got all the args. */
		if (eat > args.length) {
			Stats.usage();
		}

		final BenchmarkSecurityManager sec = new BenchmarkSecurityManager();
		System.setSecurityManager(sec);

		final String mainClassName = args[eat - 1];
		final String[] a = new String[args.length - eat];

		System.err.println("Running " + mainClassName);

		for (int i = 0; i < runs; i++) {
			try {
				final Class mainClass = Class.forName(mainClassName);

				System.arraycopy(args, eat, a, 0, a.length);

				Stats.run(mainClass, a);

			} catch (final SecurityException e) {
				continue;

			} catch (final Exception e) {
				e.printStackTrace(System.err);
				sec.allowExit = true;
				System.exit(1);
			}
		}

		sec.allowExit = true;
	}

	private static void usage() {
		System.err.print("usage: java EDU.purdue.cs.bloat.Stats ");
		System.err.println("options class args...");
		System.err.println("where options are one of:");
		System.err.println("    -run n    time n runs of the program");
		System.exit(1);
	}
}
