// AmplitudeAdjustUI Class
// Written by: Craig A. Lindley
// Last Update: 08/23/98

package audiostuff.craigl.processors;

import java.awt.*;
import java.awt.event.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.uiutils.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.beans.pots.*;
import audiostuff.craigl.beans.leds.*;
import audiostuff.craigl.beans.buttons.*;

public class AmplitudeAdjustUI extends BaseUI 
		implements CloseableFrameIF {

	public AmplitudeAdjustUI(Blinker blink, AbstractAudio aa) {
		
		super("Amplitude Adjust Processor", aa);

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Create the UI
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		// Create the amplitude slide pot
		amplitudePot = new SlidePot(100, 16, "Amplitude", 100);
        amplitudePot.setKnobColor(AudioConstants.KNOBCOLOR);
        amplitudePot.setPanelColor(AudioConstants.PANELCOLOR);
		amplitudePot.setTextColor(Color.black);
		amplitudePot.setGradColor(Color.black);
		amplitudePot.setNumberOfSections(10);
		amplitudePot.setLabelPercent(200);
		amplitudePot.setLabelsString("0db, , , , ,-20, , , , ,Inf");
		addDefaultComponent(mp, amplitudePot, gbl, gbc, 0, 0, 3, 5);

		// Add listener to this pot
		amplitudePot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				amplitudeChanged(amplitudePot);
			}
		});

		bypassLED = createLED(Color.green, RoundLED.MODEBLINK, false);
		addDefaultComponent(mp, bypassLED, gbl, gbc, 1, 5, 1, 1);
		blink.addPropertyChangeListener(bypassLED);

		bypassButton = new SquareButton();
		bypassButton.setPanelColor(AudioConstants.PANELCOLOR);
		bypassButton.setCaption("Bypass");
		addDefaultComponent(mp, bypassButton, gbl, gbc, 0, 6, 3, 1);
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

	// Called when the amplitude pot is manipulated. Note: amplitude
	// pot is modelled with a pseudo audio taper.
	public void amplitudeChanged(PotBase p) {

		((AmplitudeAdjustWithUI)aa).setAmplitudeAdj(p.getAttenuation());
	}

	public void bypassChanged(boolean state) {

		((AmplitudeAdjustWithUI)aa).setByPass(state);
	}

	public void windowClosing() {

		((AmplitudeAdjustWithUI)aa).stopUI();
	}

	// Private class data
	private SlidePot amplitudePot;
	private RoundLED bypassLED;
	private SquareButton bypassButton;
}
