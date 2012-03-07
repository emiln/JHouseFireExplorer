import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


public class ErrorFrame extends JDialog {

    public static void spawnError() {
	spawnError("You dun goofed");
    }

    public static void spawnError(String errorMessage) {
	if (errorMessage == null) {
	    errorMessage = "You dun goofed";
	}
	ErrorFrame ef = new ErrorFrame(errorMessage);
	ef.setVisible(true);
    }

    public ErrorFrame(String errorMessage) {
	setLayout(null);
	setTitle("You dun goofed");
	setModal(true);
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	setResizable(false);

	setSize(300, 150);
	Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	int y = (int) size.getHeight()/2-getHeight()/2;
	int x = (int) size.getWidth()/2-getWidth()/2;
	setLocation(x, y);

	JLabel lblError = new JLabel("<html><p><center>" + errorMessage +
				     "</center></p></html>");
	lblError.setSize(290, 50);
	lblError.setHorizontalAlignment(JLabel.CENTER);
	lblError.setVerticalAlignment(JLabel.TOP);
	lblError.setLocation(5, 25);
	add(lblError);

	JButton btnOK = new JButton("OK");
	btnOK.setSize(100, 25);
	btnOK.setLocation(getWidth()/2-50, 75);
	btnOK.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent arg0) {
		    ErrorFrame.this.dispose();
		}
	    });
	add(btnOK);
    }
}
