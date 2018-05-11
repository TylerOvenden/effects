// Phaser UI Class
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


public class PhaserUI extends BaseUI implements CloseableFrameIF {
	
	public static final double MAXSWEEPRATEINHZ = 5;
	public static final double MINSWEEPRATEINHZ = 0.2;
	public static final double MAXSWEEPRANGEINOCTAVES = 7;
	public static final double MINSWEEPRANGEINOCTAVES = 1;
	public static final double MAXBASEFREQINHZ = 150;
	public static final double MINBASEFREQINHZ = 50;

	public static final double DEFAULTSWEEPRATEINHZ = 1.0;
	public static final double DEFAULTSWEEPRANGEINOCTAVES = 5;
	public static final double DEFAULTBASEFREQINHZ  = 100;
	public static final int    DEFAULTDRYLEVEL  = 30;
	public static final int    DEFAULTWETLEVEL  = 30;
	public static final int    DEFAULTFEEDBACKLEVEL = 10;

	public PhaserUI(Blinker blink, AbstractAudio aa) {
		
		super("Phaser Effect Processor", aa);

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Use a grid bag for the panel
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		// Create the displays
		sweepRateDisplay = new ReadoutLabel("Hz");
		addDefaultComponent(mp, sweepRateDisplay, gbl, gbc, 0, 0, 3, 1);

		sweepRangeDisplay = new ReadoutLabel("octaves");
		addDefaultComponent(mp, sweepRangeDisplay, gbl, gbc, 3, 0, 3, 1);

		baseFreqDisplay = new ReadoutLabel("Hz");
		addDefaultComponent(mp, baseFreqDisplay, gbl, gbc, 8, 0, 3, 1);

		dryLevelDisplay = new ReadoutLabel(Color.red, "%");
		addDefaultComponent(mp, dryLevelDisplay, gbl, gbc, 0, 5, 3, 1);

		wetLevelDisplay = new ReadoutLabel(Color.red, "%");
		addDefaultComponent(mp, wetLevelDisplay, gbl, gbc, 3, 5, 3, 1);

		feedbackLevelDisplay = new ReadoutLabel(Color.red,"%");
		addDefaultComponent(mp, feedbackLevelDisplay, gbl, gbc, 8, 5, 3, 1);
        
		// Create the sweep rate pot
		sweepRatePot = createPot(AudioConstants.KNOBSIZE, "Sweep Rate",
			"min, , , , , , , , , ,max", 
			MAXSWEEPRATEINHZ, MINSWEEPRATEINHZ);
		sweepRatePot.setNumberOfSections(20);
		sweepRatePot.setLabelsString("min, , , , , , , , , , , , , , , , , , , ,max");
		
		// Set the default value
		sweepRatePot.setRealValue(DEFAULTSWEEPRATEINHZ);
		sweepRateChanged(sweepRatePot);
		addDefaultComponent(mp, sweepRatePot, gbl, gbc, 0, 2, 3, 3);
		
		// Add listener to this pot
		sweepRatePot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sweepRateChanged(sweepRatePot);
			}
		});
		// Create the sweep range pot
		sweepRangePot = createPot(AudioConstants.KNOBSIZE, "Sweep Range",
			"min, , , , , , , , , ,max", 
			MAXSWEEPRANGEINOCTAVES, MINSWEEPRANGEINOCTAVES);
		sweepRangePot.setNumberOfSections(20);
		sweepRangePot.setLabelsString("min, , , , , , , , , , , , , , , , , , , ,max");
		
		// Set the default value
		sweepRangePot.setRealValue(DEFAULTSWEEPRANGEINOCTAVES);
		sweepRangeChanged(sweepRangePot);
		addDefaultComponent(mp, sweepRangePot, gbl, gbc, 3, 2, 3, 3);
		
		// Add listener to this pot
		sweepRangePot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sweepRangeChanged(sweepRangePot);
			}
		});
		
		// Create the base freq pot
		baseFreqPot = createPot(AudioConstants.KNOBSIZE, "Base Frequency",
			"min, , , , , , , , , ,max",
			MAXBASEFREQINHZ, MINBASEFREQINHZ);
		
		// Set the default value
		baseFreqPot.setRealValue(DEFAULTBASEFREQINHZ);
		baseFreqChanged(baseFreqPot);
		addDefaultComponent(mp, baseFreqPot, gbl, gbc, 8, 2, 3, 3);
		
		// Add listener to this pot
		baseFreqPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				baseFreqChanged(baseFreqPot);
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
		ToggleSwitchButton tsb = new ToggleSwitchButton(10, "Invert Phase", "Normal Phase");
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

	// Called when the sweep rate pot is manipulated.
	public void sweepRateChanged(RealValuedPot p) {

		double sweepRate = p.getRealValue();
		
		// Update the display
		sweepRateDisplay.setValue(sweepRate, 4);

		// Perform real processing below
		((PhaserWithUI)aa).setSweepRate(sweepRate);
	}

	public void sweepRangeChanged(RealValuedPot p) {

		double range = p.getRealValue();

		sweepRangeDisplay.setValue(range, 4);

		// Perform real processing below
		((PhaserWithUI)aa).setSweepRange(range);
	}

	public void baseFreqChanged(RealValuedPot p) {

		double baseFreq = p.getRealValue();

		baseFreqDisplay.setValue(baseFreq, 4);

		// Perform real processing below
		((PhaserWithUI)aa).setBaseFreq(baseFreq);
	}
	
	public void dryLevelChanged(int newPotValue) {

		dryLevelDisplay.setValue(newPotValue);

		// Perform real processing below
		((PhaserWithUI)aa).setDryLevel(newPotValue);
	}
	
	public void wetLevelChanged(int newPotValue) {

		wetLevelDisplay.setValue(newPotValue);

		// Perform real processing below
		((PhaserWithUI)aa).setWetLevel(newPotValue);
	}
	
	public void phaseChanged(boolean invertPhase) {

		// Perform real processing below
		((PhaserWithUI)aa).setFeedbackPhase(invertPhase);
	}
	
	public void feedbackLevelChanged(int newPotValue) {

		feedbackLevelDisplay.setValue(newPotValue);

		// Perform real processing below
		((PhaserWithUI)aa).setFeedbackLevel(newPotValue);
	}

	public void bypassChanged(boolean state) {

		((PhaserWithUI)aa).setByPass(state);
	}

	public void windowClosing() {

		((PhaserWithUI)aa).stopUI();
	}
	
	// Private class data
	private ReadoutLabel sweepRateDisplay;
	private ReadoutLabel sweepRangeDisplay;
	private ReadoutLabel baseFreqDisplay;
	private ReadoutLabel dryLevelDisplay;
	private ReadoutLabel wetLevelDisplay;
	private ReadoutLabel feedbackLevelDisplay;

	private RealValuedPot sweepRatePot;
	private RealValuedPot sweepRangePot;
	private RealValuedPot baseFreqPot;
	private Pot dryLevelPot;
	private Pot wetLevelPot;
	private Pot feedbackLevelPot;

	private RoundLED onLED;
	private RoundLED bypassLED;

	private SquareButton bypassButton;
}


