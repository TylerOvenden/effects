// Phaser With UI Processor
// Written by: Craig A. Lindley
// Last Update: 11/11/98

package audiostuff.craigl.processors;

import java.awt.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.utils.*;

public class PhaserWithUI extends AbstractAudio implements AudioUIIF {

	public PhaserWithUI(Blinker blink) {
		
		super("PhaserWithUI", PROCESSOR);

		initializationComplete = false;
		invertPhase = false;

		// Create the UI for this processor
		pui = new PhaserUI(blink, this);
	}

	// Process the samples that pass thru this effect		
	public int getSamples(short [] buffer, int length) {

		int len = previous.getSamples(buffer, length);
		
		if (getByPass() || !initializationComplete)
			return len;

		// Not in bypass mode, process the samples
		if (numberOfChannels == 1)
			return processMonoSamples(buffer, len);
		else
			return processStereoSamples(buffer, len);
	}

	protected int processMonoSamples(short [] buffer, int len) {
		
		// Do the processing
		for (int i=0; i < len; i++) {
			
			// Calculate A in difference equation
			double A = (1.0 - wp) / (1.0 + wp);

			int inSample = (int) buffer[i];

			double in = inSample + 
				(((invertPhase ? -1:1) * feedbackLevel * thisOut4) / 100.0);
 			
			// Do the first allpass filter
			thisOut1 = A * (in       + thisOut1) - prevIn1;
			prevIn1 = in;

 			// Do the second allpass filter
			thisOut2 = A * (thisOut1 + thisOut2) - prevIn2;
			prevIn2 = thisOut1;

 			// Do the third allpass filter
			thisOut3 = A * (thisOut2 + thisOut3) - prevIn3;
			prevIn3 = thisOut2;

 			// Do the forth allpass filter
			thisOut4 = A * (thisOut3 + thisOut4) - prevIn4;
			prevIn4 = thisOut3;

			double outSample = 
				((thisOut4 * wetLevel) / 100.0) +
				((inSample * dryLevel) / 100.0);

			// Clip output to legal levels
			if(outSample > 32767.0)
				outSample = 32767;
			else if(outSample < -32768.0)
				outSample = -32768;

			buffer[i] = (short) outSample;

			// Update sweep
			wp *= currentStep;		// Apply step value
			
			if(wp > maxWp)			// Exceed max Wp ?
				currentStep = 1.0 / step;
			else if(wp < minWp)		// Exceed min Wp ?
				currentStep = step;
		}
 		return len; 
	}


	protected int processStereoSamples(short [] buffer, int len) {

		// Do the processing
		for (int i=0; i < len / 2; i++) {
			
			// Calculate A in difference equation
			double A = (1.0 - wp) / (1.0 + wp);

			int leftInSample  = (int) buffer[2 * i];
			int rightInSample = (int) buffer[2 * i + 1];

			double leftIn  = leftInSample  + 
				(((invertPhase ? -1:1) * feedbackLevel * leftThisOut4)  / 100.0);
			
			double rightIn = rightInSample + 
				(((invertPhase ? -1:1) * feedbackLevel * rightThisOut4) / 100.0);
 			
			// Do the first allpass filter - left channel
			leftThisOut1 = A * (leftIn     + leftThisOut1) - leftPrevIn1;
			leftPrevIn1 = leftIn;
			
			// Do the first allpass filter - right channel
			rightThisOut1 = A * (rightIn   + rightThisOut1) - rightPrevIn1;
			rightPrevIn1 = rightIn;

 			// Do the second allpass filter - left channel
			leftThisOut2 = A * (leftThisOut1 + leftThisOut2) - leftPrevIn2;
			leftPrevIn2 = leftThisOut1;

 			// Do the second allpass filter - right channel
			rightThisOut2 = A * (rightThisOut1 + rightThisOut2) - rightPrevIn2;
			rightPrevIn2 = rightThisOut1;

			// Do the third allpass filter - left channel
			leftThisOut3 = A * (leftThisOut2 + leftThisOut3) - leftPrevIn3;
			leftPrevIn3 = leftThisOut2;

			// Do the third allpass filter - right channel
			rightThisOut3 = A * (rightThisOut2 + rightThisOut3) - rightPrevIn3;
			rightPrevIn3 = rightThisOut2;

			// Do the forth allpass filter - left channel
			leftThisOut4 = A * (leftThisOut3 + leftThisOut4) - leftPrevIn4;
			leftPrevIn4 = leftThisOut3;

			// Do the forth allpass filter - right channel
			rightThisOut4 = A * (rightThisOut3 + rightThisOut4) - rightPrevIn4;
			rightPrevIn4 = rightThisOut3;

			double leftOutSample = 
				((leftThisOut4  * wetLevel) / 100.0) + 
				((leftInSample  * dryLevel) / 100.0);

			double rightOutSample = 
				((rightThisOut4 * wetLevel) / 100.0) +
				((rightInSample * dryLevel) / 100.0);

			// Clip output to legal levels
			if(leftOutSample > 32767.0)
				leftOutSample = 32767;
			else if(leftOutSample < -32768.0)
				leftOutSample = -32768;

			if(rightOutSample > 32767.0)
				rightOutSample = 32767;
			else if(rightOutSample < -32768.0)
				rightOutSample = -32768;

			buffer[2 * i]     = (short) leftOutSample;
			buffer[2 * i + 1] = (short) rightOutSample;

			// Update sweep
			wp *= currentStep;		// Apply step value
			
			if(wp > maxWp)			// Exceed max Wp ?
				currentStep = 1.0 / step;
			else if(wp < minWp)		// Exceed min Wp ?
				currentStep = step;
		}
 		return len; 
	}

