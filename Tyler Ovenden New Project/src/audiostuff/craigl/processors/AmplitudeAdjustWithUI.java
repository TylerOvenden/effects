// Audio Amplitude Adjustment Processor with UI 
// Written by: Craig A. Lindley
// Last Update: 11/11/98

package craigl.processors;

import java.awt.Rectangle;
import craigl.beans.blinker.*;
import craigl.utils.*;
import craigl.uiutils.*;

public class AmplitudeAdjustWithUI extends AbstractAudio
						implements AudioUIIF {

	public AmplitudeAdjustWithUI(Blinker blink) {
		
		super("AmplitudeAdjust", PROCESSOR);

		aaui = new AmplitudeAdjustUI(blink, this);
	}

	public double getAmplitudeAdj() {

		return adjValue;
	}
	
	public void setAmplitudeAdj(double adjValue) {

		this.adjValue = adjValue;
	}
	
	public int getSamples(short [] buffer, int length) {

		int len = previous.getSamples(buffer, length);
		if (getByPass())
			return len;
		
		for (int i=0; i < len; i++)
			buffer[i] = (short)(buffer[i] * adjValue);

		return len; 
	}

    public void showUI(boolean isVisible) {
        
        aaui.setVisible(isVisible);
    }
	
    public void stopUI() {
        
        setByPass(true);
    }

    public Rectangle getBounds() {

        return aaui.getBounds();
    }
    
    public void setBounds(int x, int y, int w, int h) {

        aaui.setBounds(x, y, w, h);
    }

    // Private class data
	private double adjValue = 1.0;
	private AmplitudeAdjustUI aaui = null;
}
