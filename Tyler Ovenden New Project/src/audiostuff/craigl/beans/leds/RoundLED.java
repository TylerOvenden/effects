// RoundLED Class
// Written by: Craig A. Lindley
// Last Update: 03/16/99

package audiostuff.craigl.beans.leds;

import java.awt.*;
import java.beans.*;

public class RoundLED extends LEDBase {

	private static final int BORDERPAD = 5;
	
	public RoundLED(int radius, Color ledColor, Color panelColor, 
					int mode, boolean rate, boolean state) {
		// Allow the superclass constructor to do its thing
		super(ledColor, panelColor, mode, rate, state);

		// Save incoming
		setRadius(radius);
	}

	// Zero agrument constructor
	public RoundLED() {
		
		this(3, Color.red, Color.lightGray, MODESOLID, false, false);
	}

	// Accessor methods
	public int getRadius() {
		
		return radius;
	}

	public void setRadius(int radius) {

		// Calculate values
		this.radius		= radius;
		this.sRadius1	= radius - 1;
		this.sRadius2	= radius - 2;

		this.ledWidth	= radius   * 2;
		
		ledOnImage = null;

		repaint();
	}
	
	public void setLEDColor(Color ledOnColor) {

		ledOnImage = null;
		super.setLEDColor(ledOnColor);
	}
	
	public void setPanelColor(Color panelColor) {

		super.setPanelColor(panelColor);
		this.brighterPanelColor = panelColor.brighter();
	}

	// Other public methods
	public void paint(Graphics g) {
		
		// See if we have led bitmap images for displaying
		if (ledOnImage == null) {
			int cwidth  = getSize().width;
			int cheight = getSize().height;
			int xCenter = cwidth  / 2;
			int yCenter = cheight / 2;
			
			// Calc position of led in graphics context
			ledOrgX = xCenter - radius;
			ledOrgY = yCenter - radius;

			// Create the image for the on/off led
			ledOnImage  = createImage(ledWidth + 1, ledWidth + 1);
			ledOffImage = createImage(ledWidth + 1, ledWidth + 1);

			// Get the graphics contexts
			Graphics gOnImage  = ledOnImage.getGraphics();
			Graphics gOffImage = ledOffImage.getGraphics();

			// Do background fills
			gOnImage.setColor(panelColor);
			gOnImage.fillRect(0, 0, cwidth, cheight);

			gOffImage.setColor(panelColor);
			gOffImage.fillRect(0, 0, cwidth, cheight);
			
			// Draw panel ring
			gOnImage.setColor(brighterPanelColor);
			mFillCircle(gOnImage, radius, radius, radius);

			gOffImage.setColor(brighterPanelColor);
			mFillCircle(gOffImage, radius, radius, radius);
			
			// Do the on led
			gOnImage.setColor(Color.black);
			mFillCircle(gOnImage, radius, radius, sRadius1);

			gOnImage.setColor(Color.white);
			mFillArc(gOnImage, radius, radius, sRadius1, 255, 120);
			
			gOnImage.setColor(ledOnColor);
			mFillCircle(gOnImage, radius, radius , sRadius2);

			// Now the off led
			gOffImage.setColor(Color.white);
			mFillCircle(gOffImage, radius, radius, sRadius1);

			gOffImage.setColor(Color.black);
			mFillArc(gOffImage, radius, radius, sRadius1, 45, 180);
		
			gOffImage.setColor(ledOffColor);
			mFillCircle(gOffImage, radius, radius, sRadius2);

			// Do the highlight
			gOffImage.setColor(Color.white);
			mDrawArc(gOffImage, radius/2, radius/2, radius/2, 248, 135);
 		}
		// Render the led into the device context
		if (ledState)
			g.drawImage(ledOnImage,  ledOrgX, ledOrgY, null);
		else
			g.drawImage(ledOffImage, ledOrgX, ledOrgY, null);
	}

	public void mDrawCircle(Graphics g, int xCenter, int yCenter, int radius) {

		int diameter = radius * 2;
		g.drawOval(xCenter - radius, yCenter - radius, diameter, diameter);
	}
	
	public void mFillCircle(Graphics g, int xCenter, int yCenter, int radius) {

		int diameter = radius * 2;
		g.fillOval(xCenter - radius, yCenter - radius, diameter, diameter);
	}
	
	public void mDrawArc(Graphics g, int xCenter, int yCenter, int radius,
		int startAngle, int angle) {

		int diameter = radius * 2;
		g.drawArc(xCenter - radius, yCenter - radius, diameter, diameter,
				  startAngle, angle);
	}

	public void mFillArc(Graphics g, int xCenter, int yCenter, int radius,
		int startAngle, int angle) {

		int diameter = radius * 2;
		g.fillArc(xCenter - radius, yCenter - radius, diameter, diameter,
				  startAngle, angle);
	}

	public Dimension getPreferredSize() {
		// Calculate the preferred size
		int width = (radius + BORDERPAD) * 2;
		return new Dimension(width, width);
	}


	// Private class data
	private int radius;
	private int sRadius1;
	private int sRadius2;
	private int ledWidth;
	private int ledOrgX;
	private int ledOrgY;

	private Color brighterPanelColor;
	private Image ledOnImage = null;
	private Image ledOffImage = null;
}
