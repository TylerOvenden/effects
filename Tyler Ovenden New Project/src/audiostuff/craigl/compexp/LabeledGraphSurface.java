// LabeledGraphSurface Class
// for use with the Compressor/Expander
// Written by: Craig A. Lindley
// Last Update: 05/19/99

package craigl.compexp;

import java.awt.*;
import craigl.utils.*;
import craigl.uiutils.*;

public class LabeledGraphSurface extends Panel {
	
	public static final Color TEXTCOLOR = Color.black;
	public static final int DEFCHARWIDTH = 9;
	public static final int DEFCHARHEIGHT = 15;

	// Inner class that creates a Panel with vertical lettering
	private static class OutPanel extends Panel {

		private final String PANELLABEL = "OUTPUT dB";
	
		public Dimension getPreferredSize() {

			if (fm == null)
				return new Dimension(LabeledGraphSurface.DEFCHARWIDTH,
								 GraphSurface.GRAPHGRIDS);
			else
				return new Dimension(fm.stringWidth("O"),
								 GraphSurface.GRAPHGRIDS);
		}
	
		public void paint(Graphics g) {

			Dimension d = getSize();

			if (fm == null) {
				f = g.getFont();
				f = new Font(f.getName(), f.getStyle(), 9);
				g.setFont(f); 

				fm = g.getFontMetrics();
				charHeight = fm.getHeight();
				charAscent = fm.getAscent();
				charWidth  = fm.charWidth('O');
			
				// Length of string
				chars = PANELLABEL.length();
			}
			g.setFont(f); 

			int totalHeight = chars * charHeight;
			int yOffset = (int) Math.round((d.height - totalHeight) / 2.0);
			int xOffset = (int) Math.round((d.width - charWidth) / 2.0);

			// Set the text color
			g.setColor(LabeledGraphSurface.TEXTCOLOR);
			for (int index=0; index < chars; index++) {
				g.drawString(PANELLABEL.substring(index, index + 1),
							 xOffset, yOffset);
				yOffset += charHeight;
			}
		}
		// Private class data
		private FontMetrics fm = null;
		private Font f;
		private int charHeight;
		private int charAscent;
		private int charWidth;
		private int chars;
	}

	// Inner class that creates a Panel with vertical numbering
	private static class VLabelPanel extends Panel {

		private final String [] labels = {
			"0", "-12", "-24", "-36", "-48", "-60", "-72", "-84"
		};

		private final String TESTSTRING = "-84";
	
		public Dimension getPreferredSize() {

			if (fm == null)
				return new Dimension(LabeledGraphSurface.DEFCHARWIDTH * 3,
									 GraphSurface.GRAPHGRIDS);
			else
				return new Dimension(fm.stringWidth(TESTSTRING), 
									 GraphSurface.GRAPHGRIDS);
		}

		public int calcOffset(String s) {

			return fm.stringWidth(s) / 2;
		}

		public void paint(Graphics g) {

			Dimension d = getSize();

			if (fm == null) {
				f = g.getFont();
				f = new Font(f.getName(), f.getStyle(), 9);
				g.setFont(f); 
				fm = g.getFontMetrics();
				charAscent = fm.getAscent();
				gridLabels = GraphSurface.GRAPHGRIDS;
			}
			g.setFont(f); 

			int yOffset = (3 * charAscent) / 4;
			yOffset += 2;

			int gridSize = d.height / gridLabels;

			// Set the text color
			g.setColor(LabeledGraphSurface.TEXTCOLOR);
			
			for (int i=0; i < labels.length; i++) {
				String label = labels[i]; 
				int xOffset = (d.width/2) - calcOffset(label);
				g.drawString(label, xOffset, yOffset);
				yOffset += gridSize;
			}
		}
	
		// Private class data
		private FontMetrics fm = null;
		private Font f;
		private int charAscent;
		private int gridLabels;
	}

	// Inner class that creates a Panel with horizontal lettering
	private static class InPanel extends Panel {

		private final String PANELLABEL = "INPUT dB";
		
