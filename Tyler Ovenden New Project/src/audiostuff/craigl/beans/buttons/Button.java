// Button Base Class
// Written by: Craig A. Lindley
// Last Update: 06/28/98

package audiostuff.craigl.beans.buttons;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public abstract class Button extends Canvas {

	public static final Color PANELCOLOR	= Color.lightGray;
	public static final Color BUTTONCOLOR	= Color.darkGray;
	public static final Color TEXTCOLOR		= Color.white;
	
	public static final String DEFAULTFONTNAME	= "Dialog";
	public static final int DEFAULTFONTSTYLE	= Font.PLAIN;
	public static final int DEFAULTFONTSIZE		= 9;

	// Full strength constructor sets every property of button
	public Button(int width, int height,
				  String fontName, int fontStyle, int fontSize,
				  String caption, boolean captionAtBottom, boolean sticky,
				  boolean state, boolean hasHighlight,
				  Color panelColor, Color buttonColor, Color textColor) {

		// Allow the superclass constructor to do its thing
		super();

		// Save incoming
		setFontName(fontName);
		setFontStyle(fontStyle);
		setFontSize(fontSize);
		setWidth(width);
		setHeight(height);
		setCaption(caption);
		setCaptionAtBottom(captionAtBottom);
		setSticky(sticky);
		setState(state);
		setHighlight(hasHighlight);
		setPanelColor(panelColor);
		setButtonColor(buttonColor);
		setTextColor(textColor);

		// Enable event processing
		enableEvents(AWTEvent.MOUSE_EVENT_MASK |
					 AWTEvent.KEY_EVENT_MASK);
	}

	public abstract Dimension getPreferredSize();
	
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

	// Paint method
	public abstract void paint(Graphics g);
	
	public int getWidth() {

		return width;
	}

	public void setWidth(int width) {

		this.width = width;

		// Force generation of new bitmaps if any
		onImage = null;

		// Size the knob to the text/font
		sizeToFit();
	}

	public int getHeight() {

		return height;
	}

	public void setHeight(int height) {

		this.height = height;

		// Force generation of new bitmaps if any
		onImage = null;

		// Size the knob to the text/font
		sizeToFit();
	}

	public Font getFont() {

		return font;
	}

	public void setFont(Font font) {

		this.font = font;

		// Force generation of new bitmaps if any
		onImage = null;

		// Size the knob to the text/font
		sizeToFit();
	}
	
	public String getFontName() {

		return fontName;
	}

	public void setFontName(String fontName) {

		this.fontName = fontName;
		
		font = new Font(fontName, fontStyle, fontSize);
		
		// Force generation of new bitmaps if any
		onImage = null;

		// Size the knob to the text/font
		sizeToFit();
	}

	public int getFontStyle() {

		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {

		this.fontStyle = fontStyle;

		font = new Font(fontName, fontStyle, fontSize);

		// Force generation of new bitmaps if any
		onImage = null;

		// Size the knob to the text/font
		sizeToFit();
	}
	
	public int getFontSize() {

		return fontSize;
	}

	public void setFontSize(int fontSize) {

		this.fontSize = fontSize;

		font = new Font(fontName, fontStyle, fontSize);

		// Force generation of new bitmaps if any
		onImage = null;

		// Size the knob to the text/font
		sizeToFit();
	}

	public String getCaption() {

		return caption;
	}

	public void setCaption(String caption) {

		this.caption = new String(caption);

		// Force generation of new bitmaps if any
		onImage = null;

		// Size the knob to the text/font
		sizeToFit();
	}

	public boolean getCaptionAtBottom() {

		return captionAtBottom;
	}

	public void setCaptionAtBottom(boolean captionAtBottom) {

		this.captionAtBottom = captionAtBottom;
		// Force generation of new bitmaps if any
		onImage = null;
		repaint();
	}

	public boolean getSticky() {

		return sticky;
	}

	public void setSticky(boolean sticky) {

		this.sticky = sticky;
	}

	public boolean getState() {

		return state;
	}
    
	public void setState(boolean state) {

		this.state = state;
		repaint();
	}
	
	public boolean getHighlight() {

		return hasHighlight;
	}

	public void setHighlight(boolean hasHighlight) {

		this.hasHighlight = hasHighlight;
		// Force generation of new bitmaps if any
		onImage = null;

		repaint();
	}
	
	public Color getPanelColor() {

		return panelColor;
	}

	public void setPanelColor(Color panelColor) {

		this.panelColor = panelColor;
		// Force generation of new bitmaps if any
		onImage = null;
		repaint();
	}

	public Color getButtonColor() {

		return buttonColor;
	}

	public void setButtonColor(Color buttonColor) {

		this.buttonColor = buttonColor;
		
		// Calculate highlight colors
		this.highlightBrighterColor	= buttonColor.brighter();
		this.highlightDarkerColor	= buttonColor.darker();
		
		// Force generation of new bitmaps if any
		onImage = null;

		// Cause a repaint
		repaint();
	}
  
	public Color getTextColor() {

		return textColor;
	}

	public void setTextColor(Color textColor) {

		this.textColor = textColor;
		// Force generation of new bitmaps if any
		onImage = null;
		repaint();
	}

	// Event processing methods
	public synchronized void addActionListener(ActionListener l) {

		actionListener = AWTEventMulticaster.add(actionListener, l);
	}

	public synchronized void removeActionListener(ActionListener l) {

		actionListener = AWTEventMulticaster.remove(actionListener, l);
	}

	protected void processActionEvent(ActionEvent e) {

		// Deliver the event to all registered action event listeners
		if (actionListener != null)
			actionListener.actionPerformed(e);
	}

	protected void processMouseEvent(MouseEvent e) {

		// Track mouse presses/releases
		switch(e.getID()) {
			case MouseEvent.MOUSE_PRESSED:
				requestFocus();
				state = !state;
				repaint();
				fireActionEvent();
				break;
			
			case MouseEvent.MOUSE_RELEASED:
				if (state && !sticky) {
					state = false;
					fireActionEvent();
					repaint();
				}
				break;
		}
		// Let the superclass continue delivery
		super.processMouseEvent(e);
	}

	// Due to the duration of the key event,  using keyboard keys in
	// conjunction with non sticky buttons doesn't always work well.
	protected void processKeyEvent(KeyEvent e) {

		// Simulate a mouse click for certain keys
		if (e.getKeyChar() == KeyEvent.VK_ENTER ||
			e.getKeyChar() == KeyEvent.VK_SPACE) {
			
			if (sticky) {
				state = !state;
				repaint();
			}	else	{
				state = true;
				repaint();
				state = false;
				repaint();
			}
			fireActionEvent();
		}
		// Let the superclass continue delivery
		super.processKeyEvent(e);
	}

	private void fireActionEvent() {
		processActionEvent(new 
			ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
						state ? "ON":"OFF"));
	}
	
	// Class data
	protected int width;
	protected int height;
	protected String fontName = "Dialog";
	protected int fontStyle = Font.PLAIN;
	protected int fontSize = 8;
	protected Font font;
	protected String caption = "";
	protected boolean captionAtBottom;
	protected boolean sticky;
	protected boolean state;
	protected boolean hasHighlight;
	protected Color panelColor;
	protected Color buttonColor;
	protected Color textColor;
	protected Color highlightBrighterColor;
	protected Color highlightDarkerColor;
	protected transient ActionListener actionListener = null;
	protected Image onImage = null;
	protected Image offImage = null;
}
