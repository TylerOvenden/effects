// Spectrum Analyzer Device Class
// Written by: Craig A. Lindley
// Last Update: 03/07/99

package audiostuff.craigl.spectrumanalyzer;

import java.awt.*;
import audiostuff.craigl.scope.*;
import audiostuff.craigl.utils.*;


public class SpectrumAnalyzer extends AbstractAudio {

	/**
	 * Spectrum Analyzer device class constructor
	 *
	 * @param String name is the name to be shown in the title bar of
	 * the spectrum analyzer's UI for identification purposes.
	 */
	public SpectrumAnalyzer(String name) {

		super("SpectrumAnalyzer", MONITOR);

		this.name = name;

		// Flag object for controlling analyzer triggering
		tf = new TriggerFlag();
	}

	// Grab samples from previous stage and store for analysis
	public int getSamples(short [] buffer, int length) {

		if (length == 0)
			return 0;

		// Get samples from the previous stage
		int samples = previous.getSamples(buffer, length);

		// See if scope should trigger
		if (!tf.trigger()) {

			// Indicate scope has triggered
			tf.triggered();

			// Clone the array for the scope
			short [] newBuffer = new short [samples];

			// Copy the data
			System.arraycopy(buffer, 0, newBuffer, 0, length);

			// Kick off UI for the spectrum analyzer
			saui = new SpectrumAnalyzerUI(this, name, newBuffer, tf, sampleRate, numberOfChannels);
		}
		// Return samples read
		return samples;
	}
	
	// Overload this method to get the current sample rate
	public void minMaxSamplingRate(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxSamplingRate(min, max, preferred);
		
		sampleRate = preferred.getValue();
	}

	// Overload this method to get the current number of channels
	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		super.minMaxChannels(min, max, preferred);
		
		numberOfChannels = preferred.getValue();
	}

	public void showUI(boolean isVisible) {
        
        if (saui != null)
			saui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
		setByPass(true);
    }

    public Rectangle getBounds() {

		if (saui == null)
			return null;
       
		return saui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        if (saui != null)
			saui.setBounds(x, y, w, h);
    }

	// Private class data
	private TriggerFlag tf;
	private String name;
	private int sampleRate;
	private int numberOfChannels;
	private SpectrumAnalyzerUI saui = null;
}
