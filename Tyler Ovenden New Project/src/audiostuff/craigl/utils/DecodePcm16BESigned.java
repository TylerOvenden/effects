// DecodePcm16BESigned Class
// Written by: Craig A. Lindley
// Last Update: 04/25/99

package craigl.utils;

import java.io.*;

public class DecodePcm16BESigned implements AbstractDecoderIF {

	public DecodePcm16BESigned(ConvertDataInputStream cdis,
								   long totalSamples) {

		// Save incoming
		this.cdis = cdis;
		this.totalSamples = totalSamples;
	}
	
	public String getName() {

		return "DecodePcm16BESigned decompressor";
	}
	
	public int getSamples(short [] sampleBuffer, int length) {

		if (totalSamples == 0)
			return -1;

		if (length > totalSamples) {
			length = (int) totalSamples;
			totalSamples = 0;
		}	else
			totalSamples -= length;
		
		int byteLength = 2 * length;
		byte [] byteBuffer = new byte[byteLength];
		
		// Read the data
		int bytesRead;
		try {
			bytesRead = cdis.read(byteBuffer, 0, byteLength);
			if (bytesRead == -1)
				return -1;
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
			return -1;
		}
		int count = bytesRead / 2;
		int index = 0;

		for (int i=0; i < count; i++) {
			sampleBuffer[i] = (short)((((int) byteBuffer[index++]) << 8) + 
											 (byteBuffer[index++] & 255));
		}
		return count;
	}

	// Private class data
	private ConvertDataInputStream cdis;
	private long totalSamples;
}

