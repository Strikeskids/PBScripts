package org.logicail.rsbot.scripts.testing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 10/07/2014
 * Time: 23:35
 */
public class BoundsUtilFrame extends JFrame {
	private JSpinner startX = new JSpinner(), startY = new JSpinner(), startZ = new JSpinner();
	private JSpinner stopX = new JSpinner(), stopY = new JSpinner(), stopZ = new JSpinner();

	public BoundsUtilFrame(final BoundsUtil util) throws HeadlessException {
		setLayout(new FlowLayout());

		JPanel a = new JPanel();

		final ChangeListener listener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				util.bounds((Integer) (startX.getValue()), (Integer) (stopX.getValue()), (Integer) (startY.getValue()), (Integer) (stopY.getValue()), (Integer) (startZ.getValue()), (Integer) (stopZ.getValue()));
			}
		};


		startX.addChangeListener(listener);
		startY.addChangeListener(listener);
		startZ.addChangeListener(listener);
		stopX.addChangeListener(listener);
		stopY.addChangeListener(listener);
		stopZ.addChangeListener(listener);

		a.add(startX);
		a.add(stopX);

		JPanel b = new JPanel();
		b.add(startY);
		b.add(stopY);

		JPanel c = new JPanel();
		c.add(startZ);
		c.add(stopZ);

		add(a);
		add(b);
		add(c);

		final ActionListener setBounds = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				util.bounds((Integer) (startX.getValue()), (Integer) (stopX.getValue()), (Integer) (startY.getValue()), (Integer) (stopY.getValue()), (Integer) (startZ.getValue()), (Integer) (stopZ.getValue()));
			}
		};

		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startX.setValue(-32);
				startY.setValue(-64);
				startZ.setValue(-32);
				stopX.setValue(32);
				stopY.setValue(0);
				stopZ.setValue(32);
			}
		});
		reset.addActionListener(setBounds);
		add(reset);

		JButton selecting = new JButton("Select");
		selecting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				util.select();
			}
		});
		add(selecting);

		JButton output = new JButton("Output");
		output.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("new int[] {" + startX.getValue() + ", " + stopX.getValue() + ", " + startY.getValue() + ", " + stopY.getValue() + ", " + startZ.getValue() + ", " + stopZ.getValue() + "}");
			}
		});
		add(output);

		pack();
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
}