		public Dimension getPreferredSize() {

			if (fm == null)
				return new Dimension(GraphSurface.GRAPHDIM,
									 LabeledGraphSurface.DEFCHARHEIGHT);
			else
				return new Dimension(GraphSurface.GRAPHDIM, charAscent);
		}

		public void paint(Graphics g) {

			Dimension d = getSize();

			if (fm == null) {
				f = g.getFont();
				f = new Font(f.getName(), f.getStyle(), 9);
				g.setFont(f); 
				fm = g.getFontMetrics();
				charAscent = fm.getAscent();
				
				// Length of string
				stringWidth = fm.stringWidth(PANELLABEL);
			}
			g.setFont(f); 
			int xOffset = (d.width/2)  - (stringWidth/2);
			int yOffset = (d.height/2) + (charAscent/2);

			// Set the text color
			g.setColor(LabeledGraphSurface.TEXTCOLOR);
			g.drawString(PANELLABEL, xOffset, yOffset);
		}
		
		// Private class data
		private FontMetrics fm = null;
		private Font f;
		private int charHeight;
		private int charAscent;
		private int charWidth;
		private int stringWidth;
	}

	// Inner class that creates a Panel with horizontal numbering
	private static class HLabelPanel extends Panel {

		private final String [] labels = {
			"-84", "-72", "-60", "-48", "-36", "-24", "-12", "0"
		};
		
		public Dimension getPreferredSize() {

			if (fm == null)
				return new Dimension(GraphSurface.GRAPHDIM,
									 LabeledGraphSurface.DEFCHARHEIGHT);
			else
				return new Dimension(GraphSurface.GRAPHDIM, charAscent);
		}

		public int calcOffset(String s) {

			return fm.stringWidth(s) / 2;
		}

		public void paint(Graphics g) {

			Dimension d = getSize();

			if (fm == null) {
				f = g.getFont();
				f = new Font(f.getName(), f.getStyle(), 9);
				g.setFont(f); 
				fm = g.getFontMetrics();
				charAscent = fm.getAscent();
				gridLabels = GraphSurface.GRAPHGRIDS;
			}
			g.setFont(f); 
			int yOffset = (d.height/2) + (charAscent/2);

			int gridSize = d.width / gridLabels;
			int xOffset = gridSize;

			// Set the text color
			g.setColor(LabeledGraphSurface.TEXTCOLOR);
			
			for (int i=0; i < labels.length; i++) {
				String label = labels[i]; 
				g.drawString(label, xOffset - calcOffset(label), yOffset);
				xOffset += gridSize;
			}
		}
		
		// Private class data
		private FontMetrics fm = null;
		private Font f;
		private int charAscent;
		private int gridLabels;
	}

	// Class Constructor
	public LabeledGraphSurface() {
		
		setBackground(AudioConstants.PANELCOLOR);

		// Use a grid bag for the panel
		Panel mp = new Panel();
		GridBagLayout gbl = new GridBagLayout(); 
		GridBagConstraints gbc = new GridBagConstraints();
		mp.setLayout(gbl);

		Panel vp = createVerticalPanel();
		BaseUI.addDefaultComponent(mp, vp, gbl, gbc, 0, 0, 4, 31);

		Panel hp = createHorizontalPanel();
		BaseUI.addDefaultComponent(mp, hp, gbl, gbc, 5, 32, 31, 4);

		gs = new GraphSurface();
		EtchedBorder eb = new EtchedBorder(gs); 
		BaseUI.addDefaultComponent(mp, eb, gbl, gbc, 5, 1, 30, 30);
		
		add(mp);
	}

	public Panel createVerticalPanel() {

		Panel p = new Panel();
		p.setLayout(new GridLayout(1, 2));
		p.add(new OutPanel());
		p.add(new VLabelPanel());
		return p;
	}

	public Panel createHorizontalPanel() {

		Panel p = new Panel();
		p.setLayout(new GridLayout(2, 1));
		p.add(new HLabelPanel());
		p.add(new InPanel());
		return p;
	}

	public GraphSurface getGraphSurface() {

		return gs;
	}
	
	// Private class data
	private GraphSurface gs;
}





