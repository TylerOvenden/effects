// LabeledLED Class
// Written by: Craig A. Lindley
// Last Update: 04/01/99

package audiostuff.craigl.beans.leds;

import java.awt.*;
import audiostuff.craigl.beans.blinker.Blinker;

/**
 * A LabeledLED consists of a label and a RoundLED. This combination can
 * be arranged vertically or horizontally and the position of the label
 * in relation to the LED can be controlled.
 */
public class LabeledLED extends Panel {
		
	/**
	 * Class Constructor for a Labeled LED
	 *
	 * @param Blinker blinker is the data source for the LED that allows
     * it to change states
     * @param Color panelColor is the color of the panel surrounding
     * the round LED.
     * @param Color ledColor is color of the LED
	 * @param Color textColor is the color of the labeling text
	 * @param Font font is the font used for the labeling text
	 * @param int radius is the side of the LED to create
     * @param String label is the labeling string for the LED. This is a
     * static string that cannot be changed at runtime.
	 * @param boolean isVertical if true indicates the label and the LED
	 * will be aligned vertically. If false the label and the LED will
	 * be aligned horizontally.
	 * @param boolean topLeftMode if true indicates the LED will be the
	 * top or the left component of the pair. If false, the LED will be
	 * the bottom or the right component of the pair.
     */
    public LabeledLED(Blinker blinker,
					  Color panelColor, Color ledColor, Color textColor,
					  Font font, 
					  int radius , String label,
					  boolean isVertical, boolean topLeftMode) {

		if (isVertical) 
			setLayout(new GridLayout(2, 1));
		else
			setLayout(new GridLayout(1, 2));
		
		setBackground(panelColor);

		// Create the LED
		rl = new RoundLED();
		rl.setLEDColor(ledColor);
		rl.setRadius(radius);
		rl.setPanelColor(panelColor);
		rl.setLEDMode(LEDBase.MODESOLID);
		
		// Assign LED to a blinker
		blinker.addPropertyChangeListener(rl);

		// Create the label from string
		Label l = new Label(label, Label.CENTER);
		l.setForeground(textColor);
		l.setBackground(panelColor);
		l.setFont(font);
			
		if (topLeftMode) {
			add(rl);
			add(l);
		}	else	{
			add(l);
			add(rl);
		}
	}

	/**
	 * Controls the state of the labeled LED
	 *
	 * @param boolean state if true LED is on. If false it is off.
     */
    public void setLEDState(boolean state) {

        rl.setLEDState(state);
	}

	/**
	 * Set the color of the labeled LED
	 *
	 * @param Color color is the color to set the LED to
     */
    public void setLEDColor(Color color) {

        rl.setLEDColor(color);
	}

	// Private class data
	private RoundLED rl;
}
	
