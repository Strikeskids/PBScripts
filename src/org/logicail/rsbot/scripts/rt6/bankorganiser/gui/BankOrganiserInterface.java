package org.logicail.rsbot.scripts.rt6.bankorganiser.gui;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.logicail.rsbot.scripts.framework.context.rt6.providers.IBank;
import org.logicail.rsbot.scripts.framework.tasks.Node;
import org.logicail.rsbot.scripts.rt6.bankorganiser.LogBankOrganiser;
import org.logicail.rsbot.scripts.rt6.bankorganiser.tasks.MoveToTabTask;
import org.logicail.rsbot.scripts.rt6.bankorganiser.tasks.OpenBank;
import org.logicail.rsbot.scripts.rt6.bankorganiser.tasks.SortTabTask;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.gui.SortedListModel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/03/14
 * Time: 21:18
 */
public class BankOrganiserInterface extends JFrame {
	private static final String SETTINGS_INI = "LogBankOrganiser.ini";
	private static final int MIN_TAB_WIDTH = 190;
	private final LogBankOrganiser script;
	private JTabbedPane tabbedPane;

	private JButton startButton;
	private JComponent categoryTab;

	private final SortedListModel tab0model = new SortedListModel();
	private final DefaultListModel tab1model = new DefaultListModel();
	private final DefaultListModel tab2model = new DefaultListModel();
	private final DefaultListModel tab3model = new DefaultListModel();
	private final DefaultListModel tab4model = new DefaultListModel();
	private final DefaultListModel tab5model = new DefaultListModel();
	private final DefaultListModel tab6model = new DefaultListModel();
	private final DefaultListModel tab7model = new DefaultListModel();
	private final DefaultListModel tab8model = new DefaultListModel();
	private JList tab0;
	private final JList tab1 = new JList(tab1model);
	private final JList tab2 = new JList(tab2model);
	private final JList tab3 = new JList(tab3model);
	private final JList tab4 = new JList(tab4model);
	private final JList tab5 = new JList(tab5model);
	private final JList tab6 = new JList(tab6model);
	private final JList tab7 = new JList(tab7model);
	private final JList tab8 = new JList(tab8model);
	private boolean startPressed;
	private JButton loadButton;
	private JButton saveButton;
	private final List<JList> tabs = new ArrayList<JList>(8);

	public BankOrganiserInterface(LogBankOrganiser script) {
		this.script = script;
		initComponents();

		if (script != null) {
			setTitle(script.getName()/* + " v" + script.getVersion()*/);
		}
		setMinimumSize(new Dimension(750, 500));
		setMaximumSize(new Dimension(750, 500));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

		contentPane.add(getTopPanel(), BorderLayout.NORTH);
		contentPane.add(getCenterPanel(), BorderLayout.CENTER);
		contentPane.add(getBottomPanel(), BorderLayout.SOUTH);

		load();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JComponent getBottomPanel() {
		JPanel inner = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.insets = new Insets(2, 2, 2, 2);
		inner.add(loadButton, gbc);
		gbc.gridx++;
		inner.add(saveButton, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		inner.add(startButton, gbc);

		return inner;
	}

	private JComponent getCenterPanel() {
		JPanel inner = new JPanel(new BorderLayout());
		inner.setBorder(new EmptyBorder(10, 0, 10, 0));


		//tabbedPane.addTab("Categories", categoryTab);
//		tabbedPane.addTab("House", houseTab);
//		tabbedPane.addTab("Banking", bankTab);
//		tabbedPane.addTab("Summoning", getSummoningTab());
//		tabbedPane.addTab("Other", getOtherTab());


		JPanel instructions = new JPanel(new BorderLayout());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<strong><u>Instructions</u></strong>");
		sb.append("<p>\u2022 Drag or right click on categories to move them to your desired tab</p>");
		sb.append("<p>\u2022 Drag categories to reorder them within a tab</p>");
		sb.append("<p>\u2022 Each tab will be ordered with each category in alphabetical order,<br>&nbsp;&nbsp;&nbsp;&nbsp;i.e. Arrows, Bolts. Will order all the Arrows alphabetically followed by the Bolts alphabetically</p>");
		sb.append("<p>\u2022 Items that could not be categorised will be moved to \"Tab 0\"</p>");
		sb.append("<p></p>");
		sb.append("<strong><u>After clicking start</u></strong>");
		sb.append("<p>\u2022 The script will move all items to the correct tabs</p>");
		sb.append("<p>\u2022 Once the items are on the correct tabs it will then sort each tab</p>");
		sb.append("<br></html");
		final JLabel textArea = new JLabel(sb.toString());

		instructions.add(textArea, BorderLayout.CENTER);

		inner.add(instructions, BorderLayout.NORTH);

		inner.add(categoryTab, BorderLayout.CENTER);

		return inner;
	}

	private JComponent getTopPanel() {
		JPanel inner = new JPanel(new BorderLayout());
		inner.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(5, 5, 5, 5)));

		JLabel title = new JLabel(getTitle(), SwingConstants.CENTER);
		title.setFont(new Font("Tahoma", Font.BOLD, 24));

		inner.add(title, BorderLayout.CENTER);

		return inner;
	}

