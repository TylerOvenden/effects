// Sample Scope Class
// Written by: Craig A. Lindley
// Last Update: 02/25/99

package craigl.scope;

import java.awt.*;
import craigl.utils.*;


public class Scope extends AbstractAudio {

	public Scope(String name) {

		super("Scope", MONITOR);

		this.name = name;

		// Flag object for controlling scope triggering
		tf = new TriggerFlag();
	}

	// Grab samples from previous stage and store for scope
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

			// Kick off UI for the scope
			sui = new ScopeUI(this, name, newBuffer, tf, sampleRate, numberOfChannels);
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
        
        if (sui != null)
			sui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
		setByPass(true);
    }

    public Rectangle getBounds() {

		if (sui == null)
			return null;
       
		return sui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        if (sui != null)
			sui.setBounds(x, y, w, h);
    }

	// Private class data
	private TriggerFlag tf;
	private String name;
	private int sampleRate;
	private int numberOfChannels;
	private ScopeUI sui = null;
}