	public void setSweepRate(double sweepRate) {

		this.sweepRate = sweepRate;

		// Redo initialization
		doInitialization();
	}

	public void setSweepRange(double sweepRange) {

		this.sweepRange = sweepRange;

		// Redo initialization
		doInitialization();
	}

	// Mode is either sin or triangle
	public void setBaseFreq(double baseFreq) {

		this.baseFreq = baseFreq;

		// Redo initialization
		doInitialization();
	}

	public void setDryLevel(int dryLevel) {

		this.dryLevel = dryLevel;
	}

	public void setWetLevel(int wetLevel) {

		this.wetLevel = wetLevel;
	}

	public void setFeedbackPhase(boolean invertPhase) {

		this.invertPhase = invertPhase;
	}

	public void setFeedbackLevel(int feedbackLevel) {

		this.feedbackLevel = feedbackLevel;
	}

	public void doInitialization() {

		// Cannot initialize until sample rate is known
		if (sampleRate != 0) {
			
			wp = minWp = (2.0 * Math.PI * baseFreq) / sampleRate;
 			
			// Convert octave range to freq range
			double freqRange = Math.pow(2.0, sweepRange);
			
			maxWp = minWp * freqRange;

			currentStep = step = Math.pow(freqRange, sweepRate / (sampleRate / 2.0));

			// Indicate initialization is complete
			initializationComplete = true;
		}
	}
	
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxSamplingRate(min, max, preferred);
		
		sampleRate = preferred.getValue();

		doInitialization();
	}

	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxChannels(min, max, preferred);
		numberOfChannels = preferred.getValue();
	}
		
	public void showUI(boolean isVisible) {
        
        pui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        setByPass(true);
    }

    public Rectangle getBounds() {

        return pui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        pui.setBounds(x, y, w, h);
    }

	// Private class data
	private double thisOut1, thisOut2, thisOut3, thisOut4;
	private double prevIn1, prevIn2, prevIn3, prevIn4;
	private double leftThisOut1, leftThisOut2, leftThisOut3, leftThisOut4;
	private double rightThisOut1, rightThisOut2, rightThisOut3, rightThisOut4;
	private double leftPrevIn1, leftPrevIn2, leftPrevIn3, leftPrevIn4;
	private double rightPrevIn1, rightPrevIn2, rightPrevIn3, rightPrevIn4;

	private double sweepRate;
	private double sweepRange;
	private double baseFreq;
	private double wp;
	private double minWp;
	private double maxWp;
	private double step;
	private double currentStep;
	private double sweepValue = 0;
	private boolean invertPhase;
	private int dryLevel;
	private int wetLevel;
	private int feedbackLevel;

	private PhaserUI pui = null;
	private int sampleRate = 0;
	private int numberOfChannels = 0;
	private boolean initializationComplete;
}

