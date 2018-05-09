// ConvertDataOutputStream Class for Audio Work
// Written by: Craig A. Lindley
// Last Update: 08/02/98

package craigl.utils;

import java.io.*;

public class ConvertDataOutputStream extends RandomAccessFile {

	public ConvertDataOutputStream(String fileName)
		throws IOException {
		
		super(fileName, "rw");
	}
	
	// Write a variable length integer in big-endian format
	public void writeBEInteger(int i, int size) 
		throws IOException {

		int i1 = (i >> 24) & 255;
		int i2 = (i >> 16) & 255;
		int i3 = (i >>  8) & 255;
		int i4 = (i      ) & 255;

		if (size == 2) {
			write(i3);
			write(i4);
		}	else	{
			write(i1);
			write(i2);
			write(i3);
			write(i4);
		}
	}

	// Write a variable length integer in little-endian format
	public void writeLEInteger(int i, int size)
		throws IOException {
		
		int i1 = (i >> 24) & 255;
		int i2 = (i >> 16) & 255;
		int i3 = (i >>  8) & 255;
		int i4 = (i      ) & 255;

		if (size == 2) {
			write(i4);
			write(i3);
		}	else	{
			write(i4);
			write(i3);
			write(i2);
			write(i1);
		}
	}
}
