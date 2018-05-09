// DecodeG711MuLaw Class 
// Written by: Craig A. Lindley
// Last Update: 04/25/99

package craigl.utils;

import java.io.*;

public class DecodeG711MuLaw implements AbstractDecoderIF {

	public DecodeG711MuLaw(ConvertDataInputStream cdis,
							   long totalSamples) {

		// Save incoming
		this.cdis = cdis;
		this.totalSamples = totalSamples;

		// Prepare mulaw decoding table for converting 8 bit mulaw samples
		// to 16 bit samples
		if (muLawTable == null) {
			// Allocate space for table
			muLawTable = new short[256];

			// And initialize it with sample values
			for (int i=0; i < 256; i++)
				muLawTable[i] = createMuLawTableEntry(i);
		}
	}
	
	public String getName() {

		return "DecodeG711MuLaw decompressor";
	}

	// Create a mu law sample value corresponding to binary index
	private short createMuLawTableEntry(int index) {

		index = ~index;
		int exponent = (index >> 4) & 0x7;
		int mantissa = (index & 0xF) + 16;
		int adjusted = (mantissa << (exponent + 3)) - 128 - 4;
		
		return (short)(((index & 0x80) != 0) ? adjusted : -adjusted);
	}

	public int getSamples(short [] sampleBuffer, int length) {

		if (totalSamples == 0)
			return -1;

		if (length > totalSamples) {
			length = (int) totalSamples;
			totalSamples = 0;
		}	else
			totalSamples -= length;
		
		byte [] byteBuffer = new byte[length];
		
		// Read the data
		int bytesRead;
		try {
			bytesRead = cdis.read(byteBuffer, 0, length);
			if (bytesRead == -1)
				return -1;
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
			return -1;
		}
			
		for (int i=0; i < bytesRead; i++) {
			// Make unsigned index for table lookup
			int index = (int) byteBuffer[i] + 128;

			// Lookup the sample value to return
			sampleBuffer[i] = muLawTable[index];
		} 
		return bytesRead;
	}

	// Private class data
	private ConvertDataInputStream cdis;
	private long totalSamples;
	private static short [] muLawTable = null;
}
