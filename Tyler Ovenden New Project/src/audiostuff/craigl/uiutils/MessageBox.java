// Message Box Code
// Written by: Craig A. Lindley
// Last Update: 08/04/98

package audiostuff.craigl.uiutils;

import java.awt.*;
import java.awt.event.*;

public class MessageBox extends Dialog 
		implements ActionListener {

	public MessageBox(Frame parent, String title, String text) {

		// Create a modal dialog with parent window, title and text
		super(parent, title, true);

		Panel p1 = new Panel();
		// Instantiate label for display of text
		Label label = new Label(text, Label.CENTER);
		p1.add(label);
		add("North", p1);

		Panel p2 = new Panel();
		p2.setLayout(new FlowLayout(FlowLayout.CENTER));

		// Instantiate the OK button in the dialog box
        okButton = new Button("OK");
		p2.add(okButton);
		okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });
		add("South", p2);

        // Add a handler for closing dialog from menu
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

		pack();
  	}
  
	public MessageBox(Frame parent, String Text) {
		this(parent, "User Advisory", Text);
	}

	public void actionPerformed(ActionEvent ae) {
		dispose();
	}

	// Give OK button focus so enter will dismiss dialog
	public void doLayout() {

		okButton.requestFocus();
		super.doLayout();
	}

	public static void main(String [] args) {

		MessageBox mb = new MessageBox(new Frame(), "User Advisory",
			"File is not an AU or WAV file");
		mb.show();
	}

	// Private class data
	private Button okButton;
}
