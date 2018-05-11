// PitchShifter UI Class
// Written by: Craig A. Lindley
// Last Update: 03/21/99

package audiostuff.craigl.processors;

import java.awt.*;
import java.awt.event.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.uiutils.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.beans.displays.*;
import audiostuff.craigl.beans.pots.*;
import audiostuff.craigl.beans.leds.*;
import audiostuff.craigl.beans.buttons.*;


public class PitchShifterUI extends BaseUI implements CloseableFrameIF {
	
	public static final int DEFAULTDRYLEVEL  = 25;
	public static final int DEFAULTWETLEVEL  = 75;
	public static final int DEFAULTFEEDBACKLEVEL = 0;

	public PitchShifterUI(Blinker blink, AbstractAudio aa) {
		
		super("Pitch Shifter Effect Processor", aa);

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Use a grid bag for the panel
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(2,2,2,2);
		mp.setLayout(gbl);

		Panel p = createUpperPanel();
		addDefaultComponent(mp, p, gbl, gbc, 0, 0, 15, 5);
        
		// Create the misc components
		onLED = createLED(Color.red, RoundLED.MODESOLID, true);
		addDefaultComponent(mp, onLED, gbl, gbc, 0, 5, 3, 1);
		blink.addPropertyChangeListener(onLED);
		
		Label l = new Label("pitch shifter processor", Label.CENTER);
		l.setBackground(AudioConstants.PANELCOLOR);
		l.setForeground(Color.green);
		addDefaultComponent(mp, l, gbl, gbc, 3, 5, 3, 1);
		
		bypassLED = createLED(Color.green, RoundLED.MODEBLINK, false);
		addDefaultComponent(mp, bypassLED, gbl, gbc, 9, 5, 1, 1);
		blink.addPropertyChangeListener(bypassLED);

		bypassButton = new SquareButton();
		bypassButton.setPanelColor(AudioConstants.PANELCOLOR);
		bypassButton.setCaption("Bypass");
		addDefaultComponent(mp, bypassButton, gbl, gbc, 12, 5, 3, 1);
		bypassButton.addActionListener(bypassLED);
		bypassButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				boolean state = e.getActionCommand().equals("ON");
				bypassChanged(state);
			}
		});

		// Finish up but don't make visible yet
		add(mp);
		pack();
	}

	// Create upper panel of UI
	private Panel createUpperPanel() {

		// Use a grid for the panel
		Panel mp = new Panel();
		mp.setLayout(new GridLayout(1, 4));

		// Add the panels to the grid layout
		mp.add(createPitchShiftPanel());
		mp.add(createDryLevelPanel());
		mp.add(createWetLevelPanel());
		mp.add(createFeedbackLevelPanel());

		return mp;
	}
	
	// Create the pitch shift panel
	private Panel createPitchShiftPanel() {

		// Use a grid bag for the panel
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(2,2,2,2);
		mp.setLayout(gbl);

		// Create the displays
		final ReadoutLabel pitchShiftDisplay = 
			new ReadoutLabel(Color.green, "halftones");
		addDefaultComponent(mp, pitchShiftDisplay, gbl, gbc, 0, 0, 3, 1);

		// Create the pitch shift pot
		// Pitch shift pot has 24 sections
		final IntValuedPot pitchShiftPot = 
			createPot(AudioConstants.KNOBSIZE, "Halftone Shift", 
								 "min, , , , , , , , , ,max",
								  12,-12);
		pitchShiftPot.setNumberOfSections(24);
		pitchShiftPot.setLabelsString("-12, , , , , , , , , , , ,0, , , , , , , , , , , ,+12");
		
		// Set the default value
		pitchShiftPot.setIntValue(0);
		pitchChanged(pitchShiftPot, pitchShiftDisplay);
		addDefaultComponent(mp, pitchShiftPot, gbl, gbc, 0, 2, 3, 3);
		
		// Add listener to this pot
		pitchShiftPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				pitchChanged(pitchShiftPot, pitchShiftDisplay);
			}
		});

		return mp;
	}
	
	// Create the dry level panel
	private Panel createDryLevelPanel() {

		// Use a grid bag for the panel
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(2,2,2,2);
		mp.setLayout(gbl);

		// Create the displays
		final ReadoutLabel dryLevelDisplay = new ReadoutLabel(Color.green, "%");
		addDefaultComponent(mp, dryLevelDisplay, gbl, gbc, 0, 0, 3, 1);

		// Create the dry level 
		Pot dryLevelPot = createPot(AudioConstants.KNOBSIZE, "Dry Level", 
									"0%, , , , ,50%, , , , ,100%");
		// Set the default value
		dryLevelPot.setValue(DEFAULTDRYLEVEL);
		dryLevelChanged(DEFAULTDRYLEVEL, dryLevelDisplay);
		addDefaultComponent(mp, dryLevelPot, gbl, gbc, 0, 2, 3, 3);

		// Add listener to this pot
		dryLevelPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				dryLevelChanged(e.getValue(), dryLevelDisplay);
			}
		});

		return mp;
	}
	
	// Create the wet level panel
	private Panel createWetLevelPanel() {

		// Use a grid bag for the panel
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(2,2,2,2);
		mp.setLayout(gbl);

		// Create the displays
		final ReadoutLabel wetLevelDisplay = new ReadoutLabel(Color.green, "%");
		addDefaultComponent(mp, wetLevelDisplay, gbl, gbc, 0, 0, 3, 1);

		Pot wetLevelPot = createPot(AudioConstants.KNOBSIZE, "Wet Level",
									"0%, , , , ,50%, , , , ,100%");
		// Set the default value
		wetLevelPot.setValue(DEFAULTWETLEVEL);
		wetLevelChanged(DEFAULTWETLEVEL, wetLevelDisplay);
		addDefaultComponent(mp, wetLevelPot, gbl, gbc, 0, 2, 3, 3);

		// Add listener to this pot
		wetLevelPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				wetLevelChanged(e.getValue(), wetLevelDisplay);
			}
		});

		return mp;
	}
	
	// Create the feedback level panel
	private Panel createFeedbackLevelPanel() {

		// Use a grid bag for the panel
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(2,2,2,2);
		mp.setLayout(gbl);

		// Create the displays
		final ReadoutLabel feedbackLevelDisplay = new ReadoutLabel(Color.green, "%");
		addDefaultComponent(mp, feedbackLevelDisplay, gbl, gbc, 0, 0, 3, 1);

		Pot feedbackLevelPot = createPot(AudioConstants.KNOBSIZE, "Feedback",
										 "min, , , , , , , , , ,max");
		// Set the default value
		feedbackLevelPot.setValue(DEFAULTFEEDBACKLEVEL);
		feedbackLevelChanged(DEFAULTFEEDBACKLEVEL, feedbackLevelDisplay);
		addDefaultComponent(mp, feedbackLevelPot, gbl, gbc, 0, 2, 3, 3);
		
		// Add listener to this pot
		feedbackLevelPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				feedbackLevelChanged(e.getValue(), feedbackLevelDisplay);
			}
		});

		return mp;
	}
	
	// Called when the pitch shift pot is manipulated.
	public void pitchChanged(IntValuedPot p, ReadoutLabel rl) {

		int value = p.getIntValue();

		// Update the display
		rl.setValue(value);

		// Perform real processing below
		((PitchShifterWithUI)aa).setPitchShift(value);
	}

	public void dryLevelChanged(int newLevel, ReadoutLabel rl) {

		rl.setValue(newLevel);

		// Perform real processing below
		((PitchShifterWithUI)aa).setDryLevel(newLevel);
	}
	
	public void wetLevelChanged(int newLevel, ReadoutLabel rl) {

		rl.setValue(newLevel);

		// Perform real processing below
		((PitchShifterWithUI)aa).setWetLevel(newLevel);
	}
	
	public void feedbackLevelChanged(int newLevel, ReadoutLabel rl) {

		rl.setValue(newLevel);

		// Perform real processing below
		((PitchShifterWithUI)aa).setFeedbackLevel(newLevel);
	}

	public void bypassChanged(boolean state) {

		((PitchShifterWithUI)aa).setByPass(state);
	}

	public void windowClosing() {

		((PitchShifterWithUI)aa).stopUI();
	}
	
	// Private class data
    private double delayPotGranularityCourse;
    private double delayPotGranularityMedium;
    private double delayPotGranularityFine;
	private int delayMode;
	private int currentDelayInMs;
	private int oldPotValue;

	private RoundLED onLED;
	private RoundLED bypassLED;

	private SquareButton bypassButton;
}


