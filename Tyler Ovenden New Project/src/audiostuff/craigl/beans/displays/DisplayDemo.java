// Display Demonstration Program
// Written by: Craig A. Lindley
// Last Update: 03/21/99

package audiostuff.craigl.beans.displays;

import java.awt.*;

public class DisplayDemo extends Frame {

	public static final Color PANELCOLOR = Color.black;
	public static final Color LEDCOLOR   = Color.green;
	public static final Color LEDBGCOLOR = Color.black;

	public DisplayDemo() {

		super("Display Demo");

		Panel displayPanel = new Panel();
		displayPanel.setLayout(new GridLayout(1, 2));

		// Create the seven segment display
		display1 = new IntLEDDisplay(130, 150, 1, 0);
		display1.setPanelColor(PANELCOLOR);
		display1.setLEDColor(LEDCOLOR);
		display1.setLEDBGColor(LEDBGCOLOR);
		display1.setTextColor(LEDCOLOR);
		display1.setCaption("Seven Segment Display");
		displayPanel.add(display1);

		Panel blankPanel = new Panel();
		blankPanel.setBackground(Color.black);
		blankPanel.setLayout(new GridLayout(3, 1));
		blankPanel.add(new Label(""));

		// Create a readout label
		display2 = new ReadoutLabel(Color.green, "count", 5);
		blankPanel.add(display2);
		blankPanel.add(new Label(""));
		displayPanel.add(blankPanel);
		
		add(displayPanel);

		pack();
	}


	// Test application entry point	
	public static void main(String [] args) {

		DisplayDemo btd = new DisplayDemo();
		btd.show();

		Thread t = new Thread() {
			public void run() {

				int count = -9;
				while(true) {
				
					try {
						sleep(700);
					}
					catch (InterruptedException e) {}

					// Update both displays
					display1.setValue(count);
					display2.setValue(count++);

					if (count >= 10)
						count = -9;
				}
			}
		};
		t.start();
			
	}
	// Class variables
	private static IntLEDDisplay display1;
	private static ReadoutLabel  display2;
}