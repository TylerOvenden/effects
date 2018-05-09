// DelayUI Class
// Written by: Craig A. Lindley
// Last Update: 03/21/99

package craigl.processors;

import java.awt.*;
import java.awt.event.*;
import craigl.utils.*;
import craigl.uiutils.*;
import craigl.beans.blinker.*;
import craigl.beans.displays.*;
import craigl.beans.pots.*;
import craigl.beans.leds.*;
import craigl.beans.buttons.*;

/*
The delay pot used here has three selector switches associated
with it that change the range of the control. The delay
selected with the pot is governed by the following:

    Button     Min   Max  Resolution/Tic
========================================
course button	0	2000	100ms
medium button	0	200		10ms
fine button		0	20		1ms

This assumes that the pot has 20 sections. Given the fact that
all pots have a range of returned value from 0 .. 100, the
pot granularity must change accordingly.

By default the medium selector switch is selected
*/

public class DelayUI extends BaseUI implements CloseableFrameIF {
	
	public static final int DEFAULTDELAYINMS = 100;
	public static final int DEFAULTDRYLEVEL  = 50;
	public static final int DEFAULTWETLEVEL  = 50;
	public static final int DEFAULTFEEDBACKLEVEL = 10;
	public static final int MAXDELAYINMS = 2000;
	public static final int COURSEMAXDELAYINMS = MAXDELAYINMS;
	public static final int MEDIUMMAXDELAYINMS = COURSEMAXDELAYINMS / 10;
	public static final int FINEMAXDELAYINMS = MEDIUMMAXDELAYINMS / 10;
	public static final int MINDELAYINMS = 1;

	public DelayUI(Blinker blink, AbstractAudio aa) {
		
		super("Delay Effect Processor", aa);

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Calculate delay pot granularity
		delayPotGranularityCourse = COURSEMAXDELAYINMS / 100.0;
		delayPotGranularityMedium = MEDIUMMAXDELAYINMS / 100.0;
		delayPotGranularityFine   = FINEMAXDELAYINMS   / 100.0;

		// Use a grid bag for the panel
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		Panel p = createUpperPanel();
		addDefaultComponent(mp, p, gbl, gbc, 0, 0, 15, 5);
        
		// Create the misc components
		onLED = createLED(Color.red, RoundLED.MODESOLID, true);
		addDefaultComponent(mp, onLED, gbl, gbc, 0, 5, 3, 1);
		blink.addPropertyChangeListener(onLED);
		
		Label l = new Label("delay processor", Label.CENTER);
		l.setBackground(AudioConstants.PANELCOLOR);
		l.setForeground(Color.blue);
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
		mp.setLayout(new GridLayout(1, 5));

		// Add the panels to the grid layout
		mp.add(createDelayAdjPanel());
		mp.add(createDelayPanel());
		mp.add(createDryLevelPanel());
		mp.add(createWetLevelPanel());
		mp.add(createFeedbackLevelPanel());

		return mp;
	}
	
