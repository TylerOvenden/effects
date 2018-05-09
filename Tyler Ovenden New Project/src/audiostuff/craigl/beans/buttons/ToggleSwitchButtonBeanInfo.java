// Toggle Switch Button Bean Info Class

package audiostuff.craigl.beans.buttons;

import java.awt.*;
import java.beans.*;

public class ToggleSwitchButtonBeanInfo extends SimpleBeanInfo {

	// Get the appropriate icon
	public Image getIcon(int iconKind) {
		if (iconKind == BeanInfo.ICON_COLOR_16x16) {
			Image img = loadImage("ToggleSwitchButtonIcon16.gif");
			return img;
		}

		if (iconKind == BeanInfo.ICON_COLOR_32x32) {
			Image img = loadImage("ToggleSwitchButtonIcon32.gif");
			return img;
		}
		return null;
	}
  
	// Explicit declare the properties
	public PropertyDescriptor[] getPropertyDescriptors() {

		try {
			PropertyDescriptor
			// Descriptor for each properties
			fontStyle = new PropertyDescriptor("fontStyle", ToggleSwitchButton.class),
			bottomCaption = new PropertyDescriptor("bottomCaption", ToggleSwitchButton.class),
			width = new PropertyDescriptor("width", ToggleSwitchButton.class),
			height = new PropertyDescriptor("height", ToggleSwitchButton.class),
			fontName = new PropertyDescriptor("fontName", ToggleSwitchButton.class),
			sticky = new PropertyDescriptor("sticky", ToggleSwitchButton.class),
			foreground = new PropertyDescriptor("foreground", ToggleSwitchButton.class),
			highlight = new PropertyDescriptor("highlight", ToggleSwitchButton.class),
			state = new PropertyDescriptor("state", ToggleSwitchButton.class),
			captionAtBottom = new PropertyDescriptor("captionAtBottom", ToggleSwitchButton.class),
			caption = new PropertyDescriptor("caption", ToggleSwitchButton.class),
			background = new PropertyDescriptor("background", ToggleSwitchButton.class),
			font = new PropertyDescriptor("font", ToggleSwitchButton.class),
			fontSize = new PropertyDescriptor("fontSize", ToggleSwitchButton.class),
			buttonColor = new PropertyDescriptor("buttonColor", ToggleSwitchButton.class),
			panelColor = new PropertyDescriptor("panelColor", ToggleSwitchButton.class),
			name = new PropertyDescriptor("name", ToggleSwitchButton.class),
			textColor = new PropertyDescriptor("textColor", ToggleSwitchButton.class),
			topCaption = new PropertyDescriptor("topCaption", ToggleSwitchButton.class);

			// Hide the un-need properties
			height.setHidden(true);
			foreground.setHidden(true);
			highlight.setHidden(true);
			captionAtBottom.setHidden(true);
			caption.setHidden(true);
			background.setHidden(true);
			name.setHidden(true);

			// Prepare array of property descriptors for return
			PropertyDescriptor[] pd = 
				{ foreground, highlight, captionAtBottom, caption,
				  background, name,

				  width, height, sticky, state, topCaption, bottomCaption,
				  buttonColor, textColor, panelColor,
				  font, fontName, fontStyle, fontSize };

			return pd;
		}
		catch (IntrospectionException e) {
			throw new Error(e.toString());
		}
	}
}
