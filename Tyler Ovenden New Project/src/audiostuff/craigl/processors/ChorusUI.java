// Flanger/ChorusUI Class
// Written by: Craig A. Lindley
// Last Update: 05/19/99

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


public class ChorusUI extends BaseUI implements CloseableFrameIF {
	
	public static final int MAXDELAYINMS = 40;
	public static final int MINDELAYINMS = 1;
	public static final double MAXRATEINHZ = 5;
	public static final double MINRATEINHZ = 0;
	public static final double MAXDEPTHINMS = 30;
	public static final double MINDEPTHINMS = 0;

	public static final int DEFAULTDELAYINMS = 18;
	public static final int DEFAULTDEPTHINMS = 10;
	public static final int DEFAULTRATEINHZ  = 2;
	public static final int DEFAULTDEPTHLEVEL= 10;
	public static final int DEFAULTDRYLEVEL  = 30;
	public static final int DEFAULTWETLEVEL  = 30;
	public static final int DEFAULTFEEDBACKLEVEL = 0;

	public ChorusUI(Blinker blink, AbstractAudio aa) {
		
		super("Chorus/Flanger Effect Processor", aa);

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Calculate delay pot granularity
		delayPotGranularity = MAXDELAYINMS / 100.0;
		ratePotGranularity  = MAXRATEINHZ  / 100.0;
		depthPotGranularity = MAXDEPTHINMS / 100.0;

		// Use a grid bag for the panel
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		// Create the displays
		delayDisplay = new ReadoutLabel("ms");
		addDefaultComponent(mp, delayDisplay, gbl, gbc, 0, 0, 3, 1);

		rateDisplay = new ReadoutLabel("Hz");
		addDefaultComponent(mp, rateDisplay, gbl, gbc, 3, 0, 3, 1);

		depthLevelDisplay = new ReadoutLabel("ms");
		addDefaultComponent(mp, depthLevelDisplay, gbl, gbc, 8, 0, 3, 1);

		dryLevelDisplay = new ReadoutLabel(Color.red, "%");
		addDefaultComponent(mp, dryLevelDisplay, gbl, gbc, 0, 5, 3, 1);

		wetLevelDisplay = new ReadoutLabel(Color.red, "%");
		addDefaultComponent(mp, wetLevelDisplay, gbl, gbc, 3, 5, 3, 1);

		feedbackLevelDisplay = new ReadoutLabel(Color.red,"%");
		addDefaultComponent(mp, feedbackLevelDisplay, gbl, gbc, 8, 5, 3, 1);
        
		// Create the delay pot
		delayPot = createPot(AudioConstants.KNOBSIZE, "Delay", "min, , , , , , , , , ,max");
		delayPot.setNumberOfSections(20);
		delayPot.setLabelsString("min, , , , , , , , , , , , , , , , , , , ,max");
		
		// Set the default value
		delayPot.setValue((int)(DEFAULTDELAYINMS / delayPotGranularity));
		delayChanged((int)(DEFAULTDELAYINMS / delayPotGranularity));
		addDefaultComponent(mp, delayPot, gbl, gbc, 0, 2, 3, 3);
		
		// Add listener to this pot
		delayPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				delayChanged(e.getValue());
			}
		});
		// Create the rate pot
		ratePot = createPot(AudioConstants.KNOBSIZE, "LFO Rate", "0Hz, , , , , , , , , ,5Hz");
		ratePot.setNumberOfSections(20);
		ratePot.setLabelsString("0Hz, , , , , , , , , , , , , , , , , , , ,5Hz");
		
		// Set the default value
		ratePot.setValue((int)(DEFAULTRATEINHZ / ratePotGranularity));
		rateChanged((int)(DEFAULTRATEINHZ / ratePotGranularity));
		addDefaultComponent(mp, ratePot, gbl, gbc, 3, 2, 3, 3);
		
		// Add listener to this pot
		ratePot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				rateChanged(e.getValue());
			}
		});
		// Create the modulation mode switch
		ToggleSwitchButton tsb = new ToggleSwitchButton(10, "Sin LFO", "Triangle LFO");
		tsb.setPanelColor(AudioConstants.PANELCOLOR);
		tsb.setButtonColor(Color.lightGray);
		addDefaultComponent(mp, tsb, gbl, gbc, 6, 2, 3, 3);
		tsb.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				boolean state = e.getActionCommand().equals("ON");
				lfoModeChanged(state);
			}
		});
		
		// Create the depth pot
		depthPot = createPot(AudioConstants.KNOBSIZE, "Depth", "min, , , , , , , , , ,max");
		depthPot.setNumberOfSections(20);
		depthPot.setLabelsString("min, , , , , , , , , , , , , , , , , , , ,max");
		
		// Set the default value
		depthPot.setValue((int)(DEFAULTDEPTHLEVEL / depthPotGranularity));
		depthChanged((int)(DEFAULTDEPTHLEVEL / depthPotGranularity));
		addDefaultComponent(mp, depthPot, gbl, gbc, 8, 2, 3, 3);
		
		// Add listener to this pot
		depthPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				depthChanged(e.getValue());
			}
		});
		// Create dry level pot
		dryLevelPot = createPot(AudioConstants.KNOBSIZE, "Dry Level", "0%, , , , ,50%, , , , ,100%");
		
		// Set the default value
		dryLevelPot.setValue(DEFAULTDRYLEVEL);
		dryLevelChanged(DEFAULTDRYLEVEL);
		addDefaultComponent(mp, dryLevelPot, gbl, gbc, 0, 7, 3, 3);

		// Add listener to this pot
		dryLevelPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				dryLevelChanged(e.getValue());
			}
		});
		// Create wet level pot
		wetLevelPot = createPot(AudioConstants.KNOBSIZE, "Wet Level", "0%, , , , ,50%, , , , ,100%");
		
		// Set the default value
		wetLevelPot.setValue(DEFAULTWETLEVEL);
		wetLevelChanged(DEFAULTWETLEVEL);
		addDefaultComponent(mp, wetLevelPot, gbl, gbc, 3, 7, 3, 3);

		// Add listener to this pot
		wetLevelPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				wetLevelChanged(e.getValue());
			}
		});
		// Create the modulation mode switch
		tsb = new ToggleSwitchButton(10, "Invert Phase", "Normal Phase");
		tsb.setPanelColor(AudioConstants.PANELCOLOR);
		tsb.setButtonColor(Color.lightGray);
		addDefaultComponent(mp, tsb, gbl, gbc, 6, 7, 3, 3);
		tsb.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				boolean state = e.getActionCommand().equals("ON");
				phaseChanged(state);
			}
		});

		// Create feedback level pot
		feedbackLevelPot = createPot(AudioConstants.KNOBSIZE, "Feedback", "0%, , , , ,50%, , , , ,100%");

		// Set the default value
		feedbackLevelPot.setValue(DEFAULTFEEDBACKLEVEL);
		feedbackLevelChanged(DEFAULTFEEDBACKLEVEL);
		addDefaultComponent(mp, feedbackLevelPot, gbl, gbc, 8, 7, 3, 3);
		
		// Add listener to this pot
		feedbackLevelPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				feedbackLevelChanged(e.getValue());
			}
		});
		// Create the misc components
		onLED = createLED(Color.red, RoundLED.MODESOLID, true);
		addDefaultComponent(mp, onLED, gbl, gbc, 0, 10, 1, 1);
		blink.addPropertyChangeListener(onLED);
		
		bypassLED = createLED(Color.green, RoundLED.MODEBLINK, false);
		addDefaultComponent(mp, bypassLED, gbl, gbc, 7, 10, 1, 1);
		blink.addPropertyChangeListener(bypassLED);

		bypassButton = new SquareButton();
		bypassButton.setPanelColor(AudioConstants.PANELCOLOR);
		bypassButton.setCaption("Bypass");
		addDefaultComponent(mp, bypassButton, gbl, gbc, 8, 10, 3, 1);
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

	// Called when the delay pot is manipulated. potValue is
	// always 0..100.
	public void delayChanged(int newPotValue) {

		// Determine the new delay
		int delayInMs = (int)(newPotValue * delayPotGranularity);
		
		// Range check values
		if (delayInMs <= 0)
			delayInMs = MINDELAYINMS;

		else if (delayInMs > MAXDELAYINMS)
			delayInMs = MAXDELAYINMS;
		
		// Update the display
		delayDisplay.setValue(delayInMs);

		// Perform real processing below
		((ChorusWithUI)aa).setDelayInMs(delayInMs);
	}

	// Called when the LFO rate pot is manipulated. potValue is
	// always 0..100.
	public void rateChanged(int newPotValue) {

		double rateInHz = newPotValue * ratePotGranularity;

		rateDisplay.setValue(rateInHz, 4);

		// Perform real processing below
		((ChorusWithUI)aa).setRateInHz(rateInHz);
	}

	// Called when the LFO waveform is changed
	public void lfoModeChanged(boolean state) {

		// Perform real processing below
		((ChorusWithUI)aa).setLFOMode(state);
	}
	
	// Called when the depth pot is manipulated. potValue is
	// always 0..100.
	public void depthChanged(int newPotValue) {

		double depthInMs = newPotValue * depthPotGranularity;
		
		depthLevelDisplay.setValue(depthInMs, 4);

		// Perform real processing below
		((ChorusWithUI)aa).setDepthLevel(depthInMs);
	}

	// Called when the dry level pot is manipulated.
	public void dryLevelChanged(int newPotValue) {

		dryLevelDisplay.setValue(newPotValue);

		// Perform real processing below
		((ChorusWithUI)aa).setDryLevel(newPotValue);
	}
	
	// Called when the wet level pot is manipulated.
	public void wetLevelChanged(int newPotValue) {

		wetLevelDisplay.setValue(newPotValue);

		// Perform real processing below
		((ChorusWithUI)aa).setWetLevel(newPotValue);
	}
	
	// Called when the feedback phase is changed
	public void phaseChanged(boolean invertPhase) {

		// Perform real processing below
		((ChorusWithUI)aa).setFeedbackPhase(invertPhase);
	}
	
	// Called when the feedback level pot is manipulated.
	public void feedbackLevelChanged(int newPotValue) {

		feedbackLevelDisplay.setValue(newPotValue);

		// Perform real processing below
		((ChorusWithUI)aa).setFeedbackLevel(newPotValue);
	}

	public void bypassChanged(boolean state) {

		((ChorusWithUI)aa).setByPass(state);
	}

	public void windowClosing() {

		((ChorusWithUI)aa).stopUI();
	}
	
	// Private class data
    private double delayPotGranularity;
    private double ratePotGranularity;
    private double depthPotGranularity;

	private ReadoutLabel delayDisplay;
	private ReadoutLabel rateDisplay;
	private ReadoutLabel depthLevelDisplay;
	private ReadoutLabel dryLevelDisplay;
	private ReadoutLabel wetLevelDisplay;
	private ReadoutLabel feedbackLevelDisplay;

	private Pot delayPot;
	private Pot ratePot;
	private Pot depthPot;
	private Pot dryLevelPot;
	private Pot wetLevelPot;
	private Pot feedbackLevelPot;

	private RoundLED onLED;
	private RoundLED bypassLED;

	private SquareButton bypassButton;
}


