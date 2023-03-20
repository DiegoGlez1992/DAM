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
 * This class is used to run a benchmark Java program with Perfmon running in
 * the background. Perfmon is a software package developed at Michigan State
 * University that allows user-level programs to access the hardware counters on
 * Sparc processors.
 * 
 * <p>
 * 
 * The <tt>main</tt> method of this class takes several arguments (note that
 * the first four arguments are mutually exclusive):
 * 
 * <pre>
 *    -inst-load-stall       Count load interlock induced stalls
 *    -dcache                Count data cache hit rate
 *    -cycle-ic-miss-stall   Count I-cache miss induced stalls (and cycles)
 *    -inst-cycle            Count instructions (and cycles)
 * 
 *    -run n                 How many times is the program run
 * 
 *    class                  Java class to run (the benchmark)
 *    args                   Arguments to benchmark class
 * </pre>
 * 
 * The real work is done by the native <tt>run</tt> method that is implemented
 * in benchmark.c.
 * 
 * @see BenchmarkSecurityManager
 */
public class Benchmark {
	static {
		// Load native code from libbenchmark.so
		System.loadLibrary("benchmark");
	}

	public static native void init(Class main);

	public static native void run(Class main, String[] args);

	public static native void setMode(int mode);

	public static void main(final String[] args) throws Exception {
		int mode = 0;

		int runs = 1;
		int eat = 0;

		if (args.length <= 1) {
			Benchmark.usage();
		}

		for (eat = 0; eat < args.length; eat++) {
			if (args[eat].equals("-inst-cycle")) {
				mode = 3;
			} else if (args[eat].equals("-inst-load-stall")) {
				mode = 0;
			} else if (args[eat].equals("-dcache")) {
				mode = 1;
			} else if (args[eat].equals("-cycle-ic-miss-stall")) {
				mode = 2;
			} else if (args[eat].equals("-run")) {
				if (++eat >= args.length) {
					Benchmark.usage();
				}

				runs = Integer.parseInt(args[eat]);

				if (runs <= 0) {
					Benchmark.usage();
				}
			} else {
				// The main class
				eat++;
				break;
			}
		}

		/* Got all the args. */
		if (eat > args.length) {
			Benchmark.usage();
		}

		final BenchmarkSecurityManager sec = new BenchmarkSecurityManager();
		System.setSecurityManager(sec);

		final String mainClassName = args[eat - 1];
		final String[] a = new String[args.length - eat];

		System.err.println("Running " + mainClassName + " in mode " + mode);
		Benchmark.setMode(mode);

		final Class mainClass = Class.forName(mainClassName);
		Benchmark.init(mainClass);

		for (int i = 0; i < runs; i++) {
			try {
				System.arraycopy(args, eat, a, 0, a.length);
				Benchmark.run(mainClass, a);
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
		System.err.print("usage: java EDU.purdue.cs.bloat.Benchmark ");
		System.err.println("options class args...");
		System.err.println("where options are one of:");
		System.err.println("    -inst-load-stall");
		System.err.println("    -inst-cycle");
		System.err.println("    -cycle-ic-miss-stall");
		System.err.println("    -dcache");
		System.err.println("    -run n");
		System.exit(1);
	}
}
