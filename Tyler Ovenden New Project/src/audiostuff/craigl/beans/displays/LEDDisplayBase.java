// LEDDisplayBase Class using 7 segment displays
// Written by: Craig A. Lindley
// Last Update: 04/04/99

package audiostuff.craigl.beans.displays;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;

public abstract class LEDDisplayBase extends Canvas {

	// Misc space padding values
	public static final int XPAD = 5;
	public static final int YPAD = 7;
	public static final int MINUSSIGNHEIGHT = 3;

	// Separator width between digits
	public static final double SEPARATORWIDTHPERCENT = .3;
	
	// Misc default values
	public static final Color DEFAULTPANELCOLOR	= Color.lightGray;
	public static final Color DEFAULTTEXTCOLOR	= Color.white;
	public static final Color DEFAULTLEDCOLOR	= Color.red;
	public static final Color DEFAULTLEDBGCOLOR	= Color.gray;
	
	public static final String DEFAULTFONTNAME	= "Dialog";
	public static final int DEFAULTFONTSTYLE	= Font.PLAIN;
	public static final int DEFAULTFONTSIZE		= 9;

	public LEDDisplayBase(int width, int height, int numberOfDigits,
						  boolean raised, 
						  String fontName, int fontStyle, int fontSize,
						  String caption, boolean captionAtBottom,
						  Color panelColor, Color ledColor, 
						  Color ledBGColor, Color textColor) {
		
		// Allow the superclass constructor to do its thing
		super();

		// Save incoming
		setRaised(raised);
		setFontName(fontName);
		setFontStyle(fontStyle);
		setFontSize(fontSize);
		setCaption(caption);
		setCaptionAtBottom(captionAtBottom);
		setPanelColor(panelColor);
		setLEDColor(ledColor);
		setLEDBGColor(ledBGColor);
		setTextColor(textColor);
		setNumberOfDigits(numberOfDigits);
		setWidth(width);
		setHeight(height);

		// Create array for holding digit images 0 ..9
		digitImages = new Image[10];
	}

	public abstract void paint(Graphics g);

	protected void sizeToFit() {

		// Resize to the preferred size
		Dimension d = getPreferredSize();
		setSize(d);

		Component p = getParent();
		if (p != null) {
			p.invalidate();
			p.validate();
		}
	}

	public Dimension getPreferredSize() {
		
		// Calculate the preferred size based on the caption text
		FontMetrics fm = getFontMetrics(font);
		int charHeight = fm.getMaxAscent() + fm.getMaxDescent();
		int charWidth  = fm.charWidth('0');

		int minHeight = YPAD + height + charHeight;
		int minWidth  = (2 * XPAD) + width;
		int captionWidth = getCaption().length() * charWidth;
		minWidth = Math.max(minWidth, captionWidth);
		
		return new Dimension(minWidth, minHeight);
	}

	public void renderDigits() {

		// Generate the polygons for the 7 segment display segments
		Polygon [] segments = SevenSegmentDisplay.generateSegments(digitWidth, digitHeight);

		// Generate an image for each digit
		for (int digit=0; digit < 10; digit++) {

			// Create an offscreen image for the digit
			Image digitImage = createImage(digitWidth, digitHeight);
				
			// Store the digit image into array of digits
			digitImages[digit] = digitImage;
				
			// Get graphic context for offscreen digit image
			Graphics dg = digitImage.getGraphics();

			// Render the digit into the offscreen image
			SevenSegmentDisplay.drawDigit(dg, segments, digit,
										  digitWidth, digitHeight,
										  ledBGColor, ledOnColor, ledOffColor);
		}
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

		// Calculate width of digits and separators
		double dw = ((double) width) / (numberOfDigits * (1 + SEPARATORWIDTHPERCENT));
		double sw = dw * SEPARATORWIDTHPERCENT;

		// Truncate to integers
		digitWidth = (int) dw;
		separatorWidth = (int) sw;
		
		digitsValid = false;
		sizeToFit();
	}
	
	public int calcDisplayWidth() {

		return (digitWidth + separatorWidth) * numberOfDigits;
	}
	
	
	public int getHeight() {
		
		return height;
	}

	public void setHeight(int height) {

		this.height = height;
		digitHeight = height;
		digitsValid = false;
		sizeToFit();
	}
	
	public int getNumberOfDigits() {

		return numberOfDigits;
	}

	public void setNumberOfDigits(int numberOfDigits) {

		this.numberOfDigits = numberOfDigits;
		digitsValid = false;
		repaint();
	}

	public Color getPanelColor() {

		return panelColor;
	}

	public void setPanelColor(Color panelColor) {

		this.panelColor = panelColor;
		repaint();
	}

	public Color getLEDColor() {
		return ledOnColor;
	}

	public void setLEDColor(Color ledOnColor) {
		
		// Save the on color
		this.ledOnColor  = ledOnColor;
		
		// Go 4 shades darker for the off color
		this.ledOffColor = ledOnColor.darker().darker().darker().darker();

		digitsValid = false;
		repaint();
	}

	public Color getLEDBGColor() {
		return ledBGColor;
	}

	public void setLEDBGColor(Color ledBGColor) {
		
		// Save the on color
		this.ledBGColor  = ledBGColor;
		
		digitsValid = false;
		repaint();
	}

	public Color getTextColor() {

		return textColor;
	}

	public void setTextColor(Color textColor) {

		this.textColor = textColor;
		repaint();
	}

	public Font getFont() {

		return font;
	}

	public void setFont(Font font) {

		this.font = font;

		// Size the knob to the text/font
		sizeToFit();
	}
	
	public String getFontName() {

		return fontName;
	}

	public void setFontName(String fontName) {

		this.fontName = fontName;
		
		font = new Font(fontName, fontStyle, fontSize);
		
		// Size the knob to the text/font
		sizeToFit();
	}

	public int getFontStyle() {

		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {

		this.fontStyle = fontStyle;

		font = new Font(fontName, fontStyle, fontSize);

		// Size the knob to the text/font
		sizeToFit();
	}
	
	public int getFontSize() {

		return fontSize;
	}

	public void setFontSize(int fontSize) {

		this.fontSize = fontSize;

		font = new Font(fontName, fontStyle, fontSize);

		// Size the knob to the text/font
		sizeToFit();
	}
	
	public String getCaption() {

		return caption;
	}

	public void setCaption(String caption) {

		this.caption = new String(caption);

		// Size the display to the text/font
		sizeToFit();
	}

	public boolean getCaptionAtBottom() {

		return captionAtBottom;
	}

	public void setCaptionAtBottom(boolean captionAtBottom) {

		this.captionAtBottom = captionAtBottom;
		repaint();
	}

	// Private class data
	protected int width;
	protected int height;
	protected int digitWidth;
	protected int digitHeight;
	protected int separatorWidth;
	protected int numberOfDigits;
	protected int separatorMode;
	protected boolean raised;
	protected Color ledOnColor;
	protected Color ledOffColor;
	protected Color ledBGColor;
	protected Color panelColor;
	protected Color textColor;
	protected String fontName = "Dialog";
	protected int fontStyle = Font.PLAIN;
	protected int fontSize = 8;
	protected Font font;
	protected String caption = "";
	protected boolean captionAtBottom;
	protected Image [] digitImages;
	protected boolean digitsValid = false;

}

