// GraphicEQUI Class
// Written by: Craig A. Lindley
// Last Update: 08/30/98

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

public class GraphicEQUI extends BaseUI 
		implements CloseableFrameIF {

	public GraphicEQUI(Blinker blink, AbstractAudio aa) {
		
		super("Graphic Equalizer Processor", aa);

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
		addDefaultComponent(mp, b, gbl, gbc, 0, 0, 18, 6);

		// Create the power on LED
		RoundLED onLED = createLED(Color.red, RoundLED.MODESOLID, true);
		addDefaultComponent(mp, onLED, gbl, gbc, 1, 6, 1, 1);
		blink.addPropertyChangeListener(onLED);

		Label l = new Label("Power On");
		addDefaultComponent(mp, l, gbl, gbc, 2, 6, 8, 1);

		// Create the bypass LED
		bypassLED = createLED(Color.green, RoundLED.MODEBLINK, false);
		addDefaultComponent(mp, bypassLED, gbl, gbc, 14, 6, 1, 1);
		blink.addPropertyChangeListener(bypassLED);

		// Create the bypass LED
		bypassButton = new SquareButton();
		bypassButton.setPanelColor(AudioConstants.PANELCOLOR);
		bypassButton.setCaption("Bypass");
		addDefaultComponent(mp, bypassButton, gbl, gbc, 15, 6, 2, 1);
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
	private BoostCutSlidePot createSlider() {

		BoostCutSlidePot sp = new BoostCutSlidePot(100, 14, "", -12, +12);
        sp.setKnobColor(AudioConstants.KNOBCOLOR);
        sp.setPanelColor(AudioConstants.PANELCOLOR);
		sp.setTextColor(Color.black);
		sp.setGradColor(Color.black);
		sp.setNumberOfSections(12);
		sp.setLabelPercent(170);
		sp.setLabelsString("+12, , , , , ,0dB, , , , , ,-12");
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
		Box box = new Box(panel, "Graphic Equalizer");
		panel.setLayout(new GridLayout(1, 9));
		
		// Do the 50Hz slider
		final BoostCutSlidePot sp50 = createSlider();
		
		// Add listener to this pot
		sp50.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sp50Changed(sp50);
			}
		});
		Panel p = createLabeledSlider(sp50, "50 Hz");
		panel.add(p);

		// Do the 100Hz slider
		final BoostCutSlidePot sp100 = createSlider();
		
		// Add listener to this pot
		sp100.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sp100Changed(sp100);
			}
		});
		p = createLabeledSlider(sp100, "100 Hz");
		panel.add(p);

		// Do the 200Hz slider
		final BoostCutSlidePot sp200 = createSlider();
		
		// Add listener to this pot
		sp200.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sp200Changed(sp200);
			}
		});
		p = createLabeledSlider(sp200, "200 Hz");
		panel.add(p);

		// Do the 400Hz slider
		final BoostCutSlidePot sp400 = createSlider();
		
		// Add listener to this pot
		sp400.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sp400Changed(sp400);
			}
		});
		p = createLabeledSlider(sp400, "400 Hz");
		panel.add(p);

		// Do the 800Hz slider
		final BoostCutSlidePot sp800 = createSlider();
		
		// Add listener to this pot
		sp800.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sp800Changed(sp800);
			}
		});
		p = createLabeledSlider(sp800, "800 Hz");
		panel.add(p);

		// Do the 1600Hz slider
		final BoostCutSlidePot sp1600 = createSlider();
		
		// Add listener to this pot
		sp1600.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sp1600Changed(sp1600);
			}
		});
		p = createLabeledSlider(sp1600, "1.6 KHz");
		panel.add(p);
		
		// Do the 3200Hz slider
		final BoostCutSlidePot sp3200 = createSlider();
		
		// Add listener to this pot
		sp3200.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sp3200Changed(sp3200);
			}
		});
		p = createLabeledSlider(sp3200, "3.2 KHz");
		panel.add(p);
		
		// Do the 6400Hz slider
		final BoostCutSlidePot sp6400 = createSlider();
		
		// Add listener to this pot
		sp6400.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sp6400Changed(sp6400);
			}
		});
		p = createLabeledSlider(sp6400, "6.4 KHz");
		panel.add(p);

		// Do the 12800Hz slider
		final BoostCutSlidePot sp12800 = createSlider();
		
		// Add listener to this pot
		sp12800.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				sp12800Changed(sp12800);
			}
		});
		p = createLabeledSlider(sp12800, "12.8 KHz");
		panel.add(p);
		
		return box;
	}
	
	// Called when the specific eq pot is manipulated.
	public void sp50Changed(BoostCutSlidePot p) {

		((GraphicEQWithUI)aa).f50HzGain(p.getGain());
	}

	// Called when the specific eq pot is manipulated.
	public void sp100Changed(BoostCutSlidePot p) {

		((GraphicEQWithUI)aa).f100HzGain(p.getGain());
	}

	// Called when the specific eq pot is manipulated.
	public void sp200Changed(BoostCutSlidePot p) {

		((GraphicEQWithUI)aa).f200HzGain(p.getGain());
	}

	// Called when the specific eq pot is manipulated.
	public void sp400Changed(BoostCutSlidePot p) {

		((GraphicEQWithUI)aa).f400HzGain(p.getGain());
	}

	// Called when the specific eq pot is manipulated.
	public void sp800Changed(BoostCutSlidePot p) {

		((GraphicEQWithUI)aa).f800HzGain(p.getGain());
	}

	// Called when the specific eq pot is manipulated.
	public void sp1600Changed(BoostCutSlidePot p) {

		((GraphicEQWithUI)aa).f1600HzGain(p.getGain());
	}

	// Called when the specific eq pot is manipulated.
	public void sp3200Changed(BoostCutSlidePot p) {

		((GraphicEQWithUI)aa).f3200HzGain(p.getGain());
	}

	// Called when the specific eq pot is manipulated.
	public void sp6400Changed(BoostCutSlidePot p) {

		((GraphicEQWithUI)aa).f6400HzGain(p.getGain());
	}

	// Called when the specific eq pot is manipulated.
	public void sp12800Changed(BoostCutSlidePot p) {

		((GraphicEQWithUI)aa).f12800HzGain(p.getGain());
	}

	public void bypassChanged(boolean state) {

		((GraphicEQWithUI)aa).setByPass(state);
	}

	public void windowClosing() {

		((GraphicEQWithUI)aa).stopUI();
	}

	public static void main(String [] args) {

		Blinker blink = new Blinker(250);

		GraphicEQUI gui = new GraphicEQUI(blink, null);
		gui.setVisible(true);
	}

	// Private class data
	private RoundLED bypassLED;
	private SquareButton bypassButton;
}
