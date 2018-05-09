// Slide Pot Class
// Written by: Craig A. Lindley
// Last Update: 11/07/99

package craigl.beans.pots;

import java.awt.*;
import java.awt.event.*;

public class SlidePot extends PotBase {

	// Constants for defaults
	public static final int DEFAULTWIDTH		= 14;
	public static final int DEFAULTLENGTH		= 100;
	public static final int KNOBWAISTWIDTHPERCENT = 70;
	
	public static final int DEFAULTKNOBWIDTH	= 130;
	public static final int DEFAULTKNOBHEIGHT	= 23;
	public static final int DEFAULTGRADWIDTH	= 150;
	public static final int DEFAULTLABELWIDTH	= 175;

	public static final String DEFAULTFONT		= "Monospaced";
	public static final int DEFAULTSECTIONS		= 1;
	public static final String SECTIONLABELS	= "100,0";

	// Constructors
	// Full strength constructor sets every property of pot
	public SlidePot(int length, int width,
			   int knobWidthPercent, int knobLengthPercent, int gradWidthPercent,
			   int labelPercent,
			   int fontStyle, int fontSize,
			   String caption, boolean hasLabels, String labelsString,
			   int value, boolean hasHighlight, int numberOfSections, 
			   Color panelColor, Color knobColor, Color textColor,
			   Color ticColor, Color gradColor) {
		
		// Pass most of the stuff to the base class
		super(DEFAULTFONT, fontStyle, fontSize, caption, 
			  hasLabels, labelsString, value, hasHighlight, numberOfSections,
			  panelColor, knobColor, textColor, ticColor, gradColor);

		// Process and save incoming
		setLength(length);
		setWidth(width);
		setKnobWidthPercent(knobWidthPercent);
		setKnobLengthPercent(knobLengthPercent);
		setGradWidthPercent(gradWidthPercent);
		setLabelPercent(labelPercent);

		// Calculate pots granularity
		potGranularity = (double) length / POTRANGE;
	}

	// Constructor with some reasonable defaults
	public SlidePot(int length, int width, String caption, int value) {
		this(length, width, DEFAULTKNOBWIDTH, DEFAULTKNOBHEIGHT,
			 DEFAULTGRADWIDTH, DEFAULTLABELWIDTH,
			 DEFAULTFONTSTYLE, DEFAULTFONTSIZE,
			 caption, true, SECTIONLABELS, value, true, 
			 DEFAULTSECTIONS,
			 PANELCOLOR, KNOBCOLOR, TEXTCOLOR, TICCOLOR, TICCOLOR);
	}

	// Zero argument constructor for testing
	public SlidePot() {
		this(DEFAULTLENGTH, DEFAULTWIDTH, "Volume", 50);
	}

	public Dimension getPreferredSize() {
		
		// Calculate the preferred size based on a monospaced font.
		// Get the font metrics
		FontMetrics fm = getFontMetrics(font);
		int charHeight = fm.getMaxAscent() + fm.getMaxDescent();
		int charWidth  = fm.charWidth('0');

		int minHeight = length + (2 * width);
		int captionHeight = caption.length() * charHeight;
		minHeight = Math.max(minHeight, captionHeight);
		int minWidth  = 2 * (labelWidth + charWidth);
		return new Dimension(minWidth, minHeight);
	}


	protected void drawKnob(Graphics g, int xOffset, int yOffset) {

		xOffset += width / 2;
		int yCurrent = yOffset + length - round(value * potGranularity);

		int hww = knobWaistWidth / 2;
		int hnw = knobWidth / 2;
		int hnl = knobLength / 2;

		int minX  = xOffset - hnw;
		int minWX = xOffset - hww;
		int maxX  = xOffset + hnw;
		int maxWX = xOffset + hww;

		xPoints[0] = minX;
		yPoints[0] = yCurrent - hnl;

		xPoints[1] = minWX;
		yPoints[1] = yCurrent;

		xPoints[2] = minX;
		yPoints[2] = yCurrent + hnl;
		
		xPoints[3] = maxX;
		yPoints[3] = yCurrent + hnl;

		xPoints[4] = maxWX;
		yPoints[4] = yCurrent;

		xPoints[5] = maxX;
		yPoints[5] = yCurrent - hnl;

		g.setColor(knobColor);
		g.fillPolygon(xPoints, yPoints, 6);

		// Draw the highlight
		if (hasHighlight) {
			// Draw side away from the light
			g.setColor(highlightDarkerColor);
			g.drawLine(xPoints[3], yPoints[3], xPoints[4], yPoints[4]);
			g.drawLine(xPoints[4], yPoints[4], xPoints[5], yPoints[5]);

			// Draw side towards the light
			g.setColor(highlightBrighterColor);
			g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);

			g.setColor(Color.white);
			g.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
		}
		
