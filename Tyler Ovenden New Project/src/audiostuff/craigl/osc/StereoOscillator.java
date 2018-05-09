// StereoOscillator class
// Written by: Craig A. Lindley
// Last Update: 08/22/98

package craigl.osc;

import craigl.utils.*;

// NOTE: 16 bit PCM data has a min value of -32768 (8000H)
//       and a max value of 32767 (7FFFFH).

public class StereoOscillator extends Oscillator {

	public StereoOscillator(int type, int frequencyL, int frequencyR,
							int sampleRate, 
							NegotiationCompleteIF negCompleteIF) {

		super(type, 0, sampleRate, 2, negCompleteIF);
		
		// Save incoming
		this.frequencyL = frequencyL;
		this.frequencyR = frequencyR;

		toggle = false;
		posL = posR = 0;

		leftAmplitudeAdj  = 1.0;
		rightAmplitudeAdj = 1.0;
	}

	// Constructor with reasonable defaults
	public StereoOscillator(NegotiationCompleteIF negCompleteIF) {
		
		this(SINEWAVE, 440, 880, 22050, negCompleteIF);
	}

	public int getSamples(short [] buffer, int length) {

		int sample = 0;
		int count = length;

		while(count-- != 0) {

			if (!toggle) {
				
				toggle = true;

				buffer[sample++] = (short)(leftAmplitudeAdj * waveTable[posL]);

				posL += frequencyL;
				if (posL >= sampleRate)
					posL -= sampleRate;
			
			}	else	{

				toggle = false;

				buffer[sample++] = (short)(rightAmplitudeAdj * waveTable[posR]);

				posR += frequencyR;
				if (posR >= sampleRate)
					posR -= sampleRate;
			}
		}
		return length;
	}

	public int getLeftFrequency() {

		return frequencyL;
	}

	public void setLeftFrequency(int frequency) {

		this.frequencyL = frequency;
		
		// Reset waveTable index
		posL = 0;
	}

	public int getRightFrequency() {

		return frequencyR;
	}

	public void setRightFrequency(int frequency) {

		this.frequencyR = frequency;
		
		// Reset waveTable index
		posR = 0;
	}

	public double getLeftAmplitudeAdj() {

		return leftAmplitudeAdj;
	}

	public void setLeftAmplitudeAdj(double amplitudeAdj) {

		this.leftAmplitudeAdj = amplitudeAdj;
	}

	public double getRightAmplitudeAdj() {

		return rightAmplitudeAdj;
	}

	public void setRightAmplitudeAdj(double amplitudeAdj) {

		this.rightAmplitudeAdj = amplitudeAdj;
	}

	// Private class data
	private int frequencyL;
	private int frequencyR;
	private int posL;
	private int posR;
	private boolean toggle;
	private double leftAmplitudeAdj;
	private double rightAmplitudeAdj;
}