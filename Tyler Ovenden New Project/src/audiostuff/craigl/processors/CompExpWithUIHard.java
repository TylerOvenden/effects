// Compressor/Expander/Limiter/Noise Gate Class
// Written by: Craig A. Lindley
// Last Update: 11/09/99

// This version of CompExpWithUI.java illustrates implementation
// of a hard transition between the no compression and compression states.
// The soft knee version is contained in the file CompExpWithUI.java.

package audiostuff.craigl.processors;

import java.awt.Rectangle;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.compexp.*;
import audiostuff.craigl.utils.*;

public class CompExpWithUIHard extends AbstractAudio implements AudioUIIF {

	// Set to true to output debug messages
	private static final boolean DEBUG = false;
	
	// Class constructor
	public CompExpWithUIHard(Blinker blink) {
		super("Compressor/Expander/Limiter/Noise Gate Processor",
			   PROCESSOR);

		// Initialization will take place after sample rate is known
		initializationComplete = false;

		// Create the UI for this processor
		ceui = new CompExpUI(blink, this);
	}

	public int getSamples(short [] buffer, int length) {

		// Get samples from previous stage
		int len = previous.getSamples(buffer, length);
		
		// If bypass is enabled, short circuit processing
		if (getByPass() || !initializationComplete) 
			return len;

		// We have samples to process
		for (int i=0; i < len; i++) {

			// Get a sample
			double sample = (double) buffer[i];

			if (Math.abs(sample) >= thresholdValue) {
				// Sample value exceeds threshold

				releaseCount++;
				releaseCount %= (calcReleaseCount + 1);

				if (attackExpired) {
					// Attack satisfied, process sample
					if (!limiting)
						sample *= atRatio;
					else
						sample = (sample < 0) ? 
							-thresholdValue : thresholdValue;

				}	else	{
					attackCount--;
					if (attackCount <= 0) {
						attackExpired = true;
						releaseCount = calcReleaseCount;
					}
				}

			}	else	{
				// Sample value did not exceed threshold
				if (attackExpired) {
					if (!limiting)
						sample *= atRatio;

					releaseCount--;
					if (releaseCount <= 0) {
						attackExpired = false;
						attackCount = calcAttackCount;
					}
				}	else	{
					attackCount++;
					attackCount %= (calcAttackCount + 1);
				}
				// Now process below threshold noise gating
				sample *= btRatio;
			}
			// Apply gain
			sample *= gain;

			// Range check results
			if (sample > 32767.0)
				sample = 32767.0;
			else if (sample < -32768.0)
				sample = -32768.0;

			// Store sample back into buffer
			buffer[i] = (short) sample;
		}
		// Return count of sample processed
		return len;
	}

	// These methods called when UI controls are manipulated
	public void setThreshold(double thresholdInDB) {

		// thresholdValue is the sample value which is thresholdInDB
		// below the maximum value of 32767.0
		thresholdValue = Math.pow(10, thresholdInDB / 20.0) * 32767.0;
		pdb("thresholdValue: " + thresholdValue);
	}
	
	public void setBelowThresholdRatio(double ratio) {

		// Check for noise gating function
		gating = (ratio >= CompExpUI.MINBTRATIO);
		pdb("gating: " + gating);
		
		// A noise gate clamps output to zero
		if (gating)
			btRatio = 0.0;
		else 
			btRatio = 1.0 / ratio;

		pdb("btRatio: " + btRatio);
	}
	
	public void setAboveThresholdRatio(double dBRatio) {

		limiting = (dBRatio <= CompExpUI.MINATRATIO);
		pdb("limiting: " + limiting);

		atRatio = Math.pow(10, dBRatio / 20);
		pdb("atRatio: " + atRatio);
	}
	
	public void setAttack(double attackInMs) {

		this.attackInMs = attackInMs;
		calcAttackCount = (int)(channels * attackInMs * sampleRate / 1000);
		attackCount = calcAttackCount;
		pdb("attackCount: " + attackCount);
	}
	
	public void setRelease(double releaseInMs) {

		this.releaseInMs = releaseInMs;
		calcReleaseCount = (int)(channels * releaseInMs * sampleRate / 1000);
		releaseCount = calcReleaseCount;
		pdb("releaseCount: " + releaseCount);
	}
	
	public void setGain(double gainInDb) {

		this.gain = Math.pow(10, gainInDb / 20);
		pdb("gain: " + gain);

	}
	
	// Perform calculations that require a known sample rate
	private void doInitialization() {

		calcAttackCount = (int)(channels * attackInMs * sampleRate / 1000);
		attackCount = calcAttackCount;

		calcReleaseCount= (int)(channels * releaseInMs * sampleRate / 1000);
		releaseCount = calcReleaseCount;

		// Indicate initialization is complete
		initializationComplete = true;
	}
	
	// Grab value of sample rate during negotiation
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxSamplingRate(min, max, preferred);
		
		sampleRate = preferred.getValue();

		// Cannot do initialization until sample rate is known
		doInitialization();
	}

	// Grab number of channels during negotiation
	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxChannels(min, max, preferred);
		
		channels = preferred.getValue();
	}

	public void showUI(boolean isVisible) {
        
        ceui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        setByPass(true);
    }

    public Rectangle getBounds() {

        return ceui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        ceui.setBounds(x, y, w, h);
    }

	public void pdb(String s) {

		if (DEBUG)
			System.out.println(s);
	}
	
	
	
	// Private class data
	private CompExpUI ceui = null;
	private boolean initializationComplete;
	private int sampleRate = 0;
	private int channels = 1;
	private double thresholdValue = 32767.0;
	private double btRatio = 1.0;
	private double atRatio = 1.0;
	private double attackInMs = 0;
	private double releaseInMs = 0;
	private double attackCount = 0;
	private double releaseCount = 0;
	private double gain = 1.0;
	private boolean limiting = false;
	private boolean gating = false;

	private int calcAttackCount = 0;
	private int calcReleaseCount = 0;
	private boolean attackExpired = false;
}
