package org.logicail.rsbot.scripts.rt6.loggildedaltar.gui;

import org.logicail.rsbot.scripts.framework.tasks.Task;
import org.logicail.rsbot.scripts.framework.tasks.impl.AnimationMonitor;
import org.logicail.rsbot.scripts.framework.tasks.impl.AntiBan;
import org.logicail.rsbot.scripts.framework.tasks.impl.LogoutIdle;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltar;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.LogGildedAltarOptions;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.*;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.banking.Banking;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.Path;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.tasks.pathfinding.house.LeaveHouse;
import org.logicail.rsbot.scripts.rt6.loggildedaltar.wrapper.Offering;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.Skills;
import org.powerbot.script.rt6.Summoning;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 28/10/12
 * Time: 12:49
 */
public class LogGildedAltarGUI extends JFrame {
	private final DefaultListModel houseDisabledModel = new SortedListModel();
	private final DefaultListModel houseEnabledModel = new DefaultListModel();
	private final DefaultListModel bankDisabledModel = new SortedListModel();
	private final DefaultListModel bankEnabledModel = new DefaultListModel();
	private final LogGildedAltar script;
	private final LogGildedAltarOptions options;
	private final Summoning.Familiar[] familiars = {Summoning.Familiar.BULL_ANT, Summoning.Familiar.SPIRIT_TERRORBIRD, Summoning.Familiar.WAR_TORTOISE, Summoning.Familiar.PACK_YAK/*, Summoning.Familiar.CLAN_AVATAR*/};
	private final Map<String, Summoning.Familiar> familiarMap = new LinkedHashMap<String, Summoning.Familiar>(familiars.length);
	// Variable declaration
	public boolean startPressed;
	private JTabbedPane tabbedPane;
	private JComponent generalTab;
	private JComponent houseTab;
	private JComponent bankTab;
	private JButton loadButton;
	private JButton startButton;
	private JButton saveButton;
	private JComboBox comboBoxOffering;
	private JComboBox comboBoxHouseMode;
	private JTextField friendName;
	private JTextField ignoredPlayers;
	private JCheckBox lightBurners;
	private JList disabledHouse;
	private JList enabledHouse;
	private JButton addHouse;
	private JButton removeHouse;
	private JList disabledBank;
	private JList enabledBank;
	private JButton addBank;
	private JButton removeBank;
	//private JCheckBox screenshotCheckbox;
	private JCheckBox stopOfferingCheckbox;
	//private JButton screenshotFolderButton;
	private JCheckBox enableSummoning;
	private JComboBox comboBoxBOB;
	private JCheckBox houseRechargeCheckbox;
	private JCheckBox summoningPotionCheckbox;
	//private JSlider mouseSpeedSlider;
	//private JCheckBox mouseSpeedCheckBox;
	//private JCheckBox enableAura;
	//private JComboBox<MyAuras.Aura> comboBoxAura;
	private JCheckBox stopLevelCheckbox;
	private JSpinner stopLevelSpinner;

	public LogGildedAltarGUI(LogGildedAltar script) {
		this.script = script;
		options = this.script.options;
		initComponents();
		setTitle(script.getName() + " v" + script.version());
		setMinimumSize(new Dimension(480, 550));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		for (Path p : Path.values()) {
			if (p.isEnabledInList()) {
				switch (p.getPathType()) {
					case BANK:
						if (p.isEnabledByDefault())
							bankEnabledModel.addElement(p);
						else
							bankDisabledModel.addElement(p);
						break;
					case HOME:
						if (p.isEnabledByDefault())
							houseEnabledModel.addElement(p);
						else
							houseDisabledModel.addElement(p);
						break;
				}
			}
		}

		// Sort disabled models alphbetically


		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

		contentPane.add(getTopPanel(), BorderLayout.NORTH);
		contentPane.add(getCenterPanel(), BorderLayout.CENTER);
		contentPane.add(getBottomPanel(), BorderLayout.SOUTH);

		loadActionPerformed();

		pack();
		setVisible(true);
		setLocationRelativeTo(null);
	}

	private static List<Path> modelToPathList(DefaultListModel model) {
		final List<Path> list = new ArrayList<Path>();
		final Enumeration<?> elements = model.elements();
		while (elements.hasMoreElements()) {
			final Object o = elements.nextElement();
			if (o instanceof Path) {
				list.add((Path) o);
			}
		}
		return list;
	}