		// Now draw the tic across the knob
		g.setColor(ticColor);
		g.drawLine(xPoints[1], yPoints[1], xPoints[4], yPoints[4]);
	
		// Fill knobRect with coordinates of knob at current position.
		// Used by event routines to detect mouse hit
		knobRect.x = xPoints[0];
		knobRect.y = yPoints[0];
		knobRect.width = knobWidth;
		knobRect.height = knobLength;
	}	
	
	public void drawCaption(Graphics g, int x, int y, int cw, int ch, String text) {

		int chars = text.length();
		int totalHeight = chars * ch;
		double yOffset = (y - totalHeight) / 2.0;
		x -= cw;

		// Set the text color
		g.setColor(textColor);
		for (int index=0; index < chars; index++) {
			g.drawString(text.substring(index, index + 1), x, round(yOffset));
			yOffset += ch;
		}
	}
	
	// Paint the slide pot
	public void paint(Graphics g) {

		// Get the size of the canvas
		int cwidth  = getSize().width;
		int cheight = getSize().height;
		xCenter = cwidth  / 2;
		yCenter = cheight / 2;
		int xOffset = (cwidth  - width)  / 2;
		int yOffset = (cheight - length) / 2;

		// Is there an image of the slide pot to render?
		if (potImage == null) {
			
			// No, so create the image area for the knob and surroundings
			potImage = createImage(cwidth, cheight);

			// Get graphics context for the image
			Graphics gPot = potImage.getGraphics();

			// Paint the panel
			gPot.setColor(panelColor);
			gPot.fillRect(0, 0, cwidth, cheight);

			// Set font into the graphics context
			gPot.setFont(font);

			// Get char specs
			FontMetrics fm = gPot.getFontMetrics();
			int charHeight = fm.getHeight();
			int charAscent = fm.getAscent();
			int charWidth  = fm.charWidth('0');

			// Now draw the graduations and labels, if required
			if (numberOfSections != 0) {
				int x1 = xCenter - (gradWidth / 2);
				int x2 = xCenter + (gradWidth / 2);
				gPot.setColor(gradColor);
				for (int grad=0; grad < numberOfSections + 1; grad++) {
					double gradValue = ((double) POTRANGE * grad) / numberOfSections;
					gradValue *= potGranularity;
					gradValue += yOffset;
					gPot.drawLine(x1, round(gradValue), x2, round(gradValue)); 
				}
				// Now the labels
				if (hasLabels) {
					x1 = xCenter - (labelWidth + charWidth);
					gPot.setColor(textColor);
					for (int grad=0; grad < numberOfSections + 1; grad++) {
						double gradValue = ((double) POTRANGE * grad) / numberOfSections;
						gradValue *= potGranularity;
						gradValue += yOffset + charAscent / 2;
						// Get the label
						String label = (String) labels.elementAt(grad);
				
						// Draw the label
						gPot.drawString(label, x1, round(gradValue));
					}
				}
			}
			// Draw the slot
			int y1 = yOffset - width / 2;
			int y2 = yOffset + length + width / 2;

			// Draw the slot in 3D
			gPot.setColor(Color.gray);
			gPot.fill3DRect(xOffset, y1, width, length + width, false);
		
			// Draw the middle line
			gPot.setColor(Color.black);
			gPot.drawLine(xCenter, yOffset, xCenter, yOffset + length);

			// Draw the vertical caption
			drawCaption(gPot, xCenter + labelWidth, 
						cheight, charWidth, charHeight, caption);
		}
		// Render the knob and surroundings onto the device context
		g.drawImage(potImage, 0, 0, null);

		// Draw the knob in the correct position
		drawKnob(g, xOffset, yOffset);
	}

	protected void processMouseEvent(MouseEvent e) {

		// Track mouse presses/releases
		switch(e.getID()) {
			case MouseEvent.MOUSE_PRESSED:
				requestFocus();
				mouseDown = true;
	
				// Figure out where mouse is
				downPt = e.getPoint();

				// Was mouse clicked within the knob ?
				if (knobRect.contains(downPt.x, downPt.y))
					mouseInKnob = true;
				else
					mouseInKnob = false;

				break;

			case MouseEvent.MOUSE_RELEASED:
				mouseDown = false;

				// If mouse was clicked outside of knob
				if (!mouseInKnob) {
					// If mouse clicked above center of slider
					if (downPt.y <= yCenter) 
						setValue(value + blockIncrement);
					else
						setValue(value - blockIncrement);

					fireAdjustmentEvent();
				}
				break;
		}
		// Let the superclass continue delivery
		super.processMouseEvent(e);
	}

	protected void processMouseMotionEvent(MouseEvent e) {

		// Track mouse drags
		if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
			// Only interested in movement when mouse is down
			if (mouseDown && mouseInKnob) {
				// Figure out where mouse is now
				Point pt = e.getPoint();

				// Find direction of movement
				int deltaY = pt.y - downPt.y;
				setValue(value - deltaY);

				fireAdjustmentEvent();

				// Update current position
				downPt.x = pt.x;
				downPt.y = pt.y;
			}
		}
		// Let the superclass continue delivery
		super.processMouseMotionEvent(e);
	}

	protected void processKeyEvent(KeyEvent e) {

		// Simulate a mouse click for certain keys
		int keyCode = e.getKeyCode();
		
		if (keyCode == KeyEvent.VK_UP) {
			setValue(value + unitIncrement);
			fireAdjustmentEvent();
		}	else if (keyCode == KeyEvent.VK_DOWN) {
			setValue(value - unitIncrement);
			fireAdjustmentEvent();
		}
		// Let the superclass continue delivery
		super.processKeyEvent(e);
	}

	protected int makeEven(int n) {

		if ((n % 2) == 0)
			return n;
		else
			return n + 1;
	}
	
	// Property methods

	public int getLength() {

		return length;
	}

	public void setLength(int length) {

		this.length = length;

		// Calculate pots granularity
		potGranularity = (double) length / POTRANGE;

		potImage = null;
		sizeToFit();
	}
	
	public int getWidth() {

		return width;
	}

	public void setWidth(int width) {

		this.width = makeEven(width);
		
		// Update values based on width
		setKnobWidthPercent(knobWidthPercent);
		setGradWidthPercent(gradWidthPercent);
		setLabelPercent(labelPercent);

		potImage = null;
		sizeToFit();
	}

	public int getKnobWidthPercent() {

		return knobWidthPercent;
	}

	public void setKnobWidthPercent(int percent) {

		if (percent < 100)
			return;

		knobWidthPercent = percent;

		// Calculate actual width
		knobWidth = makeEven((percent * width) / 100);
		knobWaistWidth = (knobWidth * KNOBWAISTWIDTHPERCENT) / 100;

		potImage = null;
		repaint();
	}

	public int getKnobLengthPercent() {

		return knobLengthPercent;
	}

	public void setKnobLengthPercent(int percent) {

		if (percent > 50)
			return;

		knobLengthPercent = percent;

		// Calculate actual length
		knobLength = (percent * length) / 100;
		potImage = null;
		repaint();
	}

	public int getGradWidthPercent() {

		return gradWidthPercent;
	}

	public void setGradWidthPercent(int percent) {

		if (percent < 100)
			return;

		gradWidthPercent = percent;
		
		// Calculate actual length
		gradWidth = makeEven((percent * width) / 100);
		potImage = null;
		repaint();
	}

	public int getLabelPercent() {

		return labelPercent;
	}

	public void setLabelPercent(int percent) {

		if (percent < 100)
			return;

		labelPercent = percent;

		// Calculate actual length
		labelWidth = makeEven((percent * width) / 100);
		potImage = null;
		sizeToFit();
	}

	// Private class data
	private double potGranularity;	// Rotational pixel per unit
	private int width;				// Width of slider
	private int length;				// Length of slider
	private Rectangle knobRect = new Rectangle(); // Rect enclosing the knob
	
	private int knobWidthPercent;
	private int knobWidth;
	private int knobWaistWidth;
	private int knobLengthPercent;
	private int knobLength;
	private int gradWidthPercent;
	private int gradWidth;
	private int labelPercent;
	private int labelWidth;
	private int [] xPoints = new int[6];
	private int [] yPoints = new int[6];
}
