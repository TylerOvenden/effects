// Pitch Shifter With UI Processor
// Written by: Craig A. Lindley
// Last Update: 01/20/99

package craigl.processors;

import java.awt.*;
import craigl.beans.blinker.*;
import craigl.utils.*;

/*
The algorithm for this pitch shifter was adapted from the
article titled "Examining Audio DSP Algorithms" by
Dennis Cronin published in Dr. Dobb's Journal.
*/

public class PitchShifterWithUI extends AbstractAudio implements AudioUIIF {

	// Fixed delay period with which to do pitch shifting
	private static final int FIXEDDELAYINMS = 100;
	// Fade in/out times
	private static final int CROSSFADETIMEINMS = 12;
	// Constant by which one tone differs from the next when the
	// interval is a halftone.
	private static final double twelvethRootOfTwo = Math.pow(2, 1.0 / 12.0);
	
	// Class constructor
	public PitchShifterWithUI(Blinker blink) {
		
		super("PitchShifterWithUI", PROCESSOR);

		// Initialize various parameters
		initializationComplete = false;	// Initialization not yet performed
		sweepUp = true;		// Assume upward change in frequency
		sweep = 0.0;		// Initial value for sweep rate
		channelA = true;	// Setup to use channel A sweep
		blendA = 1.0;		// Blend values for the two delay channels
		blendB = 0.0;

        // Allocate local sample buffer
		localBuffer = new short[AudioConstants.SAMPLEBUFFERSIZE];

		// Create the UI for this processor
		psui = new PitchShifterUI(blink, this);
	}

	// Constructor used for phrase sampler
	public PitchShifterWithUI() {

		super("PitchShifterWithUI", PROCESSOR);

		// Initialize various parameters
		initializationComplete = false;	// Initialization not yet performed
		sweepUp = true;		// Assume upward change in frequency
		sweep = 0.0;		// Initial value for sweep rate
		channelA = true;	// Setup to use channel A sweep
		blendA = 1.0;		// Blend values for the two delay channels
		blendB = 0.0;

        // Allocate local sample buffer
		localBuffer = new short[AudioConstants.SAMPLEBUFFERSIZE];
	}

