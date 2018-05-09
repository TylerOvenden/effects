// Audio UI Interface 
// Written by: Craig A. Lindley
// Last Update: 07/12/98

package craigl.utils;

import java.awt.Rectangle;

// All audio processors that need a UI must implement
// this interface.

public interface AudioUIIF {

    public void showUI(boolean isVisible);

	public void stopUI();

    public Rectangle getBounds();
	
    public void setBounds(int x, int y, int w, int h);
	
}
