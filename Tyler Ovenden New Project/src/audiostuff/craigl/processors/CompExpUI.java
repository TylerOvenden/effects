// Compressor/Expander UI Class
// Written by: Craig A. Lindley
// Last Update: 06/26/99

package audiostuff.craigl.processors;

import java.awt.*;
import java.awt.event.*;
import audiostuff.craigl.compexp.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.uiutils.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.beans.displays.*;
import audiostuff.craigl.beans.pots.*;
import audiostuff.craigl.beans.leds.*;
import audiostuff.craigl.beans.buttons.*;

public class CompExpUI extends BaseUI implements CloseableFrameIF {

	private static final double MAXTHRESHOLDDB = 0;
	private static final double MINTHRESHOLDDB = -60;
	public  static final double THRESHOLDDEF   = -16;

	private static final double MAXBTRATIO = 1.0;
	public  static final double MINBTRATIO = 25.0;
	public  static final double BTRATIODEF = 1.0;

	public  static final double MAXATRATIO = +11.0;
	public  static final double MINATRATIO = -11.0;
	public  static final double ATRATIODEF = 0.0;
	
	private static final double MAXATTACKMS = 500;
	private static final double MINATTACKMS = 0;
	public  static final double ATTACKMSDEF = 50;

	private static final double MAXRELEASEMS = 2000;
	private static final double MINRELEASEMS = 0;
	public  static final double RELEASEMSDEF = 100;

	public  static final double MAXGAININDB = +12.0;
	private static final double MINGAININDB = -12.0;
	public  static final double GAINDBDEF   =  0.0;

