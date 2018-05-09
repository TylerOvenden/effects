// AURead Class for reading AU files
// Written by: Craig A. Lindley
// Last Update: 04/25/99

package craigl.au;

import java.io.*;
import craigl.utils.*;
import craigl.wave.*;

public class AURead extends AudioFileDecoder {

	public AURead(String fileName, ReadCompleteIF readCompleteIF) {
		super(fileName, readCompleteIF);
		
		o("AU File: " + fileName);

		cdis = null;
		decoder = null;
	}

	// Determine if file is a AU file
	public static boolean isAUFile(String fileName) {

		// Open input stream
		try {
			// Create data stream
			ConvertDataInputStream cdis = new ConvertDataInputStream(fileName);
		
			// Read signature from file
			long signature = cdis.readBEInteger(4);

			// Close the file
			cdis.close();
			
			// See if there is a signature match
			return (signature == WaveRead.chunkName('.','s','n','d'));
		}
		catch(IOException ioe) {
			o(ioe.getMessage());
			return false;
		}
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

			// Read signature from file
			long signature = cdis.readBEInteger(4);
			
			if (signature != WaveRead.chunkName('.','s','n','d')) {
				cdis.close();
				return false;
			}
			// Read the header information
			offset				= cdis.readBEInteger(4);
			totalBytes			= cdis.readBEInteger(4);
			format				= (int) cdis.readBEInteger(4);
			sampleRate			= (int) cdis.readBEInteger(4);
			numberOfChannels	= (int) cdis.readBEInteger(4);
			pad					= cdis.readBEInteger(4);

			long totalSamples = totalBytes;
			// if 16 bits adjust sample count
			if (format == 3)
				totalSamples /= 2;
			
			// Now select appropriate decoder
			if (format == 1) {
				// Mu-law format
				decoder = new DecodeG711MuLaw(cdis, totalSamples);
			}
			else if (format == 2) {
				// 8 bit linear PCM format
				decoder = new DecodePcm8Signed(cdis, totalSamples);
			}	
			else if (format == 3) {
				// 16 bit linear PCM format
				decoder = new DecodePcm16BESigned(cdis, totalSamples);
			}
			else	{
				o("Don't support AU format: " +  format);
				System.exit(1);
				return false;
			}
			// Signal all is well			
			return true;
		}
		catch(IOException ioe) {
			o(ioe.getMessage());
			return false;
		}
	}
	// Private class data
	private long offset;
	private long totalBytes;
	private int  format;
	private long pad;
}

