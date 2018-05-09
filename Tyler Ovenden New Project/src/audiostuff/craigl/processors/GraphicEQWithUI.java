// Graphic Equalizer with UI Class
// Written by: Craig A. Lindley
// Last Update: 07/04/99

package craigl.processors;

import java.awt.*;
import craigl.beans.blinker.*;
import craigl.filters.*;
import craigl.utils.*;

/*
This graphic equalizer processor uses the optimized bandpass
filters implemented in the IIRBandpassFilter and IIRBandpassFilterDesign
classes in the filters package. A quality factor (Q) of 1.4 was
chosen for the filters to minimize the ripple in the passband with
full boost or cut. Range of boost and cut is +12db .. -12db.

The frequency of the bandpass filters were choosen to be:
50Hz, 100Hz, 200Hz, 400Hz, 800Hz, 1.6KHz, 3.2KHz, 6.4KHz, 12.8KHz

At a 11025 sample rate or lower, the highest two filters are disabled due to
Nyquist criteria. At a sample rate of 22050, only the highest filter is
disabled. At a 44100 sample rate, all filters are enabled.
*/ 

public class GraphicEQWithUI extends AbstractAudio implements AudioUIIF {

	private static final double Q = 1.4;
	
	public GraphicEQWithUI(Blinker blink) {
		super("Graphic Equalizer", PROCESSOR);

		// Initialization will take place after sample rate is known
		initializationComplete = false;

		// Create the UI for this processor
		gequi = new GraphicEQUI(blink, this);
	}

