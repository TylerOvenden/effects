// Parametric Equalizer Front Panel Class
// Written by: Craig A. Lindley
// Last Update: 07/05/99

package craigl.processors;

import java.awt.*;
import java.awt.event.*;
import craigl.utils.*;
import craigl.uiutils.*;
import craigl.beans.blinker.*;
import craigl.beans.pots.*;
import craigl.beans.leds.*;
import craigl.beans.buttons.*;

public class ParametricEQUI extends BaseUI implements CloseableFrameIF {

	private static final int HIGHPASSFREQMIN = 5000;
	private static final int HIGHPASSFREQMAX = 16000;
	public  static final int HIGHPASSFREQDEF = 5000;

	private static final int BANDPASSFREQMIN = 1500;
	private static final int BANDPASSFREQMAX = 6000;
	public  static final int BANDPASSFREQDEF = 3000;

	private static final double BANDPASSQMIN = 1.1;
	private static final double BANDPASSQMAX = 16.0;
	public  static final double BANDPASSQDEF = 8.0;

	private static final int LOWPASSFREQMIN = 40;
	private static final int LOWPASSFREQMAX = 1500;
	public  static final int LOWPASSFREQDEF = 200;

	public ParametricEQUI(Blinker blink, AbstractAudio aa) {

		super("Parametric Equalizer Processor", aa);

		this.blink = blink;

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Create the UI panel
		Panel mp = new Panel();
		
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		// Now start the layout
		// Create the high pass freq pot
		hpFreqPot = createPot(AudioConstants.KNOBSIZE, "High Frequencies",
			" , , , , , , , , , , ", 
			HIGHPASSFREQMAX, HIGHPASSFREQMIN);
		hpFreqPot.setTextColor(Color.black);
		hpFreqPot.setTicColor(Color.red);
		hpFreqPot.setGradColor(Color.red);
		hpFreqPot.setNumberOfSections(20);
		hpFreqPot.setLabelsString("5kHz, , , , , , , , , , , , , , , , , , , ,16kHz");
		
		// Set the default value
		hpFreqPot.setIntValue(HIGHPASSFREQDEF);
		hpFreqChanged(hpFreqPot);
		addDefaultComponent(mp, hpFreqPot, gbl, gbc, 0, 1, 3, 3);
		
		// Add listener to this pot
		hpFreqPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				hpFreqChanged(hpFreqPot);
			}
		});
		// Create boost/cut slide pot
		hpBoostCutPot = createSlider(Color.red);
		hpBoostCutPot.setTextColor(Color.black);
		addDefaultComponent(mp, hpBoostCutPot, gbl, gbc, 3, 0, 2, 5);

		// Add listener to this pot
		hpBoostCutPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				hpGainChanged(hpBoostCutPot);
			}
		});

		// Create the band pass freq pot
		bpFreqPot = createPot(AudioConstants.KNOBSIZE, "Mid Range Frequencies",
			" , , , , , , , , , , ", 
			BANDPASSFREQMAX, BANDPASSFREQMIN);
		bpFreqPot.setTextColor(Color.black);
		bpFreqPot.setTicColor(Color.green);
		bpFreqPot.setGradColor(Color.green);
		bpFreqPot.setNumberOfSections(20);
		bpFreqPot.setLabelsString("1.5kHz, , , , , , , , , , , , , , , , , , , ,6kHz");
		
		// Set the default value
		bpFreqPot.setIntValue(BANDPASSFREQDEF);
		bpFreqChanged(bpFreqPot);
		addDefaultComponent(mp, bpFreqPot, gbl, gbc, 0, 4, 3, 3);
		
		// Add listener to this pot
		bpFreqPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				bpFreqChanged(bpFreqPot);
			}
		});
		
		// Create the band pass Q pot
		bpQPot = createPot(AudioConstants.KNOBSIZE, "Sharpness (Q)",
			" , , , , , , , , , , ", 
			BANDPASSQMAX, BANDPASSQMIN);
		bpQPot.setTextColor(Color.black);
		bpQPot.setTicColor(Color.green);
		bpQPot.setGradColor(Color.green);
		bpQPot.setNumberOfSections(15);
		bpQPot.setLabelsString("1.1, , , , , , , , , , , , , , ,16");
		
		// Set the default value
		bpQPot.setRealValue(BANDPASSQDEF);
		bpQChanged(bpQPot);
		addDefaultComponent(mp, bpQPot, gbl, gbc, 0, 7, 3, 3);
		
		// Add listener to this pot
		bpQPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				bpQChanged(bpQPot);
			}
		});
		
		// Create boost/cut slide pot
		bpBoostCutPot = createSlider(Color.green);
		bpBoostCutPot.setTextColor(Color.black);
		addDefaultComponent(mp, bpBoostCutPot, gbl, gbc, 3, 5, 2, 5);

		// Add listener to this pot
		bpBoostCutPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				bpGainChanged(bpBoostCutPot);
			}
		});

		// Create the low pass freq pot
		lpFreqPot = createPot(AudioConstants.KNOBSIZE, "Low Frequencies",
			" , , , , , , , , , , ", 
			LOWPASSFREQMAX, LOWPASSFREQMIN);
		lpFreqPot.setTextColor(Color.black);
		lpFreqPot.setTicColor(Color.blue);
		lpFreqPot.setGradColor(Color.blue);
		lpFreqPot.setNumberOfSections(20);
		lpFreqPot.setLabelsString("40Hz, , , , , , , , , , , , , , , , , , , ,1.5kHz");
		
		// Set the default value
		lpFreqPot.setIntValue(LOWPASSFREQDEF);
		lpFreqChanged(lpFreqPot);
		addDefaultComponent(mp, lpFreqPot, gbl, gbc, 0, 11, 3, 3);
		
		// Add listener to this pot
		lpFreqPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				lpFreqChanged(lpFreqPot);
			}
		});
		// Create boost/cut slide pot
		lpBoostCutPot = createSlider(Color.blue);
		lpBoostCutPot.setTextColor(Color.black);
		addDefaultComponent(mp, lpBoostCutPot, gbl, gbc, 3, 10, 2, 5);

		// Add listener to this pot
		lpBoostCutPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				lpGainChanged(lpBoostCutPot);
			}
		});

		// Create the bypass LED
		bypassLED = createLED(Color.green, RoundLED.MODEBLINK, false);
		addDefaultComponent(mp, bypassLED, gbl, gbc, 0, 14, 1, 1);
		blink.addPropertyChangeListener(bypassLED);

		// Create the bypass LED
		bypassButton = new SquareButton();
		bypassButton.setPanelColor(AudioConstants.PANELCOLOR);
		bypassButton.setCaption("Bypass");
		addDefaultComponent(mp, bypassButton, gbl, gbc, 1, 14, 2, 1);
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

	// Called when the high pass freq pot is manipulated.
	public void hpFreqChanged(IntValuedPot p) {

		int freq = p.getIntValue();
		
		// Perform real processing below
		((ParametricEQWithUI)aa).highPassShelfFreq(freq);
	}

	// Called when the band pass freq pot is manipulated.
	public void bpFreqChanged(IntValuedPot p) {

		int freq = p.getIntValue();
		
		// Perform real processing below
		((ParametricEQWithUI)aa).bandPassPeakFreq(freq);
	}

	// Called when the low pass freq pot is manipulated.
	public void lpFreqChanged(IntValuedPot p) {

		int freq = p.getIntValue();
		
		// Perform real processing below
		((ParametricEQWithUI)aa).lowPassShelfFreq(freq);
	}

	// Called when the high pass gain pot is manipulated.
	public void hpGainChanged(BoostCutSlidePot p) {

		((ParametricEQWithUI)aa).highPassShelfGain(p.getGain());
	}

	// Called when the band pass gain pot is manipulated.
	public void bpGainChanged(BoostCutSlidePot p) {

		((ParametricEQWithUI)aa).bandPassPeakGain(p.getGain());
	}

	// Called when the low pass gain pot is manipulated.
	public void lpGainChanged(BoostCutSlidePot p) {

		((ParametricEQWithUI)aa).lowPassShelfGain(p.getGain());
	}

	// Called when the band pass Q pot is manipulated.
	public void bpQChanged(RealValuedPot p) {

		double q = p.getRealValue();
		
		// Perform real processing below
		((ParametricEQWithUI)aa).bandPassPeakQ(q);
	}

	// Create a slide pot with specified configuration
	private BoostCutSlidePot createSlider(Color c) {

		BoostCutSlidePot sp = new BoostCutSlidePot(85, 12, "gain", -12, +12);
        sp.setKnobColor(c);
		sp.setGradColor(c);
        sp.setPanelColor(AudioConstants.PANELCOLOR);
		sp.setNumberOfSections(12);
		sp.setLabelPercent(170);
		sp.setLabelsString("+12, , , , , ,0dB, , , , , ,-12");
		return sp;
	}

	public void bypassChanged(boolean state) {

		((ParametricEQWithUI)aa).setByPass(state);
	}

	public void windowClosing() {

		((ParametricEQWithUI)aa).stopUI();
	}

	// Private class data
	private Blinker blink;
	private RoundLED bypassLED;
	private SquareButton bypassButton;

	private IntValuedPot hpFreqPot;
	private IntValuedPot bpFreqPot;
	private RealValuedPot bpQPot;
	private IntValuedPot lpFreqPot;

	private BoostCutSlidePot hpBoostCutPot;
	private BoostCutSlidePot bpBoostCutPot;
	private BoostCutSlidePot lpBoostCutPot;
}
