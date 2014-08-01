package org.logicail.rsbot.scripts.rt4.osvarphelper;

import com.logicail.loader.rt4.wrappers.Script;
import com.logicail.loader.rt4.wrappers.loaders.ScriptDefinitionLoader;
import org.logicail.rsbot.scripts.framework.context.rt4.IClientContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 18/07/2014
 * Time: 17:36
 */
public class VarpHelperGUI extends JFrame {
	private final IClientContext ctx;
	DefaultListModel listModel;
	JTextArea historyText;
	JTextArea knownText;
	JTextArea area;
	FadeLabel label;
	AtomicBoolean paused = new AtomicBoolean();
	Timer fader = new Timer(200, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			float alpha = label.getAlpha();
			alpha += -0.05f;
			if (alpha < 0) {
				alpha = 0;
				label.setText("");
			}
			label.setAlpha(alpha);
		}
	});
	private final Font font = new Font("SansSerif", Font.PLAIN, 12);
	private final HashMap<Integer, Integer> previous = new HashMap<Integer, Integer>();
	private int selected = -1;

	HashMap<Integer, List<Script>> varpMap = new HashMap<Integer, List<Script>>();

	public void updateSelected() {
		if (selected > -1) {
			final Integer value = previous.get(selected);
			area.setText("Setting " + selected + "\nValue: " + value + "\nHex: 0x" + Integer.toHexString(value) + "\nBinary:" + Integer.toBinaryString(value));
			List<Script> list = varpMap.get(selected);
			if (list == null) {
				knownText.setText("");
			} else {
				StringBuilder sb = new StringBuilder();
				for (Script definition : list) {
					sb.append(definition.code()).append(" = ").append(definition.execute(ctx)).append("\n");
				}
				knownText.setText(sb.toString());
			}
		} else {
			area.setText("");
		}
	}

	public VarpHelperGUI(final IClientContext ctx) {
		this.ctx = ctx;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				ctx.controller.stop();
			}
		});

		ScriptDefinitionLoader loader = new ScriptDefinitionLoader(ctx.definitions.system());
		for (int i = 0; i < loader.size; i++) {
			if (!loader.canLoad(i)) {
				continue;
			}

			try {
				final Script script = loader.load(i);
				List<Script> list;
				if (!varpMap.containsKey(script.configId)) {
					list = new LinkedList<Script>();
					varpMap.put(script.configId, list);
				}
				list = varpMap.get(script.configId);
				list.add(script);
			} catch (IllegalArgumentException ignored) {
			}
		}

		setLayout(new BorderLayout());
		final JPanel left = new JPanel(new BorderLayout());
		{
			final JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(new EmptyBorder(1, 1, 1, 1));
			{
				listModel = new DefaultListModel();
				final JList list = new JList(listModel);
				list.setFont(font);
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						selected = list.getSelectedIndex();
						updateSelected();
					}
				});
				JScrollPane scroll = new JScrollPane(list);
				scroll.setPreferredSize(new Dimension(160, 150));
				scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				panel.add(scroll, BorderLayout.CENTER);

				JPanel button = new JPanel(new BorderLayout());
				button.setBorder(new EmptyBorder(1, 0, 0, 0));

				final JTextField gotoField = new JTextField("Goto...");
				gotoField.setFont(font);
				gotoField.setBackground(new Color(230, 230, 230));
				gotoField.addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(FocusEvent e) {
						if (gotoField.getText().equals("Goto...")) {
							gotoField.setText("");
						}
					}

					@Override
					public void focusLost(FocusEvent e) {
						if (gotoField.getText().isEmpty()) {
							gotoField.setText("Goto...");
						}
					}
				});
				gotoField.addKeyListener(new KeyListener() {
					@Override
					public void keyTyped(KeyEvent e) {
						char c = e.getKeyChar();
						if (c == KeyEvent.VK_ENTER) {
							int index = Integer.parseInt(gotoField.getText());
							if (index >= 0 && index < listModel.size()) {
								list.setSelectedIndex(index);
								list.ensureIndexIsVisible(index);
							}
						}
						if (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
							if (!(c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9')) {
								e.consume();
							}
						}
					}

					@Override
					public void keyPressed(KeyEvent e) {

					}

					@Override
					public void keyReleased(KeyEvent e) {

					}
				});

				button.add(gotoField, BorderLayout.CENTER);
				panel.add(button, BorderLayout.SOUTH);
			}
			left.add(panel, BorderLayout.CENTER);
		}
		{
			final JButton pause = new JButton("Pause");
			pause.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					paused.set(!paused.get());
					if (paused.get()) {
						pause.setText("Resume");
					} else {
						pause.setText("Pause");
					}
				}
			});
			pause.setFont(font);
			left.add(pause, BorderLayout.SOUTH);
		}
		add(left, BorderLayout.WEST);

		final JPanel main = new JPanel(new BorderLayout());
		{
			area = new JTextArea("", 4, 100);
			area.setFont(font);
			area.setEditable(false);
			main.add(area, BorderLayout.NORTH);

			historyText = new JTextArea("");
			historyText.setEditable(false);
			historyText.setFont(font);
			DefaultCaret caret = (DefaultCaret) historyText.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

			JScrollPane scroll = new JScrollPane(historyText);
			scroll.setAutoscrolls(true);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			knownText = new JTextArea("");
			knownText.setEditable(false);
			knownText.setFont(font);

			final JScrollPane known = new JScrollPane(knownText);
			known.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			final JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.addTab("History", scroll);
			tabbedPane.addTab("Known Varpbit Definitions", known);

			main.add(tabbedPane, BorderLayout.CENTER);

			final JPanel buttons = new JPanel(new GridBagLayout());
			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.gridwidth = 7;
				label = new FadeLabel();
				label.setFont(new Font("SansSerif", Font.PLAIN, 8));
				fader.start();
				buttons.add(label, gbc);

				gbc.gridy++;
				gbc.gridwidth = 6;
				gbc.weightx = 6;
				buttons.add(new JPanel(), gbc);

				gbc.gridwidth = 1;
				gbc.weightx = 1;

				gbc.gridx = 6;
				final JButton clear = new JButton("Clear");
				clear.setFont(font);
				clear.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						historyText.setText("");
					}
				});
				buttons.add(clear, gbc);
			}
			main.add(buttons, BorderLayout.SOUTH);
		}
		add(main, BorderLayout.CENTER);


		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(630, 345));
		setPreferredSize(new Dimension(630, 345));

		setLocationRelativeTo(null);
		setVisible(true);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!ctx.controller.isStopping()) {
					new VarpWorker(ctx, VarpHelperGUI.this, listModel, previous).execute();

					try {
						Thread.sleep(250);
					} catch (InterruptedException ignored) {
					}
				}
			}
		}).start();
	}


}
