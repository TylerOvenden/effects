// ConvertDataInputStream Class for Audio Work
// Written by: Craig A. Lindley
// Last Update: 08/02/98

package audiostuff.craigl.utils;

import java.io.*;

public class ConvertDataInputStream extends RandomAccessFile {

	public ConvertDataInputStream(String fileName) 
		throws IOException {
		
		super(fileName, "r");
	}

	// Read in a variable length integer in big-endian format
	public long readBEInteger(int size) 
		throws IOException {

		int i1 = read();
		int i2 = read();

		long result = (i1 << 8) + i2;

		if (size == 4) {
			i1 = read();
			i2 = read();

			result = (result << 16) + (i1 << 8) + i2;
		}
		return result;
	}

	// Read in a variable length integer in little-endian format
	public long readLEInteger(int size)
		throws IOException {
		int i1 = read();
		int i2 = read();

		long result = (i2 << 8) + i1;

		if (size == 4) {
			i1 = read();
			i2 = read();

			result = (i2 << 24) + (i1 << 16) + result;
		}

		return result;
	}
}
