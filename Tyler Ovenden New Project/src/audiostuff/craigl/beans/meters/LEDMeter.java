// LED Meter Class
// Written by: Craig A. Lindley
// Last Update: 03/28/99

package audiostuff.craigl.beans.meters;

import java.util.*;
import java.awt.*;

public class LEDMeter extends Meter {

	// Various constants used to control the meter's appearance
	private static final int DEFAULTLABELPERCENT = 175;
	private static final int DEFAULTNUMBEROFSECTIONS = 15;
	private static final int YPAD = 5;
	private static final int BORDERWIDTH = 2;
	
	/**
	 * LED Meter Class Constructor with all agruments
	 *
	 * @param int width is the width in pixels of the meter
	 * @param int height is the height in pixels of the meter
	 * @param int meterMode is not currently used
	 * @param String fontName is the name of the font for labelling
	 * @param String fontStyle is the name of the font style for labelling
	 * @param int fontSize is the size of the font for labelling
	 * @param String caption is the caption to label the meter with
	 * @param boolean hasLabels is true if the meter has labels and it
	 * is desired they are displayed.
	 * @param String labelsString is the string of comma separated label
	 * strings used to label the meter. There can be any number specified
	 * and the analog meter will spread them evenly across the scale.
	 * @param int labelPercent is the percentage relative to the meter's
	 * width where the labels will be drawn.
	 * @param int value is the value the meter should initially display
	 * @param boolean hasHighlight is true if highlighting should be
	 * used for the meter's display.
	 * @param int numberOfSections is the number of sections the meter should
	 * be divided into.
	 * @param Color panelColor is the color of the panel surrounding the
	 * meter.
	 * @param Color textColor is the color used for the labelling text
	 */
	public LEDMeter(int width, int height, int meterMode,
					String fontName, int fontStyle, int fontSize,
					String caption, boolean hasLabels, String labelsString,
					int labelPercent,
					int value, boolean hasHighlight, int numberOfSections, 
					Color panelColor, Color textColor) {

		super(width, height, meterMode, fontName, fontStyle, fontSize,
			  caption, hasLabels, labelsString, value, hasHighlight,
			  numberOfSections, panelColor, Color.black, textColor);

		setLabelPercent(labelPercent);
		setNumberOfSections(numberOfSections);
	
	}

	/**
	 * LED Meter Class Constructor with reasonable defaults
	 */
	public LEDMeter(int width, int height, String caption, int value) {

		this(width, height, MODEPEAK, 
			 DEFAULTFONTNAME, DEFAULTFONTSTYLE, DEFAULTFONTSIZE,
			 caption, false, "", DEFAULTLABELPERCENT, value,
			 true, DEFAULTNUMBEROFSECTIONS,
			 PANELCOLOR, TEXTCOLOR);
	}

	/**
	 * LED Meter Class Constructor with zero arguments. Needed for
	 * use as a bean.
	 */
	public LEDMeter() {
		
		this(13, 100, "LED Meter", 50);
	}


	/**
	 * Paint the meter into the graphics context
	 *
	 * @param Graphics g is the graphics context on which to draw the meter
	 */
	public void paint(Graphics g) {

		// Get the size of the container
		int cwidth  = getSize().width;
		int cheight = getSize().height;
		int xCenter = cwidth / 2;
		
		int basinWidth = width  + (2 * BORDERWIDTH);
		int basinHeight= height + (2 * BORDERWIDTH);

		int xOffset = (cwidth - basinWidth) / 2;
		int yOffset = YPAD;

		// Paint the panel
	    g.setColor(panelColor);
		g.fillRect(0, 0, cwidth, cheight);

		// Paint the caption
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		int charHeight = fm.getAscent();
		int xText = xCenter - labelDist;
		int chars = caption.length();
		int textTotal = chars * charHeight;
		int yText = (cheight - textTotal) / 2;

		g.setColor(textColor);
		for (int index=0; index < chars; index++) {
			g.drawString(caption.substring(index, index+1), xText, yText);
			yText += charHeight;
		}

		// Paint the labels, if any
		int yOrg  = YPAD + yBarOffset + (fm.getAscent() / 2);
		if (hasLabels) {
			xText = xCenter + labelDist;
			for (int index=0; index < numberOfSections; index++) {
				String s = (String) labels.elementAt(index);
				int textXOffset = fm.stringWidth(s) / 2;
				Rectangle r = bars[index];
				int barCenter = r.y + r.height / 2;
				int textYOffset = yOrg + barCenter;
				g.drawString(s, xText - textXOffset, textYOffset);
			}
		}
		
		// Paint the basin
		g.setColor(Color.black);
		g.fillRect(xOffset, yOffset, basinWidth, basinHeight);

		// Paint the highlights within the basin
		if (hasHighlight) {
			g.setColor(highlightDarkerColor);
			g.drawLine(xOffset,   yOffset,   xOffset + basinWidth,   yOffset);
			g.drawLine(xOffset+1, yOffset+1, xOffset + basinWidth-1, yOffset+1);
			g.drawLine(xOffset,   yOffset,   xOffset,   yOffset+basinHeight);
			g.drawLine(xOffset+1, yOffset+1, xOffset+1, yOffset+basinHeight-1);

			g.setColor(Color.white);
			g.drawLine(xOffset+basinWidth-1, yOffset+1, xOffset+basinWidth-1, yOffset+basinHeight-1);
			g.drawLine(xOffset+basinWidth,   yOffset,   xOffset+basinWidth,   yOffset+basinHeight);
			g.drawLine(xOffset+1, yOffset+basinHeight-1,xOffset+basinWidth-1, yOffset+basinHeight-1);
			g.drawLine(xOffset,   yOffset+basinHeight,  xOffset+basinWidth,   yOffset+basinHeight);
		}
		// Draw the white separators
		int x0 = xOffset + BORDERWIDTH;
		int x1 = x0 + width;
		yOrg = YPAD + yBarOffset;

		g.setColor(Color.white);
		for (int line=0; line < numberOfSections + 1; line++) {
			int y = lines[line] + yOrg;
			g.drawLine(x0, y , x1 , y);
		}

		// Paint the level
		if (value != 0) {
			// Calculate bars to light
			int barIndex = round(meterGranularity * value);

			for (int bar=0; bar < barIndex; bar++) {
				int barOffset = (numberOfSections - 1) - bar;
				Rectangle r = bars[barOffset];
				Color c = barColors[barOffset];
				g.setColor(c);
				g.fillRect(r.x + x0, r.y + yOrg + 1, r.width, r.height);
			}
		}
	}

