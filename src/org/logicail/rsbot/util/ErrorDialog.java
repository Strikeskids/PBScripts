package org.logicail.rsbot.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 26/12/13
 * Time: 15:57
 */
public class ErrorDialog extends JDialog implements ActionListener {
	public ErrorDialog(String title, String message) {
		super((JFrame) null, title, true);
		setLocationRelativeTo(null);
		JPanel messagePane = new JPanel();
		messagePane.add(new JLabel("<html>" + message.replace("\n", "<br>") + "</html>"));
		getContentPane().add(messagePane);
		JPanel buttonPane = new JPanel();
		JButton button = new JButton("OK");
		buttonPane.add(button);
		button.addActionListener(this);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(200, 100));
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		setVisible(false);
		dispose();
	}
}
