// Chorus With UI Processor
// Written by: Craig A. Lindley
// Last Update: 05/16/99

package craigl.processors;

import java.awt.*;
import craigl.beans.blinker.*;
import craigl.utils.*;

public class ChorusWithUI extends AbstractAudio implements AudioUIIF {

	public ChorusWithUI(Blinker blink) {
		
		super("ChorusWithUI", PROCESSOR);

		initializationComplete = false;
		isSinLFO = false;
		invertPhase = false;

        // Allocate local sample buffer
		localBuffer = new short[AudioConstants.SAMPLEBUFFERSIZE];

		// Create the UI for the delay
		cui = new ChorusUI(blink, this);
	}

	// Process the samples that pass thru this effect		
	public int getSamples(short [] buffer, int length) {

		if (getByPass() || !initializationComplete)
			return previous.getSamples(buffer, length);

        // Read number of samples requested from previous stage
		int len = previous.getSamples(localBuffer, length);
		if (len == -1)
			return -1;

		if (numberOfChannels == 1)
			return processMonoSamples(localBuffer, buffer, len);
		else
			return processStereoSamples(localBuffer, buffer, len);
	}

	// Process mono samples
	protected int processMonoSamples(short [] localBuffer, 
									 short [] buffer, int len) {
		
		// Do the processing
		for (int i=0; i < len; i++) {

			// Fetch the input samples from the local buffer
			int inputSample = (int) localBuffer[i];

			// Calculate sample offsets for fetching two samples
			double sampleOffset1 = sweepValue - halfDepthInSamples;
			double sampleOffset2 = sampleOffset1 - 1;
			
			// Calculate delta for linear interpolation
			double delta = Math.abs((int) sampleOffset1 - sampleOffset1);

			int actualIndex1 = readIndex   + (int) sampleOffset1;
			int actualIndex2 = readIndex++ + (int) sampleOffset2;
			boolean underflow1 = (actualIndex1 < 0);
			boolean underflow2 = (actualIndex2 < 0);

			// Adjust indices for possible under/over flow
			if (underflow1)				
				actualIndex1 += delayBufferSize;
			else
				actualIndex1 %= delayBufferSize;

			if (underflow2) 
				actualIndex2 += delayBufferSize;
			else
				actualIndex2 %= delayBufferSize;
		
			// Fetch two samples and interpolate
			int delaySample1 = (int) delayBuffer[actualIndex1];
			int delaySample2 = (int) delayBuffer[actualIndex2];
			int delaySample  = (int) (delaySample2 * delta +
							          delaySample1 * (1.0 - delta));
			// Sum wet and dry portions of the output
			int outputSample =
				((inputSample * dryLevel) / 100) +
				((delaySample * wetLevel) / 100);
			
			// Clamp output to legal range
			if (outputSample > 32767)
				outputSample = 32767;
			else if (outputSample < -32768)
				outputSample = -32768;

			// Store output sample
			buffer[i] = (short) outputSample;

			// Calculate sample for storage in delay buffer
			inputSample += 
				(delaySample * feedbackLevel * (invertPhase ? -1:+1)) / 100;

			// Store sample
			delayBuffer[writeIndex++] = inputSample;

            // Update indices
            readIndex  %= delayBufferSize;
            writeIndex %= delayBufferSize;

			// Calculate new sweep value
			if (isSinLFO) {
				// LFO is sinusoidal
				sampleNumber %= sampleRate;
				sweepValue = halfDepthInSamples * 
					Math.sin(radiansPerSample * sampleNumber++);

			}	else	{
				
				// LFO is triangular
				sweepValue += step;

				// Keep sweep in range
				if ((sweepValue >=  halfDepthInSamples) ||
					(sweepValue <= -halfDepthInSamples)) {
					// Change direction of sweep
					step *= -1;
				}
			}
		}
 		return len; 
	}

