// SquareLED Class
// Written by: Craig A. Lindley
// Last Update: 07/12/98

package audiostuff.craigl.beans.leds;

import java.awt.*;
import java.beans.*;

public class SquareLED extends LEDBase {

	private static final int BORDERPAD = 5;
	
	public SquareLED(int width, int height, boolean raised,
					 Color ledColor, Color panelColor, 
					 int mode, boolean rate, boolean state) {
		// Allow the superclass constructor to do its thing
		super(ledColor, panelColor, mode, rate, state);

		// Save incoming
		setRaised(raised);
		setWidth(width);
		setHeight(height);
	}

	// Zero agrument constructor
	public SquareLED() {
		
		this(18, 12, true, Color.red, Color.lightGray, MODESOLID, false, false);
	}

	// Accessor methods
	public boolean getRaised() {
		
		return raised;
	}

	public void setRaised(boolean raised) {

		this.raised = raised;
		repaint();
	}
	
	public int getWidth() {
		
		return width;
	}

	public void setWidth(int width) {

		this.width = width;
		repaint();
	}
	
	public int getHeight() {
		
		return height;
	}

	public void setHeight(int height) {

		this.height = height;
		repaint();
	}
	
	// Other public methods
	public void paint(Graphics g) {
		
		int cwidth  = getSize().width;
		int cheight = getSize().height;
		int xOrg = (cwidth - width)  / 2;
		int yOrg = (cheight- height) / 2;

		// Paint the background then the LED
		g.setColor(panelColor);
		g.fillRect(0, 0, cwidth, cheight);
		
		// Paint the ring around the LED
		g.setColor(Color.black);
		g.fill3DRect(xOrg, yOrg, width, height, raised);
		
		// Determine which color to paint the LED with
		Color newColor = ledState ? ledOnColor : ledOffColor;
		g.setColor(newColor);
		g.fill3DRect(xOrg, yOrg, width, height, raised);
	}

	public Dimension getPreferredSize() {
		
		// Calculate the preferred size
		return new Dimension(width + BORDERPAD, height + BORDERPAD);
	}


	// Private class data
	private boolean raised;
	private int width;
	private int height;
}
