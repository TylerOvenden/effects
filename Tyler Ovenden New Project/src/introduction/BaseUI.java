// Base UI Class
// Written by: Craig A. Lindley
// Last Update: 04/04/99

package craigl.uiutils;

import java.awt.*;
import craigl.utils.*;
import craigl.beans.pots.*;
import craigl.beans.leds.*;
import craigl.beans.buttons.*;

/**
 * This class provides some base functionality required by many of the
 * audio processor devices of section two. It provides storage for an
 * AbstractAudio device instance, extends CloseableFrame to give the UI
 * a closeable window in which run, provides various methods for creating simple
 * controls and indicators with a common look and a GridBagLayout layout
 * manager helper function to aid in the simulated front panel layout
 * process.
 */
public abstract class BaseUI extends CloseableFrame {
	
	/**
	 * BaseUI Class Constructor
	 *
	 * @param String title is the title to be placed in the window in which
	 * the UI runs.
	 * @param AbstractAudio aa is the instance of the AbstractAudio device
	 * associated with the UI.
	 */
	public BaseUI(String title, AbstractAudio aa) {
		
		super(title);

        // Save incoming
        this.aa = aa;

		// Set the panel color
		setBackground(AudioConstants.PANELCOLOR);
	}

	/**
	 * GridBagLayout Helper Function
	 * This method is called when adding a component to a UI using a
	 * GridBagLayout.
	 * @param Panel p is the panel onto which the component is added
	 * @param Component c is the component being added
	 * @param GridBagLayout gbl is the instance of the layout manager
	 * @param GridBagConstraints gbc is the constraint associated with
	 * adding this component
	 * @param int x is the x position within the panel to add the component
	 * @param int y is the y position within the panel to add the component
	 * @param int w is the width the added component should take up in the layout
	 * @param int h is the height the added component should take up in
	 * the layout.
	 */
	public static void addDefaultComponent(
					Panel p,
					Component c, 
					GridBagLayout gbl, GridBagConstraints gbc, 
					int x, int y, int w, int h) {

		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbl.setConstraints(c, gbc);
		p.add(c);
	}
	
	/**
	 * Create and configure a Pot for use in the UI
	 *
	 * @param int knobSize is the radius of the knob in pixels
	 * @param String label is the caption to label the pot with
	 * @param String labelsString is the String of comma delimited strings
	 * used to label the pot tic marks
	 *
	 * @return Pot configured as specified
	 */
	protected Pot createPot(int knobSize, String label, String labelsString) {

		Pot p = new Pot();
		configPot(p, knobSize, label, labelsString);
		return p;
	}

	/**
	 * Create and configure a RealValuedPot for use in the UI
	 *
	 * @param int knobSize is the radius of the knob in pixels
	 * @param String label is the caption to label the pot with
	 * @param String labelsString is the String of comma delimited strings
	 * used to label the pot tic marks
	 * @param double maxValue is the value the pot should return at
	 * the maximum position.
	 * @param double minValue is the value the pot should return at
	 * the minimum position.
	 *
	 * @return RealValuedPot configured as specified
	 */
	protected RealValuedPot createPot(int knobSize, String label, String labelsString,
							double maxValue, double minValue) {
		
		RealValuedPot p = new RealValuedPot(maxValue, minValue);
		configPot(p, knobSize, label, labelsString);
		return p;
	}
		
	/**
	 * Create and configure an IntValuedPot for use in the UI
	 *
	 * @param int knobSize is the radius of the knob in pixels
	 * @param String label is the caption to label the pot with
	 * @param String labelsString is the String of comma delimited strings
	 * used to label the pot tic marks
	 * @param int maxValue is the value the pot should return at
	 * the maximum position.
	 * @param int minValue is the value the pot should return at
	 * the minimum position.
	 *
	 * @return IntValuedPot configured as specified
	 */
	protected IntValuedPot createPot(int knobSize, String label, String labelsString,
							int maxValue, int minValue) {
		
		IntValuedPot p = new IntValuedPot(maxValue, minValue);
		configPot(p, knobSize, label, labelsString);
		return p;
	}

	/**
	 * Configure the Pot instance passed in to a come look
	 *
	 * @param Pot p is the Pot instance to configure
	 * @param int knobSize is the radius of the knob in pixels
	 * @param String label is the caption to label the pot with
	 * @param String labelsString is the String of comma delimited strings
	 * used to label the pot tic marks
	 */
	protected void configPot(Pot p, int knobSize, String label, String labelsString) {

		p.setPanelColor(AudioConstants.PANELCOLOR);
		p.setKnobColor(AudioConstants.KNOBCOLOR);
		p.setCaption(label);
		p.setKnobUseTics(true);
		p.setGradUseTics(true);
		p.setGradLengthPercent(40);
		p.setCaptionAtBottom(true);
		p.setNumberOfSections(10);
		p.setLabelPercent(200);
		p.setLabelsString(labelsString);
		p.setTicColor(Color.white);
		p.setGradColor(Color.white);
		p.setRadius(knobSize);
	}

	/**
	 * Create and configure a RoundLED for use in a UI
	 *
	 * @param Color color is the color the LED should be
	 * @param int mode is the mode to set the LED in. See LEDBase.java.
	 * @param boolean state is the on/off state of the LED. True turns
	 * the LED on, false turns it off.
	 */
	protected RoundLED createLED(Color color, int mode, boolean state) {
		
		RoundLED rl = new RoundLED();
		rl.setRadius(7);
		rl.setPanelColor(AudioConstants.PANELCOLOR);
		rl.setLEDMode(mode);
		rl.setLEDState(state);
		rl.setLEDColor(color);

		return rl;
	}

	// Class data
	protected AbstractAudio aa;
}