	// Create the delay panel
	private Panel createDelayPanel() {

		// Use a grid bag for the panel
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(2,2,2,2);
		mp.setLayout(gbl);

		// Create the displays
		final ReadoutLabel delayDisplay = new ReadoutLabel(Color.green, "ms");
		addDefaultComponent(mp, delayDisplay, gbl, gbc, 0, 0, 3, 1);

		// Create the delay pot
		// Delay pot has 20 sections
		Pot delayPot = createPot(AudioConstants.KNOBSIZE, "Delay", 
								 "min, , , , , , , , , ,max");
		delayPot.setNumberOfSections(20);
		delayPot.setLabelsString("min, , , , , , , , , , , , , , , ,  , , , ,max");
		
		// Set the default value
		currentDelayInMs = DEFAULTDELAYINMS;
		oldPotValue = 50;
		delayPot.setValue(oldPotValue);
		delayChanged(oldPotValue, delayDisplay);
		addDefaultComponent(mp, delayPot, gbl, gbc, 0, 2, 3, 3);
		
		// Add listener to this pot
		delayPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				delayChanged(e.getValue(), delayDisplay);
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
	
	// Create the delay adjust button panel
	private Panel createDelayAdjPanel() {

		// Use a grid for the panel
		Panel mp = new Panel();
		mp.setLayout(new GridLayout(3, 1));

		// Create buttons and add to layout
		final SquareButton courseButton = new SquareButton();
		mp.add(courseButton);
		final SquareButton mediumButton = new SquareButton();
		mp.add(mediumButton);
		final SquareButton fineButton   = new SquareButton();
		mp.add(fineButton);

		// Add the range controls for the delay pot. Make them
		// work like a set of radio buttons
		courseButton.setPanelColor(AudioConstants.PANELCOLOR);
		courseButton.setCaption("Course Delay");
		courseButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				courseButtonClicked(courseButton, mediumButton, fineButton);
			}
		});

		mediumButton.setPanelColor(AudioConstants.PANELCOLOR);
		mediumButton.setCaption("Medium Delay");
		delayMode = 1;	// Indicates medium control is selected
		mediumButton.setState(true);
		mediumButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				mediumButtonClicked(mediumButton, courseButton, fineButton);
			}
		});

		fineButton.setPanelColor(AudioConstants.PANELCOLOR);
		fineButton.setCaption("Fine Delay");
		fineButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				fineButtonClicked(fineButton, courseButton, mediumButton);
			}
		});
		
		return mp;
	}
	
	// Process delay button range selection
	public void courseButtonClicked(SquareButton sb0, SquareButton sb1, SquareButton sb2) {
		
		// Turn other buttons off
		sb1.setState(false);
		sb2.setState(false);
		
		// Necessary so button doesn't toggle off
		sb0.setState(true);

		delayMode = 0;
	}

	public void mediumButtonClicked(SquareButton sb0, SquareButton sb1, SquareButton sb2) {

		// Turn other buttons off
		sb1.setState(false);
		sb2.setState(false);
		
		// Necessary so button doesn't toggle off
		sb0.setState(true);

		delayMode = 1;
	}

	public void fineButtonClicked(SquareButton sb0, SquareButton sb1, SquareButton sb2) {

		// Turn other buttons off
		sb1.setState(false);
		sb2.setState(false);
		
		// Necessary so button doesn't toggle off
		sb0.setState(true);

		delayMode = 2;
	}

	// Called when the delay pot is manipulated. potValue is
	// always 0..100. Actual delay is dependent upon range
	// selected.
	public void delayChanged(int newPotValue, ReadoutLabel rl) {

		// Determine direction and magnitude of change
		int delta = newPotValue - oldPotValue;
		
		oldPotValue = newPotValue;
		
		// Calculate current value based on delayMode and potValue
		int newValue = 0;
		switch(delayMode) {

			case 0:	// Course range selected
				newValue = (int)(delayPotGranularityCourse * delta);
				break;
		
			case 1:	// Medium range selected
				newValue = (int)(delayPotGranularityMedium * delta);
				break;
		
			case 2:	// Fine range selected
				newValue = (int)(delayPotGranularityFine * delta);
				break;
		}
		// Update currentDelayInMs
		currentDelayInMs += newValue;
		
		// Range check values
		if (currentDelayInMs <= 0)
			currentDelayInMs = MINDELAYINMS;

		else if (currentDelayInMs > MAXDELAYINMS)
			currentDelayInMs = MAXDELAYINMS;
		
		// Update the display
		rl.setValue(currentDelayInMs);

		// Perform real processing below
		((DelayWithUI)aa).setDelayInMs(currentDelayInMs);
	}

	public void dryLevelChanged(int newLevel, ReadoutLabel rl) {

		rl.setValue(newLevel);

		// Perform real processing below
		((DelayWithUI)aa).setDryLevel(newLevel);
	}
	
	public void wetLevelChanged(int newLevel, ReadoutLabel rl) {

		rl.setValue(newLevel);

		// Perform real processing below
		((DelayWithUI)aa).setWetLevel(newLevel);
	}
	
	public void feedbackLevelChanged(int newLevel, ReadoutLabel rl) {

		rl.setValue(newLevel);

		// Perform real processing below
		((DelayWithUI)aa).setFeedbackLevel(newLevel);
	}

	public void bypassChanged(boolean state) {

		((DelayWithUI)aa).setByPass(state);
	}

	public void windowClosing() {

		((DelayWithUI)aa).stopUI();
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


