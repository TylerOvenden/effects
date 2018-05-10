// Oscillator class
// Written by: Craig A. Lindley
// Last Update: 04/22/99

package audiostuff.craigl.osc;

import java.util.Random;
import audiostuff.craigl.utils.*;

// NOTE: 16 bit PCM data has a min value of -32768 (8000H)
//       and a max value of 32767 (7FFFFH).

public class Oscillator extends AbstractAudio {

	public static final int NOTYPE			= 0;
	public static final int NOISE			= 1;
	public static final int SINEWAVE		= 2;
	public static final int TRIANGLEWAVE	= 3;
	public static final int SQUAREWAVE		= 4;

	public Oscillator(int type, int frequency,
					  int sampleRate, int numberOfChannels,
					  NegotiationCompleteIF negCompleteIF) {
		super("Oscillator", SOURCE);
		
		// Save incoming
		this.type = type;
		this.frequency = frequency;
		this.sampleRate = sampleRate;
		this.numberOfChannels = numberOfChannels;
		this.negCompleteIF = negCompleteIF;

		// Set amplitude adjustment
		amplitudeAdj = 1.0;

		// Table of samples for oscillator waveform
		waveTable = null;

		// Generate wave table
		buildWaveTable();
	}

	// Constructor with reasonable defaults
	public Oscillator(NegotiationCompleteIF negCompleteIF) {
		
		this(SINEWAVE, 1000, 22050, 1, negCompleteIF);
	}

	public int getOscType() {

		return type;
	}

	public void setOscType(int type) {

		this.type = type;

		buildWaveTable();
	}

	public int getFrequency() {

		return frequency;
	}

	public void setFrequency(int frequency) {

		this.frequency = frequency;
		
		// Reset waveTable index
		pos = 0;
	}

	public int getSampleRate() {

		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {

		this.sampleRate = sampleRate;
		
		buildWaveTable();
	}

	public int getNumberOfChannels() {

		return numberOfChannels;
	}

	public void setNumberOfChannels(int numberOfChannels) {

		this.numberOfChannels = numberOfChannels;
	}

	public double getAmplitudeAdj() {

		return amplitudeAdj;
	}

	public void setAmplitudeAdj(double amplitudeAdj) {

		this.amplitudeAdj = amplitudeAdj;
	}

	// Generate a wavetable for the waveform
	protected void buildWaveTable() {

		if (type == NOTYPE) 
			return;
		
		// Initialize waveTable index as wave table is changing
		pos = 0;

		// Allocate a table for 1 cycle of waveform
		waveTable = new short[sampleRate];

		switch(type) {
			case NOISE:
				// Create a random number generator for returning gaussian
				// distributed numbers. The result is white noise.
				Random random = new Random();
				
				for (int sample = 0; sample < sampleRate; sample++)
					waveTable[sample] = (short)((65535.0 * random.nextGaussian()) - 32768);
				break;

			case SINEWAVE:
				double scale = (2.0 * Math.PI) / sampleRate;
			
				for (int sample = 0; sample < sampleRate; sample++) 
					waveTable[sample] = (short)(32767.0 * Math.sin(sample * scale));

				break;

			case TRIANGLEWAVE:
				double sign  = 1.0;
				double value = 0.0;

				int oneQuarterWave = sampleRate / 4;
				int threeQuarterWave = (3 * sampleRate) / 4;

				scale = 32767.0 / oneQuarterWave;

				for (int sample = 0; sample < sampleRate; sample++) { 
				
					if ((sample > oneQuarterWave) && (sample <= threeQuarterWave))
						sign = -1.0;
					else
						sign = 1.0;

					value += sign * scale;
					waveTable[sample] = (short) value;
				}
				break;

			case SQUAREWAVE:
				for (int sample = 0; sample < sampleRate; sample++) {
					if (sample < sampleRate / 2)
						waveTable[sample] = 32767;
					else
						waveTable[sample] = -32768;
				}
				break;
		}
	}

	public int getSamples(short [] buffer, int length) {

		int sample = 0;
		int count = length;

		while(count-- != 0) {

			buffer[sample++] = (short)(amplitudeAdj * waveTable[pos]);

			pos += frequency;
			if (pos >= sampleRate)
				pos -= sampleRate;
		}
		return length;
	}

	// We know this is first device in chain so no propagation
	// is necessary.
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {
		
		// Use the sample rate passed in
		max.setValue(sampleRate);
		min.setValue(sampleRate);
		preferred.setValue(sampleRate);

		// Determine if there is a listener interested in whether
		// negotations have been completed or not.
		if (negCompleteIF != null)
			negCompleteIF.signalNegotiationComplete();
	}

	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		min.setValue(numberOfChannels);
		max.setValue(numberOfChannels);
		preferred.setValue(numberOfChannels);
	}

	// Class data
	protected int type;
	protected int frequency;
	protected int sampleRate;
	protected int numberOfChannels;
	protected NegotiationCompleteIF negCompleteIF;
	protected int pos;
	protected short [] waveTable;
	protected double amplitudeAdj;
}