	public CompExpUI(Blinker blink, AbstractAudio aa) {
		
		super("Compressor/Expander/Limiter/Noise Gate Processor", aa);

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Create the UI
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(1,1,1,1);
		mp.setLayout(gbl);

		// Create the graph surface
		LabeledGraphSurface lgs = new LabeledGraphSurface();
		gs = lgs.getGraphSurface();

		addDefaultComponent(mp, lgs, gbl, gbc, 0, 0, 30, 30);

		Panel p = createSlidersGroup();
		addDefaultComponent(mp, p, gbl, gbc, 34, 0, 40, 30);

		// Create the bypass LED
		bypassLED = createLED(Color.green, RoundLED.MODEBLINK, false);
		addDefaultComponent(mp, bypassLED, gbl, gbc, 5, 31, 1, 1);
		blink.addPropertyChangeListener(bypassLED);

		// Create the bypass LED
		bypassButton = new SquareButton();
		bypassButton.setTextColor(Color.black);
		bypassButton.setPanelColor(AudioConstants.PANELCOLOR);
		bypassButton.setCaption("Bypass");
		addDefaultComponent(mp, bypassButton, gbl, gbc, 8, 31, 2, 1);
		bypassButton.addActionListener(bypassLED);
		bypassButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				boolean state = e.getActionCommand().equals("ON");
				bypassChanged(state);
			}
		});
		
		// Create the power on LED
		RoundLED onLED = createLED(Color.red, RoundLED.MODESOLID, true);
		addDefaultComponent(mp, onLED, gbl, gbc, 65, 31, 1, 1);
		blink.addPropertyChangeListener(onLED);

		Label l = new Label("Power On");
		addDefaultComponent(mp, l, gbl, gbc, 66, 31, 8, 1);

		// Finish up but don't make visible yet
		add(mp);
		pack();
	}

	// Create a slide pot with specified configuration
	private RealValuedSlidePot createSlider(double maxValue,
											double minValue,
											String labelString) {
		RealValuedSlidePot sp = 
			new RealValuedSlidePot(100, 12, "", maxValue, minValue);
        sp.setKnobColor(AudioConstants.KNOBCOLOR);
        sp.setPanelColor(AudioConstants.PANELCOLOR);
		sp.setTextColor(Color.black);
		sp.setGradColor(Color.black);
		sp.setFontSize(9);
		sp.setNumberOfSections(15);
		sp.setLabelPercent(220);
		sp.setLabelsString(labelString);
		return sp;
	}

	// Group a slide pot and label together
	private Panel createLabeledSlider(ReadoutLabel rl, SlidePot sp, String label) {

		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(1,1,1,1);
		mp.setLayout(gbl);

		// Put display down
		addDefaultComponent(mp, rl, gbl, gbc, 0, 0, 2, 1);

		// Put slider down
		addDefaultComponent(mp, sp, gbl, gbc, 0, 1, 2, 5);

		// Now make the label
		Label l = new Label(label, Label.CENTER);
		addDefaultComponent(mp, l, gbl, gbc, 0, 6, 2, 1);

		// Return the panel
		return mp;
	}

	// Create the array of labeled sliders
	private Panel createSlidersGroup() {
		
		Panel panel = new Panel();
		panel.setLayout(new GridLayout(1, 6));
		
		// Create display 
		final ReadoutLabel rl1 = new ReadoutLabel(Color.green, "dB");
		
		// Threshold Pot
		final RealValuedSlidePot thresholdPot = 
			createSlider(MAXTHRESHOLDDB, MINTHRESHOLDDB, 
			"0, , , , , , , , , , , , , , ,-60dB");
		thresholdPot.setRealValue(THRESHOLDDEF);
		
		// Update display and processing element
		thresholdChanged(thresholdPot, rl1);		
		
		// Add listener to this pot
		thresholdPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				thresholdChanged(thresholdPot, rl1);
			}
		});
		Panel p = createLabeledSlider(rl1, thresholdPot, "Threshold");
		panel.add(p);

		// Below threshold ratio
		// Create display 
		final ReadoutLabel rl2 = new ReadoutLabel(Color.green, ": 1");

		final RealValuedSlidePot btrPot = 
			createSlider(MAXBTRATIO, MINBTRATIO,
			"1:1, , , , , , , , , , , , , , ,gate");

		btrPot.setRealValue(BTRATIODEF);

		// Update display and processing element
		btrChanged(btrPot, rl2);
		
		// Add listener to this pot
		btrPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				btrChanged(btrPot, rl2);
			}
		});
		p = createLabeledSlider(rl2, btrPot, "BT Ratio");
		panel.add(p);
		
		// Above threshold ratio 
		// Create display 
		final ReadoutLabel rl3 = new ReadoutLabel(Color.green, "");

		final RealValuedSlidePot atrPot = 
			createSlider(MAXATRATIO, MINATRATIO,
			"12:1, , , , , , ,1:1, , , , , , , ,lim");

		atrPot.setRealValue(ATRATIODEF);

		// Update display and processing element
		atrChanged(atrPot, rl3);
		
		// Add listener to this pot
		atrPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				atrChanged(atrPot, rl3);
			}
		});
		p = createLabeledSlider(rl3, atrPot, "AT Ratio");
		panel.add(p);

		// Attack pot
		// Create display 
		final ReadoutLabel rl4 = new ReadoutLabel(Color.green, "ms");

		final RealValuedSlidePot attackPot = 
			createSlider(MAXATTACKMS, MINATTACKMS,
			"500, , , , , , , , , , , , , , ,0ms");

		attackPot.setRealValue(ATTACKMSDEF);

		// Update display and processing element
		attackChanged(attackPot, rl4);
		
		// Add listener to this pot
		attackPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				attackChanged(attackPot, rl4);
			}
		});
		p = createLabeledSlider(rl4, attackPot, "Attack");
		panel.add(p);
		
		// Release pot
		// Create display 
		final ReadoutLabel rl5 = new ReadoutLabel(Color.green, "ms");

		final RealValuedSlidePot releasePot = 
			createSlider(MAXRELEASEMS, MINRELEASEMS,
			"2000, , , , , , , , , , , , , , ,0ms");

		releasePot.setRealValue(RELEASEMSDEF);

		// Update display and processing element
		releaseChanged(releasePot, rl5);
		
		// Add listener to this pot
		releasePot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				releaseChanged(releasePot, rl5);
			}
		});
		p = createLabeledSlider(rl5, releasePot, "Release");
		panel.add(p);
		
		// Gain pot
		// Create display 
		final ReadoutLabel rl6 = new ReadoutLabel(Color.green, "dB");

		final RealValuedSlidePot gainPot = 
			createSlider(MAXGAININDB, MINGAININDB,
			"+12, , , , , , , ,0, , , , , , ,-12dB");

		gainPot.setRealValue(GAINDBDEF);

		// Update display and processing element
		gainChanged(gainPot, rl6);
		
		// Add listener to this pot
		gainPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				gainChanged(gainPot, rl6);
			}
		});
		p = createLabeledSlider(rl6, gainPot, "Gain");
		panel.add(p);

		return panel;
	}
	
	// Called when the specific pot is manipulated.
	public void thresholdChanged(RealValuedSlidePot p, ReadoutLabel rl) {

		double value = p.getRealValue();

		// Update display
		rl.setValue(value, 5);
		
		// Update graph
		gs.setThreshold(value);

		((CompExpWithUI)aa).setThreshold(value);
	}

	// Called when the below threshold pot is manipulated.
	public void btrChanged(RealValuedSlidePot p, ReadoutLabel rl) {

		double value = p.getRealValue();

		// Update display
		rl.setValue(value, 5);

		// Update graph
		gs.setBelowThresholdRatio(value);

		((CompExpWithUI)aa).setBelowThresholdRatio(value);
	}

	// Called when the above threshold pot is manipulated.
	public void atrChanged(RealValuedSlidePot p, ReadoutLabel rl) {

		String displayString = "";
		double value = p.getRealValue();

		if (value == 0.0) {
			// If value is zero gain is 1:1
			displayString = "1 : 1";
		
		}	else if (value == MINATRATIO) {
			// If doing max compression change label
			displayString = "limiting";
		
		}	else	{
			// Normal case format value for display
			displayString = "" + (1 + Math.abs(value));
			int dotIndex = displayString.indexOf('.');
			displayString = displayString.substring(0, dotIndex + 2);

			// Change how ratio is displayed depending upon value
			if (value < 0) {
				// Value is negative so compression is occuring
				displayString = "1 : " + displayString;
			}	else	{
				// Value is positive so expansion is occuring
				displayString = displayString + " : 1";
			}
		}
		// Update display
		rl.setValue(displayString);

		// Values range from MAXATRATIO to MINATRATIO
		// Update processor and graph
		gs.setAboveThresholdRatio(value);

		((CompExpWithUI)aa).setAboveThresholdRatio(value);
	}

	// Called when the specific pot is manipulated.
	public void attackChanged(RealValuedSlidePot p, ReadoutLabel rl) {

		int value = (int) p.getRealValue();

		// Update display
		rl.setValue(value);

		((CompExpWithUI)aa).setAttack(value);
	}

	// Called when the specific pot is manipulated.
	public void releaseChanged(RealValuedSlidePot p, ReadoutLabel rl) {

		int value = (int) p.getRealValue();

		// Update display
		rl.setValue(value);

		((CompExpWithUI)aa).setRelease(value);
	}

	// Called when the specific pot is manipulated.
	public void gainChanged(RealValuedSlidePot p, ReadoutLabel rl) {

		int value = (int) p.getRealValue();

		// Update display
		rl.setValue(value);

		((CompExpWithUI)aa).setGain(value);
	}

	public void bypassChanged(boolean state) {

		((CompExpWithUI)aa).setByPass(state);
	}

	public void windowClosing() {

		((CompExpWithUI)aa).stopUI();
	}

	public static void main(String [] args) {

		CompExpUI ceui = new CompExpUI(new Blinker(500), null);
		ceui.setVisible(true);
	}
	
	// Private class data
	private GraphSurface gs;
	private RealValuedSlidePot thresholdPot;
	private RealValuedSlidePot btrPot;
	private RealValuedSlidePot atrPot;
	private RealValuedSlidePot attackPot;
	private RealValuedSlidePot releasePot;

	private RoundLED bypassLED;
	private SquareButton bypassButton;
}
