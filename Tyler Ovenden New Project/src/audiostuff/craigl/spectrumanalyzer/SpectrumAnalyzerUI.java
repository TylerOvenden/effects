// Spectrum Analyzer UI Class
// Written by: Craig A. Lindley
// Last Update: 11/07/99

package craigl.spectrumanalyzer;

import java.awt.*;
import java.awt.event.*;
import craigl.scope.*;
import craigl.utils.*;
import craigl.uiutils.*;

public class SpectrumAnalyzerUI extends BaseUI implements CloseableFrameIF {

	// Do a 4096 point fft reasonable frequency resolution
	private static final int LOG2_FFTSIZE  = 12;
	private static final int FFT_SIZE	   = 1 << LOG2_FFTSIZE;
	private static final int HALF_FFT_SIZE = FFT_SIZE / 2;

	// Establish approximate size of display surface in UI
	private static final int SCOPEWIDTH  = 300;
	private static final int SCOPEHEIGHT = 250;


	public SpectrumAnalyzerUI(
					AbstractAudio aa, 
					String name, short [] acquisitionBuffer, TriggerFlag tf,
					int sampleRate, int numberOfChannels) {

		super("Spectrum Analyzer" + ((name.length() == 0) ? "":" - " + name), aa);

		// Save incoming
		this.acquisitionBuffer = acquisitionBuffer;
		this.sampleRate = sampleRate;
		this.tf = tf;

		// Create the FFT hardware required
		// First, instantiate an FFT object to run the data through
		fft = new FFT(LOG2_FFTSIZE);

		// Allocate arrays needed to hold the data during processing
		realArray = new double[FFT_SIZE];
		imagArray = new double[FFT_SIZE];
		
		// Create the main panel with a grid bag layout
		Panel mainPanel = new Panel();
		add(mainPanel);

		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Make insets so fields don't actually touch
		gbc.insets = new Insets(3,3,3,3);
		mainPanel.setLayout(gbl);

		// Create the status display
		StatusDisplay statusDisplay = new StatusDisplay(sampleRate, numberOfChannels);
		
		// Add it to the main panel
		addDefaultComponent(mainPanel, statusDisplay, gbl, gbc, 0, 0, 18, 2);
		
		// Create the scope display surface
		ss = new ScopeSurface(sampleRate, statusDisplay);
		ss.setSize(SCOPEWIDTH, (3 * SCOPEHEIGHT) / 4);
		
		// Add it to the main panel
		addDefaultComponent(mainPanel, ss, gbl, gbc, 0, 2, 18, 6);
		
		// Create lower panel which contains the scroll bar and the two 
		// button panels
		Panel lowerPanel = new Panel();
		lowerPanel.setLayout(new GridLayout(3,1));

		// Add it to the main panel
		addDefaultComponent(mainPanel, lowerPanel, gbl, gbc, 0, 8, 18, 6);

		// Create scrollbar 
		sb = new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,HALF_FFT_SIZE);
		sb.setBlockIncrement(HALF_FFT_SIZE / 10);
		// Add listener
		sb.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                doScrollAdj(e);
            }
        });
		// Add scrollbar to the panel
		lowerPanel.add(sb);

		// Create zoom panel
		Panel buttonPanel = new Panel();
		buttonPanel.setLayout(new GridLayout(1,4));
		
		// Create VZoom In button
		vZoomInButton = new Button("VZoom In");
		
		// Add listener
		vZoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doVZoomIn();
            }
        });
		// Add to panel
		buttonPanel.add(vZoomInButton);
		
		// Create VZoom Out button
		vZoomOutButton = new Button("VZoom Out");
		
		// Add listener
		vZoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doVZoomOut();
            }
        });
		// Add to panel
		buttonPanel.add(vZoomOutButton);
		
		// Create HZoom In button
		hZoomInButton = new Button("HZoom In");
		
		// Add listener
		hZoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doHZoomIn();
            }
        });
		// Add to panel
		buttonPanel.add(hZoomInButton);

		// Create HZoom Out button
		hZoomOutButton = new Button("HZoom Out");
		
		// Add listener
		hZoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doHZoomOut();
            }
        });
		// Add to panel
		buttonPanel.add(hZoomOutButton);

		// Add zoom panel to its parent
		lowerPanel.add(buttonPanel);

		// Create retrigger button
		retriggerButton = new Button("Retrigger");
		
		// Add listener
		retriggerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doRetrigger();
            }
        });
		// Add to panel
		lowerPanel.add(retriggerButton);
		
        // Calculate the spectrum
		doSpectrum();

		pack();
        setVisible(true);
	}

	// Implemented to satisfy CloseableFrameIF needs
	public void windowClosing() {

		// Nothing special just let the window close
	}

	// Event processing functions
	
	//Scrollbar has been adjusted
	public void doScrollAdj(AdjustmentEvent e) {

		int value = e.getValue();
		
		ss.doScrollAdj(value);
	}
	
	// Vertical zoom in button clicked
	public void doVZoomIn() {

		ss.doVZoomIn();
	}

	// Vertical zoom out button clicked
	public void doVZoomOut() {
	
		ss.doVZoomOut();
	}

	// Horizontal zoom in button clicked
	public void doHZoomIn() {

		ss.doHZoomIn();
	}

	// Horizontal zoom out button clicked
	public void doHZoomOut() {

		ss.doHZoomOut();
	}
	
	public void doSpectrum() {

		// Move the sample data into the arrays for processing. The
		// data consists of real data only.
		for(int i=0; i < FFT_SIZE; i++) {
			realArray[i] = acquisitionBuffer[i];
			imagArray[i] = 0.0;
		}

		// Do the forward fft
		fft.doFFT(realArray, imagArray, false);

		// Calculate power at each point and store in realArray. We only
		// care about the first half of the sample values as all of the 
		// input data was real and therefore only the first half of the
		// results are "independent".
		double maxPower = 0.0;
		for(int i=0; i < HALF_FFT_SIZE; i++) {
			double power = Math.pow(realArray[i], 2) + 
						   Math.pow(imagArray[i], 2);

			// Record the largest power value
			if (power > maxPower)
				maxPower = power;

			realArray[i] = power;
		}
		// Go back through the data and first normalize the values and
		// then calculate the normalized power spectrum in decibels. Result
		// will be in the range 0..-???
		for(int i=0; i < HALF_FFT_SIZE; i++)
			realArray[i] = 10 * Math.log(realArray[i] / maxPower);
		
		// Set the values for display
		ss.setSpectrumValues(FFT_SIZE, realArray);
	}

	public void doRetrigger() {

		// Close down the UI
		dispose();

		// Indicate analyzer should retrigger
		tf.resetTrigger();
	}

	// Private class data
	private short [] acquisitionBuffer;
	private int sampleRate;
	private TriggerFlag tf;
	private FFT fft;
	private double [] realArray;
	private double [] imagArray;
	private ScopeSurface ss;
	private Scrollbar sb;
	private Button vZoomInButton;
	private Button vZoomOutButton;
	private Button hZoomInButton;
	private Button hZoomOutButton;
	private Button retriggerButton;
}


