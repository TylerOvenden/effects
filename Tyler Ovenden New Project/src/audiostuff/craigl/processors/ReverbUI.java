// ReverbUI Class
// Written by: Craig A. Lindley
// Last Update: 09/13/98

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

public class ReverbUI extends BaseUI implements CloseableFrameIF {

	private static final double SUSTAINTIMEMSMIN = 1;
	private static final double SUSTAINTIMEMSMAX = 1000;
	public  static final double SUSTAINTIMEMSDEF = 500;
	public  static final double MIXDEF = 0.25;

	private static final double MINCOMBDELAYMS = 1.0;
	private static final double MAXCOMBDELAYMS = 100.0;
	private static final double MINALLPASSDELAYMS = 1.0;
	private static final double MAXALLPASSDELAYMS = 50.0;

	public ReverbUI(Blinker blink, AbstractAudio aa) {
		
		super("Reverb Processor", aa);

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Create the UI
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		// Create the array of slide pots
		Box b = createSlidersGroup();
		addDefaultComponent(mp, b, gbl, gbc, 0, 0, 13, 6);

		// Create the reverb time pot
		sustainPot = createPot(AudioConstants.KNOBSIZE, "Sustain Time",
			" , , , , , , , , , , ", 
			SUSTAINTIMEMSMAX, SUSTAINTIMEMSMIN);
		sustainPot.setTicColor(Color.green);
		sustainPot.setGradColor(Color.green);
		sustainPot.setNumberOfSections(20);
		sustainPot.setLabelsString("1ms, , , , , , , , , , , , , , , , , , , ,1000ms");
		
		// Set the default value
		sustainPot.setRealValue(SUSTAINTIMEMSDEF);
		sustainChanged(sustainPot);
		addDefaultComponent(mp, sustainPot, gbl, gbc, 1, 6, 3, 3);
		
		// Add listener to this pot
		sustainPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sustainChanged(sustainPot);
			}
		});

		// Create the mix pot
		mixPot = createPot(AudioConstants.KNOBSIZE, "Dry/Wet Mix",
			"dry, , , , , , , , , ,wet", 1.0, 0.0);
		mixPot.setTicColor(Color.blue);
		mixPot.setGradColor(Color.blue);
		
		// Set the default value
		mixPot.setRealValue(MIXDEF);
		addDefaultComponent(mp, mixPot, gbl, gbc, 5, 6, 3, 3);
		
		// Add listener to this pot
		mixPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				mixChanged(mixPot);
			}
		});

		// Create the bypass LED
		bypassLED = createLED(Color.green, RoundLED.MODEBLINK, false);
		addDefaultComponent(mp, bypassLED, gbl, gbc, 10, 7, 1, 1);
		blink.addPropertyChangeListener(bypassLED);

		// Create the bypass LED
		bypassButton = new SquareButton();
		bypassButton.setPanelColor(AudioConstants.PANELCOLOR);
		bypassButton.setCaption("Bypass");
		addDefaultComponent(mp, bypassButton, gbl, gbc, 11, 7, 2, 1);
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

	// Create a slide pot with specified configuration
	private RealValuedSlidePot createSlider(double maxDelay, double minDelay) {

		RealValuedSlidePot sp = 
			new RealValuedSlidePot(100, 14, "", maxDelay, minDelay);
        sp.setKnobColor(AudioConstants.KNOBCOLOR);
        sp.setPanelColor(AudioConstants.PANELCOLOR);
		sp.setTextColor(Color.black);
		sp.setGradColor(Color.black);
		sp.setFontSize(10);
		sp.setNumberOfSections(15);
		sp.setLabelPercent(220);
		String maxString = new Integer((int)maxDelay).toString();
		String minString = new Integer((int)minDelay).toString();
		String labelString = maxString + "ms,";
		labelString += " , , , , , , , , , , , , , ,";
		labelString += minString + "ms";
		sp.setLabelsString(labelString);
		return sp;
	}

	// Group a slide pot and label together
	private Panel createLabeledSlider(SlidePot sp, String label) {

		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		// Put slider down
		addDefaultComponent(mp, sp, gbl, gbc, 0, 0, 2, 5);

		// Now make the label
		Label l = new Label(label, Label.CENTER);
		addDefaultComponent(mp, l, gbl, gbc, 0, 5, 2, 1);

		// Return the panel
		return mp;
	}

	// Create the array of labeled sliders
	private Box createSlidersGroup() {
		
		Panel panel = new Panel();
		Box box = new Box(panel, "Adjustable Delay Elements");
		panel.setLayout(new GridLayout(1, 6));
		
		// Comb filter 1
		final RealValuedSlidePot comb1Pot = createSlider(MAXCOMBDELAYMS, MINCOMBDELAYMS);
		comb1Pot.setRealValue(SchroederReverb.COMB1DELAYMSDEF);
		
		// Add listener to this pot
		comb1Pot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				comb1Changed(comb1Pot);
			}
		});
		Panel p = createLabeledSlider(comb1Pot, "Comb 1");
		panel.add(p);

		// Comb filter 2
		final RealValuedSlidePot comb2Pot = createSlider(MAXCOMBDELAYMS, MINCOMBDELAYMS);
		comb2Pot.setRealValue(SchroederReverb.COMB2DELAYMSDEF);
		
		// Add listener to this pot
		comb2Pot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				comb2Changed(comb2Pot);
			}
		});
		p = createLabeledSlider(comb2Pot, "Comb 2");
		panel.add(p);
		
		// Comb filter 3
		final RealValuedSlidePot comb3Pot = createSlider(MAXCOMBDELAYMS, MINCOMBDELAYMS);
		comb3Pot.setRealValue(SchroederReverb.COMB3DELAYMSDEF);
		
		// Add listener to this pot
		comb3Pot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				comb3Changed(comb3Pot);
			}
		});
		p = createLabeledSlider(comb3Pot, "Comb 3");
		panel.add(p);

		// Comb filter 4
		final RealValuedSlidePot comb4Pot = createSlider(MAXCOMBDELAYMS, MINCOMBDELAYMS);
		comb4Pot.setRealValue(SchroederReverb.COMB4DELAYMSDEF);
		
		// Add listener to this pot
		comb4Pot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				comb4Changed(comb4Pot);
			}
		});
		p = createLabeledSlider(comb4Pot, "Comb 4");
		panel.add(p);
		
		// AllPass filter 1
		final RealValuedSlidePot allpass1Pot = createSlider(MAXALLPASSDELAYMS, MINALLPASSDELAYMS);
		allpass1Pot.setRealValue(SchroederReverb.ALLPASS1DELAYMSDEF);
		
		// Add listener to this pot
		allpass1Pot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				allpass1Changed(allpass1Pot);
			}
		});
		p = createLabeledSlider(allpass1Pot, "AllPass 1");
		panel.add(p);

		// AllPass filter 2
		final RealValuedSlidePot allpass2Pot = createSlider(MAXALLPASSDELAYMS, MINALLPASSDELAYMS);
		allpass2Pot.setRealValue(SchroederReverb.ALLPASS2DELAYMSDEF);
		
		// Add listener to this pot
		allpass2Pot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				allpass2Changed(allpass2Pot);
			}
		});
		p = createLabeledSlider(allpass2Pot, "AllPass 2");
		panel.add(p);
		
		return box;
	}
	
	// Called when the specific pot is manipulated.
	public void comb1Changed(RealValuedSlidePot p) {

		((ReverbWithUI)aa).comb1Delay(p.getRealValue());
	}

	// Called when the specific pot is manipulated.
	public void comb2Changed(RealValuedSlidePot p) {

		((ReverbWithUI)aa).comb2Delay(p.getRealValue());
	}

	// Called when the specific pot is manipulated.
	public void comb3Changed(RealValuedSlidePot p) {

		((ReverbWithUI)aa).comb3Delay(p.getRealValue());
	}

	// Called when the specific pot is manipulated.
	public void comb4Changed(RealValuedSlidePot p) {

		((ReverbWithUI)aa).comb4Delay(p.getRealValue());
	}

	// Called when the specific pot is manipulated.
	public void allpass1Changed(RealValuedSlidePot p) {

		((ReverbWithUI)aa).allpass1Delay(p.getRealValue());
	}

	// Called when the specific pot is manipulated.
	public void allpass2Changed(RealValuedSlidePot p) {

		((ReverbWithUI)aa).allpass2Delay(p.getRealValue());
	}

	public void bypassChanged(boolean state) {

		((ReverbWithUI)aa).setByPass(state);
	}

	public void sustainChanged(RealValuedPot p) {

		((ReverbWithUI)aa).setSustainTime(p.getRealValue());
	}
	
	public void mixChanged(RealValuedPot p) {

		((ReverbWithUI)aa).setDryWetMix(p.getRealValue());
	}
	
	public void windowClosing() {

		((ReverbWithUI)aa).stopUI();
	}

	// Private class data
	private RealValuedSlidePot comb1Pot;
	private RealValuedSlidePot comb2Pot;
	private RealValuedSlidePot comb3Pot;
	private RealValuedSlidePot comb4Pot;
	private RealValuedSlidePot allpass1Pot;
	private RealValuedSlidePot allpass2Pot;

	private RealValuedPot sustainPot;
	private RealValuedPot mixPot;

	private RoundLED bypassLED;
	private SquareButton bypassButton;
}