	public int getSamples(short [] buffer, int length) {

		// Ask for a buffer of samples
		int len = previous.getSamples(buffer, length);
		if (len == -1)
			return len;
		
		// If bypass is enabled, short circuit filtering
		if (getByPass() || !initializationComplete)
			return len;
		
		// Realloc buffer as required
		if (dBuffer.length != len)
			dBuffer = new double[len];
		
		// Move short samples into summation buffer for processing
		// Prescale the data according to number of filter elements
		for (int i=0; i < len; i++)
			dBuffer[i] = (double) buffer[i] * gainFactor;

		// Apply the filters
		f50Hz.doFilter(buffer, dBuffer, len);
		f100Hz.doFilter(buffer, dBuffer, len);
		f200Hz.doFilter(buffer, dBuffer, len);
		f400Hz.doFilter(buffer, dBuffer, len);
		f800Hz.doFilter(buffer, dBuffer, len);
		f1600Hz.doFilter(buffer, dBuffer, len);
		f3200Hz.doFilter(buffer, dBuffer, len);

		if (sampleRate > 12800)
			f6400Hz.doFilter(buffer, dBuffer, len);

		if (sampleRate > 25600)
			f12800Hz.doFilter(buffer, dBuffer, len);

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
	public void f50HzGain(double gain) {

		if (f50Hz != null)
			f50Hz.setAmplitudeAdj(gain);
	}
	
	public void f100HzGain(double gain) {

		if (f100Hz != null)
			f100Hz.setAmplitudeAdj(gain);
	}
	
	public void f200HzGain(double gain) {

		if (f200Hz != null)
			f200Hz.setAmplitudeAdj(gain);
	}
	
	public void f400HzGain(double gain) {

		if (f400Hz != null)
			f400Hz.setAmplitudeAdj(gain);
	}
	
	public void f800HzGain(double gain) {

		if (f800Hz != null)
			f800Hz.setAmplitudeAdj(gain);
	}
	
	public void f1600HzGain(double gain) {

		if (f1600Hz != null)
			f1600Hz.setAmplitudeAdj(gain);
	}
	
	public void f3200HzGain(double gain) {

		if (f3200Hz != null) 
			f3200Hz.setAmplitudeAdj(gain);
	}
	
	public void f6400HzGain(double gain) {

		if (f6400Hz != null)
			f6400Hz.setAmplitudeAdj(gain);
	}
	
	public void f12800HzGain(double gain) {

		if (f12800Hz != null)
			f12800Hz.setAmplitudeAdj(gain);
	}
	
	private void doInitialization() {

		// Total the number of filter gain elements in chain
		int gainElements = 1;

		// Design the filters now that the sampling rate is known.
		// Design the filter
		fd50Hz = new IIRBandpassFilterDesign(50, sampleRate, Q);
		fd50Hz.doFilterDesign();

		// Implement the filter design
		f50Hz = new IIRBandpassFilter(fd50Hz);
		gainElements++;

		// Design the filter
		fd100Hz = new IIRBandpassFilterDesign(100, sampleRate, Q);
		fd100Hz.doFilterDesign();

		// Implement the filter design
		f100Hz = new IIRBandpassFilter(fd100Hz);
		gainElements++;

		// Design the filter
		fd200Hz = new IIRBandpassFilterDesign(200, sampleRate, Q);
		fd200Hz.doFilterDesign();

		// Implement the filter design
		f200Hz = new IIRBandpassFilter(fd200Hz);
		gainElements++;

		// Design the filter
		fd400Hz = new IIRBandpassFilterDesign(400, sampleRate, Q);
		fd400Hz.doFilterDesign();

		// Implement the filter design
		f400Hz = new IIRBandpassFilter(fd400Hz);
		gainElements++;

		// Design the filter
		fd800Hz = new IIRBandpassFilterDesign(800, sampleRate, Q);
		fd800Hz.doFilterDesign();

		// Implement the filter design
		f800Hz = new IIRBandpassFilter(fd800Hz);
		gainElements++;

		// Design the filter
		fd1600Hz = new IIRBandpassFilterDesign(1600, sampleRate, Q);
		fd1600Hz.doFilterDesign();

		// Implement the filter design
		f1600Hz = new IIRBandpassFilter(fd1600Hz);
		gainElements++;

		// Design the filter
		fd3200Hz = new IIRBandpassFilterDesign(3200, sampleRate, Q);
		fd3200Hz.doFilterDesign();

		// Implement the filter design
		f3200Hz = new IIRBandpassFilter(fd3200Hz);
		gainElements++;

		// Conditionally design and implement the higher freq filters
		if (sampleRate > 12800) {
			// Design the filter
			fd6400Hz = new IIRBandpassFilterDesign(6400, sampleRate, Q);
			fd6400Hz.doFilterDesign();

			// Implement the filter design
			f6400Hz = new IIRBandpassFilter(fd6400Hz);
			gainElements++;
		}

		if (sampleRate > 25600) {
			// Design the filter
			fd12800Hz = new IIRBandpassFilterDesign(12800, sampleRate, Q);
			fd12800Hz.doFilterDesign();

			// Implement the filter design
			f12800Hz = new IIRBandpassFilter(fd12800Hz);
			gainElements++;
		}
		
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
        
        gequi.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        setByPass(true);
    }

    public Rectangle getBounds() {

        return gequi.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        gequi.setBounds(x, y, w, h);
    }

	// Private class data
	private GraphicEQUI gequi = null;
	private double [] dBuffer = new double[1];
	private int sampleRate;
	private boolean initializationComplete;
	private double gainFactor;
	
	// Individual filter object instances
	private IIRBandpassFilterDesign fd50Hz; 
	private IIRBandpassFilter f50Hz = null;
	
	private IIRBandpassFilterDesign fd100Hz;
	private IIRBandpassFilter f100Hz = null;
	
	private IIRBandpassFilterDesign fd200Hz;
	private IIRBandpassFilter f200Hz = null;
	
	private IIRBandpassFilterDesign fd400Hz;
	private IIRBandpassFilter f400Hz = null;

	private IIRBandpassFilterDesign fd800Hz;
	private IIRBandpassFilter f800Hz = null;

	private IIRBandpassFilterDesign fd1600Hz;
	private IIRBandpassFilter f1600Hz = null;

	private IIRBandpassFilterDesign fd3200Hz;
	private IIRBandpassFilter f3200Hz = null;

	private IIRBandpassFilterDesign fd6400Hz;
	private IIRBandpassFilter f6400Hz = null;
	
	private IIRBandpassFilterDesign fd12800Hz;
	private IIRBandpassFilter f12800Hz = null;
}