// Surface on which the frequency components are drawn
class ScopeSurface extends Canvas {

	// Various constants used for display
	private static final int HZMIN = 0;
	private static final int HZMAX = 8;
	private static final int VZMIN = 0;
	private static final int VZMAX = 5;
	private static final int PADWIDTH = 1;
	private static final int TICWIDTH = 4;
	private static final int DIVISIONSPERSCREEN = 12;

	public ScopeSurface(int sampleRate, StatusDisplay statusDisplay) {

		super();

		// Save incoming
		this.sampleRate = sampleRate;
		this.statusDisplay = statusDisplay;

		// Initialize zoom to none
		horzZoomFactor = 0;
		vertZoomFactor = 0;
	}

	public void setSpectrumValues(int fftSampleSize,
								  double [] spectrum) {
		// Save incoming
		this.fftSampleSize = fftSampleSize;
		this.spectrum = spectrum;

		// Initialize zoom to none
		horzZoomFactor = 0;
		vertZoomFactor = 0;

		// Signal it is ok to paint
		paintOK = true;

		repaint();
	}

	public void doScrollAdj(int value) {

		componentOffset = value;
		repaint();
	}
	
	// Vertical zoom in button clicked
	public void doVZoomIn() {

		if (vertZoomFactor < VZMAX)
			vertZoomFactor++;

		repaint();
	}

	// Vertical zoom out button clicked
	public void doVZoomOut() {

		if (vertZoomFactor > VZMIN)
			vertZoomFactor--;
	
		repaint();
	}

	// Horizontal zoom in button clicked
	public void doHZoomIn() {

		if (horzZoomFactor < HZMAX)
			horzZoomFactor++;

		repaint();
	}

	// Horizontal zoom out button clicked
	public void doHZoomOut() {

		if (horzZoomFactor > HZMIN)
			horzZoomFactor--;

			repaint();
	}
	
