// ToggleSwitchButton Class
// Written by: Craig A. Lindley
// Last Update: 03/14/99

package audiostuff.craigl.beans.buttons;

import java.awt.*;
import java.beans.*;

public class ToggleSwitchButton extends Button {

	private static final int XPAD = 5;
	private static final int YPAD = 5;
	private static final double HEIGHTWIDTHRATIO = 2.75;
	private static final double HIGHLIGHTPERCENT = 0.2;
	private static final double CAPTIONPERCENT = 0.9;

	// Full strength constructor sets every property of button
	public ToggleSwitchButton(int width, 
				  String fontName, int fontStyle, int fontSize,
				  String topCaption, String bottomCaption,
				  boolean sticky, boolean state, 
				  Color panelColor, Color buttonColor, Color textColor) {

		// Allow the superclass constructor to do its thing
		super(width, (int)(width * HEIGHTWIDTHRATIO), 
			  fontName, fontStyle, fontSize,
			  "", true, sticky, state, true,
			  panelColor, buttonColor, textColor);
		
		this.topCaption = topCaption;
		this.bottomCaption = bottomCaption;
		this.highlightOffset = (int)(width * HIGHLIGHTPERCENT);
		this.captionOffset   = (int)(height * CAPTIONPERCENT);

		onImage = null;
	}

	// Constructor with some reasonable defaults
	public ToggleSwitchButton(int width, String bottomCaption) {

		this(width,  
			 DEFAULTFONTNAME, DEFAULTFONTSTYLE, DEFAULTFONTSIZE,
			 "", bottomCaption, true, false,  
			 PANELCOLOR, BUTTONCOLOR, TEXTCOLOR);
	}

	// Constructor with some reasonable defaults
	public ToggleSwitchButton(int width, String topCaption, String bottomCaption) {

		this(width,  
			 DEFAULTFONTNAME, DEFAULTFONTSTYLE, DEFAULTFONTSIZE,
			 topCaption, bottomCaption, true, false,  
			 PANELCOLOR, BUTTONCOLOR, TEXTCOLOR);
	}

	// Zero argument constructor
	public ToggleSwitchButton() {
	
		this(10,"Bypass");
	}

	public Dimension getPreferredSize() {

		if (bottomCaption == null) 
			return new Dimension(width, height);

		// Calculate the preferred size based on the label text
		FontMetrics fm = getFontMetrics(font);

		int captionWidth = 
			Math.max(fm.stringWidth(topCaption), fm.stringWidth(bottomCaption));
		int maxAscent = fm.getMaxAscent();
		int maxDescent = fm.getMaxDescent();
		int maxCharHeight = maxAscent + maxDescent;

		minWidth = Math.max(width, captionWidth);
		minWidth += 2 * XPAD;
		
		int capOffset = Math.max(captionOffset, height / 2);
		
		minHeight = 2 * (capOffset + YPAD + maxCharHeight);

		return new Dimension(minWidth, minHeight);
	}

	// Override base method so caption offset can be calculated
	public void setWidth(int width) {
		
		int newHeight = (int)(width * HEIGHTWIDTHRATIO);
		super.setWidth(width);
		super.setHeight(newHeight);
	}

	public void setTopCaption(String topCaption) {

		this.topCaption = topCaption;
		onImage = null;
		repaint();
	}

	public String getTopCaption() {

		return topCaption;
	}
	
	public void setBottomCaption(String bottomCaption) {

		this.bottomCaption = bottomCaption;
		onImage = null;
		repaint();
	}

	public String getBottomCaption() {

		return bottomCaption;
	}
	
