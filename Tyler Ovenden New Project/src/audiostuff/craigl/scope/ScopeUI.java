// Scope UI Class
// Written by: Craig A. Lindley
// Last Update: 11/07/99

package audiostuff.craigl.scope;

import java.awt.*;
import java.awt.event.*;
import audiostuff.craigl.utils.*;
import audiostuff.craigl.uiutils.*;

public class ScopeUI extends BaseUI implements CloseableFrameIF {

	/**
	 * This class represents the UI for the scope
	 */

	private static final int SCOPEWIDTH  = 300;
	private static final int SCOPEHEIGHT = 200;

	/**
	 * ScopeUI Class constructor
	 *
	 * @param AbstractAudio aa is the Scope device associated with this UI
	 * @param String name is provided this name is added to the UI's title
	 * @param short [] acquisitionBuffer is the buffer of data to be displayed
	 * @param TriggerFlag tf is the trigger controlling acquisition. Reseting
	 * this flag by clicking the retrigger button causes a new buffer of
	 * samples to be acquired and displayed.
	 * @param int sampleRate is the sample rate the samples were acquired
	 * at
	 * @param int numberOfChannels is 1 or 2 depending whether the samples
	 * represent a mono or stereo source respectively.
	 */
	public ScopeUI(AbstractAudio aa, 
				   String name, short [] acquisitionBuffer, TriggerFlag tf,
				   int sampleRate, int numberOfChannels) {

		super("Retriggerable Sample Scope" + ((name.length() == 0) ? "":" - " + name), aa);

		// Save incoming
		this.acquisitionBuffer = acquisitionBuffer;
		this.tf = tf;

		// Create the main panel with a grid bag layout
		Panel mainPanel = new Panel();
		add(mainPanel);

		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mainPanel.setLayout(gbl);

		// Create the status display
		statusDisplay = new StatusDisplay(sampleRate, numberOfChannels);
		
		// Add it to the main panel
		addDefaultComponent(mainPanel, statusDisplay, gbl, gbc, 0, 0, 18, 2);

		// Create the scope display surface
		ss = new ScopeSurface(acquisitionBuffer, sampleRate, statusDisplay);
		ss.setSize(SCOPEWIDTH, (3 * SCOPEHEIGHT) / 4);
		
		// Add it to the main panel
		addDefaultComponent(mainPanel, ss, gbl, gbc, 0, 2, 18, 6);

		// Create lower panel which contains the scroll bar and button panels
		Panel lowerPanel = new Panel();
		lowerPanel.setLayout(new GridLayout(2,1));

		// Add it to the main panel
		addDefaultComponent(mainPanel, lowerPanel, gbl, gbc, 0, 8, 18, 4);

		// Create scrollbar 
		int numberOfSamples = acquisitionBuffer.length;
		
		// Create scrollbar 
		sb = new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,numberOfSamples);
		sb.setBlockIncrement(numberOfSamples / 10);
		// Add listener
		sb.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                doScrollAdj(e);
            }
        });
		// Add scrollbar to the panel
		lowerPanel.add(sb);

		// Create button panel
		Panel buttonPanel = new Panel();
		buttonPanel.setLayout(new GridLayout(1,4));
		
		// Create button
		fullButton = new Button("Full");
		
		// Add listener
		fullButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doFull();
            }
        });
		// Add to panel
		buttonPanel.add(fullButton);
		
		// Create button
		zoomInButton = new Button("Zoom In");

		// Add listener
		zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doZoomIn();
            }
        });
		// Add to panel
		buttonPanel.add(zoomInButton);

		// Create button
		zoomOutButton = new Button("Zoom Out");
		
		// Add listener
		zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doZoomOut();
            }
        });
		// Add to panel
		buttonPanel.add(zoomOutButton);

		// Create button
		retriggerButton = new Button("Retrigger");
		
		// Add listener
		retriggerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doRetrigger();
            }
        });
		// Add to panel
		buttonPanel.add(retriggerButton);
		lowerPanel.add(buttonPanel);

        pack();
        setVisible(true);
	}

	// Implemented to satisfy CloseableFrameIF needs
	public void windowClosing() {

		// Nothing special just let the window close
	}

	public void doScrollAdj(AdjustmentEvent e) {

		int value = e.getValue();
		ss.setFirstSample(value);
	}

	// Reset to no zoom
	public void doFull() {

		// Set first sample to zero
		ss.setFirstSample(0);
		
		// Set scrollbar position
		sb.setValue(0);
		
		// Set zoom factor
		ss.resetZoom();
	}

	public void doZoomIn() {

		ss.doZoomIn();
	}

	public void doZoomOut() {

		ss.doZoomOut();
	}

	public void doRetrigger() {

		// Close down the UI
		dispose();

		// Indicate scope should retrigger
		tf.resetTrigger();
	}

	// Test code	
	public static void main(String [] args) {

		short [] buffer = new short[22000];
		for (int i = 0; i < 22000; i++)
			buffer[i] = (short)((65535 * Math.random()) -32767);
		
		ScopeUI sui = new ScopeUI(null, "Test Scope", buffer, null, 11025, 2);
	}
	
	// Private class data
	private short [] acquisitionBuffer  = null;
	private TriggerFlag tf;
	private StatusDisplay statusDisplay;
	private ScopeSurface ss;
	private Scrollbar sb;
	private Button retriggerButton;
	private Button fullButton;
	private Button zoomInButton;
	private Button zoomOutButton;
	private int zoomFactor;
}


