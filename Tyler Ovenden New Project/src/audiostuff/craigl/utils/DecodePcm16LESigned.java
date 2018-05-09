// DecodePcm16LESigned Class
// Written by: Craig A. Lindley
// Last Update: 09/13/98

package craigl.utils;

import java.io.*;

public class DecodePcm16LESigned implements AbstractDecoderIF {

	public DecodePcm16LESigned(ConvertDataInputStream cdis,
								   long totalSamples) {
		// Save incoming
		this.cdis = cdis;
		this.totalSamples = totalSamples;
	}
	
	public String getName() {

		return "DecodePcm16LESigned decompressor";
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
			sampleBuffer[i] = (short)((((int) byteBuffer[index++]) & 255) + 
									  (((int) byteBuffer[index++]) << 8));
		}
		return count;
	}

	// Private class data
	private ConvertDataInputStream cdis;
	private long totalSamples;
}

