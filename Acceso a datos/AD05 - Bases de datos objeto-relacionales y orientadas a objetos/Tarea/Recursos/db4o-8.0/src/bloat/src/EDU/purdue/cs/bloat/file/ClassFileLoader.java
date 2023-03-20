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
import java.util.zip.*;
import java.net.URL;

import EDU.purdue.cs.bloat.reflect.*;

/**
 * ClassFileLoder provides an interface for loading classes from files. The
 * actual loading is done by the ClassFile itself.
 * <p>
 * Classes may be specified by their full package name (<tt>java.lang.String</tt>),
 * or by the name of their class file (<tt>myclasses/Test.class</tt>). The
 * class path may contain directories or Zip or Jar files. Any classes that are
 * written back to disk ("committed") are placed in the output directory.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class ClassFileLoader implements ClassInfoLoader {
	public static boolean DEBUG = false;

	public static boolean USE_SYSTEM_CLASSES = true;

	private File outputDir; // Directory in which to write committed class files

	private String classpath; // Path to search for classes

	private Map openZipFiles; // zip files to search for class files

	private LinkedList cache; // We keep a cache of CACHE_LIMIT class files

	private boolean verbose;

	private static final int CACHE_LIMIT = 10;

	private ClassSource _classSource;

	public ClassFileLoader(ClassSource classSource) {
		outputDir = new File(".");
		classpath = System.getProperty("java.class.path");
		classpath += File.pathSeparator
				+ System.getProperty("sun.boot.class.path");
		if (ClassFileLoader.USE_SYSTEM_CLASSES) {
			classpath += File.pathSeparator
					+ System.getProperty("java.sys.class.path");
		}
		openZipFiles = new HashMap();
		cache = new LinkedList();
		verbose = false;
		_classSource = classSource;
	}
	
	/**
	 * Constructor. The classpath initially consists of the contents of the
	 * <tt>java.class.path</tt> and <tt>sun.boot.class.path</tt> system
	 * properties.
	 */
	public ClassFileLoader() {
		this(new DefaultClassSource());
	}

	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Sets the classpath.
	 */
	public void setClassPath(final String classpath) {
		this.classpath = classpath;
	}

	/**
	 * Adds to the classpath (CLASSPATH = CLASSPATH + morePath).
	 */
	public void appendClassPath(final String morePath) {
		this.classpath += File.pathSeparator + morePath;
	}

	/**
	 * Adds to the classpath (CLASSPATH = morePath + CLASSPATH).
	 */
	public void prependClassPath(final String morePath) {
		this.classpath = morePath + File.pathSeparator + this.classpath;
	}

	/**
	 * Returns the path used to search for class files.
	 */
	public String getClassPath() {
		return (this.classpath);
	}

	/**
	 * Load the class from a stream.
	 * 
	 * @param inputFile
	 *            The file from which to load the class.
	 * @param stream
	 *            The stream from which to load the class.
	 * @return A ClassInfo for the class.
	 * @exception ClassNotFoundException
	 *                The class cannot be found in the class path.
	 */
	private ClassInfo loadClassFromStream(final File inputFile,
			final InputStream stream) throws ClassNotFoundException {

		final DataInputStream in = new DataInputStream(stream);
		final ClassFile file = new ClassFile(inputFile, this, in);

		return file;
	}

	/**
	 * Load the class from the file.
	 * 
	 * @param file
	 *            The File from which to load a class.
	 * @return A ClassInfo for the class.
	 * @exception ClassNotFoundException
	 *                The class cannot be found in the class path.
	 */
	private ClassInfo loadClassFromFile(final File file)
			throws ClassNotFoundException {
		try {
			final InputStream in = new FileInputStream(file);

			final ClassInfo info = loadClassFromStream(file, in);

			if (verbose) {
				System.out.println("[Loaded " + info.name() + " from "
						+ file.getPath() + "]");
			}

			try {
				in.close();
			} catch (final IOException ex) {
			}

			return info;
		} catch (final FileNotFoundException e) {
			throw new ClassNotFoundException(file.getPath());
		}
	}

	/**
	 * Loads all of the classes that are contained in a zip (or jar) file.
	 * Returns an array of the <tt>ClassInfo</tt>s for the classes in the zip
	 * file.
	 */
	public ClassInfo[] loadClassesFromZipFile(final ZipFile zipFile)
			throws ClassNotFoundException {
		final ClassInfo[] infos = new ClassInfo[zipFile.size()];

		// Examine each entry in the zip file
		final Enumeration entries = zipFile.entries();
		for (int i = 0; entries.hasMoreElements(); i++) {
			final ZipEntry entry = (ZipEntry) entries.nextElement();
			if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
				continue;
			}

			try {
				final InputStream stream = zipFile.getInputStream(entry);
				final File file = new File(entry.getName());

				infos[i] = loadClassFromStream(file, stream);

			} catch (final IOException ex) {
				System.err.println("IOException: " + ex);
			}
		}

		return (infos);
	}

	public ClassInfo newClass(final int modifiers, final int classIndex,
			final int superClassIndex, final int[] interfaceIndexes,
			final List constants) {
		return new ClassFile(modifiers, classIndex, superClassIndex,
				interfaceIndexes, constants, this);
	}

    /**
     * Thhis method tries to load a Class by its ressource.
     * @param name the Name of the Class
     * @return the ClassInfo
     */
    private ClassInfo loadClassFromRessource(String name){
        name = name.replace('/','.');
        try {
            Class clazz = _classSource.loadClass(name);
            int i = name.lastIndexOf('.');
            if (i >= 0 && i < name.length()){
                name = name.substring(i+1);
            }
            URL url = clazz.getResource(name + ".class");
            if (url != null){
                return loadClassFromStream(new File(url.getFile()), url.openStream());
            }
        } catch (Exception e) {}
        return null;
    }

    /**
	 * Loads the class with the given name. Searches the class path, including
	 * zip files, for the class and then returns a data stream for the class
	 * file.
	 * 
	 * @param name
	 *            The name of the class to load, including the package name.
	 * @return A ClassInfo for the class.
	 * @exception ClassNotFoundException
	 *                The class cannot be found in the class path.
	 */
	public ClassInfo loadClass(String name) throws ClassNotFoundException {
		ClassInfo file = null;

		// Check to see if name ends with ".class". If so, load the class from
		// that file. Note that this is okay because we can never have a class
		// named "class" (i.e. a class named "class" with a lower-case 'c' can
		// never be specified in a fully-specified java class name) because
		// "class" is a reserved word.

		if (name.endsWith(".class")) {
			final File nameFile = new File(name);

			if (!nameFile.exists()) {
				throw new ClassNotFoundException(name);

			} else {
				return (loadClassFromFile(nameFile));
			}
		}

        if  ((file = loadClassFromRessource(name)) != null){
            addToCache(file);
            return file;
        }

        // Otherwise, we have a (possibly fully-specified) class name.
		name = name.replace('.', '/');

		// Check the cache for the class file.
		if (ClassFileLoader.DEBUG) {
			System.out
					.println("  Looking for " + name + " in cache = " + cache);
		}

		final Iterator iter = cache.iterator();

		while (iter.hasNext()) {
			file = (ClassFile) iter.next();

			if (name.equals(file.name())) {
				if (ClassFileLoader.DEBUG) {
					System.out.println("  Found " + file.name() + " in cache");
				}

				// Move to the front of the cache.
				iter.remove();
				cache.addFirst(file);

				return file;
			}
		}

		file = null;

		final String classFile = name.replace('/', File.separatorChar)
				+ ".class";

		// For each entry in the class path, search zip files and directories
		// for classFile. When found, open an InputStream and break
		// out of the loop to read the class file.
		final String path = classpath + File.pathSeparator;

		if (ClassFileLoader.DEBUG) {
			System.out.println("CLASSPATH = " + path);
		}

		int index = 0;
		int end = path.indexOf(File.pathSeparator, index);

		SEARCH: while (end >= 0) {
			final String dir = path.substring(index, end);

			File f = new File(dir);

			if (f.isDirectory()) {
				// The directory is really a directory. If the class file
				// exists, open a stream and return.
				f = new File(dir, classFile);

				if (f.exists()) {
					try {
						final InputStream in = new FileInputStream(f);

						if (verbose) {
							System.out.println("  [Loaded " + name + " from "
									+ f.getPath() + "]");
						}

						file = loadClassFromStream(f, in);

						try {
							in.close();

						} catch (final IOException ex) {
						}

						break SEARCH;

					} catch (final FileNotFoundException ex) {
					}
				}

			} else if (dir.endsWith(".zip") || dir.endsWith(".jar")) {
				// Maybe a zip file?
				try {
					ZipFile zip = (ZipFile) openZipFiles.get(dir);

					if (zip == null) {
						zip = new ZipFile(f);
						openZipFiles.put(dir, zip);
					}

					final String zipEntry = classFile.replace(
							File.separatorChar, '/');

					final ZipEntry entry = zip.getEntry(zipEntry);

					if (entry != null) {
						// Found the class file in the zip file.
						// Open a stream and return.
						if (verbose) {
							System.out.println("  [Loaded " + name + " from "
									+ f.getPath() + "]");
						}

						final InputStream in = zip.getInputStream(entry);
						file = loadClassFromStream(f, in);

						try {
							in.close();

						} catch (final IOException ex) {
						}
						break SEARCH;
					}
				} catch (final ZipException ex) {
				} catch (final IOException ex) {
				}
			}

			index = end + 1;
			end = path.indexOf(File.pathSeparator, index);
		}

		if (file == null) {
			// The class file wasn't in the class path. Try the currnet
			// directory. If not there, give up.
			final File f = new File(classFile);

			if (!f.exists()) {
				throw new ClassNotFoundException(name);
			}

			if (verbose) {
				System.out.println("  [Loaded " + name + " from " + f.getPath()
						+ "]");
			}

			try {
				final InputStream in = new FileInputStream(f);
				file = loadClassFromStream(f, in);

				try {
					in.close();
				} catch (final IOException ex) {
				}
			} catch (final FileNotFoundException ex) {
				throw new ClassNotFoundException(name);
			}
		}

		if (file == null) {
			throw new ClassNotFoundException(name);
		}

		addToCache(file);

		return file;
	}

	private void addToCache(ClassInfo file) {
		// If we've reached the cache size limit, remove the oldest file
		// in the cache. Then add the new file.
		if (cache.size() == ClassFileLoader.CACHE_LIMIT) {
			cache.removeLast();
		}

		cache.addFirst(file);
	}

	/**
	 * Set the directory into which commited class files should be written.
	 * 
	 * @param dir
	 *            The directory.
	 */
	public void setOutputDir(final File dir) {
		outputDir = dir;
	}

	/**
	 * Get the directory into which commited class files should be written.
	 */
	public File outputDir() {
		return outputDir;
	}

	/**
	 * Writes a bunch of <code>byte</code>s to an output entry with the given
	 * name.
	 */
	public void writeEntry(final byte[] bytes, final String name)
			throws IOException {
		final OutputStream os = outputStreamFor(name);
		os.write(bytes);
		os.flush();
		os.close();
	}

	/**
	 * Returns an <tt>OutputStream</tt> to which a class file should be
	 * written.
	 */
	public OutputStream outputStreamFor(final ClassInfo info)
			throws IOException {
		// Format the name of the output file
		final String name = info.name().replace('/', File.separatorChar)
				+ ".class";
		return outputStreamFor(name);
	}

	/**
	 * Returns an <code>OutputStream</code> to which somed named entity is
	 * written. Any forward slashes in the name are replaced by
	 * <code>File.separatorChar</code>.
	 */
	protected OutputStream outputStreamFor(String name) throws IOException {

		name = name.replace('/', File.separatorChar);

		final File f = new File(outputDir, name);

		if (f.exists()) {
			f.delete();
		}

		final File dir = new File(f.getParent());
		dir.mkdirs();

		if (!dir.exists()) {
			throw new RuntimeException("Couldn't create directory: " + dir);
		}

		return (new FileOutputStream(f));
	}

	/**
	 * Signifies that we are done with this <code>ClassFileLoader</code>
	 */
	public void done() throws IOException {
		// Nothing for this guy
	}
}
