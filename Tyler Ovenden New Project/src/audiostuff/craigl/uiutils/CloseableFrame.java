// Closable Frame Class
// Written by: Craig A. Lindley
// Last Update: 08/02/98


package craigl.uiutils;

import java.awt.*;
import java.awt.event.*;

public class CloseableFrame extends Frame {

	public CloseableFrame(String title) {
		super(title);

		// Add window listener, which allows Frame window to close
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				windowIsClosing();
			}
		});
	}

	// Register listener
	public void registerCloseListener(CloseableFrameIF closeableIF) {

		this.closeableIF = closeableIF;
	}
	
	
	// Called on close event
	public void windowIsClosing() {

		// Do call back if appropriate
		if (closeableIF != null)
			closeableIF.windowClosing();
		
		// Close the frame down
		dispose();
	}

	// Private class data
	private CloseableFrameIF closeableIF = null;
}

