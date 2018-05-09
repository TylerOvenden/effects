// Distortion Processor with UI 
// Written by: Craig A. Lindley
// Last Update: 06/27/99

package craigl.processors;

import java.awt.Rectangle;
import craigl.beans.blinker.*;
import craigl.utils.*;
import craigl.uiutils.*;

public class DistortionWithUI extends AbstractAudio
						implements AudioUIIF {

	public DistortionWithUI(Blinker blink) {
		
		super("Distortion", PROCESSOR);

		dui = new DistortionUI(blink, this);
	}

	public void setThreshold(int threshold) {

		this.threshold = threshold;
	}
	
	public void setGain(double gain) {

		this.gain = gain;
	}
	
	public int getSamples(short [] buffer, int length) {

		int len = previous.getSamples(buffer, length);
		if (getByPass())
			return len;
		
		for (int i=0; i < len; i++) {
			int sample = buffer[i];
			if (sample > threshold)
				sample = threshold;
			else if (sample < -threshold)
				sample = -threshold;

			buffer[i] = (short)(sample * gain);
		}

		return len; 
	}

    public void showUI(boolean isVisible) {
        
        dui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        setByPass(true);
    }

    public Rectangle getBounds() {

        return dui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        dui.setBounds(x, y, w, h);
    }

    // Private class data
	private int threshold = 32767;
	private double gain = 1.0;
	private DistortionUI dui = null;
}
