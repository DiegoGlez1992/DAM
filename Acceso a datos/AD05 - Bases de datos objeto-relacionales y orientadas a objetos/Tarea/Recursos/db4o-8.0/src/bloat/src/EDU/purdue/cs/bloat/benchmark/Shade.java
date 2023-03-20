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
 * This class is used to execute the BLOAT benchmarks while the Shade
 * performanace monitoring software is running.
 */
public class Shade {
	static {
		System.loadLibrary("shade");
	}

	public static native void run(Class main, String[] args, boolean quit);

	public static void main(final String[] args) throws Exception {
		boolean quit = false;
		int runs = 1;
		int eat = 0;

		if (args.length <= 1) {
			Shade.usage();
		}

		for (eat = 0; eat < args.length; eat++) {
			if (args[eat].equals("-quit")) {
				quit = true;
			} else if (args[eat].equals("-run")) {
				if (++eat >= args.length) {
					Shade.usage();
				}

				runs = Integer.parseInt(args[eat]);

				if (runs <= 0) {
					Shade.usage();
				}
			} else {
				// The main class
				eat++;
				break;
			}
		}

		/* Got all the args. */
		if (eat > args.length) {
			Shade.usage();
		}

		// Install a secutiry manager in which we can control whether or
		// not the virtual machine is allowed to exit. We want to be able
		// to make multiple runs of the main class without the VM exiting.
		final BenchmarkSecurityManager sec = new BenchmarkSecurityManager();
		System.setSecurityManager(sec);

		final String mainClassName = args[eat - 1];
		final String[] a = new String[args.length - eat];

		System.err.println("Running " + mainClassName);

		for (int i = 0; i < runs; i++) {
			try {
				final Class mainClass = Class.forName(mainClassName);

				System.arraycopy(args, eat, a, 0, a.length);

				Shade.run(mainClass, a, quit);

			} catch (final SecurityException e) {
				// An execution of the mainClass finished and the VM attempted
				// to exit, thus causing a SecutiryException to be thrown by
				// the BenchmarkSecurityManager.
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
		System.err.print("usage: java EDU.purdue.cs.bloat.Shade ");
		System.err.println("options class args...");
		System.err.println("where options are one of:");
		System.err.println("    -run n    time n runs of the program");
		System.exit(1);
	}
}