// Surface on which waveform is drawn
class ScopeSurface extends Canvas {

	public static final double BORDERRATIO		= 0.05;
	public static final int MINSAMPLESPERSCREEN	= 5;
	public static final int MINZOOM = 0;
	public static final int MAXZOOM = 11;

	public ScopeSurface(short [] acquisitionBuffer, int sampleRate,
						StatusDisplay statusDisplay) {
		super();

		// Save incoming
		this.acquisitionBuffer = acquisitionBuffer;
		this.numberOfSamples = acquisitionBuffer.length;
		this.sampleRate = sampleRate;
		this.statusDisplay = statusDisplay;

		sampleNumber = 0;
		zoomFactor = 0;

		// Last line position
		oldX = 0;
		oldY = 0;
	}

	public void paint(Graphics g) {

		// Get dimension of paintable area
		Rectangle rect = getBounds();
		int midY = rect.height / 2;

		// Calculate scale factor
		double maxExtent = (rect.height - (rect.height * BORDERRATIO)) / 2; 
		double scaleFactor = maxExtent / 32767.0;

		// Background is gray
		setBackground(Color.gray);

		// Draw scale lines
		g.setColor(Color.yellow);
		g.drawLine(0, (int)(midY - maxExtent - 1), rect.width, (int)(midY - maxExtent - 1));
		g.drawLine(0, (int)(midY + maxExtent + 1), rect.width, (int)(midY + maxExtent + 1));
		
		g.setColor(Color.red);
		g.drawLine(0, midY, rect.width, midY);

		// Draw the waveform if one is available
		if ((acquisitionBuffer != null) && (acquisitionBuffer.length != 0)) {
			
			g.setColor(Color.green);
			
			int samplesPerScreen = calculateSamplesPerScreen();

			// Update on screen display time
			statusDisplay.setTimeDisplay((double) samplesPerScreen / sampleRate);

			double pixelsPerSample = ((double) rect.width) / samplesPerScreen;

			for (int index=0; index < samplesPerScreen; index++) {
				int sampleIndex = sampleNumber + index;
				if (sampleIndex < numberOfSamples) {
					short sampleY = acquisitionBuffer[sampleNumber + index];
					int newX = (int)(pixelsPerSample * index);
					int newY = (int)(midY - (scaleFactor * sampleY));
					g.drawLine(oldX, oldY, newX, newY);
					oldX = newX;
					oldY = newY;
				}
			}
			// Reset for repaint
			oldX = 0;
			oldY = midY;
		}
	}

	public void setFirstSample(int sampleNumber) {

		this.sampleNumber = sampleNumber;
		repaint();
	}

	public void resetZoom() {

		zoomFactor = 0;
	}
	
	public void doZoomIn() {

		if (zoomFactor < MAXZOOM)
			zoomFactor++;

		repaint();
	}

	public void doZoomOut() {
		if (zoomFactor > MINZOOM)
			zoomFactor--;

		repaint();
	}

	private int calculateSamplesPerScreen() {

		return numberOfSamples >> zoomFactor;
	}

	// Private class data
	private short [] acquisitionBuffer  = null;
	private int numberOfSamples;
	private int sampleRate;
	private StatusDisplay statusDisplay;
	private int sampleNumber;
	private int zoomFactor;
	private int oldX;
	private int oldY;
}