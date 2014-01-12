package org.logicail.rsbot.scripts.test;

import org.logicail.rsbot.scripts.test.data.Bolt;
import org.logicail.rsbot.scripts.test.data.Log;
import org.logicail.rsbot.scripts.test.data.Option;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 12/01/14
 * Time: 19:10
 */
public class Gui extends MethodProvider {

	private AbstractScript fletch;

	public Gui(MethodContext arg0, AbstractScript fletch) {
		super(arg0);
		this.fletch = fletch;
		components();
	}


	public void components() {

		Border raised;
		TitledBorder title;

		final JPanel panel1 = new JPanel();
		final JPanel panel2 = new JPanel();
		final JPanel panel3 = new JPanel();

		final JLabel label = new JLabel("Choose Log Type:");
		final JLabel choose = new JLabel("Choose Bow:");

		final JComboBox<Log> scroll = new JComboBox<Log>(Log.values());
		scroll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				//Fletcher.log = (Log) scroll.getSelectedItem();
				//fletch.selected = scroll.getSelectedItem().toString();
			}
		});

		final JComboBox<Option> type = new JComboBox<Option>(Option.values());
		type.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				/*FletchHandler.opt = (Option) type.getSelectedItem();
				fletch.bow = type.getSelectedItem().toString();
				if(type.getSelectedItem().equals(FletchHandler.opt.LONGBOW)) {
					fletch.longBow = true;
				}
				if(scroll.getSelectedItem().equals(Fletcher.log.LOG)) {
					fletch.widget = FletchHandler.opt.getWidget() + 2;
				} else {
					fletch.widget = FletchHandler.opt.getWidget();
				}*/
			}
		});

		final JRadioButton string = new JRadioButton("Stringing/Tipping");
		string.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				//fletch.stringBow = string.isSelected() ? true : false;
			}
		});

		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		panel2.add(label);
		panel2.add(scroll);
		panel2.add(choose);
		panel2.add(type);
		panel2.add(string);
		panel2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

		JLabel lab = new JLabel("Your AIO Experience");
		lab.setFont(new Font("Serif", Font.BOLD, 14));
		raised = BorderFactory.createRaisedBevelBorder();
		title = BorderFactory.createTitledBorder(raised, "pFletcher");
		title.setTitlePosition(TitledBorder.CENTER);
		lab.setBorder(title);
		panel1.add(lab);

		final JRadioButton show = new JRadioButton("Show Paint");
		show.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				//fletch.paint = show.isSelected() ? true : false;
			}
		});

		final JFrame main = new JFrame();
		main.setLayout(new BorderLayout());
		main.setSize(240, 250);
		main.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		main.setResizable(false);
		main.setVisible(true);

		final JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				main.dispose();
				//fletch.container = new JobContainer(new BoltTipping(ctx), new Interaction(ctx), new FletchHandler(ctx, fletch), new SelectWhichHandler(ctx), new Deposit(ctx, fletch), new Withdraw(ctx, fletch), new OpenBank(ctx, fletch));
			}
		});

		panel3.add(show);
		panel3.add(start);

		JPanel panel4 = new JPanel();
		panel4.add(panel1, BorderLayout.NORTH);
		panel4.add(panel2, BorderLayout.CENTER);
		panel4.add(panel3, BorderLayout.SOUTH);

		final JLabel stop = new JLabel("The script WON'T stop if you run out of supplies!");
		final JLabel info = new JLabel("Start with supplies INSIDE inventory. No banking!");

		final JComboBox<Bolt> cb = new JComboBox<Bolt>(Bolt.values());
		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				/*Fletcher.bolt = (Bolt) cb.getSelectedItem();
				fletch.selected = cb.getSelectedItem().toString();*/
			}
		});

		JPanel panel5 = new JPanel();
		panel5.setLayout(new FlowLayout());
		panel5.add(stop);
		panel5.add(cb);
		panel5.add(info);


		JTabbedPane tp = new JTabbedPane();
		tp.addTab("Fletching/Stringing", panel4);
		tp.addTab("Bolt Tipping", panel5);

		main.add(tp);
	}
}
