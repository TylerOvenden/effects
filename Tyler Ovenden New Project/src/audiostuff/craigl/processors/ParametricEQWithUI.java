// Parametric Equalizer with UI Class
// Written by: Craig A. Lindley
// Last Update: 07/05/99

package audiostuff.craigl.processors;

import java.awt.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.filters.*;
import audiostuff.craigl.utils.*;

/*
This parametric equalizer processor features three filter sections.
A high pass shelving filter with adjustable cutoff frequency, a bandpass
peaking filter with adjustable center frequency and quality factor (Q)
and a low pass shelving filter with adjustable cutoff frequency. All 
filters are second order. This gives the highpass and lowpass filters
a slope of 12 db/octave rolloff. The range of boost and cut is
+/- 12 db.

All filter sections are IIR filters. The frequency ranges of the highpass
and bandpass filters are limited by the Nyquist frequency. The limit
is one half the sampling rate. The dampling factor controls how much
peaking the highpass and lowpass filters have. A factor of 1.0 exhibits
very little if any peaking.
*/ 

public class ParametricEQWithUI extends AbstractAudio implements AudioUIIF {

	private static final double DAMPINGFACTOR = 1.0;
	
	public ParametricEQWithUI(Blinker blink) {
		super("Parametric Equalizer", PROCESSOR);

		// Initialization will take place after sample rate is known
		initializationComplete = false;

		// Default values for bandpass
		currentBPFreq = ParametricEQUI.BANDPASSFREQDEF;
		currentBPQ = ParametricEQUI.BANDPASSQDEF;

		// Create the UI for this processor
		pequi = new ParametricEQUI(blink, this);
	}

	public int getSamples(short [] buffer, int length) {

		// If bypass is enabled, short circuit filtering
		if (getByPass() || !initializationComplete)
			return previous.getSamples(buffer, length);

		// Ask for a buffer of samples
		int len = previous.getSamples(buffer, length);
		if (len == -1)
			return len;
		
		// Realloc buffer as required
		if (dBuffer.length != len)
			dBuffer = new double[len];
		
		// Move short samples into summation buffer for processing
		// Prescale the data according to number of filter elements
		for (int i=0; i < len; i++)
			dBuffer[i] = (double) buffer[i] * gainFactor;

		// Apply the filters
		lowPassShelf.doFilter(buffer, dBuffer, len);
		bandPassPeak.doFilter(buffer, dBuffer, len);
		highPassShelf.doFilter(buffer, dBuffer, len);

		// Convert the double samples back into short samples after
		// range constraining them.
		for (int i=0; i < len; i++) {
			double dSample = dBuffer[i];
			if (dSample > 32767.0)
				dSample = 32767.0;
			else if (dSample < -32768.0)
				dSample = -32768.0;
			
			// Convert sample and store
			buffer[i] = (short) dSample;
		}
		return len; 
	}

	// These methods called when UI controls are manipulated
	public void lowPassShelfGain(double gain) {

		if (lowPassShelf != null)
			lowPassShelf.setAmplitudeAdj(gain);
	}
	
	public void bandPassPeakGain(double gain) {

		if (bandPassPeak != null)
			bandPassPeak.setAmplitudeAdj(gain);
	}
	
	public void highPassShelfGain(double gain) {

		if (highPassShelf != null)
			highPassShelf.setAmplitudeAdj(gain);
	}
	
	public void lowPassShelfFreq(int freq) {

		if (lowPassShelf != null) {
			// Recalculate and install the filter with new freq
			lpfd = new IIRLowpassFilterDesign(freq, sampleRate, DAMPINGFACTOR);
			lpfd.doFilterDesign();
			lowPassShelf.updateFilterCoefficients(lpfd);
		}
	}
	
	public void bandPassPeakFreq(int freq) {

		currentBPFreq = freq;

		if ((bandPassPeak != null)  && (freq < (sampleRate / 2))){
			// Recalculate and install the filter with new freq
			bpfd = new IIRBandpassFilterDesign(currentBPFreq, 
											   sampleRate, currentBPQ);
			bpfd.doFilterDesign();
			bandPassPeak.updateFilterCoefficients(bpfd);
		}
	}
	
	public void highPassShelfFreq(int freq) {

		if ((highPassShelf != null) && (freq < (sampleRate / 2))){
			hpfd = new IIRHighpassFilterDesign(freq, sampleRate, DAMPINGFACTOR);
			hpfd.doFilterDesign();
			highPassShelf.updateFilterCoefficients(hpfd);
		}
	}
	
	public void bandPassPeakQ(double q) {

		currentBPQ = q;

		if (bandPassPeak != null) {
			// Recalculate and install the filter with new freq
			bpfd = new IIRBandpassFilterDesign(currentBPFreq, 
											   sampleRate, currentBPQ);
			bpfd.doFilterDesign();
			bandPassPeak.updateFilterCoefficients(bpfd);
		}
	}
	
	private void doInitialization() {

		// Total the number of filter gain elements in chain
		int gainElements = 1;

		// Design the filters now that the sampling rate is known.
		// Design the low pass filter
		lpfd = new IIRLowpassFilterDesign(ParametricEQUI.LOWPASSFREQDEF,
										  sampleRate, DAMPINGFACTOR);
		lpfd.doFilterDesign();

		// Implement the filter design
		lowPassShelf = new IIRLowpassFilter(lpfd);
		gainElements++;

		// Design the band filter
		bpfd = new IIRBandpassFilterDesign(currentBPFreq,
										   sampleRate, currentBPQ);
		bpfd.doFilterDesign();

		// Implement the filter design
		bandPassPeak = new IIRBandpassFilter(bpfd);
		gainElements++;

		// Design the high pass filter
		hpfd = new IIRHighpassFilterDesign(ParametricEQUI.HIGHPASSFREQDEF,
										   sampleRate, DAMPINGFACTOR);
		hpfd.doFilterDesign();

		// Implement the filter design
		highPassShelf = new IIRHighpassFilter(hpfd);
		gainElements++;

		gainFactor = 1.0 / gainElements;

		// All filters designed, indicate initialization is complete
		initializationComplete = true;
	}
	
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxSamplingRate(min, max, preferred);
		
		sampleRate = preferred.getValue();

		// Cannot do initialization until sample rate is known
		doInitialization();
	}

	public void showUI(boolean isVisible) {
        
        pequi.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        setByPass(true);
    }

    public Rectangle getBounds() {

        return pequi.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        pequi.setBounds(x, y, w, h);
    }

	// Private class data
	private ParametricEQUI pequi = null;
	private double [] dBuffer = new double[1];
	public  int sampleRate;
	private int currentBPFreq;
	private double currentBPQ;
	private boolean initializationComplete;
	private double gainFactor;
	
	// Individual filter object instances
	private IIRLowpassFilterDesign lpfd; 
	private IIRLowpassFilter lowPassShelf = null;
	
	private IIRBandpassFilterDesign bpfd;
	private IIRBandpassFilter bandPassPeak = null;
	
	private IIRHighpassFilterDesign hpfd;
	private IIRHighpassFilter highPassShelf = null;
}
