// IntLEDDisplay Class using 7 segment displays
// Written by: Craig A. Lindley
// Last Update: 03/21/99

package audiostuff.craigl.beans.displays;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;

public class IntLEDDisplay extends LEDDisplayBase implements AdjustmentListener {


	public IntLEDDisplay(int width, int height, int numberOfDigits,
					  int value, boolean raised, 
					  String fontName, int fontStyle, int fontSize,
					  String caption, boolean captionAtBottom,
					  Color panelColor, Color ledColor, 
					  Color ledBGColor, Color textColor) {
		
		// Allow the superclass constructor to do its thing
		super(width, height, numberOfDigits, raised,
			  fontName, fontStyle, fontSize, caption, captionAtBottom,
			  panelColor, ledColor, ledBGColor, textColor);

		setValue(value);
	}

	// Constructor with reasonable defaults
	public IntLEDDisplay(int width, int height, int numberOfDigits,
						 int value) {
		
		this(width, height, numberOfDigits, value, false,
			 DEFAULTFONTNAME, DEFAULTFONTSTYLE, DEFAULTFONTSIZE,
			 "LEDDisplay", true, 
			 DEFAULTPANELCOLOR, DEFAULTLEDCOLOR, DEFAULTLEDBGCOLOR,
			 DEFAULTTEXTCOLOR);
	}

	// Zero argument constructor for the bean box
	public IntLEDDisplay() {

		this(90, 40, 3, 234);
	}

	public void paint(Graphics g) {

		int cwidth = getSize().width;
		int cheight = getSize().height;

		// Paint the panel color
		g.setColor(panelColor);
		g.fillRect(0, 0, cwidth, cheight);

		// Set font into the graphics context to get font metrics
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();

		// Get various text attributes
		int charHeight = fm.getHeight();
		int charOffset = charHeight / 2;
		int textWidth  = fm.stringWidth(caption);

		// Calculate position of first digit
		int xOrg = (cwidth - calcDisplayWidth()) / 2;
		int yOrg = captionAtBottom ? YPAD : YPAD + charHeight;

		// Fill the background around segments
		g.setColor(ledBGColor);
		g.fillRect(xOrg, yOrg, width, height);

		// Determine if digits have been generated
		if (!digitsValid) {
			
			// Render the digits
			renderDigits();

			// Digit images are now valid for this size 7 segment display
			digitsValid = true;
		}
		// Draw minus sign if required
		if (value < 0) {
			// Draw the minus sign
			int halfHeight = digitHeight / 2;
			int halfSignHeight = MINUSSIGNHEIGHT / 2;
			int x = xOrg;
			int y = yOrg + halfHeight - halfSignHeight;
			int width = separatorWidth - 1;
			int height = MINUSSIGNHEIGHT;
			
			g.setColor(ledOnColor);
			g.fillRect(x, y, width, height);
		}
		
		// Display absolute value
		int displayValue = Math.abs(value);
		
		// Draw the digits. MSD to LSD
		int xDigitPos = xOrg + separatorWidth;
		for (int i = 0; i < numberOfDigits; i++) {
			int div = (int) Math.pow(10.0, (double)(numberOfDigits - i - 1));
			
			g.drawImage(digitImages[(displayValue / div) % 10],
						xDigitPos, yOrg, this);
			
			xDigitPos += digitWidth + separatorWidth;
		}

		// Draw the caption
		g.setColor(textColor);
		int textXOffset = (cwidth - textWidth) / 2;
		int textYOffset = captionAtBottom ? 
			YPAD + height + charHeight : YPAD + charOffset;
		g.drawString(caption, textXOffset,  textYOffset);
	}
	
	public int getValue() {

		return value;
	}

	public void setValue(int value) {

		this.value = value;
		repaint();
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e) {

		setValue(e.getValue());
	}

	// Private class data
	private int separatorMode;
	private int value;
}

