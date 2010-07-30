package org.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IoUtil {

	public static void moveFile(File from, File to)
			throws FileNotFoundException, IOException {

		// Serious problem that renameTo do not work across partitions in Linux!
		// We fall back to copy the file if renameTo() failed.
		if (!from.renameTo(to)) {
			copyFile(from, to);
			from.delete();
		}
	}

	public static void copyFile(File from, File to)
			throws FileNotFoundException, IOException {
		InputStream in = new FileInputStream(from);
		OutputStream out = new FileOutputStream(to);
		// OutputStream out = new FileOutputStream(f2, true); // Append

		copyStream(in, out);
	}

	public static void copyStream(InputStream in, OutputStream out)
			throws IOException {
		try {
			int len;
			byte[] buf = new byte[1024];
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);
		} finally {
			in.close();
			out.close();
		}
	}

}