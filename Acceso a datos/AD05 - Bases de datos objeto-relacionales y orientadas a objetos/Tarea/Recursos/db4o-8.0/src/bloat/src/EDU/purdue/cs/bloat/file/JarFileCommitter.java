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
package EDU.purdue.cs.bloat.file;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

import EDU.purdue.cs.bloat.reflect.*;

/**
 * Does a lot of the same stuff as <tt>ClassFileLoader</tt>, but classes are
 * committed to a JAR file instead of regular files.
 */
public class JarFileCommitter extends ClassFileLoader {

	private FunkyJar funky;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *            <tt>File</tt> representing JAR file
	 * @param compress
	 *            If <tt>true</tt>, contents of JAR file is compressed
	 * @param version
	 *            Version for the JAR file's manifest
	 * @param author
	 *            Author string from JAR file's manifest
	 */
	public JarFileCommitter(final File file, final boolean compress,
			final String version, final String author) throws IOException {

		funky = new FunkyJar(file, compress, version, author);
	}

	protected OutputStream outputStreamFor(final String name)
			throws IOException {

		funky.newEntry(name);
		return funky;
	}

	public OutputStream outputStreamFor(final ClassInfo info)
			throws IOException {
		// This is funky. Recall that a JarOutputStream is also an output
		// stream. So, we just return it. This is why we have to
		// override the write, etc. methods.

		// Make a new entry based on the class name
		final String name = info.name() + ".class";
		return outputStreamFor(name);
	}

	/**
	 * Signifies that we are finished with this <tt>JarFileCommitter</tt>.
	 */
	public void done() throws IOException {
		funky.done();
	}
}

/**
 * We subclass JarOutputStream so that we can return an OutputStream to which a
 * BLOATed class file will be written. In order to accomodate non-compression,
 * we have to perform the checksum along the way. Bletch.
 */
class FunkyJar extends JarOutputStream {

	private static final String MANIFEST = JarFile.MANIFEST_NAME;

	private static final String MANIFEST_DIR = "META-INF/";

	private static final CRC32 crc32 = new CRC32();

	private boolean compress;

	private JarEntry currEntry;

	private Size size;

	class Size {
		long value = 0;
	}

	/**
	 * Constructor.
	 */
	public FunkyJar(final File file, boolean compress, final String version,
			final String author) throws IOException {
		super(new FileOutputStream(file));

		this.compress = compress;

		if (compress) {
			this.setMethod(ZipOutputStream.DEFLATED);
		} else {
			this.setMethod(ZipOutputStream.STORED);
		}

		final Manifest manifest = new Manifest();
		final Attributes global = manifest.getMainAttributes();
		if (global.getValue(Attributes.Name.MANIFEST_VERSION) == null) {
			global.put(Attributes.Name.MANIFEST_VERSION, version);
		}

		if (global.getValue(new Attributes.Name("Created-By")) == null) {
			global.put(new Attributes.Name("Created-By"), author);
		}

		// Add directory for manifest
		JarEntry entry = new JarEntry(FunkyJar.MANIFEST_DIR);
		entry.setTime(System.currentTimeMillis());
		entry.setSize(0); // Directories have size 0
		entry.setCrc(0); // Checksum is 0
		this.putNextEntry(entry);

		// Add manifest
		entry = new JarEntry(FunkyJar.MANIFEST);
		entry.setTime(System.currentTimeMillis());
		if (!compress) {
			// Have to compute checksum ourselves. Use an ugly anonymous
			// inner class. Influenced by CRC32OutputStream in
			// sun.tools.jar.Main. Please don't sue me. I have no money.
			// Maybe you could give me a job instead. Of course, then I'd
			// have money and you would sue me. Hmm.
			final Size size = new Size();
			FunkyJar.crc32.reset();
			manifest.write(new OutputStream() {
				public void write(final int r) throws IOException {
					FunkyJar.crc32.update(r);
					size.value++;
				}

				public void write(final byte[] b) throws IOException {
					FunkyJar.crc32.update(b, 0, b.length);
					size.value += b.length;
				}

				public void write(final byte[] b, final int off, final int len)
						throws IOException {
					FunkyJar.crc32.update(b, off, len);
					size.value += len - off;
				}
			});
			entry.setSize(size.value);
			entry.setCrc(FunkyJar.crc32.getValue());
		}
		this.putNextEntry(entry);
		manifest.write(this); // Write the manifest to JAR file
		this.closeEntry();
	}

	public void newEntry(final String name) throws IOException {
		makeDirs(name);

		currEntry = new JarEntry(name);
		currEntry.setTime(System.currentTimeMillis());
		if (compress) {
			currEntry.setMethod(ZipEntry.DEFLATED);
		} else {
			currEntry.setMethod(ZipEntry.STORED);
		}
		this.putNextEntry(currEntry);
		FunkyJar.crc32.reset();
		this.size = new Size();
	}

	private Set dirs;

	/**
	 * look at the path name specified by key and create zip entries for each
	 * directory level not already added.
	 */
	private void makeDirs(final String key) throws IOException {
		if (dirs == null) {
			dirs = new HashSet();
		}
		int idx = 0;
		int last = 0;
		while ((last = key.indexOf('/', idx + 1)) != -1) {
			final String aDir = key.substring(0, last + 1);
			if (!dirs.contains(aDir)) {
				dirs.add(aDir);
				this.putNextEntry(new ZipEntry(aDir));
				this.closeEntry();
			}
			idx = last;
		}
	}

	public void write(final int r) throws IOException {
		super.write(r);

		if (!compress && (size != null)) {
			FunkyJar.crc32.update(r);
			size.value++;
		}
	}

	public void write(final byte[] b) throws IOException {
		super.write(b);

		if (!compress && (size != null)) {
			FunkyJar.crc32.update(b, 0, b.length);
			size.value += b.length;
		}
	}

	public void write(final byte[] b, final int off, final int len)
			throws IOException {
		super.write(b, off, len);

		if (!compress && (size != null)) {
			FunkyJar.crc32.update(b, off, len);
			size.value += len - off;
		}
	}

	public void close() throws IOException {
		// Okay, everythings is done. Set some values for the entry,
		// cross your fingers, and run away.
		if (!compress && (size != null)) {
			currEntry.setSize(size.value);
			currEntry.setCrc(FunkyJar.crc32.getValue());
		}

		currEntry = null;
		size = null;
		this.closeEntry();

		// Note that we don't invoke the super class method.
	}

	/**
	 * Signifies that we are finished with this <tt>JarFileCommitter</tt>.
	 */
	public void done() throws IOException {
		super.close();
	}
}
