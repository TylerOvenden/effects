// DecodePcm8UnSigned Class
// Written by: Craig A. Lindley
// Last Update: 04/25/99

package audiostuff.craigl.utils;

import java.io.*;

public class DecodePcm8UnSigned implements AbstractDecoderIF {

	public DecodePcm8UnSigned(ConvertDataInputStream cdis,
							  long totalSamples) {

		// Save incoming
		this.cdis = cdis;
		this.totalSamples = totalSamples;
	}
	
	public String getName() {

		return "DecodePcm8UnSigned decompressor";
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
			int s = (int) byteBuffer[i];
			sampleBuffer[i] = (short)((s - 128) << 8);
		} 
		return bytesRead;
	}
	
	// Private class data
	private ConvertDataInputStream cdis;
	private long totalSamples;
}
