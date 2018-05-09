// Delay With UI Processor
// Written by: Craig A. Lindley
// Last Update: 11/11/98

package craigl.processors;

import java.awt.*;
import craigl.beans.blinker.*;
import craigl.utils.*;

public class DelayWithUI extends AbstractAudio implements AudioUIIF {

	public DelayWithUI(Blinker blink) {
		
		super("DelayWithUI", PROCESSOR);

		initializationComplete = false;

        // Allocate local sample buffer
		localBuffer = new short[AudioConstants.SAMPLEBUFFERSIZE];

		// Create the UI for the delay
		dui = new DelayUI(blink, this);
	}

	// Process the samples that pass thru this effect		
	public int getSamples(short [] buffer, int length) {

		if (getByPass() || !initializationComplete)
			return previous.getSamples(buffer, length);

        // Read number of samples requested from previous stage
		int len = previous.getSamples(localBuffer, length);

		// Do the processing
		for (int i=0; i < len; i++) {
			int inputSample = (int) localBuffer[i];
			int delaySample = (int) delayBuffer[readIndex++];
            int outputSample =
				((inputSample * dryLevel) / 100) +
				((delaySample * wetLevel) / 100);
			
			// Clamp output to legal range
			if (outputSample > 32767)
				outputSample = 32767;
			else if (outputSample < -32768)
				outputSample = -32768;

			// Store in output sample
			buffer[i] = (short) outputSample;

			// Calculate feedback
			inputSample += (delaySample * feedbackLevel) / 100;

			// Clamp output to legal range
			if (inputSample > 32767)
				inputSample = 32767;
			else if (inputSample < -32768)
				inputSample = -32768;

			delayBuffer[writeIndex++] = (short) inputSample;

            // Update indices
            readIndex  %= delayBufferSize;
            writeIndex %= delayBufferSize;
		}
 		return len; 
	}

	public void setDelayInMs(int delayInMs) {

		initializationComplete = false;
		this.delayInMs = delayInMs;

		doInitialization();
	}

	public void setDryLevel(int dryLevel) {

		this.dryLevel = dryLevel;
	}

	public void setWetLevel(int wetLevel) {

		this.wetLevel = wetLevel;
	}

	public void setFeedbackLevel(int feedbackLevel) {

		this.feedbackLevel = feedbackLevel;
	}

	public void doInitialization() {

		// See if we have the necessary data to initialize delay
		if ((sampleRate != 0) && (numberOfChannels != 0) &&
            (!initializationComplete)) {

			// Allocate delay buffer
			int delayOffset = 
				(delayInMs * sampleRate * numberOfChannels) / 1000;

			delayBufferSize = AudioConstants.SAMPLEBUFFERSIZE + delayOffset;
            
			// Allocate new delay buffer
			delayBuffer = new short[delayBufferSize];

            // Initialize indices
		    // Index where dry sample is written
		    writeIndex = 0;
			
		    // Index where wet sample is read
		    readIndex = AudioConstants.SAMPLEBUFFERSIZE;

            // Indicate initialization is complete
			initializationComplete = true;
		}
	}
	
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxSamplingRate(min, max, preferred);
		
		sampleRate = preferred.getValue();
		System.out.println("sampleRate: " + sampleRate);

		doInitialization();
	}

	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxChannels(min, max, preferred);
		
		numberOfChannels = preferred.getValue();
		System.out.println("Channels: " + numberOfChannels);
	}

		
	public void showUI(boolean isVisible) {
        
        dui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        setByPass(true);
        dui.dispose();
    }

    public Rectangle getBounds() {

        return dui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        dui.setBounds(x, y, w, h);
    }

	// Private class data
	private boolean initializationComplete = false;
	private int delayInMs;
	private int dryLevel;
	private int wetLevel;
	private int feedbackLevel;

	private DelayUI dui = null;
	private int sampleRate = 0;
	private int numberOfChannels = 0;
	private int delayBufferSize = 0;
	private short [] localBuffer = null;
	private short [] delayBuffer = null;
    private int readIndex;
    private int writeIndex;
}