	private Image generateSwitchImage(boolean isOn) {

		// Calculate important values relative to dimensions
		// of bitmap image.
		int xCenter = minWidth  / 2;
		int yCenter = minHeight / 2;

		// Create the toggle switch image
		Image toggleImage = createImage(minWidth, minHeight);
		
		// Get the graphics contexts
		Graphics gToggleImage = toggleImage.getGraphics();

		// Paint the panel
		gToggleImage.setColor(panelColor);
		gToggleImage.fillRect(0, 0, minWidth, minHeight);

		// Set font into the graphics context
		gToggleImage.setFont(font);
		FontMetrics fm = gToggleImage.getFontMetrics();

		// Draw the captions. Top then Bottom
		gToggleImage.setColor(textColor);
		int captionWidth = fm.stringWidth(topCaption);
		int captionXOffset = (minWidth - captionWidth) / 2;
		int captionYOffset = yCenter - captionOffset;
		gToggleImage.drawString(topCaption, captionXOffset, captionYOffset);

		captionWidth = fm.stringWidth(bottomCaption);
		captionXOffset = (minWidth - captionWidth) / 2;
		captionYOffset = yCenter + captionOffset + fm.getHeight();
		gToggleImage.drawString(bottomCaption, captionXOffset, captionYOffset);
		
		int hWidth  = width / 2;
		int hHeight = height / 2;

		// Draw the outer switch well
		gToggleImage.setColor(Color.white);

		int xOrg = xCenter - hWidth;
		int yOrg = yCenter - hHeight;

		int largeWidth  = width + highlightOffset;
		int largeHeight = height + highlightOffset;
		gToggleImage.fillRoundRect(
			xOrg, yOrg, largeWidth, largeHeight, width, width);

		// Draw the inner switch well
		gToggleImage.setColor(Color.darkGray);
		gToggleImage.fillRoundRect(
			xOrg, yOrg, width, height, width, width);

		// Now the switch shaft
		gToggleImage.setColor(buttonColor);
		int wsShaftWidth = (width * 3) / 4 ;
		int hsShaftWidth = wsShaftWidth  / 2;
		int wlShaftWidth = width;
		int hlShaftWidth = wlShaftWidth  / 2;
		int wShaftLength = (5 * height) / 4;
		int hShaftLength = wShaftLength / 2;

		Polygon p = new Polygon();
		p.addPoint(xCenter - hsShaftWidth, yCenter);
		p.addPoint(xCenter - hlShaftWidth, 
			isOn ? yCenter - hShaftLength : yCenter + hShaftLength);
		p.addPoint(xCenter + hlShaftWidth, 
			isOn ? yCenter - hShaftLength : yCenter + hShaftLength);
		p.addPoint(xCenter + hsShaftWidth, yCenter);
		gToggleImage.fillPolygon(p);

		// Now the shaft head
		gToggleImage.setColor(highlightBrighterColor);
		gToggleImage.fillOval(xCenter - hlShaftWidth,
			isOn ? yCenter - (hShaftLength + hlShaftWidth) :
				   yCenter + (hShaftLength - hlShaftWidth),
							  wlShaftWidth, wlShaftWidth);
		return toggleImage;
	}

	// Paint method
	public void paint(Graphics g) {

		int cwidth  = getSize().width;
		int cheight = getSize().height;
		int xCenter = cwidth  / 2;
		int yCenter = cheight / 2;

		// Calc position of switch in graphics context
		int toggleOrgX = (cwidth  - minWidth) / 2;
		int toggleOrgY = (cheight - minHeight) / 2;

		// See if we have toggle switch images for displaying
		if (onImage == null) {
			onImage = generateSwitchImage(true);

			offImage = generateSwitchImage(false);
		}
		// State is reversed if not in sticky mode
		boolean newState = state;
		
		if (!getSticky())
			newState = !state;
		
		// Render the switch into the device context
		if (newState)
			g.drawImage(onImage,  toggleOrgX, toggleOrgY, null);
		else
			g.drawImage(offImage, toggleOrgX, toggleOrgY, null);
	
	}

	public static void o(String s) {

		System.out.println(s);
	}
	
	
	public static void main(String [] args) {
		
		Frame f = new Frame("Test");
		ToggleSwitchButton tsb = new ToggleSwitchButton();
		tsb.setTopCaption("Work");
		tsb.setState(true);
		f.add(tsb);

		Dimension d = tsb.getPreferredSize();
		d.width *= 2;
		d.height *= 2;
		f.setSize(d);
		f.setVisible(true);
	}

	// Private class data
	private String topCaption = "";
	private String bottomCaption = "";
	private int minWidth = 0;
	private int minHeight = 0;
	private int highlightOffset;
	private int captionOffset;
}
