// WaveRead Class for reading WAV files
// Written by: Craig A. Lindley
// Last Update: 04/25/99

package audiostuff.craigl.wave;

import java.io.*;
import audiostuff.craigl.utils.*;

public class WaveRead extends AudioFileDecoder {

	private static final boolean debugging = false;

	public WaveRead(String fileName, ReadCompleteIF readCompleteIF) {
		super(fileName, readCompleteIF);

		o("Wave File: " + fileName);

		cdis = null;
		decoder = null;
	}

	// Determine if file is a wave file
	public static boolean isWaveFile(String fileName) {

		ConvertDataInputStream cdis = null;

		// Open input stream
		try {
			cdis = new ConvertDataInputStream(fileName);
		
			long form  = cdis.readBEInteger(4);
			
			if (form != chunkName('R','I','F','F')) {
				cdis.close();
				return false;
			}

			cdis.skipBytes(4);			// Skip chunk size

			long type = cdis.readBEInteger(4);
			cdis.close();

			if (type != chunkName('W','A','V','E')) 
				return false;
		}
		catch(IOException ioe) {
			o(ioe.getMessage());
			return false;
		}
		return true;				// Got a WAV file
	}

	// Provide samples to subsequent stages of processing
	public int getSamples(short [] buffer, int length) {

		if (decoder == null) {
			// Attempt to initialize decoder
			if (!initializeDecoder())
				return -1;
		}
		// Read samples through the decoder
		int samplesRead = decoder.getSamples(buffer, length);

		// See if we have reached the end of the samples
		if (samplesRead <= 0) {
			try {
				// Close file and prepare for it to be reopened
				cdis.close();
				decoder = null;
				// Do call back for signaling
				if (readCompleteIF != null)
					readCompleteIF.signalReadComplete();
			}
			catch(IOException ioe) {}
		}
		return samplesRead;
	}

	// Stage told to reset
	public void reset() {

		o("WaveRead reset");
		if (cdis != null) {
			try {
				// Close file and prepare for it to be reopened
				cdis.close();
				decoder = null;
				// Do call back for signaling
				if (readCompleteIF != null)
					readCompleteIF.signalReadComplete();
			}
			catch(IOException ioe) {}
		}
	}
	
	// Read wave file and initialize appropriate decoder
	public boolean initializeDecoder() {

		// See if we have a decoder already
		if (decoder != null)
			return true;

		// Open input stream
		try {
			cdis = new ConvertDataInputStream(fileName);

			// Read first chunk info
			long chunkType = cdis.readBEInteger(4);
			long chunkSize = cdis.readLEInteger(4);

			// First chunk better be RIFF
			if (chunkType != chunkName('R','I','F','F')) {
				cdis.close();
				return false;
			}

			// Container must be WAVE
			chunkType = cdis.readBEInteger(4);
			if (chunkType != chunkName('W','A','V','E')) {
				cdis.close();
				return false;
			}
			// Now we must locate a fmt and a data chunk to continue
			int chunksFound = 0;
			long dataPosition = 0;
			
			while (chunksFound != 0x03) {
				
				// Read next chunk
				chunkType = cdis.readBEInteger(4);
				chunkSize = cdis.readLEInteger(4);
				
				// A fmt chunk ?
				if (chunkType == chunkName('f','m','t',' ')) {
					// Found a format chunk
					chunksFound |= 0x01;

					// Read the important format parameters
					format				= (int) cdis.readLEInteger(2);
					numberOfChannels	= (int) cdis.readLEInteger(2);
					sampleRate 			= (int) cdis.readLEInteger(4);
					avgBytesPerSecond	= (int) cdis.readLEInteger(4);
					blockAlignment		= (int) cdis.readLEInteger(2);
					bitsPerSample		= (int) cdis.readLEInteger(2);

					if (chunkSize > 16)
						cdis.skipBytes((int) (chunkSize - 16));

					if (debugging) {
						o("format: " + format);
						o("numberOfChannels: " + numberOfChannels);
						o("sampleRate: " + sampleRate);
						o("avgBytesPerSecond: " + avgBytesPerSecond);
						o("blockAlignment: " + blockAlignment);
						o("bitsPerSample: " + bitsPerSample);
					}
				}

 				// A data chunk ?
				else if (chunkType == chunkName('d','a','t','a')) {
					// Found a data chunk
					chunksFound |= 0x02;

					// Save position of data in the file
					dataPosition = cdis.getFilePointer();
					
					// Calculate total samples to process
					totalSamples = chunkSize;
					if (bitsPerSample == 16)
						totalSamples /= 2;
				}

				// Some other kind of chunk
				else	{
					// Not a chunk we care about so skip it
					cdis.skipBytes((int) chunkSize);
				}
			}
			// If we get here we have the chunks we need
			// Determine if we can decode the file
			switch (format) {

				case 1:		// PCM format
					// Now determine the decoder to use
					if (bitsPerSample <= 8)			// 8 bit data
						decoder = new DecodePcm8UnSigned(cdis, totalSamples);
					else if (bitsPerSample <= 16)	// 16 bit data
						decoder = new DecodePcm16LESigned(cdis, totalSamples);
					else {
						o("Don't support " + bitsPerSample + " bits per sample");
						System.exit(1);
						return false;
					}
					break;

				case 7:		// 8 bit G711 mulaw format
					decoder = new DecodeG711MuLaw(cdis, totalSamples);
					break;

				default:
					// Don't support this format
					o("Don't support WAV format: " +  format);
					System.exit(1);
					return false;
			}
			// Position to data in file
			cdis.seek(dataPosition);
 
			// Signal all is well
			return true;
		} catch(IOException ioe) {
			o(ioe.getMessage());
			return false;
		}
	}
	// Private class data
	private int format;
	private int avgBytesPerSecond;
	private int blockAlignment;
	private int bitsPerSample;
	private long totalSamples;
}

