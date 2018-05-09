// AU File Writer Class
// Written by: Craig A. Lindley
// Last Update: 09/03/98

package craigl.au;

import java.io.*;
import craigl.utils.*;

public class AUWrite implements FileWriterIF {

	public AUWrite(String fileName, int sampleRate, int numberOfChannels) {

		// Save incoming
		this.fileName = fileName;
		this.sampleRate = sampleRate;
		this.numberOfChannels = numberOfChannels;

		// Create buffer for reading samples
		sampleBuffer = new short[AudioConstants.SAMPLEBUFFERSIZE];
		
		// Create byte buffer for converting data
		sampleBufferBytes = new byte[AudioConstants.SAMPLEBUFFERSIZE * 2];
	}

	// Write the incoming samples into a 16 bit AU file
	// Writing terminates when samples are exhausted
	public boolean writeFile(AbstractAudio aa) {

		// Open output stream
		try {
			out = new RandomAccessFile(fileName, "rw");
		
			// Specified file is now open for writing
			// Create AU file header with zero data length
			byte [] header = build16BitAuHeader(sampleRate, numberOfChannels, 0);
				
			// Write the header to the file
			out.write(header);

			// Now write all the file data
			int totalBytes =0;

			// Read first buffer of samples
			int length = aa.previous.getSamples(sampleBuffer, AudioConstants.SAMPLEBUFFERSIZE);
			
			while (length > 0) {
				int index=0;
				for (int i=0; i < length; i++) {
					short sample = sampleBuffer[i];
					sampleBufferBytes[index++] = (byte) (sample >> 8);
					sampleBufferBytes[index++] = (byte) (sample & 255);
				}
				// Write bytes to output file
				out.write(sampleBufferBytes, 0, length * 2);
				
				// Update total byte count
				totalBytes += length * 2;
				
				// Read the next buffer full
				length = aa.previous.getSamples(sampleBuffer, AudioConstants.SAMPLEBUFFERSIZE);
			}
			// Go back to the start of the file and rewrite header
			out.seek(0);

			// New header has correct audio byte count
			header = build16BitAuHeader(sampleRate, numberOfChannels, totalBytes);
				
			// Write the header to the file
			out.write(header);

			// All done
			out.close();
			return true;
		}
		catch(IOException ioe) {
			System.out.println(ioe.getMessage());
			return false;
		}
	}

	// Synthesize an AU file header for writing to the file
	public static byte [] build16BitAuHeader(int sampleRate,
											 int channels,
											 int length) {
		// Header is 28 bytes long
		byte [] header = new byte[28];

		// Write magic string
		header[0] = (byte) '.';
		header[1] = (byte) 's';
		header[2] = (byte) 'n';
		header[3] = (byte) 'd';
		
		// Write offset to sound data of 28 bytes
		writeIntMsb(header, 4, 28, 4);

		// Write number of bytes of sound data. 
		writeIntMsb(header, 8, length, 4);

		// Write sound format 16 bit linear PCM
		writeIntMsb(header, 12, 3, 4);

		// Write sample rate
		writeIntMsb(header, 16, sampleRate, 4);

		// Write number of channels
		writeIntMsb(header, 20, channels, 4);

		// Write four bytes of padding
		writeIntMsb(header, 24, 0, 4);

		// Return the header as a byte array
		return header;
	}
	
	// Write big endian integer to buffer
	public static void writeIntMsb(byte [] buffer, int offset, int value, int size) {

		byte b1 = (byte)((value >> 24) & 255);
		byte b2 = (byte)((value >> 16) & 255);
		byte b3 = (byte)((value >>  8) & 255);
		byte b4 = (byte)((value      ) & 255);

		if (size == 2) {
			buffer[offset++] = b3;
			buffer[offset++] = b4;
		}	else	{
			buffer[offset++] = b1;
			buffer[offset++] = b2;
			buffer[offset++] = b3;
			buffer[offset++] = b4;
		}
	}

	// Private class data
	private String fileName;
	private int sampleRate;
	private int numberOfChannels;
	private short [] sampleBuffer;
	private byte  [] sampleBufferBytes;
	private RandomAccessFile out;
}

