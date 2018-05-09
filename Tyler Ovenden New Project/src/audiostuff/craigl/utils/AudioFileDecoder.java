// Audio File Decoder Base Class
// Written by: Craig A. Lindley
// Last Update: 04/25/99

package craigl.utils;

import java.io.*;
import craigl.utils.*;

public abstract class AudioFileDecoder extends AbstractAudio {

	private static final boolean debugging = false;

	public AudioFileDecoder(String fileName, ReadCompleteIF readCompleteIF) {
		super("Audio File Decoder", SOURCE);

		// Save incoming
		this.fileName = fileName;
		
		// Callback object 
		this.readCompleteIF = readCompleteIF;
	}

	// Stage told to reset
	public abstract void reset();
	
	// Read wave file and initialize appropriate decoder
	public abstract boolean initializeDecoder();
	
	/**
	 * Set the sample rate extracted from the file when negotiation
	 * occurs.
	 */
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {
		
		initializeDecoder();

		max.setValue(sampleRate);
		min.setValue(sampleRate);
		preferred.setValue(sampleRate);
	}

	/**
	 * Set the number of channels extracted from the file when negotiation
	 * occurs.
	 */
	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		initializeDecoder();

		min.setValue(numberOfChannels);
		max.setValue(numberOfChannels);
		preferred.setValue(numberOfChannels);
	}

	/**
	 * Given four integer values, generate a long containing the chunk
	 * name tag.
	 */
	public static long chunkName(int a, int b, int c, int d) {

		long l =	(((long) a & 255) << 24) +
					(((long) b & 255) << 16) +
					(((long) c & 255) <<  8) +
					 ((long) d & 255);
		return l;
	}

	// Protected class data
	protected String fileName;
	protected ReadCompleteIF readCompleteIF;
	protected int sampleRate;
	protected int numberOfChannels;
	protected ConvertDataInputStream cdis;
	protected AbstractDecoderIF decoder;
}

