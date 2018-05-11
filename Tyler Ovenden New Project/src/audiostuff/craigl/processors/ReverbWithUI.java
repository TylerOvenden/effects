// Schroeder Reverb with UI Class
// Written by: Craig A. Lindley
// Last Update: 11/11/98

package audiostuff.craigl.processors;

import java.awt.Rectangle;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.reverb.*;
import audiostuff.craigl.utils.*;

public class ReverbWithUI extends AbstractAudio implements AudioUIIF {

	public ReverbWithUI(Blinker blink) {
		super("Reverb Processor", PROCESSOR);

		// Initialization will take place after sample rate is known
		initializationComplete = false;
		endOfData = false;

		// Create the UI for this processor
		rui = new ReverbUI(blink, this);
	}

	// Prepare for running reverb again
	public void reset() {
		o("ReverbWithUI reset");
	
		endOfData = false;
		
		// Calling this function will reset all comb and allpass filters
		// in preparation for running again.
		reverb.setSustainInMs(sustainTimeInMs);
	}
	
	public int getSamples(short [] buffer, int length) {

		// If bypass is enabled, short circuit processing
		if (getByPass() || !initializationComplete) 
			return previous.getSamples(buffer, length);

		// Must use endOfData to stop reading input otherwise
		// if source is file it will continually be reread.
		if (!endOfData)	{
			// Ask for a buffer of samples
			length = previous.getSamples(buffer, length);
			if (length == -1)
				endOfData = true;

			// Do the reverb on the samples
			return reverb.doReverb(buffer, length);
		}	else	{
			// Propagate the sustain samples
			return reverb.doReverb(buffer, -1);
		}
	}

	// These methods called when UI controls are manipulated
	public void comb1Delay(double delay) {

		if (reverb != null)
			reverb.setComb1Delay(delay);
	}
	
	public void comb2Delay(double delay) {

		if (reverb != null)
			reverb.setComb2Delay(delay);
	}
	
	public void comb3Delay(double delay) {

		if (reverb != null)
			reverb.setComb3Delay(delay);
	}
	
	public void comb4Delay(double delay) {

		if (reverb != null)
			reverb.setComb4Delay(delay);
	}
	
	public void allpass1Delay(double delay) {

		if (reverb != null)
			reverb.setAllpass1Delay(delay);
	}
	
	public void allpass2Delay(double delay) {

		if (reverb != null)
			reverb.setAllpass2Delay(delay);
	}

	public void setSustainTime(double sustainTimeInMs) {

		// Remember what sustain time was requested
		this.sustainTimeInMs = sustainTimeInMs;
		
		if (reverb != null) {
			reverb.setSustainInMs(sustainTimeInMs);
		}
	}
	
	public void setDryWetMix(double mix) {
		
		if (reverb != null)
			reverb.setDryWetMix(mix);
	}
	
	private void doInitialization() {

		reverb = new SchroederReverb(sampleRate, numberOfChannels);
		
		// Set the saved sustain time
		reverb.setSustainInMs(sustainTimeInMs);

		// Indicate initialization is complete
		initializationComplete = true;
	}
	
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxSamplingRate(min, max, preferred);
		
		sampleRate = preferred.getValue();

		// Cannot do initialization until sample rate is known
		doInitialization();
	}

	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxChannels(min, max, preferred);
		
		numberOfChannels = preferred.getValue();
	}

	public void showUI(boolean isVisible) {
        
        rui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        setByPass(true);
    }

    public Rectangle getBounds() {

        return rui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        rui.setBounds(x, y, w, h);
    }

	// Private class data
	private ReverbUI rui = null;
	private boolean initializationComplete;
	private boolean endOfData;
	private double sustainTimeInMs;
	private int sampleRate;
	private int numberOfChannels;
	private SchroederReverb reverb;
}
