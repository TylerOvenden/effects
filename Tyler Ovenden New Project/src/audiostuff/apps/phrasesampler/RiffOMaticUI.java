// RiffOMatic Phrase Sampler Application User Interface
// Written by: Craig A. Lindley
// Last Update: 11/09/99

package audiostuff.apps.phrasesampler;

import java.awt.*;
import java.awt.event.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.uiutils.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.beans.buttons.*;
import audiostuff.craigl.beans.leds.*;
import audiostuff.craigl.beans.meters.*;
import audiostuff.craigl.beans.pots.*;


public class RiffOMaticUI extends BaseUI implements CloseableFrameIF {
	
	private static final Color PANELCOLOR  = new Color(128, 64,  0);
	private static final Color KNOBCOLOR   = new Color(242,121,  0);
	public  static final Color LETTERCOLOR = new Color(255,218,181);

	public static final int MINSAMPLETIMEINSECONDS	   =  5;
	public static final int MAXSAMPLETIMEINSECONDS	   = 30;
	public static final int DEFAULTSAMPLETIMEINSECONDS = 20;

	public RiffOMaticUI(RiffOMatic rom) {
		
		super("Phrase Sampler", null);

		// Save incoming
		this.rom = rom;

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Create a global blinker for the LEDs. Need blinker even for
		// solid LEDs.
		blinker = new Blinker(250);

		// Use a grid layout for the panel
		Panel mp = new Panel();
		mp.setLayout(new GridLayout(1, 4));

		Box b = createSampleDurationPanel();
		mp.add(b);

		Box b1 = createStatusPanel();
		mp.add(b1);

		Box b2 = createLoopControlPanel();
		mp.add(b2);

		Box b3 = createPlaybackPanel();
		mp.add(b3);

		add(mp);
		pack();
		show();
	}

	// Create the sample duration panel
	private Box createSampleDurationPanel() {

		Panel p = new Panel();
		p.setBackground(PANELCOLOR);
		p.setLayout(new GridLayout(2, 1));

		Box box = new Box(p, "Sampling");
		
		String minString = "" + MINSAMPLETIMEINSECONDS + " sec";
		String maxString = "" + MAXSAMPLETIMEINSECONDS + " sec";

		// Create the sample duration pot
		final IntValuedPot sampleDurationPot = 
			createPot(AudioConstants.KNOBSIZE,
						"Sample Duration", 
						minString + ", , , , , , , , , ," + maxString,
						MAXSAMPLETIMEINSECONDS, MINSAMPLETIMEINSECONDS);

		sampleDurationPot.setPanelColor(PANELCOLOR);
		sampleDurationPot.setKnobColor(KNOBCOLOR);
		sampleDurationPot.setTextColor(LETTERCOLOR);
		p.add(sampleDurationPot);

		// Set the default value
		sampleDurationPot.setIntValue(DEFAULTSAMPLETIMEINSECONDS);
		rom.sampleDurationChanged(DEFAULTSAMPLETIMEINSECONDS);
		
		// Add listener to this pot
		sampleDurationPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				rom.sampleDurationChanged(sampleDurationPot.getIntValue());
			}
		});
		// Create the record sample button
		SquareButton recordButton = 
			createLabeledButton(false, "Record");

		p.add(recordButton);
		
		// Install an action listener which will be signalled
		// everytime the record button is clicked.
		recordButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("ON"))
					rom.recordButtonPressed();
			}
		});
		return box;
	}
	
	// Create the status panel
	private Box createStatusPanel() {

		Panel p = new Panel();
		p.setBackground(PANELCOLOR);
		p.setLayout(new BorderLayout());
		Box box = new Box(p, "Status");
		
		statusIndicator = new LEDMeter(15, 190, "Buffer", 0);
		statusIndicator.setPanelColor(PANELCOLOR);
		statusIndicator.setTextColor(LETTERCOLOR);
		statusIndicator.setNumberOfSections(20);
		statusIndicator.setHasLabels(true);
		statusIndicator.setLabelsString("100, ,90, ,80, ,70, ,60, ,50, ,40, ,30, ,20, ,10, ");
		statusIndicator.setColorRange(LETTERCOLOR, 0, 100);
		p.add("Center", statusIndicator);

		return box;
	}
	
	// Set a color range on the status indicator
	public void setStatusIndicatorColor(Color color, int min, int max) {

		statusIndicator.setColorRange(color, min, max);
		statusIndicator.repaint();
	}

	// Create the loop control panel
	private Box createLoopControlPanel() {

		Panel p = new Panel();
		Box box = new Box(p, "Looping");
		p.setLayout(new GridLayout(3, 1));

		// Create the loop begin button
		SquareButton loopBeginButton = 
			createLabeledButton(false, "Loop Begin");
		p.add(loopBeginButton);

		// Install an action listener which will be signalled
		// everytime the button is clicked.
		loopBeginButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("ON"))
					rom.loopBeginButtonPressed();
			}
		});

		// Create the loop end button
		SquareButton loopEndButton = 
			createLabeledButton(false, "Loop End");
		p.add(loopEndButton);
		
		// Install an action listener which will be signalled
		// everytime the button is clicked.
		loopEndButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("ON"))
					rom.loopEndButtonPressed();
			}
		});

		// Create the loop reset button
		SquareButton loopResetButton = 
			createLabeledButton(false, "Loop Reset");
		p.add(loopResetButton);

		// Install an action listener which will be signalled
		// everytime the button is clicked.
		loopResetButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("ON"))
					rom.loopResetButtonPressed();
			}
		});

		return box;
	}
	
	// Create the playback control panel
	private Box createPlaybackPanel() {
		
		Panel p = new Panel();
		Box box = new Box(p, "Playback");
		p.setLayout(new GridLayout(3, 1));

		// Add the normal speed play button
		SquareButton fsp = createLabeledButton(false, "Full Speed");
		
		// Install an action listener which will be signalled
		// everytime this button is clicked.
		fsp.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("ON"))
					rom.fullSpeedPlayButtonPressed();
			}
		});
		p.add(fsp);
		
		// Add the 1/2 speed play button
		SquareButton hsp = createLabeledButton(false, "1/2 Speed");
		
		// Install an action listener which will be signalled
		// everytime this button is clicked.
		hsp.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("ON"))
					rom.halfSpeedPlayButtonPressed();
			}
		});
		p.add(hsp);

		// Add the single/loop mode button
		SquareButton mode = createLabeledButton(true, "Looping");
		
		// Install an action listener which will be signalled
		// everytime this button is clicked.
		mode.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				boolean state = e.getActionCommand().equals("ON");
				rom.loopModeButtonPressed(state);
			}
		});
		p.add(mode);
		
		return box;
	}
	
	private SquareButton createLabeledButton(boolean isSticky, String label) {

		// Create a labelled square button
		SquareButton sb = new SquareButton();
		sb.setPanelColor(PANELCOLOR);
		sb.setButtonColor(KNOBCOLOR);
		sb.setTextColor(LETTERCOLOR);
		sb.setSticky(isSticky);
		sb.setCaption(label);
		return sb;
	}

	public void setMeterValue(int meterValue) {

		statusIndicator.setValue(meterValue);
	}
	
	public void windowClosing() {
		
		// Cause application to terminate
		System.exit(1);
	}

	// Call entry point for testing UI
	public static void main(String [] args) {

		RiffOMaticUI rom = new RiffOMaticUI(null);
	}
	
	// Private class data
	private RiffOMatic rom;
	private Blinker blinker;
	private	LEDMeter statusIndicator;
}


