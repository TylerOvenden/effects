// PannerUI Class
// This implements the simulated front panel for the panning processor
// Written by: Craig A. Lindley
// Last Update: 01/24/99

package craigl.processors;

import java.awt.*;
import java.awt.event.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.uiutils.*;
import audiostuff.craigl.beans.blinker.*;
import audiostuff.craigl.beans.displays.*;
import audiostuff.craigl.beans.pots.*;
import audiostuff.craigl.beans.leds.*;
import audiostuff.craigl.beans.buttons.*;

public class PannerUI extends BaseUI 
	implements CloseableFrameIF {

	public PannerUI(Blinker blink, AbstractAudio aa) {
		
		super("Panner Processor", aa);

		// Register this UI as being interested in window close events
		registerCloseListener(this);

		// Assume a mono source
		monoSource = true;

		// Create the UI
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mp.setLayout(gbl);

		// Source label
		Label l = new Label("Source", Label.CENTER);
		addDefaultComponent(mp, l, gbl, gbc, 0, 0, 5, 1);
		
		// Create the mono source LED in yellow
		monoSourceLED = createLED(Color.yellow, RoundLED.MODESOLID, false);
		addDefaultComponent(mp, monoSourceLED, gbl, gbc, 1, 1, 1, 1);
		blink.addPropertyChangeListener(monoSourceLED);
		
		// Create the stereo source LED in green
		stereoSourceLED = createLED(Color.green, RoundLED.MODESOLID, false);
		addDefaultComponent(mp, stereoSourceLED, gbl, gbc, 3, 1, 1, 1);
		blink.addPropertyChangeListener(stereoSourceLED);
		
		// Mono label
		l = new Label("mono", Label.CENTER);
		addDefaultComponent(mp, l, gbl, gbc, 0, 2, 2, 1);
		
		// Stereo label
		l = new Label("stereo", Label.CENTER);
		addDefaultComponent(mp, l, gbl, gbc, 3, 2, 2, 1);

		// Create the pan pot
		panPot = createPot(AudioConstants.KNOBSIZE, "Pan", "L, , , , ,C, , , , ,R");
		
		// Set the default value to center
		panPot.setValue(50);
		addDefaultComponent(mp, panPot, gbl, gbc, 1, 3, 3, 3);
		
		// Add listener to this pot
		panPot.addAdjustmentListener(new AdjustmentListener () {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				panChanged(e.getValue());
			}
		});

		// Create the stereo pre mix button
		SquareButton mixButton = new SquareButton();
		mixButton.setPanelColor(AudioConstants.PANELCOLOR);
		mixButton.setCaption("Stereo Pre-Mix");
		addDefaultComponent(mp, mixButton, gbl, gbc, 1, 6, 3, 1);
		mixButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				boolean state = e.getActionCommand().equals("ON");
				mixModeChanged(state);
			}
		});

		// Create the bypass LED
		bypassLED = createLED(Color.green, RoundLED.MODEBLINK, false);
		addDefaultComponent(mp, bypassLED, gbl, gbc, 2, 7, 1, 1);
		blink.addPropertyChangeListener(bypassLED);

		// Create the bypass switch
		bypassButton = new SquareButton();
		bypassButton.setPanelColor(AudioConstants.PANELCOLOR);
		bypassButton.setCaption("Bypass");
		addDefaultComponent(mp, bypassButton, gbl, gbc, 1, 8, 3, 1);
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

	// Called from device when the number of channels is known. Sets
	// the front panel LEDs appropriately.
	public void setSourceChannels(int channels) {

		monoSource = (channels == 1);
		
		if (monoSource) {
			monoSourceLED.setLEDState(true);
			stereoSourceLED.setLEDState(false);
		}	else	{
			monoSourceLED.setLEDState(false);
			stereoSourceLED.setLEDState(true);
		}
	}

	// Called when the pan pot is manipulated.
	public void panChanged(int newPotValue) {

		((PannerWithUI)aa).setPanValue(newPotValue);
	}

	// Called when the mix mode switch is toggled
	public void mixModeChanged(boolean state) {

		((PannerWithUI)aa).setMixMode(state);
	}
	
	// Called when the bypass switch is toggled
	public void bypassChanged(boolean state) {

		((PannerWithUI)aa).setByPass(state);
	}

	// Called when the front panel window is closing down
	public void windowClosing() {

		((PannerWithUI)aa).stopUI();
	}

	// Private class data
	private Pot panPot;
	private RoundLED monoSourceLED;
	private RoundLED stereoSourceLED;
	private RoundLED bypassLED;
	private SquareButton bypassButton;
	private boolean monoSource;
}
