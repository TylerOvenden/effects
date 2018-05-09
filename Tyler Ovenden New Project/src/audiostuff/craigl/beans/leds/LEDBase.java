// LEDBase Abstract Class
// Written by: Craig A. Lindley
// Last Update: 03/16/99

package audiostuff.craigl.beans.leds;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public abstract class LEDBase extends Canvas 
	implements PropertyChangeListener, ActionListener {

	// Modes for the LEDs
	public static final int MODESOLID = 0;
	public static final int MODEBLINK = 1;
	public static final int MODEPULSE = 2;
	
	public LEDBase(Color ledColor, Color panelColor, 
				   int mode, boolean rate, boolean state) {
		// Allow the superclass constructor to do its thing
		super();

		// Save incoming
		setLEDColor(ledColor);
		setPanelColor(panelColor);
		setLEDMode(mode);
		setLEDBlinkRate(rate);
		setLEDState(state);
	}

	public Color getLEDColor() {
		return ledOnColor;
	}

	public void setLEDColor(Color ledOnColor) {
		
		// Save the on color
		this.ledOnColor  = ledOnColor;
		this.ledOffColor = ledOnColor.darker().darker().darker();
		repaint();
	}

	public Color getPanelColor() {
		return panelColor;
	}

	public void setPanelColor(Color panelColor) {
		
		// Save the panel color
		this.panelColor = panelColor;
		repaint();
	}

	public int getLEDMode() {
		return mode;
	}

	public void setLEDMode(int mode) {
		
		this.mode = mode;
	}
	
	/**
	 * True means the LED will blink at a fast rate. False
	 * means the LED will blink at 1/2 the fast rate. LED will
	 * only blink, however, if mode is MODEBLINK and state is true.
	 */
	
	public boolean getLEDBlinkRate() {
		return rate;
	}

	public void setLEDBlinkRate(boolean rate) {

		this.rate = rate;
	}

	/**
	 * True means LED is not off. That is, it is either
	 * on or blinking. False means the LED is off
	 */
	public boolean getLEDState() {
		return state;
	}

	public void setLEDState(boolean state) {

		this.state = state;
	}
	
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("ON"))
			state = true;
		else
			state = false;

		repaint();
	}

	
	/**
	 * A small state machine to control the LED. It is meant to
	 * limit the number of repaints getting generated so that the
	 * LED does not studder as much as it would otherwise.
	 */
	
	public void propertyChange(PropertyChangeEvent evt) {

		// Make sure this is a blink property change.
		// Ignore all other types.
		if (!evt.getPropertyName().equals("blink"))
			return;

		// Pulse is set by blinker property value
		boolean pulse = ((Boolean) evt.getNewValue()).booleanValue();
		
		// halfPulse toggles at 1/2 the blinker rate
		if (pulse)
			halfPulse = !halfPulse;

		if (state) {
			// LED needs to be on
			offOnce = false;
			
			if (mode == MODESOLID) {
				// LED on solid
				if (!onOnce) {
					ledState = true;
					repaint();
					onOnce = true;
				}
			}	else if (mode == MODEBLINK)	{
				// LED is blinking
				onOnce = false;
				if (rate) {
					// Rate is fast
					ledState = pulse;
				}	else	{
					// Rate is slow
					ledState = halfPulse;
				}
				repaint();
			}	else	{
				// LED needs to pulse
				ledState = true;
				repaint();
				state = false;
			}
		}	else	{
			// LED needs to be off
			onOnce = false;
			if (!offOnce) {
				ledState = false;
				offOnce = true;
				repaint();
			}
		}
	}

	// Derived classes must implement these methods
	public abstract void paint(Graphics g);
	public abstract Dimension getPreferredSize();
	
	// Private support methods
	private void sizeToFit() {
		// Resize to the preferred size
		Dimension d = getPreferredSize();
		setSize(d);
		Component p = getParent();
		if (p != null) {
			p.invalidate();
			p.validate();
		}
	}

	// Private class data
	protected Color panelColor;
	protected Color ledOnColor;
	protected Color ledOffColor;

	private int mode;		// solid, blinking or pulse
	private boolean rate;	// true = fast:false = slow
	private boolean state;	// true = on:false = off

	private boolean halfPulse = false;
	private boolean offOnce = false;
	private boolean onOnce = false;
	protected boolean ledState = false;
}
