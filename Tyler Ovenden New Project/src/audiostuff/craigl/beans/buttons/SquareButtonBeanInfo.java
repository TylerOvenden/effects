// SquareButtonBeanInfo Class

package audiostuff.craigl.beans.buttons;

import java.awt.*;
import java.beans.*;

public class SquareButtonBeanInfo extends SimpleBeanInfo {

	// Get the appropriate icon
	public Image getIcon(int iconKind) {
		if (iconKind == BeanInfo.ICON_COLOR_16x16) {
			Image img = loadImage("SquareButtonIcon16.gif");
			return img;
		}

		if (iconKind == BeanInfo.ICON_COLOR_32x32) {
			Image img = loadImage("SquareButtonIcon32.gif");
			return img;
		}
		return null;
	}
}
