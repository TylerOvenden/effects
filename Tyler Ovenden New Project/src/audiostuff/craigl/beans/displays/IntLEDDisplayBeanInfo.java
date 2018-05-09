// IntLEDDisplayBeanInfo.java

package audiostuff.craigl.beans.displays;

// Imports
import java.beans.*;

public class IntLEDDisplayBeanInfo extends SimpleBeanInfo {
  
	// Get the appropriate icon
	public java.awt.Image getIcon(int iconKind) {

		if (iconKind == BeanInfo.ICON_COLOR_16x16) {
			java.awt.Image img = loadImage("LEDDisplayIcon16.gif");
			return img;
		}

		if (iconKind == BeanInfo.ICON_COLOR_32x32) {
			java.awt.Image img = loadImage("LEDDisplayIcon32.gif");
			return img;
		}
		return null;
	}
}