	public void paint(Graphics g) {

		// Background is gray
		setBackground(Color.gray);

		// Get dimension of paintable area
		Rectangle rect = getBounds();

		if ((charHeight == -1) || (charWidth == -1)) {
			// Create a small, 10 pt, font for rendering text on display
			// Font is monospaced
			font = new Font("Monospaced",Font.PLAIN, 10);
	
			g.setFont(font);

			// Read the specs of the font
			fm = g.getFontMetrics();
			charHeight = fm.getHeight();	// Get the char height
			charAscent = fm.getAscent() / 2;// Char offset
			charWidth  = fm.charWidth(' ');	// Get representive char width
		}
				
		// Set font into the graphic context
		g.setFont(font);

		if (!paintOK)
			return;

		// Calculate range of freqs on the display at this zoom and offset
		// Values stored in lowFreq and highFreq variables
		calcFreqRange(componentOffset, horzZoomFactor);

		// Update the status display panel
		statusDisplay.setRate(rate);
		statusDisplay.setLowFrequency(lowFreq);
		statusDisplay.setHighFrequency(highFreq);

		// Calculate some factors needed for drawing on the display
		int x1 = PADWIDTH;						// Distance from left end of display to labels
		int x2 = x1 + fm.stringWidth("-300");	// Distance to end of labels
		int xTic = x2 + PADWIDTH;				// Distance to tic
		int xAxis = xTic + PADWIDTH + PADWIDTH;	// Distance to Y axis
		int xOrg = xAxis + PADWIDTH + PADWIDTH;	// Distance to 1st component
		int xEnd = rect.width - (charWidth);// Distance to last component
		
		// Distance in pixels between components on the screen
		double deltaX = ((double) (xEnd - xOrg + 1)) / componentsPerScreen(horzZoomFactor);

		int yOrg = 2 * charHeight;				// Distance to 0 db tic
		int yEnd = rect.height;					
		int axisPixels = yEnd - yOrg + 1;		// Length of axis
		
		// Calc the number of pixels between tics on the axis
		int pixelsPerDivision = axisPixels / (DIVISIONSPERSCREEN - 1);
		
		// Label the axis in db starting at zero and going negative
		int yPos = yOrg + charAscent;
		int db = 0;
		int dbPerDiv = dbPerDivision(vertZoomFactor);

		String label;
		for (int l=0; l < DIVISIONSPERSCREEN; l++) {
			if (l == 0)
				label = rightJustifyString("db 0");
			else
				label = rightJustifyString(String.valueOf(db));

			g.drawString(label, x1, yPos);
			db -= dbPerDiv;
			yPos += pixelsPerDivision;
		}

		// Draw the yAxis in red with red tics
		g.setColor(Color.red);
		g.drawLine(xAxis, yOrg - PADWIDTH, xAxis, yEnd);

		// Draw the tics on the axis
		yPos = yOrg;
		for (int t=0; t < DIVISIONSPERSCREEN; t++) {
			g.drawLine(xTic, yPos, xTic + TICWIDTH, yPos);
			yPos += pixelsPerDivision;
		}

		// Finally, draw the component data
		g.setColor(Color.green);

		int componentsPerScreen = componentsPerScreen(horzZoomFactor);
		
		for (int index=0; index < componentsPerScreen; index++) {
			int componentIndex = componentOffset + index;
			if (componentIndex <= fftSampleSize  / 2) {
				
				double component = Math.abs(spectrum[componentIndex]);
				double yDivisions = component / dbPerDiv;
				int yStart = yOrg + (int)(pixelsPerDivision * yDivisions);

				int newX = (int)(xOrg + (index * deltaX));
				g.drawLine(newX, yStart, newX, yEnd);
			}	else
				break;
		}
	}
	
	// Support functions
	
	// Calculate and set the low and high freq variables used
	// for displaying freq range on screen. Their values depend
	// upon horzZoomFactor and the position of the scrollbar.
	private void calcFreqRange(int componentOffset, int hZoom) {

		// Calculate the frequency difference between samples
		rate = ((double) sampleRate) / ((double) fftSampleSize);
		
		// lowFreq depends only upon componentOffset
		lowFreq = (double)(componentOffset * sampleRate) / fftSampleSize;
		
		// highFreq depends upon both arguments
		int cps = componentsPerScreen(hZoom);
		
		// Constrain highFreq to 1/2 sample rate
		int component = Math.min(componentOffset + cps, fftSampleSize / 2);
		
		// Calc highFreq limit
		highFreq = (double)(component * sampleRate) / fftSampleSize;
	}
	
	// Given the horizontal zoom factor how many components
	// of the spectrum can fit on the screen. The total number of
	// components is 1/2 the fft sample size. The 11 is from
	// 2048, 1/2 4096.
	private int componentsPerScreen(int hZoom) {

		return 1 << (11 - hZoom);
	}

	// Given the vertical zoom factor what is the vertical
	// resolution per screen division in db.
	private int dbPerDivision(int vZoom) {

		return 1 << (5 - vZoom);
	}

	private String rightJustifyString(String s) {

		int length = s.length();
		int offset = 4 - length;
		String retString = "";
		for (int i=0; i < offset; i++)
			retString += " ";

		return retString + s;
	}
	
	// Private class data
	private StatusDisplay statusDisplay;
	private double [] spectrum;
	private int fftSampleSize;
	private int sampleRate;
	private int horzZoomFactor;
	private int vertZoomFactor;
	private double rate, lowFreq, highFreq;
	private int charHeight = -1;
	private int charWidth = -1;
	private int charAscent;
	private Font font;
	private FontMetrics fm;
	private int componentOffset = 0;
	private boolean paintOK = false;
}