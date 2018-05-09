// 7 Segment Display Generation Class
// Written by: Craig A. Lindley
// Last Update: 03/22/99

// This class contains static methods used to calculate and
// render 7 segment LED displays.

package audiostuff.craigl.beans.displays;

import java.awt.*;

public class SevenSegmentDisplay {

	private static final boolean DRAWOFFSEGMENTS = true;
	private static final int BORDERPAD	  = 1; // Separator around edges
	private static final int SEPARATORPAD = 1; // Separator between segments
	private static final double SEGMENTWIDTHPERCENT = 0.12;

	// The following array has an element for each digit.
	// The bits in the integer control which segments of
	// the 7 segment display are on. Segments are numbered
	// as follows:
	//             0
	//          -------
	//          |     |
	//        1 |  3  | 2
	//           -----
	//          |     |
	//        4 |  6  | 5
	//          -------

	private static final int [] onSegments = {
		/* Bit/Seg Nums  654 3210 */
		/* Character 0 = 111 0111 */ 0x77,
		/* Character 1 = 010 0100 */ 0x24,
		/* Character 2 = 101 1101 */ 0x5d,
		/* Character 3 = 110 1101 */ 0x6d,
		/* Character 4 = 010 1110 */ 0x2e,
		/* Character 5 = 110 1011 */ 0x6b,
		/* Character 6 = 111 1011 */ 0x7b,
		/* Character 7 = 010 0101 */ 0x25,
		/* Character 8 = 111 1111 */ 0x7f,
		/* Character 9 = 010 1111 */ 0x2f
	};

	// Render a digit onto the device context. Assumes the polygons
	// for the segments have already been calculated for this size
	// of digit.
	public static void drawDigit(Graphics g, Polygon [] segments,
								 int digit, int width, int height,
								 Color ledBGColor,
								 Color ledOnColor, Color ledOffColor) {

		// Paint background first
		g.setColor(ledBGColor);
		g.fillRect(0, 0, width, height);

		// Get the segment bits for this digit
		int theOnSegments = onSegments[digit];

		// For all seven segments
		for (int index=0; index < 7; index++) {
			int mask = 1 << index;

			boolean segmentOn = ((theOnSegments & mask) != 0); 
			
			if (!DRAWOFFSEGMENTS && !segmentOn)
				continue;

			g.setColor(segmentOn ? ledOnColor : ledOffColor);
			Polygon segmentPolygon = segments[index];
			g.fillPolygon(segmentPolygon.xpoints,
						  segmentPolygon.ypoints,
						  segmentPolygon.npoints);
		}
	}

	// Generate the polygons for each segment of the display
	public static Polygon [] generateSegments(int width, int height) {

		Polygon [] segments = new Polygon[7];
		
		// Calculate various quantities
		double segmentWidth = width * SEGMENTWIDTHPERCENT;
		int halfSegmentWidth = (int) Math.round(segmentWidth / 2.0);
		int ext = (int) (1.414 * segmentWidth);
		int yCenter = height / 2;
		int yCenterP = yCenter + halfSegmentWidth;
		int yCenterM = yCenter - halfSegmentWidth;

		int sw = (int) segmentWidth;
		int hsw = halfSegmentWidth;
		int bp = BORDERPAD;
		int w = width;
		int h = height;

		int [] xPt = new int [6];
		int [] yPt = new int [6];

		// Segment 0
		xPt[0] = bp + SEPARATORPAD;
		yPt[0] = bp;
		xPt[1] = xPt[0] + ext;
		yPt[1] = yPt[0] + sw;
		xPt[2] = w - (bp + SEPARATORPAD + ext);
		yPt[2] = yPt[1];
		xPt[3] = w - (bp + SEPARATORPAD);
		yPt[3] = yPt[0];
		segments[0] = new Polygon(xPt, yPt, 4);

		// Segment 1
		xPt[0] = bp;
		yPt[0] = bp + SEPARATORPAD;
		xPt[1] = bp;
		yPt[1] = yCenter - SEPARATORPAD;
		xPt[2] = bp + sw;
		yPt[2] = yPt[1] - ext;
		xPt[3] = bp + sw;
		yPt[3] = yPt[0] + ext;
		segments[1] = new Polygon(xPt, yPt, 4);

		// Segment 2
		xPt[0] = w - (bp + sw);
		yPt[0] = bp + SEPARATORPAD + ext;
		xPt[1] = xPt[0];
		yPt[1] = yCenter - (SEPARATORPAD + ext);
		xPt[2] = w - bp;
		yPt[2] = yCenter - SEPARATORPAD;
		xPt[3] = xPt[2];
		yPt[3] = bp + SEPARATORPAD;
		segments[2] = new Polygon(xPt, yPt, 4);

		// Segment 3
		xPt[0] = bp + sw;
		yPt[0] = yCenterM;
		xPt[1] = bp + SEPARATORPAD;
		yPt[1] = yCenter;
		xPt[2] = bp + sw;
		yPt[2] = yCenterP;
		xPt[3] = w - (bp + sw);
		yPt[3] = yCenterP;
		xPt[4] = w - (bp + SEPARATORPAD);
		yPt[4] = yCenter;
		xPt[5] = w - (bp + sw);
		yPt[5] = yCenterM;
		segments[3] = new Polygon(xPt, yPt, 6);

		// Segment 4
		xPt[0] = bp;
		yPt[0] = yCenter + SEPARATORPAD;
		xPt[1] = bp;
		yPt[1] = h - (bp + SEPARATORPAD);
		xPt[2] = bp + sw;
		yPt[2] = yPt[1] - ext;
		xPt[3] = bp + sw;
		yPt[3] = yPt[0] + ext;
		segments[4] = new Polygon(xPt, yPt, 4);

		// Segment 5
		xPt[0] = w - (bp + sw);
		yPt[0] = yCenter + SEPARATORPAD + ext;
		xPt[1] = xPt[0];
		yPt[1] = h - (bp + SEPARATORPAD + ext);
		xPt[2] = w - bp;
		yPt[2] = h - (bp + SEPARATORPAD);
		xPt[3] = w - bp;
		yPt[3] = yCenter + SEPARATORPAD;
		segments[5] = new Polygon(xPt, yPt, 4);

		// Segment 6
		xPt[0] = bp + SEPARATORPAD + ext;
		yPt[0] = h - (bp + sw);
		xPt[1] = bp + SEPARATORPAD;
		yPt[1] = h - bp;
		xPt[2] = w - (bp + SEPARATORPAD);
		yPt[2] = h - bp;
		xPt[3] = xPt[2] - ext;
		yPt[3] = h - (bp + sw);
		segments[6] = new Polygon(xPt, yPt, 4);

		return segments;
	}
}