	private JComponent getBottomPanel() {
		JPanel inner = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JLabel label = new JLabel("Start at one of your enabled banked, have required items at top of the bank.", JLabel.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		inner.add(label, gbc);
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

		tabbedPane.addTab("General", generalTab);
		tabbedPane.addTab("House", houseTab);
		tabbedPane.addTab("Banking", bankTab);
		tabbedPane.addTab("Summoning", getSummoningTab());
		tabbedPane.addTab("Other", getOtherTab());

		inner.add(tabbedPane, BorderLayout.CENTER);
		return inner;
	}

	JComponent getOtherTab() {
		JPanel inner = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.weightx = 1;
		constraints.gridy = 0;
		{
		    /*
		    {
				JPanel mouse = new JPanel(new GridBagLayout());
				mouse.setBorder(new CompoundBorder(new TitledBorder("Mouse Speed"), new EmptyBorder(5, 5, 5, 5)));
				GridBagConstraints mouseConstraints = new GridBagConstraints();
				mouseConstraints.fill = GridBagConstraints.HORIZONTAL;
				mouseConstraints.weightx = 1;
				mouseConstraints.gridx = 0;
				mouseConstraints.gridy = 0;
				{
					mouse.add(mouseSpeedCheckBox, mouseConstraints);
				}

				mouseConstraints.gridy++;
				{
					mouse.add(mouseSpeedSlider, mouseConstraints);
				}
				inner.add(mouse, constraints);
			}*/

			/*constraints.gridy++;
			{
				JPanel panel = new JPanel(new GridBagLayout());
				panel.setBorder(new CompoundBorder(new TitledBorder("Aura"), new EmptyBorder(5, 5, 5, 5)));
				GridBagConstraints panelConstraints = new GridBagConstraints();
				panelConstraints.fill = GridBagConstraints.HORIZONTAL;
				panelConstraints.weightx = 1;
				panelConstraints.gridx = 0;
				panelConstraints.gridy = 0;
				panelConstraints.gridwidth = 1;

				panel.add(enableAura, panelConstraints);
				panelConstraints.weightx = 0.5;
				panelConstraints.gridx++;
				//panel.add(comboBoxAura, panelConstraints);

				inner.add(panel, constraints);
			}*/
/*
			constraints.gridy++;
			{
				JPanel screenshots = new JPanel(new GridBagLayout());
				screenshots.setBorder(new CompoundBorder(new TitledBorder("Screenshots"), new EmptyBorder(5, 5, 5, 5)));
				GridBagConstraints screenshotConstraints = new GridBagConstraints();
				screenshotConstraints.fill = GridBagConstraints.HORIZONTAL;
				screenshotConstraints.weightx = 1;
				screenshotConstraints.weighty = 1;
				screenshotConstraints.gridx = 0;
				screenshotConstraints.gridy = 0;
				{
					screenshots.add(screenshotCheckbox, screenshotConstraints);
					screenshotConstraints.gridx++;
					screenshotConstraints.weightx = 0.25;
					screenshots.add(screenshotFolderButton, screenshotConstraints);
				}

				inner.add(screenshots, constraints);
			}
*/
			constraints.gridy++;
			{
				JPanel panel = new JPanel(new GridBagLayout());
				panel.setBorder(new CompoundBorder(new TitledBorder("Burners not lit"), new EmptyBorder(5, 5, 5, 5)));
				GridBagConstraints screenshotConstraints = new GridBagConstraints();
				screenshotConstraints.fill = GridBagConstraints.HORIZONTAL;
				screenshotConstraints.weightx = 1;
				screenshotConstraints.weighty = 1;
				screenshotConstraints.gridx = 0;
				screenshotConstraints.gridy = 0;
				{
					panel.add(stopOfferingCheckbox, screenshotConstraints);
				}

				inner.add(panel, constraints);
			}

			constraints.weighty = 1;
			constraints.gridy++;
			{
				JPanel panel = new JPanel(new GridBagLayout());
				panel.setBorder(new CompoundBorder(new TitledBorder("Stop at level"), new EmptyBorder(5, 5, 5, 5)));
				GridBagConstraints stopConstraints = new GridBagConstraints();
				stopConstraints.fill = GridBagConstraints.HORIZONTAL;
				stopConstraints.weightx = 1;
				stopConstraints.weighty = 1;
				stopConstraints.gridx = 0;
				stopConstraints.gridy = 0;
				{
					panel.add(stopLevelCheckbox, stopConstraints);
					stopConstraints.gridx++;
					stopConstraints.weightx = 0.25;
					panel.add(stopLevelSpinner, stopConstraints);
				}

				inner.add(panel, constraints);
			}
		}
		return inner;
	}

	JComponent getSummoningTab() {
		JPanel inner = new JPanel(new GridBagLayout());
		inner.setBorder(new EmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		{
			JPanel panel = new JPanel(new GridBagLayout());
			panel.setBorder(new CompoundBorder(new TitledBorder(""), new EmptyBorder(5, 5, 5, 5)));
			GridBagConstraints panelConstraints = new GridBagConstraints();
			panelConstraints.fill = GridBagConstraints.HORIZONTAL;
			panelConstraints.weightx = 0.1;
			panelConstraints.gridx = 0;
			panelConstraints.gridy = 0;
			panelConstraints.gridwidth = 2;

			panel.add(enableSummoning, panelConstraints);
			panelConstraints.gridy++;
			panel.add(houseRechargeCheckbox, panelConstraints);
			panelConstraints.gridy++;
			panel.add(summoningPotionCheckbox, panelConstraints);
			panelConstraints.gridy++;

			panelConstraints.gridwidth = 1;
			panel.add(new JLabel("Familiar:"), panelConstraints);
			panelConstraints.weightx = 1;
			panelConstraints.gridx++;
			panel.add(comboBoxBOB, panelConstraints);

			inner.add(panel, constraints);
		}
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
						script.ctx.controller.stop();
					}
				}
			}
		});

		for (Summoning.Familiar familiar : familiars) {
			familiarMap.put(String.format("%s (%d)", prettyName(familiar.name()), familiar.requiredLevel()), familiar);
		}

		// General
		comboBoxHouseMode = new JComboBox(new String[]{
				"Use own altar",
				"Use friends altar",
				"Detect open houses (w31, yanille)"}
		);
		comboBoxHouseMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				friendName.setEnabled(comboBoxHouseMode.getSelectedIndex() == 1);
				int selected = comboBoxHouseMode.getSelectedIndex();
				if (selected > 1) {
					lightBurners.setSelected(false);
				}
				ignoredPlayers.setEnabled(selected == 2);
			}
		});

		friendName = new JTextField("friend1,friend2");
		friendName.setEnabled(false);
		ignoredPlayers = new JTextField("name1, name2");
		ignoredPlayers.setEnabled(false);
		lightBurners = new JCheckBox("Light incense burners (2 clean marrentil per trip, won't offer bones unless 2 burners are lit)", true);

		comboBoxOffering = new JComboBox(Offering.values());

		// House
		disabledHouse = new JList(houseDisabledModel);
		enabledHouse = new JList(houseEnabledModel);
		addHouse = new JButton(">");
		addHouse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				add(disabledHouse, houseDisabledModel, houseEnabledModel);
			}
		});
		removeHouse = new JButton("<");
		removeHouse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remove(enabledHouse, houseDisabledModel, houseEnabledModel);
			}
		});

		// Banking
		disabledBank = new JList(bankDisabledModel);
		enabledBank = new JList(bankEnabledModel);
		addBank = new JButton(">");
		addBank.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				add(disabledBank, bankDisabledModel, bankEnabledModel);
			}
		});
		removeBank = new JButton("<");
		removeBank.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remove(enabledBank, bankDisabledModel, bankEnabledModel);
			}
		});
		disabledBank.setCellRenderer(new ListRenderer());
		enabledBank.setCellRenderer(new ListRenderer());

		// Summoning
		enableSummoning = new JCheckBox("Enable summoning");
		enableSummoning.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				final boolean enable = e.getStateChange() == ItemEvent.SELECTED;
				houseRechargeCheckbox.setEnabled(enable);
				summoningPotionCheckbox.setEnabled(enable);
				comboBoxBOB.setEnabled(enable);
			}
		});
		houseRechargeCheckbox = new JCheckBox("Only recharge summoning points at house obelisk");
		houseRechargeCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					summoningPotionCheckbox.setSelected(false);
				}
			}
		});
		summoningPotionCheckbox = new JCheckBox("Only recharge summoning points using summoning potions (inc. flasks)");
		summoningPotionCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					houseRechargeCheckbox.setSelected(false);
				}
			}
		});
		comboBoxBOB = new JComboBox(familiarMap.keySet().toArray(new String[familiarMap.size()]));

		houseRechargeCheckbox.setEnabled(false);
		summoningPotionCheckbox.setEnabled(false);
		comboBoxBOB.setEnabled(false);

		/*
		screenshotCheckbox = new JCheckBox("Save screenshot when script stops");
		screenshotFolderButton = new JButton("Open Folder");
		screenshotFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(Environment.getStorageDirectory());
				} catch (IOException ignored) {
				}
			}
		});
		*/
		stopOfferingCheckbox = new JCheckBox("Stop offering bones if burners go out", true);