	/**
	 * Return the preferred size of this LED meter
	 *
	 * @return Dimension object containing the preferred size of the meter
	 */
	public Dimension getPreferredSize() {
		
		// Calculate the preferred size based on a monospaced font.
		// Get the font metrics
		FontMetrics fm = getFontMetrics(font);
		int charHeight = fm.getMaxAscent() + fm.getMaxDescent();
		int charWidth  = fm.charWidth('0');

		int minHeight = YPAD + (2 * BORDERWIDTH) + height + charHeight;
		int minWidth =  2 * ((2 * charWidth) + labelDist);

		return new Dimension(minWidth, minHeight);
	}

	/**
	 * Set a color for a range of values on the meter's LEDs.
	 *
	 * @param Color color is the color for the specified range of LEDs
	 * @param int minPercentValue is the percentage of full scale value
	 * where this color LED should begin
	 * @param int maxPercentValue is the percentage of full scale value
	 * where this color LED should end
	 */
	public void setColorRange(Color color, 
							  int minPercentValue, int maxPercentValue) {

		int minBarIndex = round(meterGranularity * minPercentValue);
		int maxBarIndex = round(meterGranularity * maxPercentValue);

		for (int index=Math.max(minBarIndex - 1, 0); index < maxBarIndex; index++) {
			int barOffset = (numberOfSections - 1) - index;
			barColors[barOffset] = color;
		}
	}
	
	/**
	 * Overloaded base class method for setting height of LED meter
	 * Forces recalculation of section sizes
	 *
	 * @param int height is the new height for the LED meter
	 */
	public void setHeight(int height) {

		super.setHeight(height);

		setNumberOfSections(numberOfSections);
	}


	/**
	 * Overloaded base class method for setting the number of
	 * meter sections. Recalculates the section sizes
	 *
	 * @param int height is the new height for the LED meter
	 */
	public void setNumberOfSections(int numberOfSections) {

		super.setNumberOfSections(numberOfSections);

		// Calculate height of bars 
		double barHeightD = (((double) height - 1) / numberOfSections) - 1;

		// Truncate to integer
		barHeight = (int) barHeightD;

		// Now adjust padding for proper spacing
		int totalHeight = 
			(numberOfSections * barHeight) + (numberOfSections + 1);
		
		yBarOffset = (height - totalHeight) / 2;

		// Adjust for separator bar
		int yOffset = yBarOffset;

		// Allocate array of rects for the bars and for the bar colors
		bars		= new Rectangle[numberOfSections];
		barColors	= new Color[numberOfSections];
		lines		= new int[numberOfSections + 1];

		for (int bar=0; bar < numberOfSections; bar++) {

			lines[bar] = yOffset;

			bars[bar] = new Rectangle(0, yOffset, width + 1, barHeight);
			yOffset += barHeight + 1;

			// Set all bar colors to green
			barColors[bar] = Color.green;
		}
		lines[numberOfSections] = yOffset;

		repaint();
	}
	
	/**
	 * Overloaded base class method for setting panel color. Overloaded
	 * to produce other colors needed for shading.
	 *
	 * @param Color panelColor the color to set the panel surrounding the
	 * meter to.
	 */
	public void setPanelColor(Color panelColor) {

		highlightBrighterColor = panelColor.brighter();
		highlightDarkerColor = panelColor.darker();
		
		super.setPanelColor(panelColor);
	}
	
	// Code for testing the LEDMeter class
	public static void main(String [] args) {
		
		Frame f = new Frame("LEDMeter Test");
		LEDMeter lm = new LEDMeter();
		f.add(lm);

		Dimension d = lm.getPreferredSize();
		d.height *= 1.2;

		f.setSize(d);

		lm.setNumberOfSections(15);
		lm.setHasLabels(true);
		lm.setLabelsString("15, , , , , , , , , , , , , ,1");
		f.setVisible(true);
		
		lm.setColorRange(Color.green, 0, 60);
		lm.setColorRange(Color.yellow, 61, 80);
		lm.setColorRange(Color.red, 81, 100);
		
		for (int index=0; index < 1000; index++) {

			int value = (int)(Math.random() * 100);
			lm.setValue(value);
			try {
				Thread.sleep(250);
			}
			catch(Exception ignor) {}
		}
	}
	
	// Private class data
	private Rectangle [] bars;
	private Color [] barColors;
	private int [] lines;
	private int barHeight;
	private int yBarOffset;
 }

