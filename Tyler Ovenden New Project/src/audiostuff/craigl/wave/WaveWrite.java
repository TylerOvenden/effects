// Wave File Writer Class
// Written by: Craig A. Lindley
// Last Update: 04/25/99

package audiostuff.craigl.wave;

import java.io.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.au.*;

public class WaveWrite implements FileWriterIF {

	private static final int WAVEHDRSIZE = 44;
	
	public WaveWrite(String fileName, int sampleRate, int numberOfChannels) {

		// Save incoming
		this.fileName = fileName;
		this.sampleRate = sampleRate;
		this.numberOfChannels = numberOfChannels;

		// Create buffer for reading samples
		sampleBuffer = new short[AudioConstants.SAMPLEBUFFERSIZE];
		
		// Create byte buffer for converting data
		sampleBufferBytes = new byte[AudioConstants.SAMPLEBUFFERSIZE * 2];
	}

	// Write the incoming samples into a 16 bit Wave file
	// Writing terminates when samples are exhausted
	public boolean writeFile(AbstractAudio aa) {

		// Open output stream
		try {
			out = new RandomAccessFile(fileName, "rw");
		
			// Specified file is now open for writing
			// Create Wave file header with zero data length
			byte [] header = 
				build16BitWaveHeader(sampleRate, numberOfChannels, 0, 0);
				
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
					sampleBufferBytes[index++] = (byte) (sample & 255);
					sampleBufferBytes[index++] = (byte) (sample >> 8);
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
			header = 
				build16BitWaveHeader(sampleRate, numberOfChannels,
									 totalBytes + WAVEHDRSIZE - 8,
									 totalBytes);
				
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

	// Synthesize an Wave file header for writing to the file
	public static byte [] build16BitWaveHeader(int sampleRate,
											   int channels,
											   int fileLength,
											   int dataLength) {
		// Header is 44 bytes long
		byte [] header = new byte[WAVEHDRSIZE];

		// Write RIFF tag
		long tag = WaveRead.chunkName('R', 'I', 'F', 'F');
		AUWrite.writeIntMsb(header, 0, (int) tag, 4);

		// Write RIFF chunk size 
		writeIntLsb(header, 4, fileLength, 4);

		// Write WAVE tag
		tag = WaveRead.chunkName('W', 'A', 'V', 'E');
		AUWrite.writeIntMsb(header, 8, (int) tag, 4);

		// Write fmt_ tag
		tag = WaveRead.chunkName('f', 'm', 't', ' ');
		AUWrite.writeIntMsb(header, 12, (int) tag, 4);

		// Write fmt_ chunk size
		int d = 16;
		writeIntLsb(header, 16, d, 4);

		// Write format type. Type 1 is PCM.
		d = 1;
		writeIntLsb(header, 20, d, 2);

		// Write number of channels
		writeIntLsb(header, 22, channels, 2);

		// Write sample rate
		writeIntLsb(header, 24, sampleRate, 4);

		int bitsPerSample  = 16;
		int bytesPerSample = 2;
		
		// Write avg bytes per second
		int avgBytesPerSecond = channels * bytesPerSample * sampleRate;
		writeIntLsb(header, 28, avgBytesPerSecond, 4);

		// Write block alignment
		writeIntLsb(header, 32, bytesPerSample * channels, 2);

		// Write bitsPerSample
		writeIntLsb(header, 34, bitsPerSample, 2);

		// Write data tag
		tag = WaveRead.chunkName('d', 'a', 't', 'a');
		AUWrite.writeIntMsb(header, 36, (int) tag, 4);

		// Write data chunk size 
		writeIntLsb(header, 40, dataLength, 4);

		// Return the header as a byte array
		return header;
	}
	
	// Write little endian integer to buffer
	private static void writeIntLsb(byte [] buffer, int offset, int value, int size) {

		byte b1 = (byte)((value >> 24) & 255);
		byte b2 = (byte)((value >> 16) & 255);
		byte b3 = (byte)((value >>  8) & 255);
		byte b4 = (byte)((value      ) & 255);

		if (size == 2) {
			buffer[offset++] = b4;
			buffer[offset++] = b3;
		}	else	{
			buffer[offset++] = b4;
			buffer[offset++] = b3;
			buffer[offset++] = b2;
			buffer[offset++] = b1;
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

