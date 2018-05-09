// Guitar/Bass Tuner Application User Interface
// Written by: Craig A. Lindley
// Last Update: 04/01/99

package audiostuff.apps.guitartuner;

import java.awt.*;
import java.awt.event.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.uiutils.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.beans.leds.*;
import audiostuff.craigl.beans.meters.*;

public class TunerUI extends BaseUI implements CloseableFrameIF {
	
	private static final Color TEXTCOLOR    = Color.black;
	private static final Color PANELCOLOR   = AudioConstants.PANELCOLOR;
	private static final Color NOTELEDCOLOR = Color.cyan;

	public TunerUI() {
		
		super("Guitar/Bass Tuner", null);

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Create a global blinker for the LEDs. Need blinker even for
		// solid LEDs (for state machine).
		blinker = new Blinker(200);

		// Use a grid layout for the panel
		Panel mp = new Panel();
		mp.setLayout(new GridLayout(2, 1));
		mp.setBackground(PANELCOLOR);
		add(mp);

		// Create tuning meter
		tuneMeter = new AnalogMeter();
		tuneMeter.setTextColor(Color.black);
		tuneMeter.setLabelsString("-50c,0,+50c"); 
		tuneMeter.setWidth(200);
		tuneMeter.setHeight(85);
		tuneMeter.setLabelPercent(85);
		tuneMeter.setCaption("Tuning");
		tuneMeter.setColorRange(Color.red,	  0, 100);
		tuneMeter.setColorRange(Color.green, 46,  54);
		mp.add(tuneMeter);

		// Create bottom panel
		Panel bp = new Panel();
		bp.setLayout(new GridLayout(3, 1));
		mp.add(bp);

		// Create the panel with update LED
		Panel up = new Panel();
		up.setLayout(new GridLayout(1, 1));
		bp.add(up);

		Font f = up.getFont();

		updateLED = new LabeledLED(blinker, PANELCOLOR, Color.red, 
								   TEXTCOLOR, f, 7, 
								   "updating", true, false);
		up.add(updateLED);
		
		// Create the tune panel with 3 labeled LEDs
		Panel tp = new Panel();
		tp.setLayout(new GridLayout(1, 3));
		bp.add(tp);

		flatLED = new LabeledLED(blinker, PANELCOLOR, Color.red,
								 TEXTCOLOR, f, 7,
								 "flat", true, false);
		tp.add(flatLED);

		inTuneLED = new LabeledLED(blinker, PANELCOLOR, Color.green,
								   TEXTCOLOR, f, 7,
								   "in tune", true, false);
		tp.add(inTuneLED);

		sharpLED = new LabeledLED(blinker, PANELCOLOR, Color.red,
								  TEXTCOLOR, f, 7,
								  "sharp", true, false);
		tp.add(sharpLED);

		// Create the note panel with 6 labeled LEDs
		Panel np = new Panel();
		np.setLayout(new GridLayout(1, 6));
		bp.add(np);
		
		beLED = new LabeledLED(blinker, PANELCOLOR, NOTELEDCOLOR,
							   TEXTCOLOR, f, 7,
							   "E", true, true);
		np.add(beLED);

		aLED = new LabeledLED(blinker, PANELCOLOR, NOTELEDCOLOR,
							  TEXTCOLOR, f, 7,
							  "A", true, true);
		np.add(aLED);

		dLED = new LabeledLED(blinker, PANELCOLOR, NOTELEDCOLOR,
							  TEXTCOLOR, f, 7,
							  "D", true, true);
		np.add(dLED);

		gLED = new LabeledLED(blinker, PANELCOLOR, NOTELEDCOLOR,
							  TEXTCOLOR, f, 7,
							  "G", true, true);
		np.add(gLED);

		bLED = new LabeledLED(blinker, PANELCOLOR, NOTELEDCOLOR,
							  TEXTCOLOR, f, 7,
							  "B", true, true);
		np.add(bLED);

		leLED = new LabeledLED(blinker, PANELCOLOR, NOTELEDCOLOR,
							   TEXTCOLOR, f, 7,
							   "E", true, true);
		np.add(leLED);

		pack();
		show();
	}

	public void windowClosing() {
		
		// Cause application to terminate
		System.exit(1);
	}

	// Call entry point for testing UI
	public static void main(String [] args) {

		TunerUI tui = new TunerUI();
	}
	
	// Public and private class data
	private Blinker blinker;
	public	AnalogMeter tuneMeter;
	public LabeledLED updateLED;
	public LabeledLED flatLED;
	public LabeledLED inTuneLED;
	public LabeledLED sharpLED;
	public LabeledLED beLED;
	public LabeledLED aLED;
	public LabeledLED dLED;
	public LabeledLED gLED;
	public LabeledLED bLED;
	public LabeledLED leLED;
}