	private void initComponents() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (!startPressed) {
					if (script != null) {
						script.ctx().controller.stop();
					}
				}
			}
		});

		for (String category : script.itemCategoriser.getCategorys()) {
			//tab0model.addElement(prettyName(category));
			tab0model.addElement(category);
		}

		tabs.add(tab0 = new JList(tab0model));
		tabs.add(tab1);
		tabs.add(tab2);
		tabs.add(tab3);
		tabs.add(tab4);
		tabs.add(tab5);
		tabs.add(tab6);
		tabs.add(tab7);
		tabs.add(tab8);

		for (int i = 0; i < tabs.size(); i++) {
			JList tab = tabs.get(i);
			tab.addMouseListener(new MenuAdapter(this, tab, tabs, i));
			tab.setTransferHandler(new ListTransferHandler());
			tab.setDropMode(DropMode.INSERT);
			tab.setDragEnabled(true);
		}

		tabbedPane = new JTabbedPane();
		startButton = new JButton("Start Script");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});
		loadButton = new JButton("Load Settings");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		saveButton = new JButton("Save Settings");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		categoryTab = getGeneralTab();
	}

	private JComponent getGeneralTab() {
		JPanel panel = new JPanel(new BorderLayout());

		JPanel uncategorised = new JPanel(new BorderLayout());
		uncategorised.setBorder(new CompoundBorder(new TitledBorder("Tab 0 (uncategorised)"), new EmptyBorder(5, 5, 5, 5)));
		JScrollPane uncategorisedScrollPane = new JScrollPane(tab0);
		uncategorised.setPreferredSize(new Dimension(MIN_TAB_WIDTH, 100));
		uncategorised.setMinimumSize(new Dimension(MIN_TAB_WIDTH, 100));
		uncategorised.add(uncategorisedScrollPane);
		panel.add(uncategorised, BorderLayout.WEST);

		JPanel tabs = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 1;
		constraints.weightx = 1;

		addTab(tabs, constraints, "Tab 1", tab1);
		addTab(tabs, constraints, "Tab 2", tab2);
		addTab(tabs, constraints, "Tab 3", tab3);
		addTab(tabs, constraints, "Tab 4", tab4);
		addTab(tabs, constraints, "Tab 5", tab5);
		addTab(tabs, constraints, "Tab 6", tab6);
		addTab(tabs, constraints, "Tab 7", tab7);
		addTab(tabs, constraints, "Tab 8", tab8);

		JPanel scroll = new JPanel(new BorderLayout());
		scroll.setBorder(null);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setViewportView(tabs);
		scrollPane.setPreferredSize(new Dimension(400, 400));
		scroll.add(scrollPane, BorderLayout.CENTER);

		panel.add(scroll, BorderLayout.CENTER);

		tabs.setPreferredSize(new Dimension(8 * MIN_TAB_WIDTH, 100));
		tabs.setMinimumSize(new Dimension(8 * MIN_TAB_WIDTH, 100));

		return panel;
	}

	private void addTab(JPanel inner, GridBagConstraints constraints, String title, JList tab) {
		constraints.gridx++;
		{
			JPanel titlePanel = new JPanel(new BorderLayout());
			titlePanel.setBorder(new CompoundBorder(new TitledBorder(title), new EmptyBorder(5, 5, 5, 5)));
			JScrollPane scrollPane = new JScrollPane(tab, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			titlePanel.setPreferredSize(new Dimension(50, 50));
			titlePanel.add(scrollPane);
			inner.add(titlePanel, constraints);
		}
	}

	private void save() {
		if (script == null) {
			return;
		}

		final Properties settings = new Properties();
		LinkedHashMap<Integer, List<String>> map = getTabContents(1);

		JsonObject object = new JsonObject();
		int i = 1;
		for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
			JsonArray values = new JsonArray();
			for (String s : entry.getValue()) {
				values.add(s);
			}
			object.add(String.valueOf(i), values);
			i++;
		}

		settings.put("Tabs", object.toString());

		try {
			settings.store(new FileOutputStream(new File(script.getStorageDirectory(), SETTINGS_INI)), "Log Bank Organiser");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void start() {
		if (script == null) {
			return;
		}

		script.tree.clear();

		script.status = "";

		List<Node<LogBankOrganiser>> nodes = new ArrayList<Node<LogBankOrganiser>>();

		nodes.add(new OpenBank(script));

		final IBank.BankTab[] bankTabs = IBank.BankTab.values();

		List<LinkedHashSet<Integer>> result = new ArrayList<LinkedHashSet<Integer>>();

		LinkedHashMap<Integer, List<String>> map = getTabContents(0);
		int i = 0;
		for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
			if (i > 0 && entry.getValue().isEmpty()) {
				continue;
			}
			result.add(script.itemCategoriser.getData(entry.getValue()));
			i++;
		}

		final MoveToTabTask moveToTabTask = new MoveToTabTask(script, result);
		nodes.add(moveToTabTask);
		nodes.add(new SortTabTask(script, moveToTabTask));

		for (Node<LogBankOrganiser> node : nodes) {
			script.tree.add(node);
		}

		startPressed = true;
		dispose();
	}

	private LinkedHashMap<Integer, List<String>> getTabContents(int start) {
		LinkedHashMap<Integer, List<String>> map = new LinkedHashMap<Integer, List<String>>();

		for (int i = start; i < tabs.size(); i++) {
			JList tab = tabs.get(i);
			DefaultListModel model = (DefaultListModel) tab.getModel();
			if (!map.containsKey(i)) {
				map.put(i, new ArrayList<String>());
			}
			for (Object o : model.toArray()) {
				map.get(i).add(String.valueOf(o));
			}
		}
		return map;
	}

	private void load() {
		if (script == null) {
			return;
		}

		// Reset everything to tab0

		for (int i = 1; i < tabs.size(); i++) {
			JList tab = tabs.get(i);
			int[] indices = new int[tab.getModel().getSize()];
			for (int j = 0; j < indices.length; j++) {
				indices[j] = j;
			}
			tab.setSelectedIndices(indices);
			moveToTab(tab, tab0);
		}

		Properties settings = new Properties();
		try {
			final File file = new File(script.getStorageDirectory(), SETTINGS_INI);
			if (file.exists()) {
				settings.load(new FileInputStream(file));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (settings.containsKey("Tabs")) {
			final String json = settings.get("Tabs").toString();
			final JsonObject object = JsonObject.readFrom(json);

			for (int i = 1; i < tabs.size(); i++) {
				JList tab = tabs.get(i);
				final JsonValue value = object.get(String.valueOf(i));
				if (value == null || !value.isArray()) {
					continue;
				}
				final JsonArray array = value.asArray();
				for (JsonValue v : array) {
					final String string = v.asString();

					// find it
					Object[] array1 = tab0model.toArray();
					for (int x = 0; x < array1.length; x++) {
						Object o = array1[x];
						if (o.equals(string)) {
							tab0.setSelectedIndex(x);
							moveToTab(tab0, tab);
							break;
						}
					}
				}
			}
		}
	}

	public void moveToTab(JList source, JList destination) {
		for (Object o : source.getSelectedValues()) {
			final ListModel sourceModel = source.getModel();
			final ListModel destinationModel = destination.getModel();

			if (remove(sourceModel, o)) {
				add(destinationModel, o);
			}
		}

		source.clearSelection();
	}

	private void add(ListModel model, Object element) {
		((DefaultListModel) model).addElement(element);
	}

	private boolean remove(ListModel model, Object element) {
		return ((DefaultListModel) model).removeElement(element);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				BankOrganiserInterface gui = new BankOrganiserInterface(null);
				gui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				gui.setVisible(true);
			}
		});
	}
}
