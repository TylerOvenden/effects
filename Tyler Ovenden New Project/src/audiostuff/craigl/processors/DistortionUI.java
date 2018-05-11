// Distortion UI Class
// Written by: Craig A. Lindley
// Last Update: 06/27/99

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

public class DistortionUI extends BaseUI 
		implements CloseableFrameIF {

	public DistortionUI(Blinker blink, AbstractAudio aa) {
		
		super("Distortion Processor", aa);

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Create the UI
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		// Create the round distortion pot
		distortionPot = new IntValuedPot(32767, 3276);
		distortionPot.setValue(100);
		distortionPot.setCaption("Distortion");
        distortionPot.setKnobColor(AudioConstants.KNOBCOLOR);
        distortionPot.setPanelColor(AudioConstants.PANELCOLOR);
		distortionPot.setTextColor(Color.black);
		distortionPot.setGradColor(Color.black);
		distortionPot.setNumberOfSections(10);
		distortionPot.setLabelPercent(200);
		distortionPot.setLabelsString("more, , , , , , , , , ,less");
		addDefaultComponent(mp, distortionPot, gbl, gbc, 0, 0, 5, 5);
		distortionChanged(distortionPot);

		// Add listener to this pot
		distortionPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				distortionChanged(distortionPot);
			}
		});

		// Create the gain slide pot
		gainPot = new BoostCutSlidePot(100, 16, "Gain", -12, +12);
        gainPot.setKnobColor(AudioConstants.KNOBCOLOR);
        gainPot.setPanelColor(AudioConstants.PANELCOLOR);
		gainPot.setTextColor(Color.black);
		gainPot.setGradColor(Color.black);
		gainPot.setNumberOfSections(10);
		gainPot.setLabelPercent(200);
		gainPot.setLabelsString("+12db, , , , ,0, , , , ,-12db");
		addDefaultComponent(mp, gainPot, gbl, gbc, 5, 0, 3, 5);

		// Add listener to this pot
		gainPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				gainChanged(gainPot);
			}
		});

		bypassLED = createLED(Color.green, RoundLED.MODEBLINK, false);
		addDefaultComponent(mp, bypassLED, gbl, gbc, 3, 5, 1, 1);
		blink.addPropertyChangeListener(bypassLED);

		bypassButton = new SquareButton();
		bypassButton.setPanelColor(AudioConstants.PANELCOLOR);
		bypassButton.setTextColor(Color.black);
		bypassButton.setCaption("Bypass");
		addDefaultComponent(mp, bypassButton, gbl, gbc, 7, 5, 3, 1);
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

	// Called when the distortion pot is manipulated.
	public void distortionChanged(IntValuedPot p) {

		((DistortionWithUI)aa).setThreshold(p.getIntValue());
	}

	// Called when the gain pot is manipulated. Note: gain
	// pot is modelled with a pseudo audio taper.
	public void gainChanged(BoostCutSlidePot p) {

		((DistortionWithUI)aa).setGain(p.getGain());
	}

	public void bypassChanged(boolean state) {

		((DistortionWithUI)aa).setByPass(state);
	}

	public void windowClosing() {

		((DistortionWithUI)aa).stopUI();
	}

	// Private class data
	private IntValuedPot distortionPot;
	private BoostCutSlidePot gainPot;
	private RoundLED bypassLED;
	private SquareButton bypassButton;
}