	// Process the samples that pass thru this effect		
	public int getSamples(short [] buffer, int length) {

		// Don't perform processing until initialization is
		// complete and bypass is not active.
		if (getByPass() || !initializationComplete)
			return previous.getSamples(buffer, length);

        // Read number of samples requested from previous stage
		int len = previous.getSamples(localBuffer, length);

		double delaySampleA, delaySampleB;

		// Do the processing over the new buffer of samples
		for (int i=0; i < len; i++) {
			
			// Get a sample to process
			long inputSample = localBuffer[i];
			
			// Grab four samples at a time. This is required for
			// interpolation and blending.
			long dsALow  = delayBuffer[readIndexALow];
			long dsAHigh = delayBuffer[readIndexAHigh];
			long dsBLow  = delayBuffer[readIndexBLow];
			long dsBHigh = delayBuffer[readIndexBHigh];

			// Do the linear interpolation
			if (sweepUp) {
				delaySampleA = (dsAHigh * sweep) + (dsALow * (1.0 - sweep));
				delaySampleB = (dsBHigh * sweep) + (dsBLow * (1.0 - sweep));
			}	else	{
				delaySampleA = (dsAHigh * (1.0 - sweep) + (dsALow * sweep));
				delaySampleB = (dsBHigh * (1.0 - sweep) + (dsBLow * sweep));
			}

			// Combine delay channels A and B with appropriate blending
			double outputSample = 
				(delaySampleA * blendA) + (delaySampleB * blendB);

			// Store sample in delay buffer
			delayBuffer[writeIndex] = (long)
				(inputSample + ((outputSample * feedbackLevel) / 100));
            
			// Update write index
			writeIndex = (writeIndex + 1) % delayBufferSize;

			// Prepare sample for output by combining wet and dry
			// values
			outputSample = 
				((inputSample  * dryLevel) / 100) +
				((outputSample * wetLevel) / 100);
			
			// Clamp output to legal range
			if (outputSample > 32767)
				outputSample = 32767;
			if (outputSample < -32768)
				outputSample = -32768;

			// Store output sample in outgoing buffer
			buffer[i] = (short) outputSample;

			// Update cross fade blending values each sample interval
			if (crossFadeCount != 0) {
				crossFadeCount--;
				
				// Get new blending values for both channels
				blendA = fadeA[crossFadeCount];
				blendB = fadeB[crossFadeCount];
			}

			// Update sweep value for each pass if processing
			// mono signal and every other pass if processing
			// stereo.
			if ((numberOfChannels == 1) || ((i + 1) % 2 == 0))
				sweep += step;

			if (sweepUp) {
				// Upward frequency change
	            
				// Advance indices to reduce delay
		        readIndexALow = readIndexAHigh;
				readIndexAHigh = (readIndexAHigh + 1) % delayBufferSize;
		        readIndexBLow = readIndexBHigh;
				readIndexBHigh = (readIndexBHigh + 1) % delayBufferSize;

				// Check for overflow
				if (sweep < 1.0) {
					// No overflow, continue with next sample
					continue;
				}
				
				// Octave exceeded bump ptrs again
				sweep = 0.0;
				readIndexALow = readIndexAHigh;
				readIndexAHigh = (readIndexAHigh + 1) % delayBufferSize;
				readIndexBLow = readIndexBHigh;
				readIndexBHigh = (readIndexBHigh + 1) % delayBufferSize;
				
				// See if it is time to switch to other delay channel
				if (activeCount-- == 0) {
					// Reset fade in/out count
					crossFadeCount = numberOfCrossFadeSamples;
					activeCount = activeSampleCount;
					if (channelA) {
						channelA = false;
						readIndexBHigh = 
							(writeIndex + AudioConstants.SAMPLEBUFFERSIZE) % delayBufferSize;
						// Swap blend coefficient arrays
						fadeA = fadeOut;
						fadeB = fadeIn;
					}	else	{
						channelA = true;
						readIndexAHigh = 
							(writeIndex + AudioConstants.SAMPLEBUFFERSIZE) % delayBufferSize;
						// Swap blend coefficient arrays
						fadeA = fadeIn;
						fadeB = fadeOut;
					}
				}
			
			}	else	{
				// Downward frequency change

				// Check for overflow
				if (sweep < 1.0) {
					// No overflow, advance indices
					readIndexALow = readIndexAHigh;
					readIndexAHigh = (readIndexAHigh + 1) % delayBufferSize;
					readIndexBLow = readIndexBHigh;
					readIndexBHigh = (readIndexBHigh + 1) % delayBufferSize;

					// Continue with processing the next sample
					continue;
				}
				// Octave exceeded don't bump indices so the delay
				// is increased
				sweep = 0.0;
				
				// See if it is time to switch to other delay channel
				if (activeCount-- == 0) {
					// Reset fade in/out count
					crossFadeCount = numberOfCrossFadeSamples;
					activeCount = activeSampleCount;
					if (channelA) {
						channelA = false;
						readIndexBHigh = 
							(writeIndex + AudioConstants.SAMPLEBUFFERSIZE) % delayBufferSize;
						// Swap blend coefficient arrays
						fadeA = fadeOut;
						fadeB = fadeIn;
					}	else	{
						channelA = true;
						readIndexAHigh = 
							(writeIndex + AudioConstants.SAMPLEBUFFERSIZE) % delayBufferSize;
						// Swap blend coefficient arrays
						fadeA = fadeIn;
						fadeB = fadeOut;
					}
				}
			}
		}
 		return len; 
	}

	// Called when the user changes the dry level. 
	public void setDryLevel(int dryLevel) {

		// Value in the range 0..100
		this.dryLevel = dryLevel;
	}

	// Called when the user changes the wet level. 
	public void setWetLevel(int wetLevel) {

		// Value in the range 0..100
		this.wetLevel = wetLevel;
	}

	// Called when the user changes the feedback level.
	public void setFeedbackLevel(int feedbackLevel) {

		// Value in the range 0..100
		this.feedbackLevel = feedbackLevel;
	}

