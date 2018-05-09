// Panner With UI Device
// Written by: Craig A. Lindley
// Last Update: 01/24/99

package craigl.processors;

import java.awt.Rectangle;
import craigl.beans.blinker.*;
import craigl.utils.*;
import craigl.uiutils.*;

public class PannerWithUI extends AbstractAudio
	implements AudioUIIF {

	public PannerWithUI(Blinker blink) {
		
		super("Panner", PROCESSOR);

		// Instantiate the UI for this processor
		pui = new PannerUI(blink, this);

		// Assume a mono source
		monoSource = true;
	}

	// Convert pot value into an attenuation factor for left and right
	// channels.
	public void setPanValue(int panValue) {

		this.leftPanFactor  = (100.0 -  panValue) / 100.0;
		this.rightPanFactor = ((double) panValue) / 100.0;
	}
	
	// If set, stereo signals are mixed before panning is applied. If
	// not set, stereo separation is retained. Mix mode has no affect
	// on mono signals.
	public void setMixMode(boolean state) {

		this.mixMode = state;
	}
	
	// Apply the panner effect to the samples passing through this
	// stage.
	public int getSamples(short [] buffer, int length) {

		// If the previous stage constitutes a mono (single channel)
		// source, then halve the number of samples requested. This
		// allows the use of a single buffer for processing.
		
		int halfLength = length / 2;
		
		// Request samples from previous stage
		int len = previous.getSamples(buffer, 
									  monoSource ? halfLength : length);
		
		// Was EOF indication returned?
		if (len == -1)
			return -1;

		// If bypass in effect and we have a stereo source, don't do
		// anything as samples are already in the buffer. If we have
		// a mono source, copy mono samples to both the left and right
		// channels.
		if (getByPass()) {
			if (monoSource) {
				// We have a mono source to process. Work from back to front
				// of buffer to prevent over writing unprocessed data.
				int sourceIndex = halfLength - 1;
				int destIndex   = length - 2;

				for (int i=0; i < halfLength; i++) {
					short s = buffer[sourceIndex--]; // Read mono sample
					buffer[destIndex]   = s;	// Write left channel
					buffer[destIndex+1] = s;	// Write right channel
					destIndex -= 2;
				}
			}
			return length;
		}
		// Bypass not in effect, do some panning
		
		// What is done depends upon source and mode
		if (monoSource) {
			// We have a mono source to process. Work from back to front
			// of buffer to prevent over writing unprocessed data.
			int sourceIndex = halfLength - 1;
			int destIndex   = length - 2;

			for (int i=0; i < halfLength; i++) {
				short s = buffer[sourceIndex--];
				buffer[destIndex]   = (short) (s * leftPanFactor);
				buffer[destIndex+1] = (short) (s * rightPanFactor);
				destIndex -= 2;
			}

		}	else	{
			
			// We have a stereo source to process. Check the mode.
			if (mixMode) {
				// Mix left and right before panning
				for (int i=0; i < length; i+=2) {
					double s = (buffer[i] + buffer[i+1]) / 2.0;
					buffer[i]   = (short) (s * leftPanFactor);
					buffer[i+1] = (short) (s * rightPanFactor);
				}

			}	else	{
				// Leave stereo separation intact
				for (int i=0; i < length; i+=2) {
					buffer[i]   = (short) (buffer[i]   * leftPanFactor);
					buffer[i+1] = (short) (buffer[i+1] * rightPanFactor);
				}
			}
		}
		return length; 
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

	// Override AbstractAudio methods as required to influence the
	// number of channels negotiation.

	// Override this method to capture the preferred number
	// of channels from the stages preceeding the panner but
	// return a stereo preference from this panner stage. This is
	// done because the output of the panner is always stereo.
	public void minMaxChannels(MyInt min, MyInt max, MyInt preferred) {

		// Propagate call towards the source
		if (previous != null)
			previous.minMaxChannels(min,max,preferred);

		// Save the preferred value from previous stages
		preferredChannels = preferred.getValue();

		// Set flag to indicate source mode
		monoSource = (preferredChannels == 1);

		// Set up for stereo as the output of the panner is
		// always stereo.
		min.setValue(2);
		max.setValue(2);
		preferred.setValue(2);
	}

	// Override this method so that all stages before the panner
	// use their negotiated preference (stereo or mono). All 
	// stages afterwards are stereo.
	public void setChannelsRecursive(int ch) {

		ch = preferredChannels;

		super.setChannelsRecursive(ch);

		// Update panner UI with source mode
		pui.setSourceChannels(ch);
	}
		
    // Private class data
	private boolean monoSource;
	private int preferredChannels;
	private double leftPanFactor  = 0.5;
	private double rightPanFactor = 0.5;
	private boolean mixMode = false;
	private PannerUI pui = null;
}
