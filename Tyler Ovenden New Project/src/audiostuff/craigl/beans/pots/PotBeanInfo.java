// PotBeanInfo Class

package audiostuff.craigl.beans.pots;

import java.awt.*;
import java.beans.*;

public class PotBeanInfo extends SimpleBeanInfo {

	// Get the appropriate icon
	public Image getIcon(int iconKind) {

		if (iconKind == BeanInfo.ICON_COLOR_16x16) {
			Image image = loadImage("PotIcon16.gif");
			return image;
		}

		if (iconKind == BeanInfo.ICON_COLOR_32x32) {
			Image image = loadImage("PotIcon32.gif");
			return image;
		}
		return null;
	}
}
