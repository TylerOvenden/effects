// SquareButton Class
// Written by: Craig A. Lindley
// Last Update: 07/25/98

package audiostuff.craigl.beans.buttons;

import java.awt.*;
import java.beans.*;

public class SquareButton extends Button {

	private static final int XPAD = 10;
	private static final int YPAD = 10;

	// Full strength constructor sets every property of button
	public SquareButton(int width, int height,
				  String fontName, int fontStyle, int fontSize,
				  String caption, boolean captionAtBottom, boolean sticky,
				  boolean state, boolean hasHighlight,
				  Color panelColor, Color buttonColor, Color textColor) {

		// Allow the superclass constructor to do its thing
		super(width, height, fontName, fontStyle, fontSize,
			  caption, captionAtBottom, sticky, state, hasHighlight,
			  panelColor, buttonColor, textColor);
	}

	// Constructor with some reasonable defaults
	public SquareButton(int width, int height, String caption) {

		this(width, height, DEFAULTFONTNAME, DEFAULTFONTSTYLE, DEFAULTFONTSIZE,
			 caption, true, true, false, true, 
			 PANELCOLOR, BUTTONCOLOR, TEXTCOLOR);
	}

	// Zero argument constructor
	public SquareButton() {
	
		this(38,18, "Press Me");
	}

	public Dimension getPreferredSize() {

		// Calculate the preferred size based on the label text
		FontMetrics fm = getFontMetrics(font);

		int captionWidth = fm.stringWidth(caption);
		int maxAscent = fm.getMaxAscent();
		int maxDescent = fm.getMaxDescent();
		int maxCharHeight = maxAscent + maxDescent;

		int minWidth = Math.max(width, captionWidth);
		minWidth += 2 * XPAD;
		
		int minHeight = height + YPAD + maxCharHeight;

		return new Dimension(minWidth, minHeight);
	}

	// Paint method
	public void paint(Graphics g) {

		// Get dimensions of component
		int cwidth  = getSize().width;
		int cheight = getSize().height;

		// Paint the panel
	    g.setColor(panelColor);
		g.fillRect(0, 0, cwidth, cheight);

		// Set font into the graphics context
		g.setFont(font);

		// Get the font metrics
		FontMetrics fm = g.getFontMetrics();

		// Calculate important dimensions
		int xOrg = (cwidth - width) / 2; 
		int yOrg = captionAtBottom ? YPAD : fm.getMaxDescent() + YPAD;

		String textString = "ON";
		int xText = (width - fm.stringWidth(textString)) / 2;
		xText += xOrg;
		int yText = (height / 2) + (fm.getMaxAscent() / 2);
		yText += yOrg;

		Point [] pts = new Point[12];
		pts[0] = new Point(xOrg, yOrg);
		pts[1] = new Point(xOrg, yOrg + height - 1);
		pts[2] = new Point(xOrg + width - 1, yOrg + height - 1);
		pts[3] = new Point(xOrg + width - 1, yOrg);
		
		pts[4] = new Point(xOrg + 1, yOrg + 1);
		pts[5] = new Point(xOrg + 1, yOrg + height - 2);
		pts[6] = new Point(xOrg + width - 2, yOrg + height - 2);
		pts[7] = new Point(xOrg + width - 2, yOrg + 1);
		
		pts[8] = new Point(xOrg + 2, yOrg + 2);
		pts[9] = new Point(xOrg + 2, yOrg + height - 3);
		pts[10]= new Point(xOrg + width - 3, yOrg + height - 3);
		pts[11]= new Point(xOrg + width - 3, yOrg + 2);
		
		// Draw the button
		if (state) {
			// Button is on
			g.setColor(highlightBrighterColor);
			g.fillRect(xOrg, yOrg, width, height);
			
			// Draw ON
			g.setColor(textColor);
			g.drawString(textString, xText, yText);

			if (hasHighlight) {
				// Do highlights
				g.setColor(Color.white);
				g.drawLine(pts[0].x, pts[0].y, pts[3].x, pts[3].y);
				g.drawLine(pts[0].x, pts[0].y, pts[1].x, pts[1].y);

				g.setColor(highlightDarkerColor);
				g.drawLine(pts[2].x, pts[2].y, pts[3].x, pts[3].y);
				g.drawLine(pts[1].x, pts[1].y, pts[2].x, pts[2].y);
			}

		}	else	{

			// Button is off
			g.setColor(buttonColor);
			g.fillRect(xOrg, yOrg, width, height);

			if (hasHighlight) {
				// Do highlights
				g.setColor(Color.white);
				g.drawLine(pts[0].x, pts[0].y, pts[3].x, pts[3].y);
				g.drawLine(pts[4].x, pts[4].y, pts[7].x, pts[7].y);
				g.drawLine(pts[8].x, pts[8].y, pts[11].x, pts[11].y);
				g.drawLine(pts[0].x, pts[0].y, pts[1].x, pts[1].y);
				g.drawLine(pts[4].x, pts[4].y, pts[5].x, pts[5].y);
				g.drawLine(pts[8].x, pts[8].y, pts[9].x, pts[9].y);

				g.setColor(highlightDarkerColor);
				g.drawLine(pts[2].x, pts[2].y, pts[3].x, pts[3].y);
				g.drawLine(pts[6].x, pts[6].y, pts[7].x, pts[7].y);
				g.drawLine(pts[10].x, pts[10].y, pts[11].x, pts[11].y);
				g.drawLine(pts[1].x, pts[1].y, pts[2].x, pts[2].y);
				g.drawLine(pts[5].x, pts[5].y, pts[6].x, pts[6].y);
				g.drawLine(pts[9].x, pts[9].y, pts[10].x, pts[10].y);
			}
		}
		
		// Draw the caption
		int captionWidth = fm.stringWidth(caption);
		int captionXOffset = (cwidth - captionWidth) / 2;
		int captionYOffset = captionAtBottom ? height + YPAD + fm.getMaxAscent():YPAD;
		g.setColor(textColor);
		g.drawString(caption, captionXOffset, captionYOffset);
	}
}
