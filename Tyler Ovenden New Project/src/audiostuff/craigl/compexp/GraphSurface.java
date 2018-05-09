// Graph Surface Class
// for use with the Compressor/Expander
// Written by: Craig A. Lindley
// Last Update: 06/26/99

package craigl.compexp;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import craigl.processors.*;
import craigl.uiutils.*;


// Surface on which compressor/expander graph is drawn
public class GraphSurface extends Panel {

	private static final Color BACKGROUNDCOLOR = Color.darkGray;
	private static final Color GRIDCOLOR	   = Color.green;
	private static final Color GRAPHCOLOR	   = Color.yellow;
	private static final Color CROSSCOLOR	   = Color.red;
	public  static final int GRAPHDIM = 160;
	public  static final int GRAPHGRIDS = 8;
	private static final double MINDB = 96.0;
	private static final int CROSSSIZE = 13;

	public GraphSurface() {

		// Set background of graph
		setBackground(BACKGROUNDCOLOR);

		// Set initial points on graph
		atPoint = mapDbToPoint(0, 0);
		btPoint = mapDbToPoint(-96, -96);
		
		threshold = CompExpUI.THRESHOLDDEF;
		tPoint  = mapDbToPoint(threshold, threshold);

		belowThresholdRatio = 1.0;
		aboveThresholdRatio = 1.0;
	}

	public void setSize(int w, int h) {

		w = (w / GRAPHGRIDS ) * GRAPHGRIDS;
		h = w;
		super.setSize(w, h);
	}

	public void setBounds(int x, int y, int w, int h) {

		w = (w / GRAPHGRIDS) * GRAPHGRIDS;
		h = (h / GRAPHGRIDS) * GRAPHGRIDS;
		
		int min = Math.min(w, h);
		w = h = min;

		super.setBounds(x, y, w, h);
	}
	
	public void paint(Graphics g) {

		super.paint(g);

		// Get dimension of paintable area
		Dimension d = getSize();
		int gridSize = d.width / GRAPHGRIDS;

		// Draw grid lines
		g.setColor(GRIDCOLOR);
		for (int x=gridSize ; x < d.width; x += gridSize)
			g.drawLine(x, 0, x, d.height);

		for (int y=gridSize ; y < d.height; y += gridSize)
			g.drawLine(0, y, d.width, y);

		// Draw the graph
		g.setColor(GRAPHCOLOR);
		g.drawLine(atPoint.x, atPoint.y, tPoint.x, tPoint.y);
		g.drawLine(tPoint.x, tPoint.y, btPoint.x, btPoint.y);

		// Draw crosses
		g.setColor(CROSSCOLOR);
		drawCross(g, atPoint);
		drawCross(g, btPoint);
		drawCross(g,  tPoint);
	}

	public Dimension getPreferredSize() {
		
		return new Dimension(GRAPHDIM, GRAPHDIM);
	}

	private Point mapDbToPoint(double indB, double outdB) {

		int xPt = GRAPHDIM - (int)((Math.abs(indB) * GRAPHDIM) / MINDB);
		int yPt = (int)((Math.abs(outdB) * GRAPHDIM) / MINDB);
		return new Point(xPt, yPt);
	}
	
	private void drawCross(Graphics g, Point p) {

		int halfCross = CROSSSIZE / 2;
		g.drawLine(p.x - halfCross, p.y, p.x + halfCross, p.y);
		g.drawLine(p.x, p.y - halfCross, p.x, p.y + halfCross);
	}
	
	// Called when user manipulates threshold value
	public void setThreshold(double threshold) {

		this.threshold = threshold;
		tPoint = mapDbToPoint(threshold, threshold);

		// Calculate new below threshold point on graph
		calcBelowThresholdPoint(belowThresholdRatio);

		// Calculate new above threshold point on graph
		calcAboveThresholdPoint(aboveThresholdRatio);
	}

	public void calcBelowThresholdPoint(double value) {

		double outDb;
		if (belowThresholdRatio < CompExpUI.MINBTRATIO)
			outDb = threshold - ((threshold + MINDB) / value);
		else
			outDb = threshold;

		btPoint = mapDbToPoint(outDb, -MINDB);
		repaint();
	}

	public void setBelowThresholdRatio(double belowThresholdRatio) {

		// Save ratio
		this.belowThresholdRatio = belowThresholdRatio;
		
		// Calculate new point on graph
		calcBelowThresholdPoint(belowThresholdRatio);
	}

	public void calcAboveThresholdPoint(double value) {

		if (value > 0.0) {
			// Expansion is occuring. Out is constrained to 0dB on graph
			if (value != CompExpUI.MAXATRATIO) {
				double slope = value / CompExpUI.MAXATRATIO;
				double inDb = threshold * slope;
				atPoint = mapDbToPoint(inDb, 0.0);
			}	else	{
				atPoint = mapDbToPoint(threshold, 0.0);
			}

		}	else	{
			// Compression is occuring. In is constrained to 0dB on graph
			if (value != CompExpUI.MINATRATIO) {
				double slope = Math.abs(value) / Math.abs(CompExpUI.MINATRATIO);
				double outDb = threshold * slope;
				atPoint = mapDbToPoint(0.0, outDb);
			}	else	{
				atPoint = mapDbToPoint(0.0, threshold);
			}
		}
		repaint();
	}

	public void setAboveThresholdRatio(double aboveThresholdRatio) {

		// Values posted are from MAXATRATIO to MINATRATIO
		this.aboveThresholdRatio = aboveThresholdRatio;

		// Calculate new point on graph
		calcAboveThresholdPoint(aboveThresholdRatio);
	}

	// Private class data
	private Point atPoint;	// Above threshold point
	private Point btPoint;	// Below threshold point
	private Point tPoint;	// Threshold point
	private double threshold;
	private double belowThresholdRatio;
	private double aboveThresholdRatio;
}