	// Process stereo samples
	protected int processStereoSamples(short [] localBuffer, 
									   short [] buffer, int len) {
		// Do the processing
		for (int i=0; i < len / 2; i++) {

			// Fetch the input samples from the local buffer
			int leftInputSample  = (int) localBuffer[2 * i];
			int rightInputSample = (int) localBuffer[2 * i + 1];

			// Calculate sample offsets for fetching two samples
			double sampleOffset1 = sweepValue - halfDepthInSamples;
			double sampleOffset2 = sampleOffset1 - 1;
			
			// Calculate delta for linear interpolation
			double delta = Math.abs((int) sampleOffset1 - sampleOffset1);

			int actualIndex1 = readIndex   + (int) sampleOffset1;
			int actualIndex2 = readIndex++ + (int) sampleOffset2;
			boolean underflow1 = (actualIndex1 < 0);
			boolean underflow2 = (actualIndex2 < 0);

			// Adjust indices for possible under/over flow
			if (underflow1)				
				actualIndex1 += delayBufferSize;
			else
				actualIndex1 %= delayBufferSize;

			if (underflow2) 
				actualIndex2 += delayBufferSize;
			else
				actualIndex2 %= delayBufferSize;

			// Fetch two samples and interpolate
			int leftDelaySample1 = (int) leftDelayBuffer[actualIndex1];
			int leftDelaySample2 = (int) leftDelayBuffer[actualIndex2];
			int leftDelaySample  = (int) (leftDelaySample2 * delta +
							              leftDelaySample1 * (1.0 - delta));
			
			int rightDelaySample1 = (int) rightDelayBuffer[actualIndex1];
			int rightDelaySample2 = (int) rightDelayBuffer[actualIndex2];
			int rightDelaySample  = (int)(rightDelaySample2 * delta +
							              rightDelaySample1 * (1.0 - delta));            
		
			// Sum wet and dry portions of the output
			int leftOutputSample =
				((leftInputSample * dryLevel) / 100) +
				((leftDelaySample * wetLevel) / 100);
			
			int rightOutputSample =
				((rightInputSample * dryLevel) / 100) +
				((rightDelaySample * wetLevel) / 100);
			
			// Clamp output to legal range
			if (leftOutputSample > 32767)
				leftOutputSample = 32767;
			else if (leftOutputSample < -32768)
				leftOutputSample = -32768;

			if (rightOutputSample > 32767)
				rightOutputSample = 32767;
			else if (rightOutputSample < -32768)
				rightOutputSample = -32768;

			// Store in output samples
			buffer[2 * i]     = (short) leftOutputSample;
			buffer[2 * i + 1] = (short) rightOutputSample;

			// Calculate samples for storage in delay buffer
			leftInputSample  += 
				(leftDelaySample  * feedbackLevel * (invertPhase ? -1:+1)) / 100;
			rightInputSample += 
				(rightDelaySample * feedbackLevel * (invertPhase ? -1:+1)) / 100;

			// Store samples
			leftDelayBuffer[writeIndex]    = leftInputSample;
			rightDelayBuffer[writeIndex++] = rightInputSample;

            // Update indices
            readIndex  %= delayBufferSize;
            writeIndex %= delayBufferSize;
			
			// Calculate new sweep value
			if (isSinLFO) {
				// LFO is sinusoidal
				sampleNumber %= sampleRate;
				sweepValue = halfDepthInSamples * 
					Math.sin(radiansPerSample * sampleNumber++);

			}	else	{
				
				// LFO is triangular
				sweepValue += step;

				// Keep sweep in range
				if ((sweepValue >=  halfDepthInSamples) ||
					(sweepValue <= -halfDepthInSamples)) {
					// Change direction of sweep
					step *= -1;
				}
			}
		}
 		return len; 
	}

	// Set a new delay value from UI
	public void setDelayInMs(int delayInMs) {

		this.delayInMs = delayInMs;

		initializationComplete = false;

		doInitialization();
	}

	// Set a new LFO rate from UI
	public void setRateInHz(double rateInHz) {

		this.rateInHz = rateInHz;

		// Calculate step size
		calculateStepSize();
	}

	// Set a new LFO mode from the UI
	public void setLFOMode(boolean isSinLFO) {

		this.isSinLFO = isSinLFO;

		// Calculate step size
		calculateStepSize();
	}

