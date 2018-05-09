// Stereo VU Front Panel
// Written by: Craig A. Lindley
// Last Update: 04/04/99

package frontpanels;

import java.awt.*;

import craigl.beans.blinker.*;
import craigl.beans.datagen.*;
import craigl.beans.meters.*;
import craigl.uiutils.*;


public class StereoVUMetersFrontPanel extends CloseableFrame implements CloseableFrameIF {

	public static final Color PANELCOLOR = new Color(44, 148, 103);
	public static final Color TEXTCOLOR  = Color.black;
	private static final int  LEDRADIUS  = 8;
	private static final int  DEFAULTNUMBEROFSECTIONS = 16;
	private static final String DEFAULTLABELSTRING = 
		"0,-3,-6,-9,-12,-15,-18,-21,-24,-27,-30,-33,-36,-40,-50,-60";

	public StereoVUMetersFrontPanel() {

		super("VU Meters");

		// This UI is interested in window closing events
		registerCloseListener(this);

		done = false;

		// Initialize colorArray with colors for meter sections
		Color [] colorArray = new Color[DEFAULTNUMBEROFSECTIONS];
		
		for (int i=0; i < DEFAULTNUMBEROFSECTIONS; i++) {

			colorArray[i] = 
				Color.getHSBColor((float) i / DEFAULTNUMBEROFSECTIONS, (float) 1.0, (float)1.0);
		}

		double colorPercent = 100.0 / DEFAULTNUMBEROFSECTIONS;

		setBackground(PANELCOLOR);

		// Start a blinker
		Blinker blinker = new Blinker(100);

		// Use a grid for the front panel
		setLayout(new GridLayout(1, 2));

		leftLEDMeter = new RoundLEDMeter(
								LEDRADIUS, true, Meter.MODEPEAK, 
								Meter.DEFAULTFONTNAME,
								Meter.DEFAULTFONTSTYLE,
								Meter.DEFAULTFONTSIZE,
								"Left", false, true, DEFAULTLABELSTRING,
								true, 0, DEFAULTNUMBEROFSECTIONS, PANELCOLOR,
								TEXTCOLOR, blinker);
		add(leftLEDMeter);

		rightLEDMeter = new RoundLEDMeter(
								LEDRADIUS, true, Meter.MODEPEAK, 
								Meter.DEFAULTFONTNAME,
								Meter.DEFAULTFONTSTYLE,
								Meter.DEFAULTFONTSIZE,
								"Right", false, true, DEFAULTLABELSTRING,
								false, 0, DEFAULTNUMBEROFSECTIONS, PANELCOLOR,
								TEXTCOLOR, blinker);
		add(rightLEDMeter);

		// Now set the LEDMeter's color values
		for (int section = 0; section < DEFAULTNUMBEROFSECTIONS; section++) {
			int startPercent = (int)(section * colorPercent);
			int endPercent   = (int)((section + 1) * colorPercent);

			Color color = colorArray[section];

			// Now set each meter
			leftLEDMeter.setColorRange(color, startPercent, endPercent);
			rightLEDMeter.setColorRange(color, startPercent, endPercent);
		}
		
		pack();
	}

	// Set the stereo value
	void setStereoValue(int leftValue, int rightValue) {

		leftLEDMeter.setValue(leftValue);
		rightLEDMeter.setValue(rightValue);
	}

	// Called as window is closing
	public void windowClosing() {

		done = true;
	}

	
	// Test application entry point	
	public static void main(String [] args) {

		StereoVUMetersFrontPanel svum = new StereoVUMetersFrontPanel();
		svum.show();

		// Generate 1000 random data values and display them
		for (int index=0; ((index < 1000) && !svum.done); index++) {

			int leftValue  = (int)(Math.random() * 100);
			int rightValue = (int)(Math.random() * 100);
			
			svum.setStereoValue(leftValue, rightValue);
			
			try {
				int delay = (int)(Math.random() * 300);
				Thread.sleep(delay);
			}
			catch(Exception ignor) {}
		}
		System.exit(1);
	}
	// Private class data
	public boolean done = false;
	private RoundLEDMeter leftLEDMeter;
	private RoundLEDMeter rightLEDMeter;
}