/*
		comboBoxAura = new JComboBox<>(new MyAuras.Aura[]{
				MyAuras.Aura.CORRUPTION,
				MyAuras.Aura.HARMONY,
				MyAuras.Aura.SALVATION,
				MyAuras.Aura.GREATER_CORRUPTION,
				MyAuras.Aura.GREATER_HARMONY,
				MyAuras.Aura.GREATER_SALVATION,
				MyAuras.Aura.MASTER_CORRUPTION,
				MyAuras.Aura.MASTER_HARMONY,
				MyAuras.Aura.MASTER_SALVATION,
				MyAuras.Aura.SUPREME_CORRUPTION,
				MyAuras.Aura.SUPREME_HARMONY,
				MyAuras.Aura.SUPREME_SALVATION
		});
*/
		//enableAura = new JCheckBox("Enable Aura");

		stopLevelCheckbox = new JCheckBox("Stop at level", false);
		stopLevelCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				stopLevelSpinner.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		stopLevelSpinner = new JSpinner(new SpinnerNumberModel(/*Math.min(Math.max(1, Skills.getLevel(Skills.PRAYER)) + 1, 99)*/99, 1, 99, 1));
		stopLevelSpinner.setEnabled(false);

		// Bottom
		startButton = new JButton("Start Script");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startButtonActionPerformed();
			}
		});
		loadButton = new JButton("Load Settings");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadActionPerformed();
			}
		});
		saveButton = new JButton("Save Settings");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveActionPerformed();
			}
		});

		tabbedPane = new JTabbedPane();
		generalTab = getGeneralTab();
		houseTab = getHouseTab();
		bankTab = getBankingTab();
	}

	private void add(JList listDisabled, DefaultListModel disabledModel, DefaultListModel enabledModel) {
		int index = listDisabled.getSelectedIndex();
		if (index > -1) {
			Path selectedValue = (Path) listDisabled.getSelectedValue();
			enabledModel.addElement(selectedValue);
			disabledModel.removeElement(selectedValue);
			if (index > 0 && index >= disabledModel.getSize())
				listDisabled.setSelectedIndex(index - 1);
			else
				listDisabled.setSelectedIndex(index);
		}
	}

	JComponent getBankingTab() {
		JPanel inner = new JPanel(new GridBagLayout());
		GridBagConstraints innerConstraints = new GridBagConstraints();
		innerConstraints.fill = GridBagConstraints.BOTH;
		innerConstraints.gridx = 0;
		innerConstraints.gridy = 0;
		innerConstraints.weightx = 1;

		innerConstraints.gridwidth = 3;
		{
			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			constraints.gridy = 0;
			panel.add(new JLabel("You must have a failsafe method enabled (in case script forgets an item while banking)", JLabel.CENTER), constraints);
			constraints.gridy++;
			panel.add(new JLabel("[F] = Failsafe method (should never fail)", JLabel.CENTER), constraints);
			constraints.gridy++;
			JLabel label = new JLabel("Green has obelisk", JLabel.CENTER);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			label.setForeground(new Color(0, 102, 51));
			panel.add(label, constraints);
			inner.add(panel, innerConstraints);
		}

		innerConstraints.weighty = 1;
		innerConstraints.gridwidth = 1;
		innerConstraints.gridy++;
		{
			JPanel disabledTitle = new JPanel(new BorderLayout());
			disabledTitle.setBorder(new CompoundBorder(new TitledBorder("Disabled Methods"), new EmptyBorder(5, 5, 5, 5)));

			JScrollPane scrollPane = new JScrollPane(disabledBank);
			disabledTitle.setPreferredSize(new Dimension(50, 50));
			disabledTitle.add(scrollPane);
			inner.add(disabledTitle, innerConstraints);
		}

		innerConstraints.gridx++;
		innerConstraints.weightx = 0;
		{
			JPanel centerPane = new JPanel(new GridBagLayout());
			GridBagConstraints centerConstraints = new GridBagConstraints();
			centerConstraints.fill = GridBagConstraints.HORIZONTAL;

			centerConstraints.gridy = 0;
			centerPane.add(addBank, centerConstraints);

			centerConstraints.gridy = 1;
			centerPane.add(removeBank, centerConstraints);

			inner.add(centerPane, innerConstraints);
		}

		innerConstraints.gridx++;
		innerConstraints.weightx = 1;
		{
			JPanel enabledTitle = new JPanel(new BorderLayout());
			enabledTitle.setBorder(new CompoundBorder(new TitledBorder("Enabled Methods"), new EmptyBorder(5, 5, 5, 5)));

			enabledBank.setVisibleRowCount(-1);
			enabledBank.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			enabledBank.setTransferHandler(new TransferHandler() {
				@Override
				public boolean canImport(TransferSupport support) {
					if (!support.isDrop()) {
						return false;
					}

					support.setShowDropLocation(true);

					JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
					return dl.getIndex() >= 0 && dl.getIndex() <= enabledBank.getModel().getSize();
				}

				@Override
				public boolean importData(TransferSupport support) {
					if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						return false;
					}

					JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
					int index = dl.getIndex();

					java.util.List list = Arrays.asList(enabledBank.getSelectedValues());
					for (int i = list.size() - 1; i >= 0; i--) {
						final Object o = list.get(i);
						if (index > bankEnabledModel.indexOf(o)) {
							index--;
						}
						bankEnabledModel.removeElement(o);
						bankEnabledModel.insertElementAt(o, index);
						index = bankEnabledModel.indexOf(o);
					}

					return true;
				}

				@Override
				public int getSourceActions(JComponent component) {
					return MOVE;
				}

				@Override
				protected Transferable createTransferable(JComponent component) {
					return new StringSelection("");
				}
			});

			enabledBank.setDropMode(DropMode.INSERT);
			enabledBank.setDragEnabled(true);

			JScrollPane scrollPane = new JScrollPane(enabledBank);
			enabledTitle.setPreferredSize(new Dimension(50, 50));

			enabledTitle.add(scrollPane, BorderLayout.CENTER);

			enabledTitle.add(new JLabel("Drag to set priority (highest at top)", JLabel.CENTER), BorderLayout.SOUTH);

			inner.add(enabledTitle, innerConstraints);
		}

		return inner;
	}

	private JComponent getGeneralTab() {
		JPanel generalPane = new JPanel(new GridBagLayout());
		GridBagConstraints generalPaneConstraints = new GridBagConstraints();
		generalPaneConstraints.fill = GridBagConstraints.HORIZONTAL;
		generalPaneConstraints.anchor = GridBagConstraints.NORTH;
		generalPaneConstraints.weightx = 1;
		generalPaneConstraints.gridx = 0;
		generalPaneConstraints.gridy = 0;
		{
			JPanel houseModePane = new JPanel(new GridBagLayout());
			houseModePane.setBorder(new CompoundBorder(new TitledBorder("House Mode"), new EmptyBorder(5, 5, 5, 5)));
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.weightx = 0.1;

			{
				houseModePane.add(new JLabel("Mode:"), constraints);
				constraints.gridx++;
				constraints.weightx = 1;
				houseModePane.add(comboBoxHouseMode, constraints);
			}

			constraints.insets = new Insets(5, 0, 0, 0);
			constraints.gridx = 0;
			constraints.gridy++;
			constraints.weightx = 0.1;
			{
				houseModePane.add(new JLabel("Friends name:"), constraints);
				constraints.gridx++;
				constraints.weightx = 1;
				houseModePane.add(friendName, constraints);
			}

			constraints.gridx = 0;
			constraints.gridy++;
			constraints.weightx = 0.1;
			{
				houseModePane.add(new JLabel("Ignore:"), constraints);
				constraints.gridx++;
				constraints.weightx = 1;
				houseModePane.add(ignoredPlayers, constraints);
			}

			constraints.gridx = 0;
			constraints.weightx = 0.1;
			constraints.gridy++;
			{
				constraints.gridwidth = 2;
				houseModePane.add(lightBurners, constraints);
			}

			generalPane.add(houseModePane, generalPaneConstraints);
		}

		generalPaneConstraints.weighty = 0.1;
		generalPaneConstraints.gridy++;
		{
			JPanel offeringPane = new JPanel(new GridBagLayout());
			offeringPane.setBorder(new CompoundBorder(new TitledBorder("Offering"), new EmptyBorder(5, 5, 5, 5)));
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.weightx = 0.1;

			{
				offeringPane.add(new JLabel("Bone Type:"), constraints);
				constraints.gridx++;
				constraints.weightx = 1;
				offeringPane.add(comboBoxOffering, constraints);
			}

			generalPane.add(offeringPane, generalPaneConstraints);
		}

		return generalPane;
	}

	JComponent getHouseTab() {
		JPanel inner = new JPanel(new GridBagLayout());
		GridBagConstraints innerConstraints = new GridBagConstraints();
		innerConstraints.fill = GridBagConstraints.BOTH;
		innerConstraints.gridx = 0;
		innerConstraints.gridy = 0;
		innerConstraints.weightx = 1;
		innerConstraints.weighty = 1;

		{
			JPanel disabledTitle = new JPanel(new BorderLayout());
			disabledTitle.setBorder(new CompoundBorder(new TitledBorder("Disabled Methods"), new EmptyBorder(5, 5, 5, 5)));

			JScrollPane scrollPane = new JScrollPane(disabledHouse);
			disabledTitle.setPreferredSize(new Dimension(50, 50));
			disabledTitle.add(scrollPane);
			inner.add(disabledTitle, innerConstraints);
		}

		innerConstraints.gridx++;
		innerConstraints.weightx = 0;
		{
			JPanel centerPane = new JPanel(new GridBagLayout());
			GridBagConstraints centerConstraints = new GridBagConstraints();
			centerConstraints.fill = GridBagConstraints.HORIZONTAL;

			centerConstraints.gridy = 0;
			centerPane.add(addHouse, centerConstraints);

			centerConstraints.gridy = 1;
			centerPane.add(removeHouse, centerConstraints);

			inner.add(centerPane, innerConstraints);
		}

		innerConstraints.gridx++;
		innerConstraints.weightx = 1;
		{
			JPanel enabledTitle = new JPanel(new BorderLayout());
			enabledTitle.setBorder(new CompoundBorder(new TitledBorder("Enabled Methods"), new EmptyBorder(5, 5, 5, 5)));

			enabledHouse.setVisibleRowCount(-1);
			enabledHouse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			enabledHouse.setTransferHandler(new TransferHandler() {
				@Override
				public boolean canImport(TransferSupport support) {
					if (!support.isDrop()) {
						return false;
					}

					support.setShowDropLocation(true);

					JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
					return dl.getIndex() >= 0 && dl.getIndex() <= enabledHouse.getModel().getSize();
				}

				@Override
				public boolean importData(TransferSupport support) {
					if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						return false;
					}

					JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
					int index = dl.getIndex();

					Object[] list = enabledHouse.getSelectedValues();
					for (int i = list.length - 1; i >= 0; i--) {
						final Object object = list[i];
						if (index > houseEnabledModel.indexOf(object)) {
							index--;
						}
						houseEnabledModel.removeElement(object);
						houseEnabledModel.insertElementAt(object, index);
						index = houseEnabledModel.indexOf(object);
					}

					return true;
				}

				@Override
				public int getSourceActions(JComponent component) {
					return MOVE;
				}

				@Override
				protected Transferable createTransferable(JComponent component) {
					return new StringSelection("");
				}
			});

			enabledHouse.setDropMode(DropMode.INSERT);
			enabledHouse.setDragEnabled(true);

			JScrollPane scrollPane = new JScrollPane(enabledHouse);
			enabledTitle.setPreferredSize(new Dimension(50, 50));

			enabledTitle.add(scrollPane, BorderLayout.CENTER);

			enabledTitle.add(new JLabel("Drag to set priority (highest at top)", JLabel.CENTER), BorderLayout.SOUTH);

			inner.add(enabledTitle, innerConstraints);
		}

		return inner;
	}

	private String prettyName(String value) {
		if (value == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder(value.length());
		if (value.length() > 0) {
			sb.append(Character.toUpperCase(value.charAt(0)));
		}
		if (value.length() > 1) {
			sb.append(value.substring(1).toLowerCase());
		}

		return sb.toString().replace('_', ' ');
	}

	private void remove(JList listEnabled, DefaultListModel disabledModel, DefaultListModel enabledModel) {
		int index = listEnabled.getSelectedIndex();
		if (listEnabled.getSelectedIndex() > -1) {
			Path selectedValue = (Path) listEnabled.getSelectedValue();
			disabledModel.addElement(selectedValue);
			enabledModel.removeElement(selectedValue);
			if (index > 0 && index >= enabledModel.getSize())
				listEnabled.setSelectedIndex(index - 1);
			else
				listEnabled.setSelectedIndex(index);
		}
	}

	private void saveActionPerformed() {
		script.log.info("Save GUI settings");
		final Properties settings = new Properties();
		settings.setProperty("offering", comboBoxOffering.getSelectedItem().toString());
		settings.setProperty("mode", comboBoxHouseMode.getSelectedItem().toString());
		settings.setProperty("friendname", friendName.getText());
		settings.setProperty("ignorename", ignoredPlayers.getText());
		settings.setProperty("lightburners", String.valueOf(lightBurners.isSelected()));
		settings.setProperty("summoning", String.valueOf(enableSummoning.isSelected()));
		settings.setProperty("bob", comboBoxBOB.getSelectedItem().toString());

		settings.setProperty("banks", joinModel(bankEnabledModel));
		settings.setProperty("house", joinModel(houseEnabledModel));

		//settings.setProperty("enablemousespeed", String.valueOf(mouseSpeedCheckBox.isSelected()));
		//settings.setProperty("mousespeed", String.valueOf(mouseSpeedSlider.getValue()));

		//settings.setProperty("screenshots", String.valueOf(screenshotCheckbox.isSelected()));
		settings.setProperty("onlyhouseobelisk", String.valueOf(houseRechargeCheckbox.isSelected()));
		settings.setProperty("summoningPotion", String.valueOf(summoningPotionCheckbox.isSelected()));

		//settings.setProperty("enableaura", String.valueOf(enableAura.isSelected()));
		//settings.setProperty("aura", String.valueOf(comboBoxAura.getSelectedItem()));

		settings.setProperty("stop_offering", String.valueOf(stopOfferingCheckbox.isSelected()));

		settings.setProperty("stoplevelselected", String.valueOf(stopLevelCheckbox.isSelected()));
		settings.setProperty("stoplevel", String.valueOf(stopLevelSpinner.getValue()));

		options.save(settings);
	}

	private String joinModel(DefaultListModel model) {
		StringBuilder joined = new StringBuilder();
		final Enumeration<Path> elements = (Enumeration<Path>) model.elements();
		while (elements.hasMoreElements()) {
			final Path path = elements.nextElement();
			joined.append(path.getName());
			joined.append("~");
		}

		if (joined.length() > 0) {
			joined.deleteCharAt(joined.length() - 1);
		}

		return joined.toString();
	}

	private void startButtonActionPerformed() {
		script.log.info("Start script");
		if (startPressed) {
			return;
		}

		if (houseEnabledModel.isEmpty()) {
			tabbedPane.setSelectedComponent(houseTab);
			JOptionPane.showMessageDialog(this, "You must add at least one house method, otherwise how will I get there?");
			return;
		}
		if (bankEnabledModel.isEmpty()) {
			tabbedPane.setSelectedComponent(bankTab);
			JOptionPane.showMessageDialog(this, "You must add at least one bank method, otherwise how will I get there?");
			return;
		}

		// General
		switch (comboBoxHouseMode.getSelectedIndex()) {
			case 1:
				options.useOtherHouse.set(true);
				if (friendName.getText().length() == 0 || friendName.getText().startsWith("Player Name")) {
					tabbedPane.setSelectedComponent(generalTab);
					JOptionPane.showMessageDialog(this, "Invalid friends name, consider using open house detection.");
					return;
				}

				if (friendName.getText().length() > 0) {
					String[] names = friendName.getText().split(",");
					if (names.length == 0)
						names = new String[]{friendName.getText()};
					if (names.length > 0) {
						for (String name : names) {
							name = name.trim();
							if (name.length() > 0) {
								final OpenHouse openHouse = new OpenHouse(script, name);
								openHouse.setTimeAdded(Long.MAX_VALUE);
								if (!script.houseHandler.openHouses.contains(openHouse)) {
									script.houseHandler.openHouses.add(openHouse);
								}
							}
						}
					}
				}
				break;
			case 2:
				options.useOtherHouse.set(true);
				options.detectHouses.set(true);

				if (ignoredPlayers.getText().length() > 0) {
					String[] names = ignoredPlayers.getText().split(",");
					if (names.length == 0)
						names = new String[]{ignoredPlayers.getText()};
					if (names.length > 0) {
						for (String name : names) {
							if (name.length() > 0) {
								script.houseHandler.ignoreHouses.add(name);
							}
						}
					}
				}
				break;
		}

		boolean requiresFailsafe = false;
		boolean hasFailsafe = false;

		// House
		if (!houseEnabledModel.isEmpty()) {
			for (Object object : houseEnabledModel.toArray()) {
				if (object instanceof Path) {
					if (((Path) object).isFailsafe()) {
						hasFailsafe = true;
					}
					if (((Path) object).isFailsafeRequired()) {
						requiresFailsafe = true;
					}
				}
			}
		}

		if (requiresFailsafe && !hasFailsafe) {
			tabbedPane.setSelectedComponent(houseTab);
			JOptionPane.showMessageDialog(this, "Require a failsafe house method to be enabled (in case forgot teleport item)");
			return;
		}

		requiresFailsafe = false;
		hasFailsafe = false;

		// Banking
		if (!bankEnabledModel.isEmpty()) {
			for (Object object : bankEnabledModel.toArray()) {
				if (object != null && object instanceof Path) {
					if (((Path) object).isFailsafe()) {
						hasFailsafe = true;
					}
					if (((Path) object).isFailsafeRequired()) {
						requiresFailsafe = true;
					}
				}
			}
		}

		if (requiresFailsafe && !hasFailsafe) {
			tabbedPane.setSelectedComponent(bankTab);
			JOptionPane.showMessageDialog(this, "Require a failsafe bank method to be enabled (in case forgot teleport item)");
			return;
		}

		startPressed = true;

		options.offering = (Offering) comboBoxOffering.getSelectedItem();
		options.lightBurners.set(lightBurners.isSelected());

		//other
		//options.screenshots = screenshotCheckbox.isSelected();
		options.stopOffering.set(stopOfferingCheckbox.isSelected());


		//options.useAura = enableAura.isSelected();
		//options.aura = (MyAuras.Aura) comboBoxAura.getSelectedItem();

		options.stopLevelEnabled.set(stopLevelCheckbox.isSelected());
		options.stopLevel.set(Integer.parseInt(String.valueOf(stopLevelSpinner.getValue())));

		// Summoning
		options.useBOB.set(enableSummoning.isSelected());
		options.onlyHouseObelisk.set(houseRechargeCheckbox.isSelected());
		options.onlySummoningPotions.set(summoningPotionCheckbox.isSelected());
		options.beastOfBurden = familiars[comboBoxBOB.getSelectedIndex()];

		final Script.Controller executor = script.ctx.controller;
		executor.offer(new Runnable() {
			@Override
			public void run() {
				try {
					if (options.detectHouses.get()) {
						script.ctx.properties.put("login.world", Integer.toString(31));
					}

				/*if (options.useBOB) {
					try {
						Summoning.getSecondsLeft();
					} catch (AbstractMethodError ignored) {
						options.useBOB = false;
						instance.getLogHandler().print("Settings hook broken, disabling summoning", Color.red);
					}
				}*/

					script.altarTask = new AltarTask(script);

					script.ctx.submit(new AnimationMonitor<LogGildedAltar>(script));
					script.tree.add(new AntiBan<LogGildedAltar>(script));

					// v4
		/*
				// POSTBACK
				// LEAVE HOUSE
				new WidgetCloser(),
				// LogoutIdle
				new WorldThirtyOne(),
				// PickupBones
				new ActivateAura(),
				// BANKING
		*/

					script.tree.add(new LogoutIdle<LogGildedAltar>(script, script.options.bonesOffered));

//				if (options.useBOB) {
//					script.tree.add(new PickupBones(script));
//				}

					final Postback postback = new Postback(script);
					script.tree.add(postback);
					script.getExecQueue(Script.State.STOP).add(new Runnable() {
						@Override
						public void run() {
							postback.postback();
						}
					});

					if (options.stopLevelEnabled.get()) {
						script.tree.add(new StopLevel<LogGildedAltar>(script, Skills.PRAYER, options.stopLevel.get()));
					}

					script.tree.add(script.leaveHouse = new LeaveHouse(script));
					script.bankingTask = new BankingTask(script, modelToPathList(bankEnabledModel));

					if (script.ctx.game.loggedIn() && script.ctx.players.local() != null) {
						if (options.lightBurners.get() && !(script.ctx.backpack.select().id(Banking.ID_MARRENTIL).count() >= 2) || !(script.ctx.backpack.select().id(options.offering.getId()).count() > 0)) {
							script.bankingTask.setBanking();
						}
					} else {
						script.bankingTask.setBanking();
					}

					script.tree.add(new RenewFamiliar(script));

					script.tree.add(script.summoningTask = new SummoningTask(script));
					script.tree.add(script.bankingTask);
					script.tree.add(script.houseTask = new HouseTask(script, modelToPathList(houseEnabledModel)));


					script.tree.add(script.altarTask);

					options.TimeLastOffering.set(System.currentTimeMillis());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		if (options.detectHouses.get()) {
			executor.offer(new Task<LogGildedAltar>(script) {
				@Override
				public void run() {
					script.houseHandler.addOpenHouses();
				}
			});
		}

		options.status = "Setup finished";
		dispose();
	}

	private void loadActionPerformed() {
		script.log.info("Load GUI settings");
		try {
			final Properties settings = options.load();
			if (settings.size() > 0) {
				String temp = settings.getProperty("offering", "Bones").toLowerCase();
				for (int i = 0; i < comboBoxOffering.getItemCount(); i++) {
					if (comboBoxOffering.getItemAt(i).toString().toLowerCase().equals(temp)) {
						comboBoxOffering.setSelectedIndex(i);
						break;
					}
				}
				temp = settings.getProperty("mode", "Use own altar").toLowerCase();
				for (int i = 0; i < comboBoxHouseMode.getItemCount(); i++) {
					if (comboBoxHouseMode.getItemAt(i).toString().toLowerCase().equals(temp)) {
						comboBoxHouseMode.setSelectedIndex(i);
						break;
					}
				}
				temp = settings.getProperty("bob", "Bull ant (40)").toLowerCase();
				for (int i = 0; i < comboBoxBOB.getItemCount(); i++) {
					if (comboBoxBOB.getItemAt(i).toString().toLowerCase().equals(temp)) {
						comboBoxBOB.setSelectedIndex(i);
						break;
					}
				}

				friendName.setText(settings.getProperty("friendname", "name1,name2"));
				ignoredPlayers.setText(settings.getProperty("ignorename", "name1,name2"));
				lightBurners.setSelected(Boolean.parseBoolean(settings.getProperty("lightburners", "true")));
				enableSummoning.setSelected(Boolean.parseBoolean(settings.getProperty("summoning", "false")));

				//mouseSpeedCheckBox.setSelected(Boolean.parseBoolean(settings.getProperty("enablemousespeed", "false")));
				//mouseSpeedSlider.setValue(Integer.parseInt(settings.getProperty("mousespeed", "2")));

				//screenshotCheckbox.setSelected(Boolean.parseBoolean(settings.getProperty("screenshots", "false")));
				stopOfferingCheckbox.setSelected(Boolean.parseBoolean(settings.getProperty("stop_offering", "true")));

				houseRechargeCheckbox.setSelected(Boolean.parseBoolean(settings.getProperty("onlyhouseobelisk", "false")));
				summoningPotionCheckbox.setSelected(Boolean.parseBoolean(settings.getProperty("summoningPotion", "false")));

				/*enableAura.setSelected(Boolean.parseBoolean(settings.getProperty("enableaura", "false")));
				MyAuras.Aura aura = MyAuras.getAura(settings.getProperty("aura", "Corruption"));

				if (aura == null) {
					comboBoxAura.setSelectedIndex(0);
				} else {
					comboBoxAura.setSelectedItem(aura);
				}*/

				stopLevelCheckbox.setSelected(Boolean.parseBoolean(settings.getProperty("stoplevelselected", "false")));
				stopLevelSpinner.setValue(Integer.parseInt(settings.getProperty("stoplevel", "99")));

				bankEnabledModel.clear();
				bankDisabledModel.clear();
				houseEnabledModel.clear();
				houseDisabledModel.clear();

				for (Path p : Path.values()) {
					if (p.isEnabledInList()) {
						switch (p.getPathType()) {
							case BANK:
								bankDisabledModel.addElement(p);
								break;
							case HOME:
								houseDisabledModel.addElement(p);
								break;
						}
					}
				}


				for (String name : settings.getProperty("banks", "").split("~")) {
					for (int i = 0; i < bankDisabledModel.getSize(); i++) {
						final Object element = bankDisabledModel.getElementAt(i);
						if (element != null) {
							if (((Path) element).getName().equals(name)) {
								disabledBank.setSelectedIndex(i);
								add(disabledBank, bankDisabledModel, bankEnabledModel);
							}
						}
					}
				}

				for (String name : settings.getProperty("house", "").split("~")) {
					for (int i = 0; i < houseDisabledModel.getSize(); i++) {
						final Object element = houseDisabledModel.getElementAt(i);
						if (element != null) {
							if (((Path) element).getName().equals(name)) {
								disabledHouse.setSelectedIndex(i);
								add(disabledHouse, houseDisabledModel, houseEnabledModel);
							}
						}
					}
				}
			}
			//script.log.info("Settings Loaded");
		} catch (Exception ignored) {
		}
	}
}