	// Set a new depth value from UI
	public void setDepthLevel(double depthInMs) {

		halfDepth = depthInMs / 2.0;

		// Calculate step size
		calculateStepSize();
	}

	// Calculate new sweep value from LFO rate and depth
	private void calculateStepSize() {

		// Calculate half depth in samples
		halfDepthInSamples = (halfDepth * sampleRate) / 1000;
		
		sweepValue = 0.0;
		
		// Calculations for triangle wave
		double periodInSamples = (1.0 / rateInHz) * sampleRate;
		double quarterPeriod = periodInSamples / 4.0;

		step = halfDepthInSamples / quarterPeriod;

		// Calculations for sin wave
		sampleNumber = 0;
		radiansPerSample = (2 * Math.PI * rateInHz) / sampleRate;
	}
	
	// Set a new dry level value from UI
	public void setDryLevel(int dryLevel) {

		this.dryLevel = dryLevel;
	}

	// Set a new wet level value from UI
	public void setWetLevel(int wetLevel) {

		this.wetLevel = wetLevel;
	}

	// Set feedback phase from UI
	public void setFeedbackPhase(boolean invertPhase) {

		this.invertPhase = invertPhase;
	}

	// Set a new feedback level value from UI
	public void setFeedbackLevel(int feedbackLevel) {

		this.feedbackLevel = feedbackLevel;
	}

	// Calculate buffer sizes from delay values
	public void doInitialization() {

		// See if we have the necessary data to initialize delay
		if ((sampleRate != 0) && (numberOfChannels != 0) &&
            (!initializationComplete)) {

			// Calculate number of samples required for delay
			int delayOffset = (delayInMs * sampleRate) / 1000;
			
			if (numberOfChannels == 1) {
				// We're doing a mono signal
				// Calculate buffer size required
				delayBufferSize = AudioConstants.SAMPLEBUFFERSIZE +
					delayOffset;

				// Allocate new delay buffer
				delayBuffer = new int[delayBufferSize];

				// Initialize indices
				// Index where dry sample is written
				writeIndex = 0;
				
				// Index where wet sample is read
				readIndex = AudioConstants.SAMPLEBUFFERSIZE;
			
			}	else	{

				// We're doing a stereo signal
				// Calculate buffer size required
				int halfBufferSize = AudioConstants.SAMPLEBUFFERSIZE / 2;
				delayBufferSize = halfBufferSize + delayOffset;

				// Allocate new delay buffers
				leftDelayBuffer  = new int[delayBufferSize];
				rightDelayBuffer = new int[delayBufferSize];

				// Initialize indices
				// Index where dry sample is written
				writeIndex  = 0;
				
				// Index where wet sample is read
				readIndex = halfBufferSize;
			}
			// Indicate initialization is complete
			initializationComplete = true;
		}
	}

	// Trap sample rate during negotiation
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxSamplingRate(min, max, preferred);
		
		sampleRate = preferred.getValue();
		doInitialization();
		calculateStepSize();
	}

	// Trap number of channels during negotiation
	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxChannels(min, max, preferred);
		numberOfChannels = preferred.getValue();
	}
		
	public void showUI(boolean isVisible) {
        
        cui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        setByPass(true);
    }

    public Rectangle getBounds() {

        return cui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        cui.setBounds(x, y, w, h);
    }

	// Private class data
	private boolean initializationComplete = false;
	private int delayInMs;
	private double halfDepth = 1;
	private double halfDepthInSamples;
	private double rateInHz;
	private boolean isSinLFO;
	private boolean invertPhase;
	private double step;
	private double sweepValue = 0;
	private int sampleNumber = 0;
	private double radiansPerSample;

	private int depthLevel;
	private int dryLevel;
	private int wetLevel;
	private int feedbackLevel;

	private ChorusUI cui = null;
	private int sampleRate = 0;
	private int numberOfChannels = 0;
	private int delayBufferSize;

	private short [] localBuffer = null;
	private int [] delayBuffer = null;
	private int [] leftDelayBuffer = null;
	private int [] rightDelayBuffer = null;
    
	private int readIndex;
    private int writeIndex;
}