	// Called when the user changes the pitch shift value
	public void setPitchShift(int pitchShift) {

		// Values are in half steps (semitones) in the 
		// range -12..0..+12 corresponding to -/+ 1 octave for
		// a range of 2 octaves.

		// Determine which direction the sweep is going
		sweepUp = (pitchShift >= 0);

		setIndices();

		double newStep = 1.0;

		// If pitch shift is 0 short circuit calculations
		if (pitchShift == 0)
			step = 0;

		else	{
			// Step is rate at which samples read out
			for (int i=0; i < Math.abs(pitchShift); i++) {
				if (pitchShift > 0)
					newStep *= twelvethRootOfTwo;
				else
					newStep /= twelvethRootOfTwo;
			}
			step = Math.abs(newStep - 1.0);
		}
		// Reset the following values whenever pitch shift value changes
		sweep = 0.0;
		crossFadeCount = 0;
		activeSampleCount = numberOfDelaySamples - 
			(int)(numberOfCrossFadeSamples * (newStep - 1.0) - 2); 
	}

	// Set read/write indices depending upon audio format and
	// frequency change direction
	private void setIndices() {

		// Index where dry sample is written
	    writeIndex = 0;
		readIndexBLow = 0;
		readIndexBHigh = 0;

		if (sweepUp) {
			// Sweeping upward, start at max delay
		    readIndexALow = AudioConstants.SAMPLEBUFFERSIZE;
		
		}	else	{
		
			// Sweeping downward, start at min delay
			if (numberOfChannels == 1)
				readIndexALow = delayBufferSize - 2;
			else
				readIndexALow = delayBufferSize - 4;
		}
		// Initialize other read ptr
		if (numberOfChannels == 1)
			readIndexAHigh = readIndexALow + 1;
		else
			readIndexAHigh = readIndexALow + 2;
	}
  	
	// Do necessary initialization as required for pitch shifting
	private void doInitialization() {

		// See if we have the necessary data to initialize delay
		if ((sampleRate != 0) && (numberOfChannels != 0) &&
            (!initializationComplete)) {

			// Allocate delay buffer for the fixed delay time
			numberOfDelaySamples = 
				(FIXEDDELAYINMS * sampleRate * numberOfChannels) / 1000;

			// Total buffer size
			delayBufferSize = AudioConstants.SAMPLEBUFFERSIZE + numberOfDelaySamples;
            
			// Allocate new delay buffer
			delayBuffer = new long[delayBufferSize];

            // Initialize indices in the delay buffer
		    setIndices();

			// Calculate the number of cross fade samples
			numberOfCrossFadeSamples = 
				(CROSSFADETIMEINMS * sampleRate) / 1000;
			
			// Allocate arrays for cross fade coefficients
			fadeIn  = new double[numberOfCrossFadeSamples];
			fadeOut = new double[numberOfCrossFadeSamples];
			
			// Fill in the arrays with fade in/out values. Sin and Cos
			// values are used for smooth results.
			for (int i=0; i < numberOfCrossFadeSamples; i++) {
				double angle = (i * Math.PI) / (2.0 * numberOfCrossFadeSamples);
				fadeIn[i]  = Math.cos(angle);
				fadeOut[i] = Math.sin(angle);
			}
            // Indicate initialization is complete
			initializationComplete = true;
		}
	}
	
	// Negotiate the sample rate
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxSamplingRate(min, max, preferred);
		sampleRate = preferred.getValue();
		doInitialization();
	}

	// Negotiate the number of channels
	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxChannels(min, max, preferred);
		numberOfChannels = preferred.getValue();
	}
		
	// The methods that follow are required for the AudioUIIF
	// interface.
	
	public void showUI(boolean isVisible) {
        
        psui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        setByPass(true);
        psui.dispose();
    }

    public Rectangle getBounds() {

        return psui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        psui.setBounds(x, y, w, h);
    }

	// Private class data
	private boolean initializationComplete;
	private boolean sweepUp;
	private double step;
	private double sweep;
	private int dryLevel;
	private int wetLevel;
	private int feedbackLevel;

	private PitchShifterUI psui = null;
	private int sampleRate = 0;
	private int numberOfChannels = 0;
	private int delayBufferSize = 0;
	private short [] localBuffer = null;
	private long  [] delayBuffer = null;
    private int readIndexALow;
    private int readIndexAHigh;
    private int readIndexBLow;
    private int readIndexBHigh;
    private int writeIndex;
	private int numberOfDelaySamples;
	private int numberOfCrossFadeSamples;
	private int crossFadeCount;
	private int activeSampleCount;
	private int activeCount;
	private boolean channelA;
	private double blendA;
	private double blendB;

	private double [] fadeIn;
	private double [] fadeOut;
	private double [] fadeA;
	private double [] fadeB;
}
